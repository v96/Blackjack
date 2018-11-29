/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bj.sim.decks;

/**
 *
 * @author vasil.kuzevski
 */
public class InfiniteDeck extends Deck {

    @Override
    public double p(int card) {
        checkCard(card);
        if (card < 10) {
            return 1 / (double) 13;
        }
        return 4 / (double) 13;
    }

    @Override
    public Deck remove(int card) {
        checkCard(card);
        return this;
    }

    @Override
    public int hashCode() {
        return 3692457;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }
    
    

    public InfiniteDeck() {
    }
}
