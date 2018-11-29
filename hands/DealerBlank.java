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
public class DealerBlank extends DealerMustHit {
    
    @Override
    public DealerHand applyAction(Action action, int card) {
        switch(action) {
            case HIT:
                checkCard(card);
                return new Dealer1Card(getRules(), card, false);
            default:
                throw new IllegalArgumentException();
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof DealerBlank;
    }

    @Override
    public int hashCode() {
        return 97642224;
    }
    
    public DealerBlank(Rules rules) {
        super(rules);
    }
    
    public DealerBlank() {
        this(new Rules());
    }
}
