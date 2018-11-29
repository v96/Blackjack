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
public class PlayerBlank extends PlayerHand {

    @Override
    public Action[] availableActions() {
        return new Action[] {Action.HIT};
    }

    @Override
    public PlayerHand applyAction(Action action, int card) {
        switch (action) {
            case HIT:
                checkCard(card);
                return new Player1Card(getRules(), card);
            default:
                throw new IllegalArgumentException();
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerBlank;
    }

    @Override
    public int hashCode() {
        return 333356883;
    }

    public PlayerBlank(Rules rules) {
        super(rules);
    }
    
    public PlayerBlank() {
        this(new Rules());
    }

}
