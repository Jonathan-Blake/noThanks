package no.thanks.runners;

import no.thanks.game.AbstractNoThanksPlayer;
import no.thanks.game.NoThanksGame;
import no.thanks.game.impl.PredictPickupPlayerBugFixed;
import no.thanks.runners.util.SearchSpace;
import no.thanks.runners.util.TestResults;

import java.util.*;

public class GeneticAlgorithmRunner {
    public static final String COUNTER_VALUE = "COUNTER_VALUE";
    public static final String COUNTER_TURN_WEIGHTING = "COUNTER_TURN_WEIGHTING";
    public static final String FUTURE_CARD_WEIGHTING = "FUTURE_CARD_WEIGHTING";
    public static final String CARD_WEIGHT = "CARD_WEIGHT";
    public static final String PICKUP_THRESHOLD = "PICKUP_THRESHOLD";
    public static final int playersPerGame = 3;
    static final SearchSpace searchSpace = SearchSpace.builder()
            .variable(COUNTER_VALUE).from(-50).increment(0.1).to(10)
            .nextVariable(COUNTER_TURN_WEIGHTING).from(-30).increment(0.1).to(0)
            .nextVariable(FUTURE_CARD_WEIGHTING).from(-10).increment(0.1).to(15)
            .nextVariable(CARD_WEIGHT).from(0).increment(0.1).to(60)
            .nextVariable(PICKUP_THRESHOLD).from(-30).increment(0.1).to(30)
            .complete();
    //    static final Map<String, Double> baseLineConfig = Map.of(COUNTER_TURN_WEIGHTING,7.493773407266295, FUTURE_CARD_WEIGHTING,8.872168814380444, PICKUP_THRESHOLD,-14.415119618229486, COUNTER_VALUE,-21.6657550030092, CARD_WEIGHT,7.144260519536426);
    static final Map<String, Double> baseLineConfig = Map.of(COUNTER_TURN_WEIGHTING, -16.4, CARD_WEIGHT, 16.900000000000002, FUTURE_CARD_WEIGHTING, 14.100000000000001, COUNTER_VALUE, -3.6999999999999957, PICKUP_THRESHOLD, -30.0);
    private static final String TURN_WEIGHTING = "TURN_WEIGHTING";
    public static final List<String> axes = List.of(COUNTER_VALUE, COUNTER_TURN_WEIGHTING, FUTURE_CARD_WEIGHTING, CARD_WEIGHT, PICKUP_THRESHOLD, TURN_WEIGHTING);
    private static final int groups = 33;

    public static void main(String[] args) {
        final int maxSize = groups * playersPerGame;
        List<TestResults> testResults = new ArrayList<>(searchSpace.getRandomValues().limit(maxSize).map(TestResults::new).toList());
        Map<ConfObject, TestResults> globalResults = new HashMap<>();
        int defaultWins = 0;
        int totalGames = 0;
        for (int seedloop = 0; seedloop < 30; seedloop++) {
            if (seedloop != 0) {
                Collections.shuffle(testResults);
            }
            for (int y = 0; y < 30; y++) {
//                System.out.println("Swiss Tournament Round "+y +" . . . ");
                if (y != 0) {
                    testResults.sort(Comparator.comparing(TestResults::getWinPercentage));
                }
                for (int i = 0; i < testResults.size(); i += playersPerGame) {
                    System.out.print("Beginning Iteration " + seedloop + ":" + y + ":" + i + " . . . ");
                    for (int gameNo = 0; gameNo < 75; gameNo++) {
                        PredictPickupPlayerBugFixed p0 = new PredictPickupPlayerBugFixed(testResults.get(i).config, "P0");
                        PredictPickupPlayerBugFixed p1 = new PredictPickupPlayerBugFixed(testResults.get(i + 1).config, "P1");
                        PredictPickupPlayerBugFixed p2 = new PredictPickupPlayerBugFixed(testResults.get(i + 2).config, "P2");
//                        PredictPickupPlayerBugFixed p3 = new PredictPickupPlayerBugFixed(testResults.get(i+3).config, "P3");
                        PredictPickupPlayerBugFixed p4 = new PredictPickupPlayerBugFixed(baseLineConfig, "Default");

                        AbstractNoThanksPlayer[] abstractNoThanksPlayers = {p0, p1, p2, p4};
                        final ArrayList<AbstractNoThanksPlayer> shuffledPlayers = new ArrayList<>(Arrays.asList(abstractNoThanksPlayers));
                        Collections.shuffle(shuffledPlayers);
                        abstractNoThanksPlayers = shuffledPlayers.toArray(abstractNoThanksPlayers);//unneccassary assignment?

                        NoThanksGame game = new NoThanksGame(abstractNoThanksPlayers);

                        game.play();
                        testResults.get(i).addResults(game, p0);
                        testResults.get(i + 1).addResults(game, p1);
                        testResults.get(i + 2).addResults(game, p2);
//                        testResults.get(i+3).addResults(game, p3);
                        if (game.getScore().values().stream().mapToInt(in -> in).min().getAsInt() == (game.getScore().get(p4.getId()))) {
                            defaultWins++;
                        }
                        totalGames++;
                    }
                    System.out.println("Iteration " + i + " complete");
                }
            }
            double defaultWinPercent = defaultWins / (double) totalGames;
            for (int index = 0; index < maxSize; index++) {
                if (testResults.get(index).getWinPercentage() > defaultWinPercent) {
                    final TestResults winningVal = testResults.get(index);
                    globalResults.merge(new ConfObject(winningVal.config), winningVal, TestResults::merge);
                    testResults.set(index, new TestResults(winningVal.config));
                } else {
                    testResults.set(index, new TestResults(searchSpace.getRandomValue()));
                    //Previous high performance fell off.
                    int finalIndex = index;
                    globalResults.computeIfPresent(new ConfObject(testResults.get(index).config), (o, n) -> n.merge(testResults.get(finalIndex)));
                }
            }
//            for (int index = 0; index < maxSize/2; index++) {
//                testResults.set(index, new TestResults(searchSpace.getRandomValue()));
//                //Previous high performance fell off.
//                int finalIndex = index;
//                globalResults.computeIfPresent(new ConfObject(testResults.get(index).config), (o, n)-> n.merge(testResults.get(finalIndex)));
//            }
//            for (int index = maxSize/2; index < maxSize; index++) {
//                final TestResults winningVal = testResults.get(index);
//                globalResults.merge(new ConfObject(winningVal.config), winningVal, TestResults::merge);
//                testResults.set(index, new TestResults(winningVal.config));
//            }
        }

        final double winPercentageToBeat = 1 / (playersPerGame + 1.0);
        final double defaultWinRate = ((double) defaultWins) / totalGames;
        final double previousDefaultWins = 0.6762154882154883;
        System.out.println("Default Wins " + defaultWinRate + " Beat last time? " + (defaultWinRate > previousDefaultWins * 1.01));
        System.out.println("winPercentageToBeat " + winPercentageToBeat);
        globalResults.entrySet().stream()
                .filter(kv -> kv.getValue().getWinPercentage() >= winPercentageToBeat)// Average score = 1 / maxPlayers
                .sorted(Comparator.comparingDouble(kv -> kv.getValue().getWinPercentage()))
                .forEach(System.out::println);
    }

