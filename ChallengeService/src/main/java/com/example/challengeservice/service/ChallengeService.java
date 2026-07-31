package com.example.challengeservice.service;

import com.example.challengeservice.client.FoodServiceClient;
import com.example.challengeservice.dtos.FoodDto;
import com.example.challengeservice.entities.Challenge;
import com.example.challengeservice.repositories.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final FoodServiceClient foodServiceClient;

    private static final float CALORIES_WARN_LOW = 800f;
    private static final float CALORIES_WARN_HIGH = 1800f;
    private static final float CALORIES_TARGET = 2000f;
    private static final float PROTEIN_WARN_LOW = 25f;
    private static final float PROTEIN_TARGET = 50f;
    private static final float FIBER_WARN_LOW = 10f;
    private static final float FIBER_TARGET = 25f;
    private static final float SUGAR_WARN_HIGH = 40f;
    private static final float FAT_WARN_HIGH = 60f;

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    @Transactional
    public List<Challenge> generateChallenges(String authorizationHeader) {
        List<FoodDto> foods = foodServiceClient.getAllFoods(authorizationHeader);

        challengeRepository.deleteAllByCompletedFalse();

        List<Challenge> newChallenges = foods == null || foods.isEmpty()
                ? starterChallenges()
                : foodBasedChallenges(foods);

        return challengeRepository.saveAll(newChallenges);
    }

    private List<Challenge> starterChallenges() {
        LocalDateTime now = LocalDateTime.now();
        List<Challenge> starterPool = new ArrayList<>(List.of(
                challenge("WATER", "Hydration Baseline",
                        "Drink two glasses of water before your next meal.", now),
                challenge("FIBER", "Add a Whole Food",
                        "Log a fruit, vegetable, legume, or whole grain today so your challenges can become more specific.", now),
                challenge("PROTEIN", "Plan Protein",
                        "Choose one protein source for your next meal, such as eggs, yogurt, tofu, fish, chicken, or beans.", now),
                challenge("LOGGING", "Use Gram Amounts",
                        "Log your next food with an accurate gram amount, such as 30g or 100g.", now),
                challenge("SUGAR", "Start Low Sugar",
                        "Choose water, unsweetened tea, or another low-sugar drink today.", now),
                challenge("CALORIES", "Build a Balanced Plate",
                        "For your first logged meal, include one protein source, one carbohydrate source, and one fruit or vegetable.", now)
        ));
        Collections.shuffle(starterPool);
        return starterPool.stream().limit(3).toList();
    }

    private List<Challenge> foodBasedChallenges(List<FoodDto> foods) {
        float totalCalories = sum(foods, Nutrient.CALORIES);
        float totalProteins = sum(foods, Nutrient.PROTEIN);
        float totalFiber = sum(foods, Nutrient.FIBER);
        float totalSugars = sum(foods, Nutrient.SUGAR);
        float totalFat = sum(foods, Nutrient.FAT);

        FoodDto highestSugar = maxBy(foods, Nutrient.SUGAR);
        FoodDto highestFat = maxBy(foods, Nutrient.FAT);
        FoodDto highestProtein = maxBy(foods, Nutrient.PROTEIN);
        FoodDto highestFiber = maxBy(foods, Nutrient.FIBER);
        FoodDto latestFood = foods.get(foods.size() - 1);

        List<Challenge> challenges = new ArrayList<>();
        List<Challenge> optionalChallenges = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        challenges.add(challenge("WATER", "Balance Your Log",
                String.format("You logged %d food item%s. Drink a glass of water before adding the next item.",
                        foods.size(), foods.size() == 1 ? "" : "s"), now));

        if (totalCalories < CALORIES_WARN_LOW) {
            optionalChallenges.add(challenge("CALORIES", "Add a Balanced Meal",
                    String.format("Your logged total is %.0f kcal. Add a meal with carbohydrates, protein, and healthy fats.", totalCalories), now));
        } else if (totalCalories > CALORIES_WARN_HIGH) {
            optionalChallenges.add(challenge("CALORIES", "Choose a Lighter Next Meal",
                    String.format("Your logged total is %.0f kcal against a rough %.0f kcal daily target. Keep the next meal lighter.", totalCalories, CALORIES_TARGET), now));
        } else {
            optionalChallenges.add(challenge("CALORIES", "Keep Calories Steady",
                    String.format("Your logged total is %.0f kcal. Keep your next snack moderate and nutrient dense.", totalCalories), now));
        }

        if (totalProteins < PROTEIN_WARN_LOW) {
            optionalChallenges.add(challenge("PROTEIN", "Raise Protein Intake",
                    String.format("You logged %.0fg protein. Add a protein-focused food to move toward %.0fg.", totalProteins, PROTEIN_TARGET), now));
        } else if (highestProtein != null) {
            optionalChallenges.add(challenge("PROTEIN", "Repeat a Strong Protein Choice",
                    String.format("%s contributed the most protein in your log. Use a similar protein source again if it fits your plan.", foodName(highestProtein)), now));
        }

        if (totalFiber < FIBER_WARN_LOW) {
            optionalChallenges.add(challenge("FIBER", "Add Fiber",
                    String.format("You logged %.0fg fiber. Add fruit, vegetables, oats, beans, or whole grains to move toward %.0fg.", totalFiber, FIBER_TARGET), now));
        } else if (highestFiber != null) {
            optionalChallenges.add(challenge("FIBER", "Keep the Fiber Habit",
                    String.format("%s helped your fiber total. Add another high-fiber food later today.", foodName(highestFiber)), now));
        }

        if (totalSugars > SUGAR_WARN_HIGH && highestSugar != null) {
            optionalChallenges.add(challenge("SUGAR", "Lower Added Sugar",
                    String.format("%s is the highest-sugar item in your log. Choose a low-sugar option next.", foodName(highestSugar)), now));
        } else {
            optionalChallenges.add(challenge("SUGAR", "Prefer Low-Sugar Drinks",
                    "Keep drinks unsweetened for the rest of this tracking session.", now));
        }

        if (totalFat > FAT_WARN_HIGH && highestFat != null) {
            optionalChallenges.add(challenge("FAT", "Balance Fat Intake",
                    String.format("%s contributed the most fat. Prefer grilled, baked, or lean options next.", foodName(highestFat)), now));
        }

        optionalChallenges.add(challenge("LOGGING", "Log One More Detail",
                String.format("Your latest entry was %s at %.0fg. Add one more food with an accurate gram amount.",
                        foodName(latestFood), latestFood.getQuantityGrams() != null ? latestFood.getQuantityGrams() : 100f), now));
        optionalChallenges.add(challenge("LOGGING", "Compare Portions",
                String.format("Your latest portion was %.0fg. Try logging a smaller or larger portion next to see how nutrients change.",
                        latestFood.getQuantityGrams() != null ? latestFood.getQuantityGrams() : 100f), now));

        Collections.shuffle(optionalChallenges);
        challenges.addAll(optionalChallenges);
        return challenges.stream().limit(5).toList();
    }

    private Challenge challenge(String type, String title, String description, LocalDateTime createdAt) {
        return Challenge.builder()
                .type(type)
                .title(title)
                .description(description)
                .completed(false)
                .createdAt(createdAt)
                .build();
    }

    private float sum(List<FoodDto> foods, Nutrient nutrient) {
        return foods.stream()
                .map(nutrient::value)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(0f, Float::sum);
    }

    private FoodDto maxBy(List<FoodDto> foods, Nutrient nutrient) {
        return foods.stream()
                .filter(food -> nutrient.value(food).isPresent())
                .max(Comparator.comparing(food -> nutrient.value(food).orElse(0f)))
                .orElse(null);
    }

    private String foodName(FoodDto food) {
        if (food == null) {
            return "the logged food";
        }
        if (food.getProduct_name() != null && !food.getProduct_name().isBlank()) {
            return food.getProduct_name();
        }
        if (food.getGeneric_name() != null && !food.getGeneric_name().isBlank()) {
            return food.getGeneric_name();
        }
        return food.getCode() != null ? food.getCode() : "the logged food";
    }

    public Optional<Challenge> completeChallenge(Long id) {
        return challengeRepository.findById(id).map(challenge -> {
            challenge.setCompleted(true);
            return challengeRepository.save(challenge);
        });
    }

    private enum Nutrient {
        CALORIES {
            Optional<Float> value(FoodDto food) { return Optional.ofNullable(food.getEnergyKcal()); }
        },
        PROTEIN {
            Optional<Float> value(FoodDto food) { return Optional.ofNullable(food.getProteins()); }
        },
        FIBER {
            Optional<Float> value(FoodDto food) { return Optional.ofNullable(food.getFiber()); }
        },
        SUGAR {
            Optional<Float> value(FoodDto food) { return Optional.ofNullable(food.getSugars()); }
        },
        FAT {
            Optional<Float> value(FoodDto food) { return Optional.ofNullable(food.getFat()); }
        };

        abstract Optional<Float> value(FoodDto food);
    }
}
