package no.thanks.game.impl;

import no.thanks.game.AbstractNoThanksPlayer;

import java.util.Random;

public class RandomActionPlayer extends AbstractNoThanksPlayer {
    private static final Random RAND = new Random();
    private static int nextId = 0;
    private final int id;

    public RandomActionPlayer() {
        id = nextId++;
    }

    @Override
    public boolean offer(Integer capture, int i) {
        final int counters = getCounters();
        if (counters == 0) {
            return true;
        }
        return RAND.nextDouble() > 1.0 / counters;
    }

    @Override
    public String getId() {
        return "RandomActor " + id;
    }
}
