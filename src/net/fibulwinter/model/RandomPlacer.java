package net.fibulwinter.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.fibulwinter.physic.Disk;
import net.fibulwinter.utils.RandUtils;
import net.fibulwinter.utils.Rectangle;
import net.fibulwinter.utils.V;

import java.util.Iterator;

public class RandomPlacer implements Placer {
    private int count;
    private double radius;
    private Iterator<Integer> players;

    public RandomPlacer(int count, double radius, Iterator<Integer> players) {
        this.count = count;
        this.radius = radius;
        this.players = players;
    }

    @Override
    public void setupCheckers(Board board) {
        for(int i=0;i<count;i++){
            V pos=randomPos(board,radius);
            Checker checker = new Checker(pos.getX(), pos.getY(), radius, players.next());
            board.add(checker);
        }
    }

    private V randomPos(Board board, final double r) {
        Rectangle borders = board.getBorders();
        while (true){
            final V pos= new V(RandUtils.rand(borders.getMinX() + r, borders.getMaxX() - r),
                    RandUtils.rand(borders.getMinY() + r, borders.getMaxY() - r));
            if(Iterables.all(board.getCheckers(), new Predicate<Checker>() {
                @Override
                public boolean apply(Checker checker) {
                    Disk disk = checker.getDisk();
                    return !pos.inDistance(disk.getCenter(), r + disk.getRadius());
                }
            })){
                return pos;
            }
        }
    }


}
