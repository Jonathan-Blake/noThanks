package no.thanks.game;

import static no.thanks.game.NoThanksEventType.DRAW;

public class DrawEvent implements NoThanksEvent {
    public final Integer nextCard;

    public DrawEvent(Integer nextCard) {
        this.nextCard = nextCard;
    }

    @Override
    public NoThanksEventType getType() {
        return DRAW;
    }
}
