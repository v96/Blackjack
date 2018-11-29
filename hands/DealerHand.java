/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bj.sim.hands;

import bj.sim.Action;
import bj.sim.Rules;

/**
 *
 * @author vasil.kuzevski
 */
public abstract class DealerHand extends Hand {
    
    public static final DealerHand BLANK = new DealerBlank(Rules.DEFAULT_RULES);
    
    public abstract DealerHand applyAction(Action action, int card);
    
    public DealerHand applyAction(Action action) {
        if(action == Action.HIT || action == Action.DOUBLEDOWN) {
            throw new IllegalArgumentException();
        }
        return applyAction(action, 0);
    }
    
    public DealerHand hit(int card) {
        return applyAction(Action.HIT, card);
    }
    
    public DealerHand getHolecard() {
        return applyAction(Action.GET_HOLECARD);
    }
    
    @Override
    public boolean isFinal() {
        return false;
    }
    
    @Override
    public boolean isBlackjack() {
        return this instanceof DealerBlackjack;
    }
    
    public boolean hasHolecard() {
        return (this instanceof Dealer1Card) && ((Dealer1Card) this).holecard;
    }
    
    @Override
    public boolean isBlank() {
        return this instanceof DealerBlank;
    }
    
    DealerHand(Rules rules) {
        super(rules);
    }
}
