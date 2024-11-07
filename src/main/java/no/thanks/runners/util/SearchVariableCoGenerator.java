package no.thanks.runners.util;

import java.util.Map;
import java.util.Random;

public class SearchVariableCoGenerator {
    private static final Random RANDOM = new Random();
    private final String name;
    private final double lowerLimit;
    private final double upperLimit;
    private final double increment;
    private final double steps;
    private double current;

    public SearchVariableCoGenerator(String name, double lowerLimit, double upperLimit, double increment) {
        this.name = name;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.increment = increment;
        this.current = lowerLimit;
        steps = (this.upperLimit - this.lowerLimit) / this.increment;
    }

    public Map.Entry<String, Double> currentEntry() {
        return Map.entry(name, current);
    }

    public boolean increment() {
        this.current += increment;
        if (this.current > upperLimit) {
            this.current = this.lowerLimit;
            return true;
        }
        return false;
    }

    public Map.Entry<String, Double> getRandomValue() {
        return Map.entry(name, this.lowerLimit + RANDOM.nextInt((int) steps) * this.increment);
    }

    public String getName() {
        return this.name;
    }

    public Double wiggle(Double currentValue) {
        if (RANDOM.nextBoolean()) {
            return Math.min(currentValue + increment, this.upperLimit);
        } else {
            return Math.max(currentValue - increment, this.lowerLimit);
        }
    }
}
