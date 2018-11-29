/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bj.sim.nodes;

/**
 *
 * @author Vasil
 */
public class Outcome {

    public final double p;
    public final Node node;

    public Outcome(double p, Node node) {
        this.p = p;
        this.node = node;
    }
}
