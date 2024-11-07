package no.thanks.runners;

import no.thanks.game.AbstractNoThanksPlayer;
import no.thanks.game.NoThanksGame;
import no.thanks.game.Utility;
import no.thanks.game.eval.LogFncEvaluationFunctionBuilder;
import no.thanks.game.impl.EvalFunctionPredictionPlayer;
import no.thanks.game.impl.PredictPickupPlayerBugFixed;
import no.thanks.runners.util.ResultsCSV;
import no.thanks.runners.util.SearchSpace;
import no.thanks.runners.util.TestResults;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.DoubleStream;

public class EvaluationFunctionRunner {

    static double estimateElo = 1000;
    private static double bestGuessElo;
    private static String currentCounterWeighting = "currentCounterWeighting";
    private static String cardWeight = "cardWeight";
    private static String futureCardWeight = "futureCardWeight";
    private static String counterTurnInfluence = "counterTurnInfluence";
    private static String pickupThreshold = "pickupThreshold";
    private static String counterValue = "counterValue";


    public static void main(String[] args) {
        annealing();
    }

    private static void annealing() {
        SearchSpace searchSpace = getLinearEvaluationSearchSpace();
        Map<String, Double> stepSizes = Map.of("pickupThreshold", 1.0,
                "futureCardWeight", 0.5,
                "cardWeight", 0.5,
                "counterValue", 0.5,
                "counterTurnInfluence", 0.5,
                "currentCounterWeighting", 0.5
        );

        //61.096	1	4.4	1	-1.1	-0.34	-1.1	-0.54	5
        //118.504	0.864	1.9	0.6	0.04	0.36	0.54	0.3	1174

//        double bestEvaluation = 1.0;
//        Map<String, Double> bestConfig = searchSpace.getRandomValue();
//        Map<String, Double> bestConfig = Map.of("pickupThreshold", 4.5,
//                "futureCardWeight", -0.43,
//                "cardWeight", 0.05,
//                "counterValue", 0.13,
//                "counterTurnInfluence", 0.65,
//                "currentCounterWeighting", 0.59
//        );


        //Map.of("pickupThreshold", -2.7,
        //                "futureCardWeight", -0.26,
        //                "cardWeight", -0.14,
        //                "counterValue", -0.3,
        //                "counterTurnInfluence", 0.46,
        //                "currentCounterWeighting", 0.9
        //        );

        //Map<String, Double> bestConfig = Map.of("counterValue", -1.0444331801081967, "counterTurnInfluence", 0.7996075573527404, "pickupThreshold", -5.758866797123446, "futureCardWeight", 1.277394616038141, "cardWeight", -0.915651875756538, "currentCounterWeighting", 0.7657516851823037);//2243.283242 -> 828.2435139825191
//        Map<String, Double> bestConfig = Map.of(counterValue,-0.8458636330875553, pickupThreshold,-9.722186484744777, counterTurnInfluence,0.6684566304039337, futureCardWeight,0.9749068036069901, cardWeight,-0.4751292418648413, currentCounterWeighting,1.6734212816172735); // 2414.99046 -> 839.3612194738046
//        Map<String, Double> bestConfig = Map.of(counterValue,-1.782638679, pickupThreshold,-9.319827517, counterTurnInfluence,-0.337859668, futureCardWeight,0.981155737, cardWeight,0.101210246, currentCounterWeighting,1.358235955); // 2204.681369 -> 777.8342797383996

//        Map<String, Double> bestConfig = Map.of(counterValue,-1.0, pickupThreshold,-9.319827517, counterTurnInfluence,-0.337859668, futureCardWeight,0.981155737, cardWeight,0.101210246, currentCounterWeighting,1.0);
//        Map<String, Double> bestConfig = Map.of(counterValue,-1.0, counterTurnInfluence,-0.75018858791983, pickupThreshold,-8.764857758678502, futureCardWeight,0.6884803041143401, cardWeight,-0.14185150107752026, currentCounterWeighting,1.0);
//        Map<String, Double> bestConfig = Map.of(counterValue,-1.18179000153537, counterTurnInfluence,-0.464740361,pickupThreshold,-10.53877855, futureCardWeight,0.680223601, cardWeight,-0.25536652, currentCounterWeighting,1.340757477);
//        Map<String, Double> bestConfig = Map.of(counterValue,-0.509833185
//                , counterTurnInfluence,-0.589129539
//                ,pickupThreshold,-9.192539907
//                , futureCardWeight,0.622935573
//                , cardWeight,-0.262338364
//                , currentCounterWeighting,1.29729636);//894.3109419 -> 828.2435139825191 / 818
//        Map<String, Double> bestConfig = Map.of(counterValue,-0.672862594
//                , counterTurnInfluence,0.130420609
//                ,pickupThreshold,-11.47428953
//                , futureCardWeight,-0.393419344
//                , cardWeight,-1.227629943
//                , currentCounterWeighting,-0.374445815);//967.6743966343842 -> 856.4171439323073
//        Map<String, Double> bestConfig = Map.of(counterValue, -0.8758273356289924, counterTurnInfluence, -0.8597447862788914, pickupThreshold, -12.668153045695039, futureCardWeight, 1.1314639527428338, cardWeight, -0.5912275715875256, currentCounterWeighting, -1.4603927231427687); //963 -> 840
        Map<String, Double> bestConfig = Map.of(counterValue, -0.1592937895514105, counterTurnInfluence, -0.15273125741366145, pickupThreshold, -10.840281965356017, futureCardWeight, 0.8266623970380863, cardWeight, -1.0387635067362337, currentCounterWeighting, -2.2781372241837907); //970.5364389001011 -> 847.1739405658171

//        searchSpace.getRandomValue();// initialGuess
//        int i = 0;
//        while (!searchSpace.looped){
//            System.out.println(i+"  "+searchSpace.getNext());
//        }
        double tMin = 0.0001;
        double t = 3.0;
        final double decayRate = 1.5;
//        double energy =0;
//        double energyMax = 1.0;
        LogFncEvaluationFunctionBuilder builder = new LogFncEvaluationFunctionBuilder();

        TestResults temp = new TestResults(bestConfig);
        for (int j = 0; j < 250; j++) {
            final EvalFunctionPredictionPlayer player = new EvalFunctionPredictionPlayer(builder.build(builder.parametersFrom(bestConfig)), "candidate");
            final AbstractNoThanksPlayer baselinePlayer = getBaselinePlayer();
            final List<AbstractNoThanksPlayer> abstractNoThanksPlayers = Arrays.asList(player, baselinePlayer);
            Collections.shuffle(abstractNoThanksPlayers);
            NoThanksGame game = new NoThanksGame(
                    abstractNoThanksPlayers.toArray(new AbstractNoThanksPlayer[0])
            );
            game.play();
            temp.addResults(game, player);
        }
        final double tmpElo = temp.getWinPercentage() - (temp.getDrawPercentage() / 2);
        bestGuessElo = 1000;
        for (int j = 0; j < 75; j++) {
            bestGuessElo += (32 * (tmpElo - eloPrediction(bestGuessElo, 1000)));
        }


        var ref = new Object() {
            int x = 0;
        };

        try (ResultsCSV writer = new ResultsCSV()) {

            writer.setColumns(new String[]{
                            "Mean Score",
                            "Win Percentage",
                            "Pickup Threshold",
                            "Future Card Weighting",
                            "Card Weight",
                            "Counter Value",
                            "Turn Weighting",
                            "Current Counter Weighting",
                            "Loop Count",
                            "elo",
                            "BC Pickup Threshold",
                            "BC Future Card Weighting",
                            "BC Card Weight",
                            "BC Counter Value",
                            "BC Turn Weighting",
                            "BC Current Counter Weighting",
                            "BC elo",
                    },
                    new ResultsCSV.ColumnValueFunction[]{
                            (cb, totalResults, grad) -> String.valueOf(totalResults.getMeanScore()),
                            (cb, totalResults, grad) -> String.valueOf(totalResults.getWinPercentage()),
                            (cb, tr, grad) -> String.valueOf(cb.get("pickupThreshold")),
                            (cb, tr, grad) -> String.valueOf(cb.get("futureCardWeight")),
                            (cb, tr, grad) -> String.valueOf(cb.get("cardWeight")),
                            (cb, tr, grad) -> String.valueOf(cb.get("counterValue")),
                            (cb, tr, grad) -> String.valueOf(cb.get("counterTurnInfluence")),
                            (cb, tr, grad) -> String.valueOf(cb.get("currentCounterWeighting")),
                            (cb, tr, grad) -> String.valueOf(ref.x),
                            (cb, tr, grad) -> String.valueOf(estimateElo),
                            (cb, tr, grad) -> String.valueOf(grad.get("pickupThreshold")),
                            (cb, tr, grad) -> String.valueOf(grad.get("futureCardWeight")),
                            (cb, tr, grad) -> String.valueOf(grad.get("cardWeight")),
                            (cb, tr, grad) -> String.valueOf(grad.get("counterValue")),
                            (cb, tr, grad) -> String.valueOf(grad.get("counterTurnInfluence")),
                            (cb, tr, grad) -> String.valueOf(grad.get("currentCounterWeighting")),
                            (cb, tr, grad) -> String.valueOf(bestGuessElo),
                    });
            writer.ready();
            while (t > tMin) {
                System.out.print("Loop " + ref.x + ", current T " + t + " current energy " + bestGuessElo);
                if (ref.x == 5000) {
                    break;
                }
                Map<String, Double> candidate = getNeighbourValue(searchSpace, bestConfig, stepSizes, t);
                Map<String, Double> candidateB = getNeighbourValue(searchSpace, bestConfig, stepSizes, t);
                Map<String, Double> candidateC = getNeighbourValue(searchSpace, bestConfig, stepSizes, t);
                TestResults testResults = new TestResults(candidate);
                TestResults testResultsB = new TestResults(candidate);
                TestResults testResultsC = new TestResults(candidate);
                TestResults baseLine = new TestResults(Map.of());
                for (int j = 0; j < 750; j++) {
                    final EvalFunctionPredictionPlayer player = new EvalFunctionPredictionPlayer(
                            builder.build(builder.parametersFrom(candidate)),
                            "candidate");
                    final EvalFunctionPredictionPlayer playerB = new EvalFunctionPredictionPlayer(
                            builder.build(builder.parametersFrom(candidateB)),
                            "candidateB");
                    final EvalFunctionPredictionPlayer playerC = new EvalFunctionPredictionPlayer(
                            builder.build(builder.parametersFrom(candidateC)),
                            "candidateC");
                    final AbstractNoThanksPlayer baselinePlayer = getBaselinePlayer();
                    final List<AbstractNoThanksPlayer> abstractNoThanksPlayers = Arrays.asList(player, playerB, playerC, baselinePlayer);
                    Collections.shuffle(abstractNoThanksPlayers);
                    NoThanksGame game = new NoThanksGame(
                            abstractNoThanksPlayers.toArray(abstractNoThanksPlayers.toArray(new AbstractNoThanksPlayer[0]))
                    );
                    game.play();
                    testResults.addResults(game, player);
                    testResultsB.addResults(game, playerB);
                    testResultsC.addResults(game, playerC);
                    baseLine.addResults(game, baselinePlayer);
                }
                double testWinPercentage = testResults.getWinPercentage() - (testResults.getDrawPercentage() / 2);
                double testWinPercentageB = testResultsB.getWinPercentage() - (testResultsB.getDrawPercentage() / 2);
                double testWinPercentageC = testResultsC.getWinPercentage() - (testResultsC.getDrawPercentage() / 2);
                estimateElo = 1000;
                double estimateEloB = 1000;
                double estimateEloC = 1000;
                for (int j = 0; j < 75; j++) {
                    estimateElo += (32 * (testWinPercentage - eloPrediction(estimateElo, 1000, estimateEloB, estimateEloC)));
                    estimateEloB += (32 * (testWinPercentageB - eloPrediction(estimateEloB, 1000, estimateElo, estimateEloC)));
                    estimateEloC += (32 * (testWinPercentageC - eloPrediction(estimateEloC, 1000, estimateEloB, estimateElo)));
                }
                System.out.println(" :  [" + estimateElo + " : " + testWinPercentage + "] [" + estimateEloB + " : " + testWinPercentageB + "] [" + estimateEloC + " : " + testWinPercentageC + "]    [ " + 1000 + " " + (baseLine.getWinPercentage() - (baseLine.getDrawPercentage() / 2)) + " ]");
//                TestResults bestResults = new TestResults(bestConfig);
//                for (int j = 0; j < 500; j++) {
//                    final EvalFunctionPredictionPlayer player = new EvalFunctionPredictionPlayer(builder.build(builder.parametersFrom(candidate)), "candidate");
//                    final AbstractNoThanksPlayer bestGuess = new EvalFunctionPredictionPlayer(builder.build(builder.parametersFrom(bestConfig)), "bestPosition");
//                    final AbstractNoThanksPlayer[] abstractNoThanksPlayers = {player, bestGuess};
//                    NoThanksGame game = new NoThanksGame(
//                            abstractNoThanksPlayers
//                    );
//                    game.play();
//                    testResults.addResults(game, player);
//                    bestResults.addResults(game, bestGuess);
//                }
//                double bestWinPercentage = bestResults.getWinPercentage() - (bestResults.getDrawPercentage()/2);
//                for (int j = 0; j < 50; j++) {
////                    bestGuessElo += (32 * (bestWinPercentage - eloPrediction(bestEvaluation, estimateElo))) ;
//                    estimateElo += (16 * ((1-bestWinPercentage) - eloPrediction(estimateElo, bestGuessElo))) ;
//                }
//                System.out.print(testResults.toShortString() + " : " + baseLine.toShortString() + " : "
//                        +bestResults.toShortString()
//                );

//                double energyCandidate = baseLine.getWinPercentage() - (baseLine.getDrawPercentage() / 2);
                final Utility.Tuple<Integer, Double> bestCandidate = Utility.max(estimateElo, estimateEloB, estimateEloC);
                double energyDelta = bestCandidate.b() - bestGuessElo;

//                System.out.println(energyCandidate + " " + energyDelta + " " + t + "  " + (energyCandidate < t));
                if (energyDelta >= 0) {

                    System.out.println("Improved candidate " + candidate);
                    bestConfig = new Map[]{candidate, candidateB, candidateC}[bestCandidate.a()];
                    bestGuessElo = bestCandidate.b();
                    ref.x = 0;
//                    t /= decayRate;
                }
//                else {
//                    t /= 1+(decayRate/1000);
//                }
                writer.writeNext(candidate, testResults, bestConfig);
                writer.writeNext(candidateB, testResultsB, bestConfig);
                writer.writeNext(candidateC, testResultsC, bestConfig);
                ref.x++;
            }
        } catch (IOException ignored) {
            throw new RuntimeException();
        }

        System.out.println();

        System.out.println(bestConfig.toString().replace("=", ","));
    }

