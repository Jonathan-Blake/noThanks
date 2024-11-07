package no.thanks.game;

import java.util.*;
import java.util.stream.IntStream;

public class Utility {
    public static <T> List<T> join(List<T> myCards, T capture) {
        LinkedList<T> ret = new LinkedList<>(myCards);
        ret.add(capture);
        return ret;
    }

    public static <T> List<T> join(List<T> a, List<T> b) {
        LinkedList<T> ret = new LinkedList<>(a);
        ret.addAll(b);
        return ret;
    }

    public static AbstractNoThanksPlayer[] shuffle(List<AbstractNoThanksPlayer> playerList) {
        Collections.shuffle(playerList);
        return playerList.toArray(new AbstractNoThanksPlayer[playerList.size()]);
    }

    public static double clamp(int min, double futureCardWeight, int max) {
        return Math.min(max, Math.max(min, futureCardWeight));
    }

    public static Tuple<Integer, Double> max(double... values) {
        final Optional<Tuple<Integer, Double>> max = IntStream.range(0, values.length)
                .mapToObj(i -> new Tuple<>(i, values[i]))
                .max(Comparator.comparingDouble(tuple -> tuple.b));
        assert (max.isPresent());
        return max.get();
    }

    public record Tuple<T, T1>(T a, T1 b) {
    }
}
