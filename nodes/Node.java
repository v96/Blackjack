/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bj.sim.nodes;

import bj.sim.Action;
import bj.sim.hands.*;
import bj.sim.decks.*;
import java.util.Objects;

/**
 *
 * @author Vasil
 */
public abstract class Node {
    
    public final PlayerHand hand;
    public final DealerHand dealer;
    public final Deck deck;

    public static Node generateNode(PlayerHand hand, DealerHand dealer, Deck deck) {
        if(hand.isBlank()) {
            return new ChanceNode(hand, dealer, deck, Action.HIT);
        }
        if(dealer.isBlank()) {
            return new ChanceNode(hand, dealer, deck);
        }
        if(hand.has1Card()) {
            return new ChanceNode(hand, dealer, deck, Action.HIT);
        }
        if(dealer.has1Card() && dealer.getRules().dealerHasAHolecard) {
            return new ChanceNode(hand, dealer, deck);
        }
        if(!hand.isFinal() && (dealer.isBlackjack() || (!dealer.getRules().dealerHasAHolecard && dealer.has1Card()) ||
                                                        (dealer.getRules().dealerHasAHolecard && dealer.hasHolecard()))) {
            return new ChoiceNode(hand, dealer, deck);
        }
        if(hand.isFinal() && !dealer.isFinal()) {
            return new ChanceNode(hand, dealer, deck);
        }
        if(hand.isFinal() && dealer.isFinal()) {
            return new ValueNode(hand, dealer, deck);
        }
        throw new RuntimeException("Unexpected state!");
    }
    
    protected static void checkCard(int card) {
        if(card < 1 || card > 10)
            throw new IllegalArgumentException("Illegal card!");
    }
    
    public Action[] getChoices() {
        throw new IllegalArgumentException();
    }
    
    public Node makeChoice(Action action) {
        throw new IllegalArgumentException();
    }
    
    public Outcome[] getOutcomes() {
        throw new IllegalArgumentException();
    }
    
    public double getValue() {
        if(!isValueNode()) {
            throw new RuntimeException();
        }
        return ((ValueNode) this).value;
    }
    
    public boolean isChanceNode() {
        return this instanceof ChanceNode;
    }
    
    public boolean isChoiceNode() {
        return this instanceof ChoiceNode;
    }
    
    public boolean isValueNode() { 
        return this instanceof ValueNode;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.hand);
        hash = 53 * hash + Objects.hashCode(this.dealer);
        hash = 53 * hash + Objects.hashCode(this.deck);
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
        final Node other = (Node) obj;
        if (!Objects.equals(this.hand, other.hand)) {
            return false;
        }
        if (!Objects.equals(this.dealer, other.dealer)) {
            return false;
        }
        if (!Objects.equals(this.deck, other.deck)) {
            return false;
        }
        return true;
    }
    
    
    
    Node(PlayerHand hand, DealerHand dealer, Deck deck) {
        this.hand = hand;
        this.dealer = dealer;
        this.deck = deck;
        if(!hand.getRules().equals(dealer.getRules())) {
            throw new RuntimeException("The player and the dealer must play under the same rules!");
        }
    }
}