    private static Map<String, Double> getNeighbourValue(SearchSpace searchSpace, Map<String, Double> bestConfig, Map<String, Double> stepSizes, Double t) {
//        return searchSpace.getNeighbourValue(bestConfig);
        HashMap<String, Double> ret = new HashMap<>(Map.copyOf(bestConfig));
        stepSizes.forEach((axis, step) ->
                ret.computeIfPresent(
                        axis,
                        (string, value) -> value + step * (RandomUtils.nextDouble(0, 2) - 1))
        );
        return ret;
    }


    public static double eloPrediction(double p0Rating, double... otherPlayerRatings) {
        double meanOtherRating = DoubleStream.of(otherPlayerRatings).average().getAsDouble();
        double numberOfOthers = otherPlayerRatings.length;
        return (1.0 / (1 + Math.pow(10, (meanOtherRating - p0Rating) / 400))) * (2 / (numberOfOthers + 1));
    }

    private static AbstractNoThanksPlayer getBaselinePlayer() {
        return new PredictPickupPlayerBugFixed(Map.of("COUNTER_TURN_WEIGHTING", -25.090515607909694, "FUTURE_CARD_WEIGHTING", 32.95805641335263, "PICKUP_THRESHOLD", -67.10482550588529, "COUNTER_VALUE", -18.73812737346414, "CARD_WEIGHT", 37.30473516005936), "baseline");
    }

    private static SearchSpace getLinearEvaluationSearchSpace() {
//        public double pickupThreshold;// Reluctance to pickup, used in pickup but not estimateValue
//        public double futureCardWeight; // Value of average future draw used to evaluate pickup but not pickup value
//        public double cardWeight;  // Value of score change from picking up the card
//        public double counterValue; // Value of counters gained by pick up
//        public double counterTurnInfluence; // Value used to modify value of counters based on number of turns remaining
//        public double currentCounterWeighting; // Value used to modify the current counters a player has
        return SearchSpace.builder().variable("pickupThreshold").from(-10).increment(0.1).to(10)
                .nextVariable("futureCardWeight").from(-1.1).increment(0.005).to(1.1)
                .nextVariable("cardWeight").from(-1.1).increment(0.005).to(0.5)// Changed based on EvalFunction_1_1
                .nextVariable("counterValue").from(-1.1).increment(0.005).to(1.1)
                .nextVariable("counterTurnInfluence").from(-1.1).increment(0.005).to(1.1)
                .nextVariable("currentCounterWeighting").from(-1.1).increment(0.005).to(1.1)

                .complete();
    }
}
