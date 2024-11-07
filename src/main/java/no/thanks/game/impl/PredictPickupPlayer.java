package no.thanks.game.impl;

import no.thanks.game.*;
import no.thanks.game.eval.DecisionInformation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class PredictPickupPlayer extends AbstractNoThanksPlayer {
    public static final String COUNTER_VALUE = "COUNTER_VALUE";
    public static final String TURN_WEIGHTING = "TURN_WEIGHTING";
    public static final String CARD_WEIGHT = "CARD_WEIGHT";
    public static final String PICKUP_THRESHOLD = "PICKUP_THRESHOLD";
    private static final String COUNTER_TURN_WEIGHTING = "COUNTER_TURN_WEIGHTING";
    private static final String FUTURE_CARD_WEIGHTING = "FUTURE_CARD_WEIGHTING";
    private static final Logger LOGGER = Logger.getLogger(PredictPickupPlayer.class.getName());
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
    private Map<String, DecisionInformation> predictions = new HashMap<>();


//    public static Map<Boolean, Integer> predictionResults = new HashMap<>();
//    public static int totalPredictionResults;

    public PredictPickupPlayer(String name) {
//        this(Map.of(PICKUP_THRESHOLD,-23.599999999999795, TURN_WEIGHTING,-3.0, CARD_WEIGHT,8.5, COUNTER_VALUE,-12.5),name);
//        this(Map.of(PICKUP_THRESHOLD, -23.599999999999795,COUNTER_TURN_WEIGHTING, -3.0,CARD_WEIGHT, 8.5,FUTURE_CARD_WEIGHTING, 6.0,COUNTER_VALUE, -12.5), name);
//        PICKUP_THRESHOLD=19.0, COUNTER_TURN_WEIGHTING=-9.0, CARD_WEIGHT=48.0, FUTURE_CARD_WEIGHTING=-1.0, COUNTER_VALUE=-26.0
//        this(Map.of(
//                COUNTER_TURN_WEIGHTING,-3.535121876,
//                FUTURE_CARD_WEIGHTING, 0.63415,
//                PICKUP_THRESHOLD,-19.8386821,
//                COUNTER_VALUE,-16.341,
//                CARD_WEIGHT,7.965069029304528)
//                ,name);
        //Started playing around with swiss test
//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,-9.0,
//                        FUTURE_CARD_WEIGHTING, -1.0,
//                        PICKUP_THRESHOLD,19.0,
//                        COUNTER_VALUE,-26.0,
//                        CARD_WEIGHT,48.0)
//                ,name);
//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,-8.0,
//                        FUTURE_CARD_WEIGHTING, 1.0,
//                        PICKUP_THRESHOLD,25.0,
//                        COUNTER_VALUE,-35.0,
//                        CARD_WEIGHT,52.0)
//                ,name);


        this(Map.of(
                        COUNTER_TURN_WEIGHTING, -12.0,
                        FUTURE_CARD_WEIGHTING, -2.0,
                        PICKUP_THRESHOLD, -30.0,
                        COUNTER_VALUE, -8.0,
                        CARD_WEIGHT, 26.0)
                , name);// 0.11724545454545454


//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,-25.8,
//                        FUTURE_CARD_WEIGHTING, -0.9000000000000004,
//                        PICKUP_THRESHOLD,23.5,
//                        COUNTER_VALUE,-10.799999999999997,
//                        CARD_WEIGHT,52.800000000000004)
//                ,name);
//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,-6.899999999999999,
//                        FUTURE_CARD_WEIGHTING, -0.09999999999999964,
//                        PICKUP_THRESHOLD,0.7000000000000028,
//                        COUNTER_VALUE,-31.0,
//                        CARD_WEIGHT,42.900000000000006)
//                ,name);
//        0.04138484848484848 this(Map.of(
//                        COUNTER_TURN_WEIGHTING,-18.6,
//                        FUTURE_CARD_WEIGHTING, -1.4000000000000004,
//                        PICKUP_THRESHOLD,15.900000000000006,
//                        COUNTER_VALUE,-15.799999999999997,
//                        CARD_WEIGHT,51.6)
//                ,name);
// this(Map.of(
//                 COUNTER_TURN_WEIGHTING,-11.599999999999998,
//                 FUTURE_CARD_WEIGHTING, 3.9000000000000004,
//                 PICKUP_THRESHOLD,-8.2,
//                 COUNTER_VALUE,-24.7,
//                 CARD_WEIGHT,47.300000000000004)
//         ,name); //0.08672121212121212
//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,-21.299999999999997,
//                        FUTURE_CARD_WEIGHTING, 0.0,
//                        PICKUP_THRESHOLD,8.200000000000003,
//                        COUNTER_VALUE,-17.799999999999997,
//                        CARD_WEIGHT,58.400000000000006)
//                ,name); //0.04316060606060606
//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,-25.4,
//                        FUTURE_CARD_WEIGHTING, -2.6999999999999993,
//                        PICKUP_THRESHOLD,-0.6999999999999993,
//                        COUNTER_VALUE,-10.799999999999997,
//                        CARD_WEIGHT,49.800000000000004)
//                ,name);//0.05199393939393939
//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,-9.299999999999997,
//                        FUTURE_CARD_WEIGHTING, -0.3999999999999986,
//                        PICKUP_THRESHOLD,4.800000000000004,
//                        COUNTER_VALUE,-27.5,
//                        CARD_WEIGHT,43.300000000000004)
//                ,name);0.05146969696969697
//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,-5.0,
//                        FUTURE_CARD_WEIGHTING, -3.3999999999999995,
//                        PICKUP_THRESHOLD,-27.0,
//                        COUNTER_VALUE,-50.0,
//                        CARD_WEIGHT,52.6)
//                ,name);//0.05597878787878788
//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,-6.699999999999999,
//                        FUTURE_CARD_WEIGHTING, -0.09999999999999964,
//                        PICKUP_THRESHOLD,-14.299999999999999,
//                        COUNTER_VALUE,-30.2,
//                        CARD_WEIGHT,34.2)
//                ,name);//0.05849876543209877
//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,-7.599999999999998,
//                        FUTURE_CARD_WEIGHTING,0.8000000000000007,
//                        PICKUP_THRESHOLD,-23.4,
//                        COUNTER_VALUE,-32.0,
//                        CARD_WEIGHT,37.9)
//                ,name);

//        this(Map.of(
//                        COUNTER_TURN_WEIGHTING,,
//                        FUTURE_CARD_WEIGHTING,,
//                        PICKUP_THRESHOLD,,
//                        COUNTER_VALUE,,
//                        CARD_WEIGHT,)
//                ,name);
    }

    public PredictPickupPlayer(Map<String, Double> config, String name) {
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
            final DecisionInformation decisionInformation = getPrediction(card, counters + i, cards.get(playerId), playersCurrentCounters);
            predictions.put(playerId, decisionInformation);
        }
//        return this.predictions.get(this.getId()).willPickup;
        if (predictions.entrySet().stream()
                .filter(kv -> !kv.getKey().equals(getId()))
                .anyMatch(kv -> willPickup(kv.getValue()))) {
            return willPickup(this.predictions.get(this.getId()));
        } else {
            //If nobody is going to pickup, do not pickup either.
            return false;
        }

    }

    private DecisionInformation getPrediction(Integer card, int countersAvailable, List<Integer> cards, int playersCurrentCounters) {
        int currentScore = NoThanksGame.scoreCards(cards);
        int valueForPlayerThisRound = NoThanksGame.fastScoreNewCard(cards, card);
        double averageValueOfNextDraw = this.possibleDeckCards.stream()
                .mapToInt(next -> NoThanksGame.fastScoreNewCard(cards, next))
                .average()
                .getAsDouble();
        return new DecisionInformation(playersCurrentCounters,
                currentScore,
                valueForPlayerThisRound,
                countersAvailable,
                averageValueOfNextDraw, cardsRemaining);
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
                            final DecisionInformation decisionInformation = getPrediction(drawEvent.nextCard, counters, cards.get(playerId), playersCurrentCounters);
                            predictions.put(playerId, decisionInformation);
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


    private boolean willPickup(DecisionInformation decisionInformation) {
        final Integer currentCounters = decisionInformation.playersCurrentCounters;
        double valueOfCounters = config.get("COUNTER_VALUE") * (Math.sqrt(currentCounters + decisionInformation.countersAvailable) - Math.sqrt(currentCounters))
                * (config.get("COUNTER_TURN_WEIGHTING") * Math.sqrt(cardsRemaining));
        double valueOfPickup = config.get("CARD_WEIGHT") * decisionInformation.scoreDelta;

//            System.out.println(" Evalutated to " + defaultConfig.get("PICKUP_THRESHOLD") +"<"+ (valueOfCounters - valueOfPickup));
        return config.get("PICKUP_THRESHOLD") + (decisionInformation.averageValueOfNextDraw * config.get("FUTURE_CARD_WEIGHTING"))
                < valueOfCounters - valueOfPickup;
    }

}
