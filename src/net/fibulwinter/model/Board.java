package net.fibulwinter.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import net.fibulwinter.physic.Continuum;
import net.fibulwinter.physic.LineObstacle;
import net.fibulwinter.physic.RectangleBody;
import net.fibulwinter.utils.RandUtils;
import net.fibulwinter.utils.Rectangle;
import net.fibulwinter.utils.V;
import net.fibulwinter.view.IModel;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;

public class Board implements IModel {

    private Continuum continuum = new Continuum();

    public enum BouncingMode{
        PASS(0),
        STOP(0),
        BOUNCE(-1);

        private double bounceFactor;

        private BouncingMode(double bounceFactor) {
            this.bounceFactor = bounceFactor;
        }

    }
    private Rectangle borders;
    private BouncingMode bouncingMode=BouncingMode.STOP;
    private List<Checker> checkers = newArrayList();

    public Board(Rectangle borders, BouncingMode bouncingMode) {
        this.borders = borders;
        this.bouncingMode = bouncingMode;
        if(bouncingMode==BouncingMode.BOUNCE){
            continuum.getBodies().add(new LineObstacle(new V(borders.getMinX(),borders.getMinY()),new V(1,0)));
            continuum.getBodies().add(new LineObstacle(new V(borders.getMinX(),borders.getMinY()),new V(0,1)));
            continuum.getBodies().add(new LineObstacle(new V(borders.getMaxX(),borders.getMaxY()),new V(-1,0)));
            continuum.getBodies().add(new LineObstacle(new V(borders.getMaxX(),borders.getMaxY()),new V(0,-1)));
        }
    }

    public List<Checker> getCheckers() {
        return Collections.unmodifiableList(checkers);
    }

    public void add(Checker checker){
        checkers.add(checker);
        continuum.getBodies().add(checker.getCircle());
    }

    public Rectangle getBorders() {
        return borders;
    }

    public void generate(double r, int count, Iterator<Integer> players){
        checkers.clear();
        for(int i=0;i<count;i++){
            V pos=randomPos(this,r);
            Checker checker = new Checker(pos.getX(), pos.getY(), r, players.next());
            add(checker);
        }
    }

    public void regenerate(){
        ArrayList<Checker> freeCheckers = newArrayList(checkers);
        checkers.clear();
        continuum.getBodies().clear();//todo only checkers
        for(Checker checker: freeCheckers){
            checker.getCircle().setCenter(randomPos(this, checker.getRadius()));
            add(checker);
        }
    }

    public Checker getClosest(int filterColor, V pos, double maxD){
        Checker closest=null;
        double minD=maxD;
        for(Checker checker:getCheckers()){
            if(checker.getColor()==filterColor){
                double d = checker.getCenter().subtract(pos).getLength();
                if(d<minD){
                    minD=d;
                    closest=checker;
                }
            }
        }
        return closest;
    }

    private V randomPos(Board board, final double r) {
        Rectangle borders = board.getBorders();
        while (true){
            final V pos= new V(RandUtils.rand(borders.getMinX() + r, borders.getMaxX() - r),
                    RandUtils.rand(borders.getMinY() + r, borders.getMaxY() - r));
            if(Iterables.all(board.getCheckers(), new Predicate<Checker>() {
                @Override
                public boolean apply(Checker checker) {
                    return !pos.inDistance(checker.getCenter(), r + checker.getRadius());
                }
            })){
                return pos;
            }
        }
    }

    public boolean isAnyMoving(){
        return Iterables.any(getCheckers(), new Predicate<Checker>() {
            @Override
            public boolean apply(Checker checker) {
                return checker.getCircle().getSpeed().getLength()>1;
            }
        });
    }

    @Override
    public void simulate() {
        continuum.simulate(1.0);
/*
        if(bouncingMode!=BouncingMode.PASS){
            for(Checker checker:checkers){
                if(checker.getPos().getX()-checker.getRadius()<borders.getMinX()){
                    checker.setPosX(borders.getMinX() + checker.getRadius());
                    checker.setSpeed(checker.getSpeed().scale(bouncingMode.bounceFactor,1));
                }
                if(checker.getPos().getX()+checker.getRadius()>borders.getMaxX()){
                    checker.setPosX(borders.getMaxX()-checker.getRadius());
                    checker.setSpeed(checker.getSpeed().scale(bouncingMode.bounceFactor,1));
                }
                if(checker.getPos().getY()-checker.getRadius()<borders.getMinY()){
                    checker.setPosY(borders.getMinY() + checker.getRadius());
                    checker.setSpeed(checker.getSpeed().scale(1,bouncingMode.bounceFactor));
                }
                if(checker.getPos().getY()+checker.getRadius()>borders.getMaxY()){
                    checker.setPosY(borders.getMaxY() - checker.getRadius());
                    checker.setSpeed(checker.getSpeed().scale(1,bouncingMode.bounceFactor));
                }
            }
        }
        for (int i = 0, checkersSize = checkers.size(); i < checkersSize; i++) {
            Checker checker1 = checkers.get(i);
            for (int j = i+1; j < checkersSize; j++) {
                Checker checker2 = checkers.get(j);
                if (checker1.isTouched(checker2)) {
                    bounce(checker1, checker2);
                }
            }
        }
*/
    }

    public Set<Integer> remainingColors(){
        Set<Integer> set = Sets.newTreeSet();
        for(Checker checker:checkers){
            if(!isOutside(checker)){
                set.add(checker.getColor());
            }
        }
        return set;
    }



    private boolean isOutside(Checker checker){
        return (checker.getCenter().getX()+checker.getRadius()<borders.getMinX()) ||
                (checker.getCenter().getX()-checker.getRadius()>borders.getMaxX()) ||
                (checker.getCenter().getY()+checker.getRadius()<borders.getMinY()) ||
                (checker.getCenter().getY()-checker.getRadius()>borders.getMaxY());
    }
}
