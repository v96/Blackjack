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
class Dealer1Card extends DealerHand {

    private final int card;
    protected final boolean holecard;

    @Override
    public Action[] availableActions() {
        if (!getRules().dealerHasAHolecard || holecard) {
            return new Action[]{Action.HIT};
        } else {
            return new Action[]{Action.HIT, Action.GET_HOLECARD};
        }
    }

    @Override
    public DealerHand applyAction(Action action, int card) {
        switch (action) {
            case HIT:
                checkCard(card);
                if ((this.card == 1 && card == 10) || (this.card == 10 && card == 1)) {
                    return new DealerBlackjack(getRules());
                }
                int newTotal = newTotal(this.card == 1 ? 11 : this.card, this.card == 1, card);
                boolean newSoft = newSoft(this.card == 1 ? 11 : this.card, this.card == 1, card);
                if (newTotal > 17 || (newTotal == 17 && !newSoft) || (newTotal == 17 && newSoft && getRules().dealerStandsOnSoft17)) {
                    return new Dealer17To21(getRules(), newTotal);
                }
                return new DealerUnder17(getRules(), newTotal, newSoft);
            case GET_HOLECARD:
                if (!getRules().dealerHasAHolecard || holecard) {
                    throw new IllegalArgumentException();
                }
                return new Dealer1Card(getRules(), this.card, true);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.card;
        hash = 37 * hash + (this.holecard ? 1 : 0);
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
        final Dealer1Card other = (Dealer1Card) obj;
        if (this.card != other.card) {
            return false;
        }
        if (this.holecard != other.holecard) {
            return false;
        }
        return true;
    }

    Dealer1Card(Rules rules, int card, boolean holecard) {
        super(rules);
        this.card = card;
        this.holecard = holecard;
    }
}
