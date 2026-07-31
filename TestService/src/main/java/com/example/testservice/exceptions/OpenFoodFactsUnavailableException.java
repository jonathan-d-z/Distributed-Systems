package com.example.testservice.exceptions;

public class OpenFoodFactsUnavailableException extends RuntimeException {
    public OpenFoodFactsUnavailableException() {
        super("OpenFoodFacts is temporarily unavailable due to high demand. Please try again.");
    }
}
