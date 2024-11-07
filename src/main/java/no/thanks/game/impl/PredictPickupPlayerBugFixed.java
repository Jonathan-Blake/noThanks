package no.thanks.game.impl;

import no.thanks.game.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class PredictPickupPlayerBugFixed extends AbstractNoThanksPlayer {
    public static final String COUNTER_VALUE = "COUNTER_VALUE";
    public static final String TURN_WEIGHTING = "TURN_WEIGHTING";
    public static final String CARD_WEIGHT = "CARD_WEIGHT";
    public static final String PICKUP_THRESHOLD = "PICKUP_THRESHOLD";
    private static final String COUNTER_TURN_WEIGHTING = "COUNTER_TURN_WEIGHTING";
    private static final String FUTURE_CARD_WEIGHTING = "FUTURE_CARD_WEIGHTING";
    private static final Logger LOGGER = Logger.getLogger(PredictPickupPlayerBugFixed.class.getName());
    private final Map<String, Double> config;
    ;
    private int cardsRemaining;
    //    private int scoreChange;
    private int countersOnTile;
    private Map<String, Integer> playerCounters = new HashMap<>();
    private String name;
    //    private Map<String, SimulatedPlayers> playerModels;
//    private Map<Integer, Integer> possibleDeckCardsAndScoreChange;
    private List<Integer> possibleDeckCards;
    private Map<String, Prediction> predictions = new HashMap<>();


//    public static Map<Boolean, Integer> predictionResults = new HashMap<>();
//    public static int totalPredictionResults;

    public PredictPickupPlayerBugFixed(String name) {
//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,-1.2893578660713454,
//                        FUTURE_CARD_WEIGHTING, 2.26405723926474,
//                        PICKUP_THRESHOLD,-23.538180904170584,
//                        COUNTER_VALUE,-15.160275389920729,
//                        CARD_WEIGHT,9.74252192426275)
//                ,name);
//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,0.4107529768408768,
//                        FUTURE_CARD_WEIGHTING, 0.3634420324169121,
//                        PICKUP_THRESHOLD,-18.42571319023225,
//                        COUNTER_VALUE,-17.081239853164693,
//                        CARD_WEIGHT,5.699762538206623)
//                ,name);
        this(Map.of(
                        COUNTER_TURN_WEIGHTING, -0.3174147535948586,
                        FUTURE_CARD_WEIGHTING, 1.0162918298353412,
                        PICKUP_THRESHOLD, -16.706082638158982,
                        COUNTER_VALUE, -17.267635029812972,
                        CARD_WEIGHT, 5.698208410678869)
                , name); // I think this config is kinda shit.
    }

    public PredictPickupPlayerBugFixed(Map<String, Double> config, String name) {
        this.name = name;
        this.config = config;
    }

    @Override
    public void bindToGame(NoThanksGame noThanksGame, Double secret) {
        super.bindToGame(noThanksGame, secret);
        final int counters = this.getCounters();
        cardsRemaining = 24;
//        this.possibleDeckCardsAndScoreChange = IntStream.range(3, 35).boxed().collect(Collectors.toMap(
//                i -> i,
//                i -> i,
//                (o,n) -> o
//        ));
        this.possibleDeckCards = new LinkedList<>(IntStream.range(3, 35).boxed().toList());
        this.getGame().getScore().keySet().forEach(playerId -> playerCounters.put(playerId, counters));
    }

    @Override
    public boolean offer(Integer card, int counters) {
        List<String> players = getGame().getPlayers(); //used to guarentee order
        final Map<String, List<Integer>> cards = getGame().cards();
        for (int i = 0; i < players.size(); i++) {
            String playerId = players.get(i);
            int playersCurrentCounters = playerCounters.get(playerId);
            final Prediction prediction = getPrediction(card, counters + i, cards.get(playerId), playersCurrentCounters);
            predictions.put(playerId, prediction);
        }
//        return this.predictions.get(this.getId()).willPickup;
        if (predictions.entrySet().stream()
                .filter(kv -> !kv.getKey().equals(getId()))
                .anyMatch(kv -> kv.getValue().willPickup)) {
            return this.predictions.get(this.getId()).willPickup;
        } else {
            //If nobody is going to pickup, do not pickup either.
            return false;
        }

    }

    private Prediction getPrediction(Integer card, int countersAvailable, List<Integer> cards, int playersCurrentCounters) {
        int currentScore = NoThanksGame.scoreCards(cards);
//        int valueForPlayerThisRound = NoThanksGame.fastScoreNewCard(cards, card);
        int valueForPlayerThisRound = NoThanksGame.scoreCards(Utility.join(cards, card)) - NoThanksGame.scoreCards(cards);
        double averageValueOfNextDraw = this.possibleDeckCards.stream()
                .mapToInt(next -> NoThanksGame.scoreCards(Utility.join(cards, next)))
                .average()
                .getAsDouble();
        final Prediction prediction = new Prediction(playersCurrentCounters,
                currentScore,
                valueForPlayerThisRound,
                countersAvailable,
                averageValueOfNextDraw);
        return prediction;
    }

    @Override
    public Map<NoThanksEventType, Consumer<NoThanksEvent>> getEventBindings() {
        return Map.of(
                NoThanksEventType.DRAW, (noThanksEvent -> {
                    if (noThanksEvent instanceof DrawEvent drawEvent) {
                        cardsRemaining--;
                        getGame().getPlayers();
                        possibleDeckCards.remove(drawEvent.nextCard);
                        List<String> players = getGame().getPlayers();
                        Map<String, List<Integer>> cards = getGame().cards();
                        int counters = 0;
                        while (counters < players.size()) {
                            String playerId = players.get(counters);
                            if (playerId.equals(this.getId())) {
                                break;
                            }
                            int playersCurrentCounters = playerCounters.get(playerId);
                            final Prediction prediction = getPrediction(drawEvent.nextCard, counters, cards.get(playerId), playersCurrentCounters);
                            predictions.put(playerId, prediction);
                            counters++;
                        }
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
//                        if(!Objects.equals(pickEvent.currentPlayer, this.getId()) && predictions.get(pickEvent.currentPlayer)!=null){
//                            totalPredictionResults ++;
//                            predictionResults.merge(
//                                    (!pickEvent.cardRejected == predictions.get(pickEvent.currentPlayer).willPickup) ? Boolean.TRUE: Boolean.FALSE,
//                                    1,
//                                    Integer::sum
//                            );
////                            System.out.println("predicted action of "+pickEvent.currentPlayer+" to be "+predictions.get(pickEvent.currentPlayer).willPickup+ " and was "+ !pickEvent.cardRejected);
////                            LOGGER.log(Level.INFO, "predicted action of "+pickEvent.currentPlayer+" to be "+predictions.get(pickEvent.currentPlayer).willPickup+ " and was "+ !pickEvent.cardRejected);
//                        }
                    }
                }


                ));
    }

    @Override
    public String getId() {
        return this.name;
    }

    public Map<String, Double> getConfig() {
        return this.config;
    }

    private class Prediction {
        private final int playersCurrentCounters;
        private final int valueForPlayerThisRound;
        private final int countersAvailable;
        private final double averageValueOfNextDraw;
        private final int scoreDelta;
        private boolean willPickup;

        public Prediction(int playersCurrentCounters, int currentPlayerScore, int valueForPlayerThisRound, int countersAvailable, double averageValueOfNextDraw) {

            this.playersCurrentCounters = playersCurrentCounters;
            this.valueForPlayerThisRound = valueForPlayerThisRound;
            this.countersAvailable = countersAvailable;
            this.averageValueOfNextDraw = averageValueOfNextDraw;
            scoreDelta = valueForPlayerThisRound - currentPlayerScore;
            willPickup = playersCurrentCounters == 0;
            if (!willPickup) {
                willPickup = isWillPickup();
            }
        }

        private boolean isWillPickup() {
            final Integer currentCounters = playersCurrentCounters;
            double valueOfCounters = config.get("COUNTER_VALUE") * (Math.sqrt(currentCounters + countersAvailable) - Math.sqrt(currentCounters))
                    * (config.get("COUNTER_TURN_WEIGHTING") * Math.sqrt(cardsRemaining));
            double valueOfPickup = config.get("CARD_WEIGHT") * scoreDelta;

//            System.out.println(" Evalutated to " + defaultConfig.get("PICKUP_THRESHOLD") +"<"+ (valueOfCounters - valueOfPickup));
            return config.get("PICKUP_THRESHOLD") + (averageValueOfNextDraw * config.get("FUTURE_CARD_WEIGHTING"))
                    < valueOfCounters - valueOfPickup;
        }
    }
}
