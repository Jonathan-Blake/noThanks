package no.thanks.game.impl;

import no.thanks.game.AbstractNoThanksPlayer;
import no.thanks.game.NoThanksEvent;
import no.thanks.game.NoThanksEventType;

public class PickEvent implements NoThanksEvent {
    public final boolean cardRejected;
    public final String currentPlayer;

    public PickEvent(boolean cardRejected, AbstractNoThanksPlayer currentPlayer) {
        this.cardRejected = cardRejected;
        this.currentPlayer = currentPlayer.getId();
    }

    @Override
    public NoThanksEventType getType() {
        return NoThanksEventType.PICK;
    }
}
