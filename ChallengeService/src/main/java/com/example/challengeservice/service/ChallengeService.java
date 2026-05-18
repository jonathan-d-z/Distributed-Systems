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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final FoodServiceClient foodServiceClient;

    private static final float CALORIES_WARN_LOW  = 800f;
    private static final float CALORIES_WARN_HIGH = 1800f;
    private static final float CALORIES_TARGET    = 2000f;
    private static final float PROTEIN_WARN_LOW   = 25f;
    private static final float PROTEIN_TARGET     = 50f;
    private static final float FIBER_WARN_LOW     = 10f;
    private static final float FIBER_TARGET       = 25f;
    private static final float SUGAR_WARN_HIGH    = 40f;
    private static final float FAT_WARN_HIGH      = 60f;

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    @Transactional
    public List<Challenge> generateChallenges() {
        List<FoodDto> foods = foodServiceClient.getAllFoods();

        float totalCalories = 0f;
        float totalProteins = 0f;
        float totalFiber    = 0f;
        float totalSugars   = 0f;
        float totalFat      = 0f;

        for (FoodDto food : foods) {
            totalCalories += food.getEnergyKcal() != null ? food.getEnergyKcal() : 0f;
            totalProteins += food.getProteins()   != null ? food.getProteins()   : 0f;
            totalFiber    += food.getFiber()       != null ? food.getFiber()      : 0f;
            totalSugars   += food.getSugars()      != null ? food.getSugars()     : 0f;
            totalFat      += food.getFat()         != null ? food.getFat()        : 0f;
        }

        challengeRepository.deleteAllByCompletedFalse();

        List<Challenge> newChallenges = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Water – always active
        newChallenges.add(Challenge.builder()
                .type("WATER").title("Stay Hydrated 💧")
                .description("Aim for 8 glasses (about 2 liters) of water throughout the day. " +
                             "Proper hydration boosts your energy and concentration.")
                .completed(false).createdAt(now).build());

        // Calories
        if (totalCalories < CALORIES_WARN_LOW) {
            newChallenges.add(Challenge.builder()
                    .type("CALORIES").title("Fuel Up! 🍽️")
                    .description(String.format("You've only consumed %.0f kcal today. " +
                            "Your body needs energy – make sure to eat a balanced meal soon!", totalCalories))
                    .completed(false).createdAt(now).build());
        } else if (totalCalories > CALORIES_WARN_HIGH) {
            newChallenges.add(Challenge.builder()
                    .type("CALORIES").title("Watch Your Calories 🔥")
                    .description(String.format("You've reached %.0f kcal today (goal: %.0f kcal). " +
                            "Consider lighter options for your next meal.", totalCalories, CALORIES_TARGET))
                    .completed(false).createdAt(now).build());
        }

        // Protein
        if (totalProteins < PROTEIN_WARN_LOW) {
            newChallenges.add(Challenge.builder()
                    .type("PROTEIN").title("Boost Your Protein 💪")
                    .description(String.format("You've had only %.0fg of protein today (goal: %.0fg). " +
                            "Try adding eggs, chicken, fish, legumes, or Greek yogurt to your next meal.",
                            totalProteins, PROTEIN_TARGET))
                    .completed(false).createdAt(now).build());
        }

        // Fiber
        if (totalFiber < FIBER_WARN_LOW) {
            newChallenges.add(Challenge.builder()
                    .type("FIBER").title("Add Some Fiber 🥦")
                    .description(String.format("Your fiber intake is %.0fg today (goal: %.0fg). " +
                            "Eat more vegetables, fruits, whole grains, or legumes to support your digestion.",
                            totalFiber, FIBER_TARGET))
                    .completed(false).createdAt(now).build());
        }

        // Sugar
        if (totalSugars > SUGAR_WARN_HIGH) {
            newChallenges.add(Challenge.builder()
                    .type("SUGAR").title("Cut Back on Sugar 🍬")
                    .description(String.format("You've already consumed %.0fg of sugar today. " +
                            "Avoid sweets, sugary drinks, and processed snacks for the rest of the day.", totalSugars))
                    .completed(false).createdAt(now).build());
        }

        // Fat
        if (totalFat > FAT_WARN_HIGH) {
            newChallenges.add(Challenge.builder()
                    .type("FAT").title("Reduce Fat Intake 🥑")
                    .description(String.format("Your fat intake is %.0fg today. " +
                            "Choose leaner protein sources and avoid fried foods for your next meals.", totalFat))
                    .completed(false).createdAt(now).build());
        }

        return challengeRepository.saveAll(newChallenges);
    }

    public Optional<Challenge> completeChallenge(Long id) {
        return challengeRepository.findById(id).map(challenge -> {
            challenge.setCompleted(true);
            return challengeRepository.save(challenge);
        });
    }
}
