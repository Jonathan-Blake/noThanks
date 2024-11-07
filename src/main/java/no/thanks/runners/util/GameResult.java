package no.thanks.runners.util;

import no.thanks.game.AbstractNoThanksPlayer;
import no.thanks.game.NoThanksGame;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

class GameResult {
    public final Integer score;
    public final boolean win;
    public final boolean draw;

    public GameResult(NoThanksGame game, AbstractNoThanksPlayer testedPlayer) {
        final Map<String, Integer> results = game.getScore();
        this.score = results.get(testedPlayer.getId());
        AtomicBoolean tempWin = new AtomicBoolean(true);
        AtomicBoolean tempDraw = new AtomicBoolean(false);
        results.forEach((abstractNoThanksPlayer, otherScore) -> {
            if (otherScore < score) {
                tempWin.set(false);
                tempDraw.set(false);
            } else if (Objects.equals(otherScore, score) && !Objects.equals(abstractNoThanksPlayer, testedPlayer.getId())) {
                tempDraw.set(true);
            }
        });
        draw = tempDraw.get();
        win = tempWin.get();
    }
}
