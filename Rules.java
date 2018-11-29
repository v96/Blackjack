/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bj.sim;

import java.util.Objects;

/**
 *
 * @author vasil.kuzevski
 */
public class Rules {

    public enum Surrender {
        NO_SURRENDER,
        LATE_SURRENDER,
        EARLY_SURRENDER_AGAINST_10_ONLY, //not implemented yet
        FULL_SURRENDER,
    }
    
    public enum Peeking {
        ALWAYS,
        ON_10_ONLY, //not implemented yet
        ON_ACE_ONLY, //not implemented yet
        NEVER,
    }
    
    public static final Rules DEFAULT_RULES = new Rules();
    public final boolean dealerHasAHolecard;
    public final Peeking peeking;
    public final boolean dealerStandsOnSoft17;
    public final boolean doubleAfterSplitAllowed;
    public final boolean canHitSplitAces;
    public final double blackjackPayout;
    public final Surrender surrender;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.dealerHasAHolecard ? 1 : 0);
        hash = 41 * hash + Objects.hashCode(this.peeking);
        hash = 41 * hash + (this.dealerStandsOnSoft17 ? 1 : 0);
        hash = 41 * hash + (this.doubleAfterSplitAllowed ? 1 : 0);
        hash = 41 * hash + (this.canHitSplitAces ? 1 : 0);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.blackjackPayout) ^ (Double.doubleToLongBits(this.blackjackPayout) >>> 32));
        hash = 41 * hash + Objects.hashCode(this.surrender);
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
        final Rules other = (Rules) obj;
        if (this.dealerHasAHolecard != other.dealerHasAHolecard) {
            return false;
        }
        if (this.dealerStandsOnSoft17 != other.dealerStandsOnSoft17) {
            return false;
        }
        if (this.doubleAfterSplitAllowed != other.doubleAfterSplitAllowed) {
            return false;
        }
        if (this.canHitSplitAces != other.canHitSplitAces) {
            return false;
        }
        if (Double.doubleToLongBits(this.blackjackPayout) != Double.doubleToLongBits(other.blackjackPayout)) {
            return false;
        }
        if (this.peeking != other.peeking) {
            return false;
        }
        if (this.surrender != other.surrender) {
            return false;
        }
        return true;
    }
    
    public Rules() {
        dealerHasAHolecard = true;
        peeking = Peeking.ALWAYS;
        dealerStandsOnSoft17 = true;
        doubleAfterSplitAllowed = true;
        canHitSplitAces = false;
        blackjackPayout = 1.5;
        surrender = Surrender.LATE_SURRENDER;
    }
}
