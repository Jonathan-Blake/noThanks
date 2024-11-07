package no.thanks.game;

import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractNoThanksPlayer {
    private NoThanksGame game;
    private Double secret;

    public abstract boolean offer(Integer card, int counters);

    public void bindToGame(NoThanksGame noThanksGame, Double secret) {
        this.game = noThanksGame;
        this.secret = secret;
    }

    public NoThanksGame getGame() {
        return game;
    }

    protected int getCounters() {
        return this.game.getCounters(this, secret);
    }

    public Map<NoThanksEventType, Consumer<NoThanksEvent>> getEventBindings() {
        return Map.of();
    }

    public abstract String getId();
}
