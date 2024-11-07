package no.thanks.game.eval;

import java.util.Map;

public class LogFncEvaluationFunctionBuilder {
    public EvaluationFunction build(FunctionParameters parameters) {
        return new EvaluationFunction() {
            private final double pickupThreshold = parameters.pickupThreshold;
            private final double futureCardWeighting = Math.log(parameters.futureCardWeight);
            private final double cardScoreValue = Math.log(parameters.cardWeight);
            private final double counterScoreValue = Math.log(parameters.counterValue);
            private final double currentCounterValueWeight = Math.log(parameters.currentCounterWeighting);
            ;
            private final double counterTurnWeighting = Math.log(parameters.counterTurnInfluence);

            @Override
            public double estimateValue(DecisionInformation decisionInformation) {
                double valueOfCounters = (counterScoreValue * decisionInformation.countersAvailable) +
                        (counterTurnWeighting * Math.sqrt(decisionInformation.cardsRemaining)) -
                        (currentCounterValueWeight * decisionInformation.playersCurrentCounters);
                double valueOfPickup = cardScoreValue * decisionInformation.scoreDelta;

                return valueOfCounters - valueOfPickup;
            }

            @Override
            public boolean shouldPickup(DecisionInformation decisionInformation) {
                return pickupThreshold + (decisionInformation.averageValueOfNextDraw * futureCardWeighting)
                        < estimateValue(decisionInformation);
            }
        };
    }

    public FunctionParameters parametersFrom(Map<String, Double> candidate) {
        FunctionParameters ret = new FunctionParameters();
        ret.pickupThreshold = candidate.get("pickupThreshold");
        ret.futureCardWeight = candidate.get("futureCardWeight");
        ret.cardWeight = candidate.get("cardWeight");
        ret.counterValue = candidate.get("counterValue");
        ret.counterTurnInfluence = candidate.get("counterTurnInfluence");
        ret.currentCounterWeighting = candidate.get("currentCounterWeighting");
        return ret;
    }

    private class FunctionParameters {
        public double pickupThreshold;// Reluctance to pickup, used in pickup but not estimateValue
        public double futureCardWeight; // Value of average future draw used to evaluate pickup but not pickup value
        public double cardWeight;  // Value of score change from picking up the card
        public double counterValue; // Value of counters gained by pick up
        public double counterTurnInfluence; // Value used to modify value of counters based on number of turns remaining
        public double currentCounterWeighting; // Value used to modify the current counters a player has
    }
}
