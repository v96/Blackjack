/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bj.sim.decks;

/**
 *
 * @author Vasil
 */
public abstract class Deck {

    public abstract double p(int card);

    public abstract Deck remove(int card);

    protected static void checkCard(int card) {
        if (card < 1 || card > 10) {
            throw new IllegalArgumentException("Not a card!");
        }
    }
}
