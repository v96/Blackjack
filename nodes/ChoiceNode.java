/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bj.sim.nodes;

import bj.sim.Action;
import bj.sim.hands.*;
import bj.sim.decks.*;

/**
 *
 * @author Vasil
 */
public class ChoiceNode extends Node {
    
    @Override
    public Action[] getChoices() {
        return hand.availableActions();
    }

    @Override
    public Node makeChoice(Action action) {
        switch(action) {
            case HIT:
                return new ChanceNode(hand, dealer, deck, Action.HIT);
            case DOUBLEDOWN:
                return new ChanceNode(hand, dealer, deck, Action.DOUBLEDOWN);
            case STAND:
                return generateNode(hand.stand(), dealer, deck);
            case SPLIT:
                return new ChanceNode(hand.split(), dealer, deck, Action.HIT);
            case SURRENDER:
                return generateNode(hand.surrender(), dealer, deck);
            default:
                throw new IllegalArgumentException();
        }
    }
    
    public ChoiceNode(PlayerHand hand, DealerHand dealer, Deck deck) {
        super(hand, dealer, deck);
    }
}
