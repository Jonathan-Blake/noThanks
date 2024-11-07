package no.thanks.game.eval;

import no.thanks.game.NoThanksGame;

import java.util.List;

public class DecisionInformation {
    public final int playersCurrentCounters;
    public final int valueForPlayerThisRound;
    public final int countersAvailable;
    public final double averageValueOfNextDraw;
    public final int scoreDelta;
    public final double cardsRemaining;

    public DecisionInformation(int playersCurrentCounters, int currentPlayerScore, int valueForPlayerThisRound, int countersAvailable, double averageValueOfNextDraw, double cardsRemaining) {
        this.playersCurrentCounters = playersCurrentCounters;
        this.valueForPlayerThisRound = valueForPlayerThisRound;
        this.countersAvailable = countersAvailable;
        this.averageValueOfNextDraw = averageValueOfNextDraw;
        this.cardsRemaining = cardsRemaining;
        scoreDelta = valueForPlayerThisRound - currentPlayerScore;
    }

    public static DecisionInformation gatherInfo(Integer card, int countersAvailable, List<Integer> cards, int playersCurrentCounters, List<Integer> possibleRemainingCards) {
        int currentScore = NoThanksGame.scoreCards(cards);
        int valueForPlayerThisRound = NoThanksGame.fastScoreNewCard(cards, card);
        final DecisionInformation decisionInformation = new DecisionInformation(playersCurrentCounters,
                currentScore,
                valueForPlayerThisRound,
                countersAvailable,
                averageScoreOfRemainingDraws(possibleRemainingCards, cards),
                possibleRemainingCards.size() - 9);
        return decisionInformation;
    }

    public static double averageScoreOfRemainingDraws(List<Integer> possibleDeckCards, List<Integer> currentCards) {
        return possibleDeckCards.stream()
                .mapToInt(next -> NoThanksGame.fastScoreNewCard(currentCards, next))
                .average()
                .getAsDouble();
    }
}
