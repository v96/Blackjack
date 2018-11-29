/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bj.sim.nodes;

import bj.sim.Action;
import bj.sim.Rules;
import bj.sim.hands.*;
import bj.sim.decks.*;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Vasil
 */
public class ChanceNode extends Node {

    private final Outcome[] outcomes;
    private final Action playerAction;
    public static int calls = 0;

    private void expand() {
        int n = 0;
        double pSum = 0;
        if (playerAction == null) { //dealer's turn
            if (dealer.getRules().dealerHasAHolecard && dealer.has1Card()) {
                double pHolecard = 0;
                double pBlackjack1 = 0;
                double pBlackjack10 = 0;
                if ((hand.getRules().peeking == Rules.Peeking.ALWAYS || hand.getRules().peeking == Rules.Peeking.ON_10_ONLY) && dealer.hit(1).isBlackjack()) {
                    pBlackjack1 += deck.p(1);
                } else {
                    pHolecard += deck.p(1);
                }
                for (int i = 2; i <= 9; i++) {
                    pHolecard += deck.p(i);
                }
                if ((hand.getRules().peeking == Rules.Peeking.ALWAYS || hand.getRules().peeking == Rules.Peeking.ON_ACE_ONLY) && dealer.hit(10).isBlackjack()) {
                    pBlackjack10 += deck.p(10);
                } else {
                    pHolecard += deck.p(10);
                }
                if (pHolecard > 0) {
                    outcomes[n++] = new Outcome(pHolecard, generateNode(hand, dealer.getHolecard(), deck));
                }
                if (pBlackjack1 > 0) {
                    outcomes[n++] = new Outcome(pBlackjack1, generateNode(hand, dealer.hit(1), deck.remove(1)));
                }
                if (pBlackjack10 > 0) {
                    outcomes[n++] = new Outcome(pBlackjack10, generateNode(hand, dealer.hit(10), deck.remove(10)));
                }
                pSum = pHolecard + pBlackjack1 + pBlackjack10;
            } else if (dealer.getRules().dealerHasAHolecard && dealer.hasHolecard()) {
                double pNotBlackjackOrUnseenBlackjack = 0;
                for (int i = 1; i <= 10; i++) {
                    if (!peekForBJ(dealer, i)) {
                        pNotBlackjackOrUnseenBlackjack += deck.p(i);
                    }
                }
                for (int i = 1; i <= 10; i++) {
                    if (deck.p(i) > 0 && !peekForBJ(dealer, i)) {
                        outcomes[n++] = new Outcome(deck.p(i) / pNotBlackjackOrUnseenBlackjack, generateNode(hand, dealer.hit(i), deck.remove(i)));
                        pSum += deck.p(i) / pNotBlackjackOrUnseenBlackjack;
                    }
                }
            } else {
                for (int i = 1; i <= 10; i++) {
                    if (deck.p(i) > 0) {
                        outcomes[n++] = new Outcome(deck.p(i), generateNode(hand, dealer.hit(i), deck.remove(i)));
                        pSum += deck.p(i);
                    }
                }
            }
        } else { //player's turn
            for (int i = 1; i <= 10; i++) {
                if (deck.p(i) > 0) {
                    outcomes[n++] = new Outcome(deck.p(i), generateNode(hand.applyAction(playerAction, i), dealer, deck.remove(i)));
                    pSum += deck.p(i);
                }
            }
        }
        if (Math.abs(pSum - 1) > 1E-6) {
            throw new RuntimeException("The probabilities don't add up to 1!");
        }
        if (n != outcomes.length) {
            throw new RuntimeException("Not all possible outcomes are generated! n, outcomes.length = " + n + ", " + outcomes.length);
        }
    }

    @Override
    public Outcome[] getOutcomes() {
        if (outcomes[0] == null) {
            expand();
        }
        return outcomes;
    }

    private boolean peekForBJ(DealerHand dealer, int card) {
        checkCard(card);
        if (!(dealer.getRules().dealerHasAHolecard && (dealer.has1Card() || dealer.hasHolecard()))) {
            throw new RuntimeException("You can't peek for blackjack on this hand!");
        }
        if (card != 10 && card != 1) {
            return false;
        }
        if (card == 10) {
            return (dealer.getRules().peeking == Rules.Peeking.ALWAYS || dealer.getRules().peeking == Rules.Peeking.ON_ACE_ONLY) && dealer.hit(10).isBlackjack();
        }
        if (card == 1) {
            return (dealer.getRules().peeking == Rules.Peeking.ALWAYS || dealer.getRules().peeking == Rules.Peeking.ON_10_ONLY) && dealer.hit(1).isBlackjack();
        }
        throw new RuntimeException("Unexpected state!");
    }

    public ChanceNode(PlayerHand hand, DealerHand dealer, Deck deck) { //dealer's turn
        super(hand, dealer, deck);
        int n = 0;
        if (dealer.getRules().dealerHasAHolecard && dealer.has1Card()) {
            if (deck.p(1) > 0 && peekForBJ(dealer, 1)) {
                n += 1;
            }
            for (int i = 1; i <= 10; i++) {
                if (deck.p(i) > 0 && !peekForBJ(dealer, i)) {
                    n += 1;
                    break;
                }
            }
            if (deck.p(10) > 0 && peekForBJ(dealer, 10)) {
                n += 1;
            }
        } else {
            if (dealer.getRules().dealerHasAHolecard && dealer.hasHolecard()) {
                for (int i = 1; i <= 10; i++) {
                    if (deck.p(i) > 0 && !peekForBJ(dealer, i)) {
                        n++;
                    }
                }
            } else {
                for (int i = 1; i <= 10; i++) {
                    if (deck.p(i) > 0) {
                        n++;
                    }
                }
            }
        }
        outcomes = new Outcome[n];
        playerAction = null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.hand);
        hash = 53 * hash + Objects.hashCode(this.dealer);
        hash = 53 * hash + Objects.hashCode(this.deck);
        hash = 29 * hash + Objects.hashCode(this.playerAction);
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
        final ChanceNode other = (ChanceNode) obj;
        if (!Objects.equals(this.hand, other.hand)) {
            return false;
        }
        if (!Objects.equals(this.dealer, other.dealer)) {
            return false;
        }
        if (!Objects.equals(this.deck, other.deck)) {
            return false;
        }
        if (this.playerAction != other.playerAction) {
            return false;
        }
        return true;
    }

    public ChanceNode(PlayerHand hand, DealerHand dealer, Deck deck, Action playerAction) { //player's turn
        super(hand, dealer, deck);
        int n = 0;
        for (int i = 1; i <= 10; i++) {
            if (deck.p(i) > 0) {
                n++;
            }
        }
        outcomes = new Outcome[n];
        this.playerAction = playerAction;
    }
}
