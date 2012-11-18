package net.fibulwinter.model;

import net.fibulwinter.geometry.Rectangle;
import net.fibulwinter.geometry.V;

import java.util.Iterator;

import static net.fibulwinter.utils.RandUtils.mix;

public class LinearPlacer implements Placer {
    private int count;
    private Iterator<Integer> players;

    public LinearPlacer(int count,  Iterator<Integer> players) {
        this.count = count;
        this.players = players;
    }

    @Override
    public void setupCheckers(Board board) {
        for(int i=0;i<count;i++){
            double x = i%2==0? 0.15 : 0.85;
            double y = 1.0/(count/2+1)*(i/2+1);
            V pos= board.getBorders().getRelative(x, y);
            Checker checker = new Checker(pos.getX(), pos.getY(), players.next());
            board.add(checker);
        }
    }

}
