package no.thanks.game.impl;

import no.thanks.game.*;
import no.thanks.game.eval.DecisionInformation;
import no.thanks.game.eval.EvaluationFunction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class EvalFunctionPredictionPlayer extends AbstractNoThanksPlayer {
    private final EvaluationFunction evaluationFunction;
    private final String id;
    private final Map<String, Integer> playerCounters;
    private final Map<String, DecisionInformation> predictions;
    private final List<Integer> possibleDeckCards;
    private int countersOnTile;

    public EvalFunctionPredictionPlayer(EvaluationFunction evaluationFunction, String id) {
        this.evaluationFunction = evaluationFunction;
        this.id = id;
        playerCounters = new HashMap<>();
        possibleDeckCards = new LinkedList<>(IntStream.range(3, 35).boxed().toList());
        predictions = new HashMap<>();
    }

    @Override
    public boolean offer(Integer card, int counters) {
        List<String> players = getGame().getPlayers(); //used to guarentee order
        final Map<String, List<Integer>> cards = getGame().cards();
        for (int i = 0; i < players.size(); i++) {
            final String playerId = players.get(i);
            updatePrediction(card, counters + i, cards.get(playerId), playerId);
        }
        if (predictions.entrySet().stream()
                .filter(kv -> !kv.getKey().equals(getId()))
                .anyMatch(kv -> this.evaluationFunction.shouldPickup(kv.getValue()))) {
            return this.evaluationFunction.shouldPickup(this.predictions.get(this.getId()));
        } else {
            //If nobody is going to pickup, do not pickup either.
            return false;
        }
    }

    private void updatePrediction(Integer card, int counters, List<Integer> cards, String playerId) {
        int playersCurrentCounters = playerCounters.get(playerId);
        final DecisionInformation decisionInformation = DecisionInformation.gatherInfo(
                card,
                counters,
                cards,
                playersCurrentCounters,
                (this.possibleDeckCards)
        );
        predictions.put(playerId, decisionInformation);
    }

    @Override
    public void bindToGame(NoThanksGame noThanksGame, Double secret) {
        super.bindToGame(noThanksGame, secret);
        final int counters = this.getCounters();
        this.getGame().getScore().keySet().forEach(playerId -> playerCounters.put(playerId, counters));
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Map<NoThanksEventType, Consumer<NoThanksEvent>> getEventBindings() {
        return Map.of(
                NoThanksEventType.DRAW, (noThanksEvent -> {
                    if (noThanksEvent instanceof DrawEvent drawEvent) {
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
                            updatePrediction(drawEvent.nextCard, counters, cards.get(playerId), playerId);
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
                    }
                })
        );
    }

}
