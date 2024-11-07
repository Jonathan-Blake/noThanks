package no.thanks.game.impl;

import no.thanks.game.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PlayYourBoardPlayer extends AbstractNoThanksPlayer {
    private final Map<String, Double> config;

    private final Map<String, Integer> playerCounters;
    private Integer countersOnTile = 0;
    private int cardsRemaining = 24;
    private int scoreChange;
    private String name;

    public PlayYourBoardPlayer(Map<String, Double> config, String name) {
        this.config = config;
        this.name = name;
        this.playerCounters = new HashMap<>();
    }

    @Override
    public boolean offer(Integer card, int counters) {
        final Integer currentCounters = playerCounters.get(getId());
        double valueOfCounters = config.get("COUNTER_VALUE") * (Math.sqrt(currentCounters + counters) - Math.sqrt(currentCounters))
                * (config.get("TURN_WEIGHTING") * Math.sqrt(cardsRemaining));
        double valueOfPickup = config.get("CARD_WEIGHT") * scoreChange;

        return config.get("PICKUP_THRESHOLD") < valueOfCounters - valueOfPickup;
    }

    @Override
    public void bindToGame(NoThanksGame noThanksGame, Double secret) {
        super.bindToGame(noThanksGame, secret);
        final int counters = this.getCounters();
        this.getGame().getScore().keySet().forEach(abstractNoThanksPlayer -> playerCounters.put(abstractNoThanksPlayer, counters));
    }

    @Override
    public Map<NoThanksEventType, Consumer<NoThanksEvent>> getEventBindings() {
        return Map.of(
                NoThanksEventType.DRAW, (noThanksEvent -> {
                    if (noThanksEvent instanceof DrawEvent drawEvent) {
//                        System.out.println("New Card: " + drawEvent.nextCard);
                        cardsRemaining--;
                        scoreChange = NoThanksGame.scoreCards(Utility.join(getGame().myCards(this), drawEvent.nextCard)) - NoThanksGame.scoreCards(getGame().myCards(this));
                    }
                }),
                NoThanksEventType.PICK, (noThanksEvent -> {
                    if (noThanksEvent instanceof PickEvent pickEvent) {
                        if (pickEvent.cardRejected) {
                            countersOnTile++;
                            playerCounters.put(pickEvent.currentPlayer, playerCounters.get(pickEvent.currentPlayer) - 1);

                        } else {
                            playerCounters.put(pickEvent.currentPlayer, playerCounters.get(pickEvent.currentPlayer) + countersOnTile);
                            countersOnTile = 0;
                        }
                    }
                }


                ));
    }

    @Override
    public String getId() {
        return this.name;
    }
}
