package no.thanks.game.impl;

import no.thanks.game.AbstractNoThanksPlayer;

import java.util.Scanner;

public class CMDPlayer extends AbstractNoThanksPlayer {
    Scanner in = new Scanner(System.in);
    private String name;

    public CMDPlayer(String name) {
        this.name = name;
    }

    @Override
    public boolean offer(Integer capture, int i) {
        System.out.println("Would you like to take " + capture + " with " + i + " tokens? Or no thanks?");
        String s = in.nextLine();
        return !s.toUpperCase().startsWith("N");
    }

    @Override
    public String getId() {
        return this.name;
    }
}
