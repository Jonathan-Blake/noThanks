package no.thanks.runners;

import no.thanks.game.AbstractNoThanksPlayer;
import no.thanks.game.NoThanksGame;
import no.thanks.game.Utility;
import no.thanks.game.impl.PredictPickupPlayerBugFixed;
import no.thanks.runners.util.ResultsCSV;
import no.thanks.runners.util.TestResults;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PredictionRunner {
    public static final String COUNTER_VALUE = "COUNTER_VALUE";
    public static final String COUNTER_TURN_WEIGHTING = "COUNTER_TURN_WEIGHTING";
    public static final String FUTURE_CARD_WEIGHTING = "FUTURE_CARD_WEIGHTING";
    public static final String CARD_WEIGHT = "CARD_WEIGHT";
    public static final String PICKUP_THRESHOLD = "PICKUP_THRESHOLD";
    //TestResults{config={COUNTER_TURN_WEIGHTING=-7.049053607020172, FUTURE_CARD_WEIGHTING=16.541248045604522, PICKUP_THRESHOLD=-62.17841939879959, COUNTER_VALUE=1.0682086509031148, CARD_WEIGHT=20.007706478860555}, results={ totalGames= 7080, averageScore= 1.097457627118644, winPercentage= 0.7305084745762712 } }
//TestResults{config={COUNTER_TURN_WEIGHTING=, FUTURE_CARD_WEIGHTING=, PICKUP_THRESHOLD=, COUNTER_VALUE=, CARD_WEIGHT=}, results={ totalGames= 46180, averageScore= 32.427262884365526, winPercentage= 0.4091598094413166 } }
//TestResults{config={COUNTER_TURN_WEIGHTING=-9.14830535709095, FUTURE_CARD_WEIGHTING=3.599992517386021, PICKUP_THRESHOLD=-36.21764214005483, COUNTER_VALUE=-4.381508724818985, CARD_WEIGHT=27.09368930284212}, results={ totalGames= 43540, averageScore= 27.202135966926964, winPercentage= 0.42080845199816264 } }
//
//Process finished with exit code 0
    public static final Random rand = new Random();
    //    static final Map<String, Double> baseLineConfig = Map.of(            PICKUP_THRESHOLD, -23.599999999999795,           COUNTER_TURN_WEIGHTING, -3.0,            CARD_WEIGHT, 8.5,            FUTURE_CARD_WEIGHTING, 6.0,            COUNTER_VALUE, -12.5);
//static final Map<String, Double> baseLineConfig = Map.of(
//        COUNTER_TURN_WEIGHTING,-2.9957332263168683, FUTURE_CARD_WEIGHTING,0.17067339306959273, PICKUP_THRESHOLD,-24.7703471192712, COUNTER_VALUE,-14.405765688455762, CARD_WEIGHT,8.37512487881736);
//    static final Map<String, Double> baseLineConfig = Map.of(
//            COUNTER_TURN_WEIGHTING,-3.535121876,
//            FUTURE_CARD_WEIGHTING,1.2768662076120327,
//            PICKUP_THRESHOLD,-19.8386821,
//            COUNTER_VALUE,-15.14518858,
//            CARD_WEIGHT,7.965069029304528);
    static final List<Map<String, Double>> baseLineConfigList = List.of(Map.of(COUNTER_TURN_WEIGHTING, 7.493773407266295, FUTURE_CARD_WEIGHTING, 8.872168814380444, PICKUP_THRESHOLD, -14.415119618229486, COUNTER_VALUE, -21.6657550030092, CARD_WEIGHT, 7.144260519536426),
            Map.of(COUNTER_TURN_WEIGHTING, -16.4, CARD_WEIGHT, 16.900000000000002, FUTURE_CARD_WEIGHTING, 14.100000000000001, COUNTER_VALUE, -3.6999999999999957, PICKUP_THRESHOLD, -30.0),
            Map.of(COUNTER_TURN_WEIGHTING, -16.48021516919564, CARD_WEIGHT, 17.652338399801668, FUTURE_CARD_WEIGHTING, 13.74754078078411, COUNTER_VALUE, -6.79023115329573, PICKUP_THRESHOLD, -24.513548320332028),
            Map.of(COUNTER_TURN_WEIGHTING, -8.473313327375147, CARD_WEIGHT, 24.35473217162971, FUTURE_CARD_WEIGHTING, 9.638349426409013, COUNTER_VALUE, -8.926987796338834, PICKUP_THRESHOLD, -46.00544412186672),
            Map.of(COUNTER_TURN_WEIGHTING, -9.185421088486269, CARD_WEIGHT, 27.348526259519083, FUTURE_CARD_WEIGHTING, 3.7245184975959, COUNTER_VALUE, -4.780565192012347, PICKUP_THRESHOLD, -33.660066896798384),
            Map.of(COUNTER_TURN_WEIGHTING, -12.364189529407996, CARD_WEIGHT, 26.145364169878636, FUTURE_CARD_WEIGHTING, 17.815492187582382, COUNTER_VALUE, -9.653906985495716, PICKUP_THRESHOLD, -20.95093045383845),
            Map.of(COUNTER_TURN_WEIGHTING, -25.090515607909694, FUTURE_CARD_WEIGHTING, 32.95805641335263, PICKUP_THRESHOLD, -67.10482550588529, COUNTER_VALUE, -18.73812737346414, CARD_WEIGHT, 37.30473516005936),
            Map.of(COUNTER_TURN_WEIGHTING, -18.635363933509485, FUTURE_CARD_WEIGHTING, 30.6803486852977, PICKUP_THRESHOLD, -70.66784993149916, COUNTER_VALUE, -14.73281525941755, CARD_WEIGHT, 36.654577973579556),
            Map.of(COUNTER_TURN_WEIGHTING, -18.718823926305667, FUTURE_CARD_WEIGHTING, 38.14808339241452, PICKUP_THRESHOLD, -35.091976150568726, COUNTER_VALUE, -32.0020558354392, CARD_WEIGHT, 44.160608333632084),
            Map.of(COUNTER_TURN_WEIGHTING, -12.629693950673992, FUTURE_CARD_WEIGHTING, 34.34873219782533, PICKUP_THRESHOLD, -31.998686214886952, COUNTER_VALUE, -30.716761974044616, CARD_WEIGHT, 63.784558966616146),
            Map.of(COUNTER_TURN_WEIGHTING, -10.714034839942203, FUTURE_CARD_WEIGHTING, 41.812884147542924, PICKUP_THRESHOLD, -31.57218918863408, COUNTER_VALUE, -31.50767856969544, CARD_WEIGHT, 65.68977428256952),
            Map.of(COUNTER_TURN_WEIGHTING, -19.673772693834678, FUTURE_CARD_WEIGHTING, 52.338448446542344, PICKUP_THRESHOLD, 7.769741993730669, COUNTER_VALUE, -28.21938205182723, CARD_WEIGHT, 66.39604886523544),// 3000 ish
            Map.of(COUNTER_TURN_WEIGHTING, -49.87653940977298, FUTURE_CARD_WEIGHTING, 57.35585355209492, PICKUP_THRESHOLD, 7.367377176493755, COUNTER_VALUE, -18.386283642439842, CARD_WEIGHT, 67.70108566413421), //110
            Map.of(COUNTER_TURN_WEIGHTING, -52.09640023863672, FUTURE_CARD_WEIGHTING, 57.811534054822886, PICKUP_THRESHOLD, -3.553084966807517, COUNTER_VALUE, -12.83259436880156, CARD_WEIGHT, 69.49277299952064), //2549
            Map.of(COUNTER_TURN_WEIGHTING, -42.55473645185496, FUTURE_CARD_WEIGHTING, 45.15033685338485, PICKUP_THRESHOLD, -25.43695679524438, COUNTER_VALUE, -7.988818087008729, CARD_WEIGHT, 77.23481083735926),  // 1185
            Map.of(COUNTER_TURN_WEIGHTING, -22.696188802496454, FUTURE_CARD_WEIGHTING, 48.21715422130189, PICKUP_THRESHOLD, -62.15109054881284, COUNTER_VALUE, -19.298557473092888, CARD_WEIGHT, 65.28857827505406), // 6288
            Map.of(COUNTER_TURN_WEIGHTING, -16.64907162524983, FUTURE_CARD_WEIGHTING, 82.35332144104596, PICKUP_THRESHOLD, -126.69451979062204, COUNTER_VALUE, -51.119292726849395, CARD_WEIGHT, 101.38528936236814), //1430
            Map.of(COUNTER_TURN_WEIGHTING, -15.328269904305143, FUTURE_CARD_WEIGHTING, 80.38485712632057, PICKUP_THRESHOLD, -153.18214392579128, COUNTER_VALUE, -44.726119740895705, CARD_WEIGHT, 125.62866004223146),  //2604
            Map.of(COUNTER_TURN_WEIGHTING, -17.216298032350196, FUTURE_CARD_WEIGHTING, 87.13350960782577, PICKUP_THRESHOLD, -173.18453609467548, COUNTER_VALUE, -51.50571502906478, CARD_WEIGHT, 105.9317045825815), //6186
            Map.of(COUNTER_TURN_WEIGHTING, -9.320856352166142, FUTURE_CARD_WEIGHTING, 63.801417327795065, PICKUP_THRESHOLD, -191.01593488053626, COUNTER_VALUE, -63.41571750476898, CARD_WEIGHT, 104.43970784446316), //821
            Map.of(COUNTER_TURN_WEIGHTING, -7.617423429164065, FUTURE_CARD_WEIGHTING, 66.19751835697409, PICKUP_THRESHOLD, -201.32594278286854, COUNTER_VALUE, -58.76573753167009, CARD_WEIGHT, 108.10275158957627), //5466
            Map.of(COUNTER_TURN_WEIGHTING, -8.193230044575607, FUTURE_CARD_WEIGHTING, 61.18938289395879, PICKUP_THRESHOLD, -198.18992857008843, COUNTER_VALUE, -59.62301246628613, CARD_WEIGHT, 83.56521356087264)
    );
    static final Map<String, Double> baseLineConfig = baseLineConfigList.get(baseLineConfigList.size() - 1);
    static final Map<String, Double> currentBestimate = new HashMap<>(baseLineConfig);
    //COUNTER_TURN_WEIGHTING=, FUTURE_CARD_WEIGHTING=, PICKUP_THRESHOLD=, COUNTER_VALUE=, CARD_WEIGHT=
    private static final String TURN_WEIGHTING = "TURN_WEIGHTING";
    private static final Map<String, Double> pybBaseline = Map.of(PICKUP_THRESHOLD, -23.599999999999795, TURN_WEIGHTING, -3.0, CARD_WEIGHT, 8.5, COUNTER_VALUE, -12.5);
    private static final Logger LOGGER = Logger.getLogger(PredictionRunner.class.getName());
    static List<String> axis = List.of(
            COUNTER_VALUE, COUNTER_TURN_WEIGHTING, CARD_WEIGHT, PICKUP_THRESHOLD, FUTURE_CARD_WEIGHTING
    );
    //    private static final double K = 32;
    private static Map<String, Double> stepSize = Map.of(
            PICKUP_THRESHOLD, 5.0,
            COUNTER_TURN_WEIGHTING, 2.5,
            CARD_WEIGHT, 2.5,
            FUTURE_CARD_WEIGHTING, 2.5,
            COUNTER_VALUE, 2.5);

    public static void main(String[] args) {
        playerComparison();
    }

    public static void playerComparison() {

//        TestResults randomComparison = new TestResults(Map.of());
//        for (int i = 0; i < 500; i++) {
//            PredictPickupPlayerBugFixed predictingPlayer = new PredictPickupPlayerBugFixed("Predict_fixed");
//            NoThanksGame game = new NoThanksGame(predictingPlayer, new RandomActionPlayer());
//            game.play();
//            randomComparison.addResults(game, predictingPlayer);
//        }
//        System.out.println(randomComparison);
////        System.out.println(PredictPickupPlayerBugFixed.predictionResults);
////        System.out.println(PredictPickupPlayerBugFixed.totalPredictionResults);
////        PredictPickupPlayerBugFixed.predictionResults = new HashMap<>();
//
//        TestResults playYourBoardComparisonPredict = new TestResults(Map.of());
//        TestResults playYourBoardComparisonBase = new TestResults(Map.of());
//        for (int i = 0; i < 1000; i++) {
//            PredictPickupPlayerBugFixed predictingPlayer = new PredictPickupPlayerBugFixed("Predict_fixed");
////            PredictPickupPlayerBugFixed predictingPlayer2 = new PredictPickupPlayerBugFixed("Predict2");
////            PredictPickupPlayerBugFixed predictingPlayer3= new PredictPickupPlayerBugFixed("Predict3");
//            final PlayYourBoardPlayer baseline = new PlayYourBoardPlayer(pybBaseline, "baseline");
////            final PlayYourBoardPlayer baseline2 = new PlayYourBoardPlayer(pybBaseline, "baseline2");
//            NoThanksGame game = new NoThanksGame( predictingPlayer,baseline);
////            game.log(true);
//            game.play();
//            playYourBoardComparisonPredict.addResults(game, predictingPlayer);
////            playYourBoardComparisonPredict.addResults(game, predictingPlayer2);
//            playYourBoardComparisonBase.addResults(game, baseline);
////            playYourBoardComparisonBase.addResults(game, baseline2);
////            LOGGER.log(Level.WARNING, game.cards().toString());
//        }
//        System.out.println("Prediction Results " + playYourBoardComparisonPredict);
//        System.out.println("Baseline PlayBoard Results " + playYourBoardComparisonBase);
////        System.out.println(PredictPickupPlayerBugFixed.predictionResults);
////        System.out.println(PredictPickupPlayerBugFixed.totalPredictionResults);
////        PredictPickupPlayerBugFixed.predictionResults = new HashMap<>();
//        TestResults unfixedComparisonPredict = new TestResults(Map.of());
//        TestResults unfixedComparisonBase = new TestResults(Map.of());
//        for (int i = 0; i < 1000; i++) {
//            PredictPickupPlayerBugFixed predictingPlayer = new PredictPickupPlayerBugFixed("Predict_fixed");
////            PredictPickupPlayerBugFixed predictingPlayer2 = new PredictPickupPlayerBugFixed("Predict2");
////            PredictPickupPlayerBugFixed predictingPlayer3= new PredictPickupPlayerBugFixed("Predict3");
//            final PredictPickupPlayer unfixed_predict = new PredictPickupPlayer( "unfixed_predict");
////            final PlayYourBoardPlayer baseline2 = new PlayYourBoardPlayer(pybBaseline, "baseline2");
//            NoThanksGame game = new NoThanksGame( predictingPlayer,unfixed_predict);
////            game.log(true);
//            game.play();
//            unfixedComparisonPredict.addResults(game, predictingPlayer);
//            unfixedComparisonBase.addResults(game, unfixed_predict);
//        }
//        System.out.println("Fixed Prediction Results " + unfixedComparisonPredict);
//        System.out.println("Baseline Prediction Results " + unfixedComparisonBase);

        System.out.println("Baseline Comparisons");

        List<TestResults> testResults = new ArrayList<>(baseLineConfigList.stream().map(TestResults::new).toList());
        Collections.shuffle(testResults);// Theoretically test results should be getting stronger each time so shuffle to ensure random first round
        int playersPerGame = 2;
        List<Double> eloEstimate = new ArrayList<>(Stream.generate(() -> 1000.0).limit(testResults.size()).toList());
//        TestResults[][] matchUpFavour = new TestResults[baseLineConfigList.size()][baseLineConfigList.size()];
        for (int y = 0; y < 128; y++) {
            System.out.println("Swiss Tournament Round " + y + " . . . ");
            for (int i = 0; i < testResults.size(); i += playersPerGame) {
                final int x = baseLineConfigList.indexOf(testResults.get(i).config);
                if (i + 1 >= testResults.size()) {
                    System.out.println("skipping " + x + "  " + testResults.get(i).getWinPercentage());
                    continue;
                }
                final int x1 = baseLineConfigList.indexOf(testResults.get(i + 1).config);
                final double eloPrediction = eloPrediction(eloEstimate.get(x), eloEstimate.get(x1));
                System.out.print("Test between " + x + "(" + eloEstimate.get(x) + ")" + "   " + x1 + "(" + eloEstimate.get(x1) + ")" + " expected Score " + eloPrediction + " :");
                final TestResults t0 = new TestResults(testResults.get(i));
                final TestResults t1 = new TestResults(testResults.get(i + 1));
                for (int gameNo = 0; gameNo < 250; gameNo++) {

                    PredictPickupPlayerBugFixed p0 = new PredictPickupPlayerBugFixed(testResults.get(i).config, "P0");
                    PredictPickupPlayerBugFixed p1 = new PredictPickupPlayerBugFixed(testResults.get(i + 1).config, "P1");
//                    PredictPickupPlayerBugFixed p2 = new PredictPickupPlayerBugFixed(testResults.get(i+2).config, "P2");
//                        PredictPickupPlayerBugFixed p3 = new PredictPickupPlayerBugFixed(testResults.get(i+3).config, "P3");
//                    PredictPickupPlayerBugFixed p4 = new PredictPickupPlayerBugFixed(baseLineConfig, "Default");

                    AbstractNoThanksPlayer[] abstractNoThanksPlayers = {p0, p1};
                    final ArrayList<AbstractNoThanksPlayer> shuffledPlayers = new ArrayList<>(Arrays.asList(abstractNoThanksPlayers));
                    Collections.shuffle(shuffledPlayers);
                    abstractNoThanksPlayers = shuffledPlayers.toArray(abstractNoThanksPlayers);//unneccassary assignment?

                    NoThanksGame game = new NoThanksGame(abstractNoThanksPlayers);

                    game.play();
                    t0.addResults(game, p0);
                    t1.addResults(game, p1);
                }
//                if(matchUpFavour[x][x1] == null){
//                    matchUpFavour[x][x1] = t0;
//                } else {
//                    matchUpFavour[x][x1].merge(t0);
//                }
//                if(matchUpFavour[x1][x] == null){
//                    matchUpFavour[x1][x] = t1;
//                } else {
//                    matchUpFavour[x1][x].merge(t1);
//                }
                assert (t0.getWinPercentage() + t1.getWinPercentage() >= 1.0);
                final double actualElo = t0.getWinPercentage() - (t0.getDrawPercentage() / 2);
                System.out.print(" : " + t0.getWinPercentage() + "   " + t1.getWinPercentage() + " ");
                eloEstimate.set(x, eloEstimate.get(x) + (64 - y / 2) * (actualElo - eloPrediction));
                eloEstimate.set(x1, eloEstimate.get(x1) + (64 - y / 2) * ((1 - actualElo) - (1 - eloPrediction)));
//                testResults.get(i).merge(t0);
//                testResults.get(i+1).merge(t1);
                System.out.println(" actual Results: " + actualElo + " " + eloEstimate.get(x) + "  " + eloEstimate.get(x1));
//                System.out.println(" : "+testResults.get(i).getWinPercentage()+"   "+testResults.get(i+1).getWinPercentage());
//                    testResults.get(i+2).addResults(game, p2);
//                        testResults.get(i+3).addResults(game, p3);
//                System.out.println("Iteration "+i +" complete");
            }
//            System. gc();
//            Collections.shuffle(testResults);
            testResults.sort(Comparator.comparingDouble(testResult -> eloEstimate.get(baseLineConfigList.indexOf(((TestResults) testResult).config))).reversed());
        }
        System.out.println();
//        System.out.println(eloEstimate);
        System.out.println();
//        for (TestResults[] results : matchUpFavour) {
//            System.out.println(Arrays.toString(Arrays.stream(results).map((results1 -> {
//                if (results1== null){
//                    return "";
//                } else {
//                    return results1.getWinPercentage();
//                }
//            })).toArray()).replace("[","").replace("]",""));
//        }
        testResults.forEach(each -> System.out.println(baseLineConfigList.indexOf(each.config) + ",   " + each.getWinPercentage() + ", " + each.getMeanScore() + ", " + eloEstimate.get(baseLineConfigList.indexOf(each.config))));
        String[][] distArray = new String[baseLineConfigList.size()][baseLineConfigList.size()];
        for (int i = 0; i < baseLineConfigList.size(); i++) {
//            distArray[i] = new String[baseLineConfigList.size()];
            Map<String, Double> x = baseLineConfigList.get(i);
            for (int j = 0; j < baseLineConfigList.size(); j++) {
                Map<String, Double> y = baseLineConfigList.get(j);
                distArray[i][j] = String.format("%s", dist(x, y));
            }
        }
        for (String[] strings : distArray) {
            System.out.println(Arrays.toString(strings));
        }
    }

    public static void gradientTuning() {
        final double strictness_base_limit = 0.15;
        final double strictness_comparison_limit = 0.275;
        Map<Map<String, Double>, TestResults> bestPerformances = new HashMap<>();

        try {
            ResultsCSV writer = new ResultsCSV();
            writer.setColumns(
                    new String[]{
                            "MEAN SCORE", "WIN_PERCENTAGE", COUNTER_VALUE, COUNTER_TURN_WEIGHTING, CARD_WEIGHT, PICKUP_THRESHOLD, FUTURE_CARD_WEIGHTING, "Baseline Win %", "Baseline mean score"
                    },
                    new ResultsCSV.ColumnValueFunction[]{
                            (currentBestimate, results, ignored) -> String.valueOf(results.getMeanScore()),
                            (currentBestimate, results, ignored) -> String.valueOf(results.getWinPercentage()),
                            (currentBestimate, results, ignored) -> String.valueOf(currentBestimate.get(COUNTER_VALUE)),
                            (currentBestimate, results, ignored) -> String.valueOf(currentBestimate.get(COUNTER_TURN_WEIGHTING)),
                            (currentBestimate, results, ignored) -> String.valueOf(currentBestimate.get(CARD_WEIGHT)),
                            (currentBestimate, results, ignored) -> String.valueOf(currentBestimate.get(PICKUP_THRESHOLD)),
                            (currentBestimate, results, ignored) -> String.valueOf(currentBestimate.get(FUTURE_CARD_WEIGHTING)),
                            (currentBestimate, results, baseResults) -> String.valueOf(baseResults.get("BaseWin")),
                            (currentBestimate, results, baseResults) -> String.valueOf(baseResults.get("BaseMeanScore"))
                    }
            );
            writer.ready();

            int j = 0;
            while (bestPerformances.size() < 30) {
                if (j > 20000) {
                    break;
                }
                Map<AbstractNoThanksPlayer, TestResults> testResultsMap = new HashMap<>();

                int finalJ = j;
                IntStream.range(0, 3).forEach(
                        i -> {
                            HashMap<String, Double> c = new HashMap<>(currentBestimate);
                            axis.forEach(variable -> {
                                c.put(variable, currentBestimate.get(variable) + (rand.nextDouble(-0.5, 0.5) * stepSize.get(variable)));
                                ;
                            });
                            testResultsMap.put(
                                    new PredictPickupPlayerBugFixed(c, "Prediction_" + finalJ + "_" + i),
                                    new TestResults(c)
                            );
                        }
                );

                TestResults baseResults = new TestResults(baseLineConfig);
                TestResults bestimateResults = new TestResults(currentBestimate);
                AtomicReference<Double> totalWins = new AtomicReference<>(0.0);

                for (int i = 0; i < 300; i++) {
                    AbstractNoThanksPlayer baseLinePlayer = new PredictPickupPlayerBugFixed(baseLineConfig, "PPPBF_" + finalJ + "_Base");
                    AbstractNoThanksPlayer bestimatePlayer = new PredictPickupPlayerBugFixed(currentBestimate, "BestimatePerformance");
                    List<AbstractNoThanksPlayer> playerList = new LinkedList<>(testResultsMap.keySet().stream().toList());
                    playerList.add(baseLinePlayer);
                    playerList.add(bestimatePlayer);
                    NoThanksGame g = new NoThanksGame(Utility.shuffle(playerList));
                    g.play();
                    testResultsMap.forEach((key, value) -> value.addResults(g, key));
                    baseResults.addResults(g, baseLinePlayer);
                    bestimateResults.addResults(g, bestimatePlayer);
                }
                final List<TestResults> testResultsStream = testResultsMap.values().stream().filter(testResults -> testResults.getWinPercentage() == 0).toList();
                if (testResultsStream.size() == 1) {
                    testResultsStream.forEach(each -> writer.writeNext(each.config, each, new HashMap<>()));

                    testResultsMap.forEach((key, results) -> totalWins.updateAndGet(v -> v + results.getWinPercentage()));
                } else {
                    if (baseResults.getWinPercentage() < strictness_base_limit) {
                        for (int i = 0; i < 400; i++) {
                            AbstractNoThanksPlayer baseLinePlayer = new PredictPickupPlayerBugFixed(baseLineConfig, "PPPBF_" + finalJ + "_Base");
                            AbstractNoThanksPlayer bestimatePlayer = new PredictPickupPlayerBugFixed(currentBestimate, "BestimatePerformance");
                            List<AbstractNoThanksPlayer> playerList = new LinkedList<>(testResultsMap.keySet().stream().toList());
                            playerList.add(baseLinePlayer);
                            playerList.add(bestimatePlayer);
                            NoThanksGame g = new NoThanksGame(playerList.toArray(new AbstractNoThanksPlayer[5]));
                            g.play();
                            testResultsMap.forEach((key, value) -> value.addResults(g, key));
                            baseResults.addResults(g, baseLinePlayer);
                            bestimateResults.addResults(g, bestimatePlayer);
                        }
                    }

                    if (baseResults.getWinPercentage() < strictness_base_limit) {
                        testResultsMap.forEach((abstractNoThanksPlayer, testResults) -> {
                            if (testResults.getWinPercentage() > strictness_comparison_limit) {
                                bestPerformances.merge(testResults.config, testResults, TestResults::merge);
                            }
                        });
//                        if (bestimateResults.getWinPercentage() > 0.3) {
//                            bestPerformances.merge( Map.copyOf(currentBestimate), bestimateResults, TestResults::merge);
//                        }
                    }

                    testResultsMap.forEach((key, value) -> {
                        totalWins.updateAndGet(v -> v + value.getWinPercentage());
                        //Logging specific game configs
//                    System.out.println(value.config + " : " + value.toShortString());
                        writer.writeNext(value.config, value, new HashMap<>());
                    });
                    writer.writeNext(currentBestimate, bestimateResults, Map.of("BaseWin", baseResults.getWinPercentage(), "BaseMeanScore", baseResults.getMeanScore()));
                    System.out.println(j + " " + bestPerformances.size() + " " + currentBestimate + " Base Performance: " + baseResults.getMeanScore() + " " + baseResults.getWinPercentage() + "  Bestimate performance: " + bestimateResults.getMeanScore() + "  " + bestimateResults.getWinPercentage() + "  Dist To Base: " + dist(baseLineConfig, currentBestimate));
                }
                axis.forEach(
                        variable -> {
                            AtomicReference<Double> updatedBestimate = new AtomicReference<>(0.0);
                            testResultsMap.forEach((key, value) -> updatedBestimate
                                    .updateAndGet(v -> (v + value.config.get(variable) * (value.getWinPercentage() / totalWins.get()))));
                            currentBestimate.put(variable, updatedBestimate.get());
                        }
                );

                j++;
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

        System.out.println();
//        ObjectMapper mapper = new ObjectMapper();
        //        mapper.writeValue(new File("PredictionGradient.json"), );
        bestPerformances.values().stream()
                .sorted(Comparator.comparingDouble(TestResults::getWinPercentage))
                .forEach(System.out::println);

        System.out.println();
        System.out.println();
        bestPerformances.merge(currentBestimate, new TestResults(currentBestimate), TestResults::merge);

        bestPerformances.forEach((key, value) -> {
            bestPerformances.forEach((key1, value1) -> {
                if (key1 == key) {
                    return;
                }
                bestPerformances.forEach((key2, value2) -> {
                    if (key2 == key) {
                        return;
                    }
                    if (key1 == key2) {
                        return;
                    }
                    if (value2.getWinPercentage() < 0.20) {
                        return;
                    }
                    if (value1.getWinPercentage() < 0.175) {
                        return;
                    }
                    if (value.getWinPercentage() < 0.15) {
                        return;
                    }
                    for (int i = 0; i < 20; i++) {
                        PredictPickupPlayerBugFixed player = new PredictPickupPlayerBugFixed(value.config, "Player1");
                        PredictPickupPlayerBugFixed player1 = new PredictPickupPlayerBugFixed(value1.config, "Player2");
                        PredictPickupPlayerBugFixed player2 = new PredictPickupPlayerBugFixed(value2.config, "Player3");
                        NoThanksGame game = new NoThanksGame(player, player1, player2, new PredictPickupPlayerBugFixed(currentBestimate, "Dummy"), new PredictPickupPlayerBugFixed(currentBestimate, "Dummy2"));
                        game.play();
                        value.addResults(game, player);
                        value1.addResults(game, player1);
                        value2.addResults(game, player2);
                    }
                });
            });
            System.out.print("LoopDone");
        });
        System.out.println();
        System.out.println(currentBestimate);
        System.out.println();

        bestPerformances.values().stream()
                .sorted(Comparator.comparingDouble(TestResults::getWinPercentage))
                .forEach(System.out::println);

    }

    private static double dist(Map<String, Double> a, Map<String, Double> b) {
        AtomicReference<Double> ret = new AtomicReference<>(0.0);
        axis.forEach(axe -> ret.getAndAccumulate(Math.pow((a.get(axe) - b.get(axe)) / stepSize.get(axe), 2), Double::sum));
        return Math.sqrt(ret.get());
    }

    public static double eloPrediction(double p0Rating, double p1Rating) {
        return 1.0 / (1 + Math.pow(10, (p1Rating - p0Rating) / 400));
    }

}
