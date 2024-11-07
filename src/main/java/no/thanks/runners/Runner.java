package no.thanks.runners;

import no.thanks.game.NoThanksGame;
import no.thanks.game.Utility;
import no.thanks.game.impl.PlayYourBoardPlayer;
import no.thanks.runners.util.TestResults;

import java.util.*;

public class Runner {

    public static final String COUNTER_VALUE = "COUNTER_VALUE";
    public static final String TURN_WEIGHTING = "TURN_WEIGHTING";
    public static final String CARD_WEIGHT = "CARD_WEIGHT";
    public static final String PICKUP_THRESHOLD = "PICKUP_THRESHOLD";

    public static void main(String[] args) {
        List<TestResults> globalResults = new ArrayList<>(240000);
        Map<Double, List<TestResults>> bestConfigs = new HashMap<>();
//        Map<String, Double> baseLineConfig = Map.of(
//                COUNTER_VALUE, 0.0,
//                CARD_WEIGHT, 4.5,
//                TURN_WEIGHTING, 0.0,
//                PICKUP_THRESHOLD, 42.0
//        );
//        Map<String, Double> baseLineConfig = Map.of(
//                COUNTER_VALUE, 4.0,
//                CARD_WEIGHT, 3.0,
//                TURN_WEIGHTING, -5.0,
//                PICKUP_THRESHOLD, 18.0
//        );
//        Map<String, Double> baseLineConfig = Map.of(
//                COUNTER_VALUE, 4.0,
//                CARD_WEIGHT, 7.0,
//                TURN_WEIGHTING, 4.0,
//                PICKUP_THRESHOLD, 58.0
//        );
//        Map<String, Double> baseLineConfig = Map.of(
//                COUNTER_VALUE, -8.0,
//                CARD_WEIGHT, 4.0,
//                TURN_WEIGHTING, -10.0,
//                PICKUP_THRESHOLD, 72.0
//        );
//        Map<String, Double> baseLineConfig = Map.of(COUNTER_VALUE,-9.0, CARD_WEIGHT,3.0, TURN_WEIGHTING,-10.0, PICKUP_THRESHOLD,54.0);
//        Map<String, Double> baseLineConfig = Map.of(TURN_WEIGHTING,-8.0, CARD_WEIGHT,6.0, COUNTER_VALUE,-14.0, PICKUP_THRESHOLD,34.0);
//        Map<String, Double> baseLineConfig = Map.of(COUNTER_VALUE,-13.0, CARD_WEIGHT,10.0, TURN_WEIGHTING,-10.0, PICKUP_THRESHOLD,10.0);
//        Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,10.0, TURN_WEIGHTING,-6.0, CARD_WEIGHT,7.0, COUNTER_VALUE,-14.0);
//        Map<String, Double> baseLineConfig = Map.of(COUNTER_VALUE,-10.0, CARD_WEIGHT,6.5, TURN_WEIGHTING,-7.0, PICKUP_THRESHOLD,7.0);
//        Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,2.0, TURN_WEIGHTING,-2.0, CARD_WEIGHT,2.0, COUNTER_VALUE,-9.0);
// Reduced Step sizes
//        Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,-12.0, TURN_WEIGHTING,-2.0, CARD_WEIGHT,4.0, COUNTER_VALUE,-9.5);
//        Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,11.0, COUNTER_VALUE,-8.0, CARD_WEIGHT,1.5, TURN_WEIGHTING,-3.0);
//        Map<String, Double> baseLineConfig = Map.of(COUNTER_VALUE,-10.5, CARD_WEIGHT,3.0, TURN_WEIGHTING,-3.0, PICKUP_THRESHOLD,5.0);
//        Map<String, Double> baseLineConfig = Map.of(COUNTER_VALUE,-9.5, CARD_WEIGHT,5.0, TURN_WEIGHTING,-4.0, PICKUP_THRESHOLD,9.0);
// 7977-8000ish were better        Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,13.0, TURN_WEIGHTING,-3.0, CARD_WEIGHT,6.0, COUNTER_VALUE,-11.5);
// 12000 ish better        Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,11.0, TURN_WEIGHTING,-7.0, CARD_WEIGHT,5.0, COUNTER_VALUE,-11.5);
// 7429 better       Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,7.0, TURN_WEIGHTING,-6.0, CARD_WEIGHT,6.0, COUNTER_VALUE,-11.5);
// 1174 better       Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,5.0, TURN_WEIGHTING,-3.0, CARD_WEIGHT,5.0, COUNTER_VALUE,-13.5);
// 2825 better        Map<String, Double> baseLineConfig = Map.of(COUNTER_VALUE,-13.5, CARD_WEIGHT,6.5, TURN_WEIGHTING,-3.0, PICKUP_THRESHOLD,-15.0);
// 877 better       Map<String, Double> baseLineConfig = Map.of(TURN_WEIGHTING,-2.0, PICKUP_THRESHOLD,-32.0, COUNTER_VALUE,-15.0, CARD_WEIGHT,6.5);
// 1130 better        Map<String, Double> baseLineConfig = Map.of(CARD_WEIGHT,6.0, COUNTER_VALUE,-14.0, PICKUP_THRESHOLD,-17.0, TURN_WEIGHTING,-2.);
// 1173 BETTER        Map<String, Double> baseLineConfig = Map.of(CARD_WEIGHT,6.0, COUNTER_VALUE,-14.0, PICKUP_THRESHOLD,-17.0, TURN_WEIGHTING,-2.);
//  2424 better       Map<String, Double> baseLineConfig = Map.of(CARD_WEIGHT,7.5, TURN_WEIGHTING,-3.0, PICKUP_THRESHOLD,-16.0, COUNTER_VALUE,-12.5);
//  8659      Map<String, Double> baseLineConfig = Map.of(COUNTER_VALUE,-10.5, CARD_WEIGHT,8.0, TURN_WEIGHTING,-8.0, PICKUP_THRESHOLD,8.0);
// 4747 better        Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,14.0, TURN_WEIGHTING,-8.0, CARD_WEIGHT,9.5, COUNTER_VALUE,-9.0);
// 2608         Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,13.0, TURN_WEIGHTING,-6.0, CARD_WEIGHT,8.5, COUNTER_VALUE,-10.0);
// 3914        Map<String, Double> baseLineConfig = Map.of(COUNTER_VALUE,-8.5, CARD_WEIGHT,7.5, TURN_WEIGHTING,-5.0, PICKUP_THRESHOLD,9.0);
// increasing search size
// 9168       Map<String, Double> baseLineConfig = Map.of(CARD_WEIGHT,5.0, TURN_WEIGHTING,-10.0, PICKUP_THRESHOLD,8.0, COUNTER_VALUE,-6.5);
//3265  Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,5.599999999999993, TURN_WEIGHTING,-6.0, CARD_WEIGHT,5.5, COUNTER_VALUE,-8.5);
// 2574        Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,7.999999999999987, COUNTER_VALUE,-8.0, CARD_WEIGHT,6.5, TURN_WEIGHTING,-6.0);
//4951        Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,5.599999999999979, TURN_WEIGHTING,-6.0, CARD_WEIGHT,7.0, COUNTER_VALUE,-7.5);
// Counting % better
//revert to 877
//Better %.	Better Absolute,	Global win %,	Global Average Score // Is
// 0.01236	618	0.2068784	85.6573519        Map<String, Double> baseLineConfig = Map.of(TURN_WEIGHTING,-2.0, PICKUP_THRESHOLD,-32.0, COUNTER_VALUE,-15.0, CARD_WEIGHT,6.5);
// 0.0172	860	0.2379127	84.318788       Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,-30.799999999999933, TURN_WEIGHTING,-2.0, CARD_WEIGHT,6.75, COUNTER_VALUE,-13.0);
// 0.02824	1412	0.2550592	83.3722238        Map<String, Double> baseLineConfig = Map.of(COUNTER_VALUE,-12.5, PICKUP_THRESHOLD,-34.39999999999987, TURN_WEIGHTING,-3.0, CARD_WEIGHT,7.75);
        // Reduced PT search to remove 0% wins as much as possible
// 0.050372549	2569	0.366423529	79.54823686       Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,-23.599999999999795, TURN_WEIGHTING,-3.0, CARD_WEIGHT,8.5, COUNTER_VALUE,-12.5);
// 0.067666667	3451	0.35449549	79.99607108       Map<String, Double> baseLineConfig = Map.of(TURN_WEIGHTING,-3.0, PICKUP_THRESHOLD,-36.39999999999982, COUNTER_VALUE,-14.0, CARD_WEIGHT,8.5);
// 0.042313725	2158	0.338037059	80.72361833       Map<String, Double> baseLineConfig = Map.of(COUNTER_VALUE,-16.0, PICKUP_THRESHOLD,-37.999999999999886, TURN_WEIGHTING,-2.5, CARD_WEIGHT,9.0);
// 0.129098039	6584	0.430037451	77.28874294       Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,-20.399999999999967, TURN_WEIGHTING,-2.5, CARD_WEIGHT,8.5, COUNTER_VALUE,-14.0);
// 0.107784314	5497	0.432445098	77.20449353       Map<String, Double> baseLineConfig = Map.of(COUNTER_VALUE,-15.5, CARD_WEIGHT,9.25, TURN_WEIGHTING,-3.0, PICKUP_THRESHOLD,-24.399999999999984);
// 0.158039216	8866	0.457852674	76.58417255       Map<String, Double> baseLineConfig = Map.of(TURN_WEIGHTING,-3.25, PICKUP_THRESHOLD,-33.200000000000024, COUNTER_VALUE,-15.0, CARD_WEIGHT,10.25);
// 0.160891266	9026	0.45552656	76.60157665       Map<String, Double> baseLineConfig = Map.of(TURN_WEIGHTING,-3.4000000000000004, PICKUP_THRESHOLD,-31.600000000000097, COUNTER_VALUE,-16.5, CARD_WEIGHT,11.0);
        // revert to start
// 0.117468806	6590	0.446975579	76.67896889       Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD,-23.599999999999795, TURN_WEIGHTING,-3.0, CARD_WEIGHT,8.5, COUNTER_VALUE,-12.5);
        //revert to current best
        Map<String, Double> baseLineConfig = Map.of(PICKUP_THRESHOLD, -23.599999999999795, TURN_WEIGHTING, -3.0, CARD_WEIGHT, 8.5, COUNTER_VALUE, -12.5);

        double cvHalfRange = 2.5;
        int cvSteps = 10;
        double twHalfRange = 0.75;
        int twSteps = 10;
        double cwHalfRange = 1.25;
        int cwSteps = 10;
        double ptHalfRange = 20;
        int ptSteps = 50;

        for (double cv = baseLineConfig.get(COUNTER_VALUE) - cvHalfRange; cv < baseLineConfig.get(COUNTER_VALUE) + cvHalfRange; cv += (cvHalfRange * 2) / (cvSteps)) {
            for (double tw = baseLineConfig.get(TURN_WEIGHTING) - twHalfRange; tw < baseLineConfig.get(TURN_WEIGHTING) + twHalfRange; tw += (twHalfRange * 2) / twSteps) {
                for (double cw = baseLineConfig.get(CARD_WEIGHT) - cwHalfRange; cw < baseLineConfig.get(CARD_WEIGHT) + cwHalfRange; cw += (cwHalfRange * 2) / cwSteps) {
                    for (double pt = baseLineConfig.get(PICKUP_THRESHOLD) - ptHalfRange; pt < baseLineConfig.get(PICKUP_THRESHOLD) + ptHalfRange; pt += (ptHalfRange * 2) / ptSteps) {
                        Map<String, Double> config = Map.of(
                                COUNTER_VALUE, cv,
                                TURN_WEIGHTING, tw,
                                CARD_WEIGHT, cw,
                                PICKUP_THRESHOLD, pt
                        );

                        TestResults results = new TestResults(config);
                        for (int i = 0; i < 100; i++) {
                            final PlayYourBoardPlayer testedPlayer = new PlayYourBoardPlayer(config, "Test");
                            NoThanksGame game = new NoThanksGame(testedPlayer, new PlayYourBoardPlayer(baseLineConfig, "Baseline"));
                            game.play();
                            results.addResults(game, testedPlayer);
                        }
                        if (results.getWinPercentage() > .5) {

                            for (int i = 0; i < 100; i++) {
                                final PlayYourBoardPlayer testedPlayer = new PlayYourBoardPlayer(config, "Test");
                                NoThanksGame game = new NoThanksGame(testedPlayer, new PlayYourBoardPlayer(baseLineConfig, "Baseline"));
                                game.play();
                                results.addResults(game, testedPlayer);
                            }
                            bestConfigs.merge(results.getWinPercentage(), Utility.join(new LinkedList<>(), results), (o, n) -> Utility.join(o, results));
                        }
                        if (results.getWinPercentage() == 0) {
                            System.out.println(results);
                        }
                        globalResults.add(results);
                    }
                }
            }
        }
        System.out.println(globalResults.size());

//        TestResults results = new TestResults(baseLineConfig);
//        TestResults otherResults = new TestResults(baseLineConfig);
//                        for (int i = 0; i < 500; i++) {
//                            final PlayYourBoardPlayer testedPlayer = new PlayYourBoardPlayer(baseLineConfig);
//                            final PlayYourBoardPlayer other = new PlayYourBoardPlayer(baseLineConfig);
//                            NoThanksGame game = new NoThanksGame(testedPlayer, other);
//                            game.play();
//                            results.addResults(game, testedPlayer);
//                            otherResults.addResults(game, other);
//                        }
//        System.out.println(results);
//        System.out.println(otherResults);
        Map<String, Map<Double, TestResults>> binnedResults = Map.of(
                COUNTER_VALUE, new HashMap<>(),
                TURN_WEIGHTING, new HashMap<>(),
                CARD_WEIGHT, new HashMap<>(),
                PICKUP_THRESHOLD, new HashMap<>()
        );
        globalResults.forEach(testResults -> {
            binnedResults.get(COUNTER_VALUE)
                    .merge(
                            testResults.config.get(COUNTER_VALUE),
                            new TestResults(testResults),
                            TestResults::merge);
            binnedResults.get(TURN_WEIGHTING)
                    .merge(
                            testResults.config.get(TURN_WEIGHTING),
                            new TestResults(testResults),
                            TestResults::merge);
            binnedResults.get(CARD_WEIGHT)
                    .merge(
                            testResults.config.get(CARD_WEIGHT),
                            new TestResults(testResults),
                            TestResults::merge);
            binnedResults.get(PICKUP_THRESHOLD)
                    .merge(
                            testResults.config.get(PICKUP_THRESHOLD),
                            new TestResults(testResults),
                            TestResults::merge);

        });
//        System.out.println(binnedResults);

        binnedResults.forEach(
                (s, doubleTestResultsMap) -> {
                    System.out.println(s);
                    doubleTestResultsMap.entrySet().stream()
                            .sorted(
                                    Comparator.<Map.Entry<Double, TestResults>>comparingDouble(kv -> kv.getValue().getWinPercentage())
                                            .thenComparingDouble(kv -> kv.getValue().getMeanScore())
                                            .thenComparingDouble(Map.Entry::getKey)
                            )
                            .forEach(kv -> System.out.println(kv.getKey() + " " + kv.getValue().toShortString()));
                }
        );
        final long totalNumberOfBetter = bestConfigs.entrySet().stream().filter(kv -> kv.getKey() >= 0.5).map(Map.Entry::getValue).mapToLong(Collection::size).sum();
        System.out.println("Best Configs: " + totalNumberOfBetter + " => " + ((double) totalNumberOfBetter) / globalResults.size());
        bestConfigs.entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getKey)).map(Object::toString).forEach(each -> System.out.printf("%3.800s" + System.lineSeparator(), each));

        System.out.println("Global Averages % " + globalResults.stream().mapToDouble(TestResults::getWinPercentage).average() + " score " + globalResults.stream().mapToDouble(TestResults::getMeanScore).average());


    }


//    int[] localOptimize(final int[] initialGuess) {
//        int nParams = initialGuess.length;
//        double bestE = E(initialGuess);
//        int[] bestParValues = initialGuess;
//        boolean improved = true;
//        while ( improved ) {
//            improved = false;
//            for (int pi = 0; pi < nParams; pi++) {
//                int[] newParValues = bestParValues;
//                newParValues[pi] += 1;
//                double newE = E(newParValues);
//                if (newE < bestE) {
//                    bestE = newE;
//                    bestParValues = newParValues;
//                    improved = true;
//                } else {
//                    newParValues[pi] -= 2;
//                    newE = E(newParValues);
//                    if (newE < bestE) {
//                        bestE = newE;
//                        bestParValues = newParValues;
//                        improved = true;
//                    }
//                }
//            }
//        }
//        return bestParValues;
//    }

//    private double E(int[] initialGuess) {
//        IntStream.range(0, initialGuess.length);
//    }

}
