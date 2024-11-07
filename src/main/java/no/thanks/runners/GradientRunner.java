package no.thanks.runners;

import no.thanks.game.NoThanksGame;
import no.thanks.game.impl.PlayYourBoardPlayer;
import no.thanks.runners.util.ResultsCSV;
import no.thanks.runners.util.TestResults;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradientRunner {

    public static final String COUNTER_VALUE = "COUNTER_VALUE";
    public static final String TURN_WEIGHTING = "TURN_WEIGHTING";
    public static final String CARD_WEIGHT = "CARD_WEIGHT";
    public static final String PICKUP_THRESHOLD = "PICKUP_THRESHOLD";

    public static void main(String[] args) {
        Map<Double, List<TestResults>> bestConfigs = new HashMap<>();
        Map<String, Double> baseline = Map.of(PICKUP_THRESHOLD, -23.599999999999795, TURN_WEIGHTING, -3.0, CARD_WEIGHT, 8.5, COUNTER_VALUE, -12.5);
        Map<String, Double> currentBestimate = new HashMap<>(baseline);
        Map<String, Double> stepSize = Map.of(PICKUP_THRESHOLD, 1.0, TURN_WEIGHTING, 0.25, CARD_WEIGHT, 0.25, COUNTER_VALUE, 0.25);

        List<String> axis = List.of(COUNTER_VALUE, TURN_WEIGHTING, CARD_WEIGHT, PICKUP_THRESHOLD);


        try {
            ResultsCSV writer = new ResultsCSV();
            writer.setColumns(new String[]{
                            "Mean Score",
                            "Win Percentage",
                            "Pickup Threshold",
                            "Counter Value",
                            "Turn Weighting",
                            "Card Weight",
                            "Pickup Threshold Gradient",
                            "Counter Value Gradient",
                            "Turn Weighting Gradient",
                            "Card Weight Gradient"
                    },
                    new ResultsCSV.ColumnValueFunction[]{
                            (cb, totalResults, grad) -> String.valueOf(totalResults.getMeanScore()),
                            (cb, totalResults, grad) -> String.valueOf(totalResults.getWinPercentage()),
                            (cb, tr, grad) -> String.valueOf(cb.get(PICKUP_THRESHOLD)),
                            (cb, tr, grad) -> String.valueOf(cb.get(COUNTER_VALUE)),
                            (cb, tr, grad) -> String.valueOf(cb.get(TURN_WEIGHTING)),
                            (cb, tr, grad) -> String.valueOf(cb.get(CARD_WEIGHT)),
                            (cb, tr, grad) -> String.valueOf(grad.get(PICKUP_THRESHOLD)),
                            (cb, tr, grad) -> String.valueOf(grad.get(COUNTER_VALUE)),
                            (cb, tr, grad) -> String.valueOf(grad.get(TURN_WEIGHTING)),
                            (cb, tr, grad) -> String.valueOf(grad.get(CARD_WEIGHT))
                    });
            writer.ready();

            HashMap<String, Double> gradient = new HashMap<>();
            for (int j = 0; j < 10000; j++) {
                TestResults totalResults = new TestResults(currentBestimate);
                axis.forEach(variable -> {
                    HashMap<String, Double> config = new HashMap<>(currentBestimate);
                    config.put(variable, currentBestimate.get(variable) - stepSize.get(variable));
                    TestResults low = new TestResults(config);
                    for (int i = 0; i < 200; i++) {
                        final PlayYourBoardPlayer testedPlayer = new PlayYourBoardPlayer(config, variable + "_low");
                        NoThanksGame game = new NoThanksGame(testedPlayer, new PlayYourBoardPlayer(baseline, "baseline"));
                        game.play();
                        low.addResults(game, testedPlayer);
                    }
                    TestResults high = new TestResults(config);
                    config.put(variable, currentBestimate.get(variable) + stepSize.get(variable));
                    for (int i = 0; i < 200; i++) {
                        final PlayYourBoardPlayer testedPlayer = new PlayYourBoardPlayer(config, variable + "_high");
                        NoThanksGame game = new NoThanksGame(testedPlayer, new PlayYourBoardPlayer(baseline, "baseline"));
                        game.play();
                        high.addResults(game, testedPlayer);
                    }
                    totalResults.merge(low);
                    totalResults.merge(high);
                    gradient.put(variable, (high.getWinPercentage() - low.getWinPercentage()) * 100);
                });

                axis.forEach(variable -> currentBestimate.put(
                        variable,
                        currentBestimate.get(variable) + gradient.get(variable) * stepSize.get(variable))
                );
//                System.out.println(gradient);
                System.out.println(j + " " + currentBestimate + " : " + totalResults.toShortString());
                writer.writeNext(currentBestimate, totalResults, gradient);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
