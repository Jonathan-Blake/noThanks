package no.thanks.runners;

public class NemePointRunner {
    //        https://nemestats.com/Home/AboutNemePoints
    public static void main(String[] args) {
//
//        pointsAvailable = players.length *10;
//        rewards = new double[players.length];
//        split = factiorial(players.length);
//        for (int i = players.length-1; i >= 0; i++) {
//            rewards[i] = i*(1/ split)*pointsAvailable;
//        }

    }

    public static int factiorial(int i) {
        int result = 1;
        for (int factor = 2; factor <= i; factor++) {
            result *= factor;
        }
        return result;
    }
}
