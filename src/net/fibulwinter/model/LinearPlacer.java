package net.fibulwinter.model;

import net.fibulwinter.geometry.Rectangle;
import net.fibulwinter.geometry.V;

import java.util.Iterator;

import static net.fibulwinter.utils.RandUtils.mix;

public class LinearPlacer implements Placer {
    private int count;
    private double radius;
    private Iterator<Integer> players;

    public LinearPlacer(int count, double radius, Iterator<Integer> players) {
        this.count = count;
        this.radius = radius;
        this.players = players;
    }

    @Override
    public void setupCheckers(Board board) {
        for(int i=0;i<count;i++){
            double x = 1.0/(count/2+1)*(i/2+1);
            double y = i%2==0? 0.15 : 0.85;
            V pos= board.getBorders().getRelative(x, y);
            Checker checker = new Checker(pos.getX(), pos.getY(), radius, players.next());
            board.add(checker);
        }
    }

}
