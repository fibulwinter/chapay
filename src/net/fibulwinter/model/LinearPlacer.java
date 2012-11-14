package net.fibulwinter.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.fibulwinter.physic.Disk;
import net.fibulwinter.utils.RandUtils;
import net.fibulwinter.utils.Rectangle;
import net.fibulwinter.utils.V;

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
            double y = i%2==0? 0.1 : 0.9;
            Rectangle borders = board.getBorders();
            V pos=new V(mix(borders.getMinX(), borders.getMaxX(), x),mix(borders.getMinY(), borders.getMaxY(), y));
            Checker checker = new Checker(pos.getX(), pos.getY(), radius, players.next());
            board.add(checker);
        }
    }

}
