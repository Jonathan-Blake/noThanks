package no.thanks.game;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractPlayerTest {
    @Test
    void playsGameWithoutError() {
        List<Integer> scores = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            final AbstractNoThanksPlayer[] abstractNoThanksPlayers = {getNewPlayerForTest(), getNewPlayerForTest(), getNewPlayerForTest(), getNewPlayerForTest()};
            NoThanksGame game = new NoThanksGame(abstractNoThanksPlayers);
            game.log(shouldLog());
            game.play();
            final Map<String, Integer> gameScore = game.getScore();
            game.cards().forEach((playerId, cardList) -> System.out.println(
                    playerId + ": "
                            + cardList + " [ "
                            + (gameScore.get(playerId) - NoThanksGame.scoreCards(cardList)) +
                            " ] " + " => " + gameScore.get(playerId)));
            scores.addAll(gameScore.values());
        }
        System.out.println(scores.stream().mapToInt(i -> i).summaryStatistics());
    }

    protected abstract boolean shouldLog();

    protected abstract AbstractNoThanksPlayer getNewPlayerForTest();
}
