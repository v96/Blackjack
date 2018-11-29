/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bj.sim.decks;

import java.util.Arrays;

/**
 *
 * @author vasil.kuzevski
 */
public class SingleDeck extends Deck {
    
    private final int[] ncards;
    private final int total;
    
    @Override
    public double p(int card) {
        checkCard(card);
        return ncards[card] / (double) total;
    }
    
    @Override
    public Deck remove(int card) {
        checkCard(card);
        if(ncards[card] == 0) {
            throw new RuntimeException("You can't remove a card which is not in the deck!");
        }
        int[] newNcards = new int[11];
        newNcards[0] = 0;
        for(int i=1; i<=10; i++)
            newNcards[i] = ncards[i];
        newNcards[card]--;
        return new SingleDeck(newNcards, total-1);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Arrays.hashCode(this.ncards);
        hash = 37 * hash + this.total;
        return hash;
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
        final SingleDeck other = (SingleDeck) obj;
        if (this.total != other.total) {
            return false;
        }
        if (!Arrays.equals(this.ncards, other.ncards)) {
            return false;
        }
        return true;
    }
    
    public SingleDeck() {
        ncards = new int[11];
        ncards[0] = 0;
        for(int i=1; i<=9; i++) {
            ncards[i] = 4;
        }
        ncards[10] = 16;
        total = 52;
    }
    
    private SingleDeck(int[] ncards, int total) {
        this.ncards = ncards;
        this.total = total;
    }
    
}
