/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bj.sim.nodes;

import bj.sim.hands.*;
import bj.sim.decks.*;

/**
 *
 * @author Vasil
 */
public class ValueNode extends Node {
    
    public double value;
    
    ValueNode(PlayerHand hand, DealerHand dealer, Deck deck) {
        super(hand, dealer, deck);
        if(hand.isFinal() && dealer.isFinal()) {
            this.value = hand.compare(dealer);
        } else {
            throw new RuntimeException();
        }
    }
}
