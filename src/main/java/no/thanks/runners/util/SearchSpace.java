package no.thanks.runners.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SearchSpace {
    private final List<SearchVariableCoGenerator> variables;
    public boolean looped;
//        private final Double counter_value;
//        private final Double counter_turn_weighting;
//        private final Double future_card_weighting;
//        private final Double card_weight;
//        private final Double pickup_threshold;
//        private final Double turn_weighting;

//        public SearchSpace(Double counter_value, Double counter_turn_weighting, Double future_card_weighting, Double card_weight, Double pickup_threshold, Double turn_weighting) {
//            this.counter_value = counter_value;
//            this.counter_turn_weighting = counter_turn_weighting;
//            this.future_card_weighting = future_card_weighting;
//            this.card_weight = card_weight;
//            this.pickup_threshold = pickup_threshold;
//            this.turn_weighting = turn_weighting;
//        }

    public SearchSpace(List<SearchVariableCoGenerator> variableList) {
        this.variables = variableList;
        this.looped = false;
    }

    public static SearchSpaceBuilder builder() {
        return new SearchSpaceBuilder();
    }

    public List<Map<String, Double>> getAllValues() {
        LinkedList<Map<String, Double>> ret = new LinkedList<>();
        completeCycle:
        while (true) {
            ret.add(Map.ofEntries(this.variables.stream().map(SearchVariableCoGenerator::currentEntry).toList().toArray(new Map.Entry[variables.size()])));
            addNext:
            for (int i = variables.size() - 1; i >= 0; i--) {
                SearchVariableCoGenerator each = variables.get(i);
                if (!each.increment()) {
                    break addNext;
                } else if (i == 0) {
                    looped = true;
                    break completeCycle;
                }
            }
        }

        return ret;
    }

    public Map<String, Double> getNext() {
        Map<String, Double> ret = Map.ofEntries(this.variables.stream().map(SearchVariableCoGenerator::currentEntry).toList().toArray(new Map.Entry[variables.size()]));

        int i = variables.size() - 1;
        while (i >= 0) {
            SearchVariableCoGenerator each = variables.get(i);
            if (!each.increment()) {
                break;
            }
            i--;
        }
        looped = i == -1;
        return ret;
    }

    public Stream<Map<String, Double>> getRandomValues() {
        return Stream.generate(this::getRandomValue);
    }

    public Map<String, Double> getRandomValue() {
        return Map.ofEntries(variables.stream().map(SearchVariableCoGenerator::getRandomValue).toList().toArray(new Map.Entry[variables.size()]));
    }

    public Map<String, Double> getNeighbourValue(Map<String, Double> seed) {
        HashMap<String, Double> ret = new HashMap<>(Map.copyOf(seed));
        this.variables.forEach(searchVariableCoGenerator -> {
            if (ret.computeIfPresent(searchVariableCoGenerator.getName(), (key, value) -> searchVariableCoGenerator.wiggle(value)) == null) {
                ret.put(searchVariableCoGenerator.getName(), searchVariableCoGenerator.getRandomValue().getValue());
            }
        });
        return ret;
    }

    public static class SearchSpaceBuilder {
        private List<VariableBuilder> list = new LinkedList<>();

        public VariableBuilder variable(String counterValue) {
            final VariableBuilder newVar = new VariableBuilder(counterValue, this);
            this.list.add(newVar);
            return newVar;
        }

        private SearchSpace build() {
            return new SearchSpace(this.list.stream().map(VariableBuilder::build).toList());
        }
    }

    public static class VariableBuilder {
        private final String name;
        private final SearchSpaceBuilder parent;
        private double lowerLimit;
        private double increment;
        private double upperLimit;

        private VariableBuilder(String name, SearchSpaceBuilder parent) {
            this.name = name;
            this.parent = parent;
        }

        public VariableBuilder from(double from) {
            this.lowerLimit = from;
            return this;
        }

        public VariableBuilder increment(double increment) {
            this.increment = increment;
            return this;
        }

        public VariableBuilder to(double to) {
            this.upperLimit = to;
            return this;
        }

        public VariableBuilder nextVariable(String nextVariableName) {
            return parent.variable(nextVariableName);
        }

        public SearchSpace complete() {
            return parent.build();
        }

        private SearchVariableCoGenerator build() {
            return new SearchVariableCoGenerator(name, lowerLimit, upperLimit, increment);
        }
    }
}
