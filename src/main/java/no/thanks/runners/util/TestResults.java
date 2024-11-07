package no.thanks.runners.util;

import no.thanks.game.AbstractNoThanksPlayer;
import no.thanks.game.NoThanksGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TestResults {
    public final Map<String, Double> config;
    private List<GameResult> results;
    private Double winPercentage;
    private Double mean;
    private Double draw;

    public TestResults(Map<String, Double> variableConfig) {
        this.config = variableConfig;
        this.results = new ArrayList<>();
    }

    public TestResults(TestResults testResults) {
        this.config = testResults.config;
        this.results = new ArrayList<>(testResults.results);
    }

    public void addResults(NoThanksGame game, AbstractNoThanksPlayer testedPlayer) {
        this.results.add(new GameResult(game, testedPlayer));
        this.mean = null;
        this.winPercentage = null;
        this.draw = null;
    }

    @Override
    public String toString() {
        return "TestResults{" +
                "config=" + config.toString().replace("=", ",") +
                ", results={ totalGames= " + results.size() + ", averageScore= " + getMeanScore() + ", winPercentage= " + getWinPercentage() + " } }";
    }

    public String toShortString() {
        return "TestResults{" +
                "results={ totalGames= " + results.size() + ", averageScore= " + getMeanScore() + ", winPercentage= " + getWinPercentage() + " } }";
    }

    public TestResults merge(TestResults other) {
        assert this.config.equals(other.config);
//        System.out.println("Merging "+this+" "+other);
        results.addAll(other.results);
        this.mean = null;
        this.winPercentage = null;
        this.draw = null;
        return this;
    }

    private void calculateMeanAndWin() {
        AtomicReference<Double> tempMeanScore = new AtomicReference<>(0.0);
        AtomicReference<Double> tempWinPercentage = new AtomicReference<>(0.0);
        AtomicReference<Double> tempDrawPercentage = new AtomicReference<>(0.0);
        results.forEach(gameResult -> {
            tempMeanScore.updateAndGet(v -> v + gameResult.score);
            if (gameResult.win) {
                tempWinPercentage.getAndUpdate((x) -> x + 1);
            }
            if (gameResult.draw) {
                tempDrawPercentage.getAndUpdate((x) -> x + 1);
            }
        });
        mean = tempMeanScore.get() / results.size();
        draw = tempDrawPercentage.get() / results.size();
        winPercentage = tempWinPercentage.get() / results.size();
    }

    public double getWinPercentage() {
        if (this.winPercentage == null) {
            calculateMeanAndWin();
        }
        return winPercentage;
    }

    public double getDrawPercentage() {
        if (this.draw == null) {
            calculateMeanAndWin();
        }
        return draw;
    }

    public Double getMeanScore() {
        if (this.mean == null) {
            calculateMeanAndWin();
        }
        return mean;
    }
}
