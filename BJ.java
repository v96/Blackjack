/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bj.sim;

import bj.sim.decks.Deck;
import bj.sim.decks.InfiniteDeck;
import bj.sim.decks.SingleDeck;
import bj.sim.hands.*;
import bj.sim.nodes.ChoiceNode;
import bj.sim.nodes.Node;
import bj.sim.nodes.Outcome;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author vasil.kuzevski
 */
public class BJ {

    private static class MapKey {

        private final PlayerHand hand;
        private final DealerHand dealer;

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + Objects.hashCode(this.hand);
            hash = 79 * hash + Objects.hashCode(this.dealer);
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
            final MapKey other = (MapKey) obj;
            if (!Objects.equals(this.hand, other.hand)) {
                return false;
            }
            if (!Objects.equals(this.dealer, other.dealer)) {
                return false;
            }
            return true;
        }

        public String toString() {
            return hand.toString() + " " + dealer.toString();
        }

        public MapKey(PlayerHand hand, DealerHand dealer) {
            this.hand = hand;
            this.dealer = dealer;
        }
    }

    static int calls = 0, calculations = 0;
    static int stackDepth = 0;
    static Map<MapKey, Double> cachedEV;
    static Map<MapKey, Action> basicStrategy;

    public static double EV(PlayerHand hand, DealerHand dealer) {
        MapKey key = new MapKey(hand, dealer);
        if (cachedEV.containsKey(key)) {
            return cachedEV.get(key);
        }

        if (hand.isFinal() && dealer.isFinal()) {
            cachedEV.put(key, hand.compare(dealer));
            return hand.compare(dealer);
        }
        if (dealer.isBlank() || hand.isFinal()) {
            double ev = 0;
            if (dealer.hit(1).isBlackjack()) {
                for (int i = 2; i <= 9; i++) {
                    ev += (1 / (double) 12) * EV(hand, dealer.hit(i));
                }
                ev += (4 / (double) 12) * EV(hand, dealer.hit(10));
//                ev = (12 / (double) 13) * ev + (1 / (double) 13) * hand.compare(dealer.hit(1)); //adjust for dealer blackjack
            } else if (dealer.hit(10).isBlackjack()) {
                for (int i = 1; i <= 9; i++) {
                    ev += (1 / (double) 9) * EV(hand, dealer.hit(i));
                }
//                ev = (9 / (double) 13) * ev + (4 / (double) 13) * hand.compare(dealer.hit(10)); //adjust for dealer blackjack
            } else {
                for (int i = 1; i <= 9; i++) {
                    ev += (1 / (double) 13) * EV(hand, dealer.hit(i));
                }
                ev += (4 / (double) 13) * EV(hand, dealer.hit(10));
            }
            cachedEV.put(key, ev);
            return ev;
        }
        double bestEV = Double.NEGATIVE_INFINITY;
        Action bestAction = Action.STAND;
        for (Action action : hand.availableActions()) {
            double ev = 0;
            switch (action) {
                case HIT:
                case DOUBLEDOWN:
                    for (int i = 1; i <= 9; i++) {
                        ev += (1 / (double) 13) * EV(hand.applyAction(action, i), dealer);
                    }
                    ev += (4 / (double) 13) * EV(hand.applyAction(action, 10), dealer);
                    break;
                case SPLIT:
                    ev = 2 * EV((PlayerHand) hand.applyAction(action), dealer);
                    break;
                default:
                    ev = EV((PlayerHand) hand.applyAction(action), dealer);
                    break;
            }
            if (ev > bestEV) {
                bestEV = ev;
                bestAction = action;
            }
        }
        basicStrategy.put(key, bestAction);
        cachedEV.put(key, bestEV);
        return bestEV;
    }

    private static String niceStr(Action action) {
        switch (action) {
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

    private static final Random random = new Random();

    public static int nextCard() {
        return Math.min(random.nextInt(13) + 1, 10);
    }

    public static Action bestAction(PlayerHand hand, DealerHand dealer) {
        EV(hand, dealer);
        return basicStrategy.get(new MapKey(hand, dealer));
    }

    public static void sim() {
        PlayerHand handEmpty = new PlayerBlank();
        DealerHand dealerEmpty = new DealerBlank();

        int bankroll = 0;
        for (int i = 1; i <= 100000000; i++) {
            if (i != 0 && i % 100000 == 0) {
                DecimalFormat df = new DecimalFormat();
                df.setMinimumFractionDigits(10);
                df.setMaximumFractionDigits(10);
                System.out.println(df.format(bankroll / (double) i));
            }

            PlayerHand hand = handEmpty;
            DealerHand dealer = dealerEmpty;
            boolean split = false;
            PlayerHand secondHand = null;

            dealer = dealer.hit(nextCard());
            while (!hand.isFinal()) {
                if (!split && bestAction(hand, dealer) == Action.SPLIT) {
                    secondHand = hand.split();
                    while (!secondHand.isFinal()) {
                        secondHand = secondHand.applyAction(bestAction(secondHand, dealer), nextCard());
                    }
                    split = true;
                }
                hand = hand.applyAction(bestAction(hand, dealer), nextCard());
            }
            while (!dealer.isFinal()) {
                dealer = dealer.hit(nextCard());
            }
            if (split) {
                bankroll += secondHand.compare(dealer);
            }
            bankroll += hand.compare(dealer);
        }
    }

    public static void main_2(String[] args) {
        cachedEV = new HashMap();
        basicStrategy = new HashMap();

        PlayerHand emptyHand = new PlayerBlank();
        DealerHand emptyDealer = new DealerBlank();
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(14);
        df.setMaximumFractionDigits(14);

        System.out.println("Soft hands:");
        for (int i = 13; i <= 21; i++) {
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(df.format(EV(PlayerHand.handWith2CardTotal(i, true), emptyDealer.hit(j))) + " ");
            }
            System.out.print(df.format(EV(PlayerHand.handWith2CardTotal(i, true), emptyDealer.hit(1))) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Hard hands:");
        for (int i = 5; i <= 19; i++) {
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(df.format(EV(PlayerHand.handWith2CardTotal(i, false), emptyDealer.hit(j))) + " ");
            }
            System.out.print(df.format(EV(PlayerHand.handWith2CardTotal(i, false), emptyDealer.hit(1))) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Pairs:");
        for (int i = 1; i <= 10; i++) {
            PlayerHand hand = emptyHand.hit(i).hit(i);
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(df.format(EV(hand, emptyDealer.hit(j))) + " ");
//                System.out.print(niceStr(bestAction(hand, emptyDealer.hit(j))) + " ");
            }
            System.out.print(df.format(EV(hand, emptyDealer.hit(1))) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Soft hands:");
        for (int i = 13; i <= 20; i++) {
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(niceStr(bestAction(PlayerHand.handWith2CardTotal(i, true), emptyDealer.hit(j))) + " ");
            }
            System.out.print(niceStr(bestAction(PlayerHand.handWith2CardTotal(i, true), emptyDealer.hit(1))) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println();
        System.out.println("Hard hands:");
        for (int i = 5; i <= 19; i++) {
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(niceStr(bestAction(PlayerHand.handWith2CardTotal(i, false), emptyDealer.hit(j))) + " ");
            }
            System.out.print(niceStr(bestAction(PlayerHand.handWith2CardTotal(i, false), emptyDealer.hit(1))) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Pairs:");
        for (int i = 1; i <= 10; i++) {
            PlayerHand hand = emptyHand.hit(i).hit(i);
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(niceStr(bestAction(hand, emptyDealer.hit(j))) + " ");
//                System.out.print(niceStr(bestAction(hand, emptyDealer.hit(j))) + " ");
            }
            System.out.print(niceStr(bestAction(hand, emptyDealer.hit(1))));
            System.out.println();
        }
        System.out.println();
        System.out.println();

        PlayerHand hand = new PlayerBlank();
        DealerHand dealer = new DealerBlank();

        System.out.println(df.format(EV(hand, dealer)));
        System.out.println(cachedEV.size());
    }

    private static double pBJ() {
        return 8 / 169.0;
    }

    public static void main_3(String[] args) {

        //checks for bj, doesn't check for bj, surrender, etc. rules are not implemented correctly. 
        double ev = Calculations.EV(PlayerHand.BLANK, DealerHand.BLANK, CardDistribution.INFINITE_DECK);
        //ev = ev + pBJ() * (1 - pBJ()) * (-1);
        System.out.println(ev);
        System.out.println();
        return;

        PlayerHand emptyHand = new PlayerBlank();
        DealerHand emptyDealer = new DealerBlank();
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(6);
        df.setMaximumFractionDigits(6);

        System.out.println("Soft hands:");
        for (int i = 13; i <= 21; i++) {
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(df.format(Calculations.EV(PlayerHand.handWith2CardTotal(i, true), emptyDealer.hit(j), CardDistribution.INFINITE_DECK)) + " ");
            }
            System.out.print(df.format(Calculations.EV(PlayerHand.handWith2CardTotal(i, true), emptyDealer.hit(1), CardDistribution.INFINITE_DECK)) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Hard hands:");
        for (int i = 5; i <= 19; i++) {
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(df.format(Calculations.EV(PlayerHand.handWith2CardTotal(i, false), emptyDealer.hit(j), CardDistribution.INFINITE_DECK)) + " ");
            }
            System.out.print(df.format(Calculations.EV(PlayerHand.handWith2CardTotal(i, false), emptyDealer.hit(1), CardDistribution.INFINITE_DECK)) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Pairs:");
        for (int i = 1; i <= 10; i++) {
            PlayerHand hand = emptyHand.hit(i).hit(i);
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(df.format(Calculations.EV(hand, emptyDealer.hit(j), CardDistribution.INFINITE_DECK)) + " ");
            }
            System.out.print(df.format(Calculations.EV(hand, emptyDealer.hit(1), CardDistribution.INFINITE_DECK)) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Soft hands:");
        for (int i = 13; i <= 20; i++) {
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(Calculations.bestAction(PlayerHand.handWith2CardTotal(i, true), emptyDealer.hit(j), CardDistribution.INFINITE_DECK) + " ");
            }
            System.out.print(Calculations.bestAction(PlayerHand.handWith2CardTotal(i, true), emptyDealer.hit(1), CardDistribution.INFINITE_DECK) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println();
        System.out.println("Hard hands:");
        for (int i = 5; i <= 19; i++) {
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(Calculations.bestAction(PlayerHand.handWith2CardTotal(i, false), emptyDealer.hit(j), CardDistribution.INFINITE_DECK) + " ");
            }
            System.out.print(Calculations.bestAction(PlayerHand.handWith2CardTotal(i, false), emptyDealer.hit(1), CardDistribution.INFINITE_DECK) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Pairs:");
        for (int i = 1; i <= 10; i++) {
            PlayerHand hand = emptyHand.hit(i).hit(i);
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(Calculations.bestAction(hand, emptyDealer.hit(j), CardDistribution.INFINITE_DECK) + " ");
            }
            System.out.print(Calculations.bestAction(hand, emptyDealer.hit(1), CardDistribution.INFINITE_DECK) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println();
        System.out.println("Soft hands:");
        for (int i = 13; i <= 21; i++) {
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(df.format(Calculations.pOfHand(PlayerHand.BLANK, DealerHand.BLANK, PlayerHand.handWith2CardTotal(i, true), emptyDealer.hit(j))) + " ");
            }
            System.out.print(df.format(Calculations.pOfHand(PlayerHand.BLANK, DealerHand.BLANK, PlayerHand.handWith2CardTotal(i, true), emptyDealer.hit(1))) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Hard hands:");
        for (int i = 5; i <= 19; i++) {
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(df.format(Calculations.pOfHand(PlayerHand.BLANK, DealerHand.BLANK, PlayerHand.handWith2CardTotal(i, false), emptyDealer.hit(j))) + " ");
            }
            System.out.print(df.format(Calculations.pOfHand(PlayerHand.BLANK, DealerHand.BLANK, PlayerHand.handWith2CardTotal(i, false), emptyDealer.hit(1))) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Pairs:");
        for (int i = 1; i <= 10; i++) {
            PlayerHand hand = emptyHand.hit(i).hit(i);
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(df.format(Calculations.pOfHand(PlayerHand.BLANK, DealerHand.BLANK, hand, emptyDealer.hit(j))) + " ");
            }
            System.out.print(df.format(Calculations.pOfHand(PlayerHand.BLANK, DealerHand.BLANK, hand, emptyDealer.hit(1))) + " ");
            System.out.println();
        }
        System.out.println();
    }

    static Map<Node, Double> evmap = new HashMap();
    static Map<Node, Action> bestaction = new HashMap();

    public static double EV(Node node) {
        if (evmap.containsKey(node)) {
            return evmap.get(node);
        }
        if (node.isValueNode()) {
            evmap.put(node, node.getValue());
            return node.getValue();
        }
        double ev = 0;
        if (node.isChoiceNode()) {
            double maxEV = Double.NEGATIVE_INFINITY;
            Action best = null;
            for (Action action : node.getChoices()) {
                ev = EV(node.makeChoice(action));
                if (action == Action.SPLIT) {
                    ev *= 2;
                }
                if (ev > maxEV) {
                    maxEV = ev;
                    best = action;
                }
            }
            bestaction.put(node, best);
            evmap.put(node, maxEV);
            return maxEV;
        }
        if (node.isChanceNode()) {
            for (Outcome outcome : node.getOutcomes()) {
                ev += outcome.p * EV(outcome.node);
            }
            evmap.put(node, ev);
            return ev;
        }
        throw new RuntimeException("Unexpected node type!");
    }

    public static void main(String[] args) {
        Node root = new ChoiceNode(PlayerHand.BLANK, DealerHand.BLANK, new InfiniteDeck());
        System.out.println(EV(root));
        return;
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(4);
        df.setMaximumFractionDigits(4);
        Deck deck = new InfiniteDeck();
        System.out.println("Soft hands:");
        for (int i = 13; i <= 21; i++) {
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(df.format(EV(Node.generateNode(PlayerHand.handWith2CardTotal(i, true), DealerHand.BLANK.hit(j).getHolecard(), deck))) + " ");
            }
            System.out.print(df.format(EV(Node.generateNode(PlayerHand.handWith2CardTotal(i, true), DealerHand.BLANK.hit(1).getHolecard(), deck))) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Hard hands:");
        for (int i = 5; i <= 19; i++) {
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(df.format(EV(Node.generateNode(PlayerHand.handWith2CardTotal(i, false), DealerHand.BLANK.hit(j).getHolecard(), deck))) + " ");
            }
            System.out.print(df.format(EV(Node.generateNode(PlayerHand.handWith2CardTotal(i, false), DealerHand.BLANK.hit(1).getHolecard(), deck))) + " ");
            System.out.println();
        }
        System.out.println();
        System.out.println("Pairs:");
        for (int i = 1; i <= 10; i++) {
            PlayerHand hand = PlayerHand.BLANK.hit(i).hit(i);
            System.out.print(i + ": ");
            for (int j = 2; j <= 10; j++) {
                System.out.print(df.format(EV(Node.generateNode(hand, DealerHand.BLANK.hit(j).getHolecard(), deck))) + " ");
            }
            System.out.print(df.format(EV(Node.generateNode(hand, DealerHand.BLANK.hit(1).getHolecard(), deck))) + " ");
            System.out.println();
        }
        System.out.println(EV(Node.generateNode(PlayerHand.BLANK, DealerHand.BLANK, deck)));
//        System.out.println();
//        DealerHand dealer = DealerHand.BLANK.hit(8);
//        dealer = dealer.getHolecard();
//        dealer = dealer.hit(2);
//        dealer = dealer.hit(3);
//        dealer = dealer.hit(5);
//        dealer = dealer.hit(10);
//        Node node = Node.generateNode(PlayerHand.BLANK.hit(10).hit(9), DealerHand.BLANK.hit(8), deck);
//        double ev = EV(node);
//        System.out.println(ChanceNode.calls);
//        System.out.println(ev);
    }
}