    private static class ConfObject {
        public final Double counterValue;
        public final Double counterTurnWeighting;
        public final Double futureCardWeighting;
        public final Double cardWeight;
        public final Double pickupThreshold;
        private final Double turnWeighting;

        //        private ConfObject(Map.Entry<String, Double> counter_value, Map.Entry<String, Double> counter_turn_weighting, Map.Entry<String, Double> future_card_weighting, Map.Entry<String, Double> card_weight, Map.Entry<String, Double> pickup_threshold, Map.Entry<String, Double> turn_weighting) {
//            counterValue = counter_value;
//            counterTurnWeighting = counter_turn_weighting;
//            futureCardWeighting = future_card_weighting;
//            cardWeight = card_weight;
//            pickupThreshold = pickup_threshold;
//            turnWeighting = turn_weighting;
//        }
        private ConfObject(Double counter_value, Double counter_turn_weighting, Double future_card_weighting, Double card_weight, Double pickup_threshold, Double turn_weighting) {
            counterValue = counter_value;
            counterTurnWeighting = counter_turn_weighting;
            futureCardWeighting = future_card_weighting;
            cardWeight = card_weight;
            pickupThreshold = pickup_threshold;
            turnWeighting = turn_weighting;
        }

        private ConfObject(Map<String, Double> config) {
            this(config.get(COUNTER_VALUE), config.get(COUNTER_TURN_WEIGHTING), config.get(FUTURE_CARD_WEIGHTING), config.get(CARD_WEIGHT), config.get(PICKUP_THRESHOLD), config.get(TURN_WEIGHTING));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConfObject that = (ConfObject) o;

            if (!Objects.equals(counterValue, that.counterValue))
                return false;
            if (!Objects.equals(counterTurnWeighting, that.counterTurnWeighting))
                return false;
            if (!Objects.equals(futureCardWeighting, that.futureCardWeighting))
                return false;
            if (!Objects.equals(cardWeight, that.cardWeight)) return false;
            if (!Objects.equals(pickupThreshold, that.pickupThreshold))
                return false;
            return Objects.equals(turnWeighting, that.turnWeighting);
        }

        @Override
        public int hashCode() {
            int result = counterValue != null ? counterValue.hashCode() : 0;
            result = 31 * result + (counterTurnWeighting != null ? counterTurnWeighting.hashCode() : 0);
            result = 31 * result + (futureCardWeighting != null ? futureCardWeighting.hashCode() : 0);
            result = 31 * result + (cardWeight != null ? cardWeight.hashCode() : 0);
            result = 31 * result + (pickupThreshold != null ? pickupThreshold.hashCode() : 0);
            result = 31 * result + (turnWeighting != null ? turnWeighting.hashCode() : 0);
            return result;
        }
    }

}
