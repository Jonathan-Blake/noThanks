package no.thanks.game;

import no.thanks.game.impl.PickEvent;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NoThanksGame {
    //    private static final Logger LOGGER = LoggerFactory.getLogger(NoThanksGame.class);
//    Logger LOGGER = Logger.getLogger( NoThanksGame.class.getName() );
    private final AbstractNoThanksPlayer[] players;
    private final Queue<Integer> deck;
    //    private final int[] counterArray;
    private final List<List<Integer>> playerCards;
    private final List<Integer> playerCounters;
    private AbstractNoThanksPlayer currentPlayer;
    private int playerIndex;
    private Double[] secrets;
    private boolean log = false;


    public NoThanksGame(AbstractNoThanksPlayer... players) {
        this.players = players;
        playerCards = new ArrayList<>(this.players.length);
        playerCounters = new ArrayList<>(this.players.length);
        secrets = new Double[this.players.length];
        Integer initialCounters = switch (this.players.length) {
            case 1, 2, 3:
                yield 11;
            case 4:
                yield 9;
            default:
                yield 7;
        };
        for (int i = 0; i < players.length; i++) {
            secrets[i] = new Random().nextDouble();
            playerCards.add(new LinkedList<>());
            playerCounters.add(initialCounters);
        }

        for (int i = 0; i < players.length; i++) {
            players[i].bindToGame(this, secrets[i]);
        }
        playerIndex = 0;
        currentPlayer = players[0];

        List<Integer> tempDeck = new ArrayList<>(IntStream.range(3, 36).boxed().toList());
        Collections.shuffle(tempDeck);
        deck = new LinkedList<>(tempDeck.stream().skip(9).toList());
    }

    public static Integer scoreCards(List<Integer> cards) {
        Collections.sort(cards);
        int score;
        if (cards.isEmpty()) {
            score = 0;
        } else {
            score = cards.get(0);
            for (int i = 1; i < cards.size(); i++) {
                if (cards.get(i - 1) != cards.get(i) - 1) {
                    score += cards.get(i);
                }
            }
        }
        return score;
    }

    public static int fastScoreNewCard(List<Integer> cards, Integer card) {
        if (cards.contains(card + 1)) {
            return -1;
        } else if (cards.contains(card - 1)) {
            return 0;
        } else {
            return card;
        }
    }

    public void log(boolean shouldLog) {
        this.log = shouldLog;
    }

    public void play() {
        while (!deck.isEmpty()) {
            Integer currentCard = deck.poll();
            fireEvent(new DrawEvent(currentCard));
            if (log) {
                System.out.println(currentPlayer.getId() + " drew a " + currentCard + ". " + playerCounters);
//                LOGGER.log( Level.INFO, currentPlayer.getId()+" drew a "+currentCard);
            }
            boolean cardRejected = true;
            final CardOffer cardOffer = new CardOffer(currentCard, 0);
            while (cardRejected) {
                cardRejected = offerCard(cardOffer);
            }
            playerCards.get(playerIndex).add(currentCard);
            playerCounters.set(playerIndex, playerCounters.get(playerIndex) + cardOffer.getCounters());
        }
    }

    boolean offerCard(CardOffer cardOffer) {
        boolean cardAccepted = currentPlayer.offer(cardOffer.getCurrentCard(), cardOffer.getCounters());
        boolean canAfford = playerCounters.get(playerIndex) > 0;
        boolean cardRejected = !cardAccepted && canAfford;
        fireEvent(new PickEvent(cardRejected, currentPlayer));
        if (log) {
            System.out.println((cardRejected ? currentPlayer.getId() + ": No Thanks " : currentPlayer.getId() + ": Yes Please") + " to offer " + cardOffer);
        }
        if (cardRejected) {
            cardOffer.setCounters(cardOffer.getCounters() + 1);
            playerCounters.set(playerIndex, playerCounters.get(playerIndex) - 1);
            if (players.length != 1) {//TODO: only accept more than one player
//                playerIndex++;
                playerIndex = ++playerIndex % (players.length);
                currentPlayer = players[playerIndex];
/*                else {
                    if(!cardAccepted){
                        System.out.println("Tried to reject however you are poor.");
                    }
                }*/
            }
        }
        if (playerCounters.stream().anyMatch(c -> c < 0)) {
            throw new RuntimeException("Negative Counters somehow;");
        }
        return cardRejected;
    }

    private void fireEvent(NoThanksEvent dealEvent) {
        for (AbstractNoThanksPlayer player : players) {
            Consumer<NoThanksEvent> binding = player.getEventBindings().get(dealEvent.getType());
            if (binding != null) {
                binding.accept(dealEvent);
            }
        }
    }

    public Map<String, Integer> getScore() {
        return IntStream.range(0, players.length).boxed().collect(Collectors.toMap(
                (noThanksPlayerIndex -> players[noThanksPlayerIndex].getId()),
                (this::scorePlayer),
                ((o, n) -> o)
        ));
    }

    private Integer scorePlayer(int noThanksPlayer) {
        return scoreCards(playerCards.get(noThanksPlayer)) - playerCounters.get(noThanksPlayer);
    }

    public Map<String, List<Integer>> cards() {
        return Collections.unmodifiableMap(IntStream.range(0, players.length).boxed().collect(Collectors.toMap(
                (noThanksPlayerIndex -> players[noThanksPlayerIndex].getId()),
                (this.playerCards::get),
                ((o, n) -> o)
        )));
    }

    public int getCounters(AbstractNoThanksPlayer abstractNoThanksPlayer, Double secret) {
        int index = Arrays.asList(players).indexOf(abstractNoThanksPlayer);
        if (index != -1 && secrets[index].equals(secret)) {
            return playerCounters.get(index);
        }
        return -1;
    }

    public List<Integer> myCards(AbstractNoThanksPlayer playYourBoardPlayer) {
        return new LinkedList<>(cards().get(playYourBoardPlayer.getId()));
    }

    public List<String> getPlayers() {
        List<String> list = new ArrayList<>(players.length);
        for (int i = playerIndex; i < players.length; i++) {
            AbstractNoThanksPlayer player = players[i];
            String id = player.getId();
            list.add(id);
        }
        for (int i = 0; i < playerIndex; i++) {
            AbstractNoThanksPlayer player = players[i];
            String id = player.getId();
            list.add(id);
        }
        return list;
    }

//    public String getWinner() {
//        return getScore().entrySet().stream()
//                .sorted(Comparator.comparingInt(kv -> (kv).getValue()))
//                .findFirst()
//                .map(kv -> kv.getKey())
//                .get();
//    }

    static class CardOffer {
        private final Integer currentCard;
        private int counters;

        CardOffer(Integer currentCard, int counters) {
            this.currentCard = currentCard;
            this.counters = counters;
        }

        public Integer getCurrentCard() {
            return currentCard;
        }

        public int getCounters() {
            return counters;
        }

        public void setCounters(int counters) {
            this.counters = counters;
        }

        @Override
        public String toString() {
            return "CardOffer{currentCard= %d, counters= %d}".formatted(currentCard, counters);
        }
    }
}
