package no.thanks.game.impl;

import no.thanks.game.AbstractNoThanksPlayer;
import no.thanks.game.AbstractPlayerTest;
import org.junit.jupiter.api.AfterEach;

class PredictPickupPlayerTest extends AbstractPlayerTest {

    private int playerNumber;

    @AfterEach
    void printAndClearResults() {
//        System.out.println(PredictPickupPlayer.predictionResults);
//        System.out.println(PredictPickupPlayer.totalPredictionResults);
//        PredictPickupPlayer.predictionResults = new HashMap<>();
//        PredictPickupPlayer.totalPredictionResults = 0;
    }

    @Override
    protected boolean shouldLog() {
        return false;
    }

    @Override
    protected AbstractNoThanksPlayer getNewPlayerForTest() {
        return new PredictPickupPlayer("Prediction Player no." + playerNumber++);
    }
}