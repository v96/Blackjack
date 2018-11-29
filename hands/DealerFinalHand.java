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
abstract class DealerFinalHand extends DealerHand {
    
    @Override
    public Action[] availableActions() {
        return new Action[] {};
    }
    
    @Override
    public DealerHand applyAction(Action action, int card) {
        throw new IllegalArgumentException();
    }
    
    @Override
    public boolean isFinal() {
        return true;
    }
    
    DealerFinalHand(Rules rules) {
        super(rules);
    }
}
