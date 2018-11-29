/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bj.sim;

/**
 *
 * @author vasil.kuzevski
 */
public enum Action {
    HIT,
    STAND,
    DOUBLEDOWN,
    SPLIT,
    SURRENDER,
    GET_HOLECARD;
    
    @Override
    public String toString() {
        switch(this) {
            case HIT:
                return "H ";
            case STAND: 
                return "S ";
            case DOUBLEDOWN:
                return "Dd";
            case SPLIT:
                return "Sp";
            case SURRENDER:
                return "Sr";
            default:
                throw new IllegalArgumentException();
        }
    }
}
