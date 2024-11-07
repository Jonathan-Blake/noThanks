package no.thanks.game.eval;

public interface EvaluationFunction {
    double estimateValue(DecisionInformation decisionInformation);

    boolean shouldPickup(DecisionInformation decisionInformation);
}
