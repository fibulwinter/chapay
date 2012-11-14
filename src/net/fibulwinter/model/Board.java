package net.fibulwinter.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import net.fibulwinter.physic.Continuum;
import net.fibulwinter.physic.Disk;
import net.fibulwinter.physic.FrictionModel;
import net.fibulwinter.physic.StaticBody;
import net.fibulwinter.utils.RandUtils;
import net.fibulwinter.utils.Rectangle;
import net.fibulwinter.utils.V;
import net.fibulwinter.view.IModel;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static net.fibulwinter.utils.RandUtils.mix;

public class Board implements IModel {

    private Continuum continuum;

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
    private final FrictionModel frictionModel;
    private List<Checker> checkers = newArrayList();

    public Board(Rectangle borders, BouncingMode bouncingMode, FrictionModel frictionModel) {
        this.borders = borders;
        this.bouncingMode = bouncingMode;
        this.frictionModel = frictionModel;
        continuum = new Continuum(frictionModel);
        continuum.getBodies().addAll(StaticBody.asClosed(
                new V(mix(borders.getMinX(), borders.getMaxX(), 0.33), borders.getMidY()),
                new V(mix(borders.getMinX(), borders.getMaxX(), 0.66), borders.getMidY()),
                new V(borders.getMidX(), mix(borders.getMinY(), borders.getMaxY(), 0.33))
        ));
        if(bouncingMode==BouncingMode.BOUNCE){
            continuum.getBodies().add(StaticBody.fromTo(
                    new V(borders.getMinX(), borders.getMinY()), new V(borders.getMaxX(), borders.getMinY())));
            continuum.getBodies().add(StaticBody.fromTo(
                    new V(borders.getMaxX(), borders.getMinY()), new V(borders.getMaxX(), borders.getMaxY())));
            continuum.getBodies().add(StaticBody.fromTo(
                    new V(borders.getMaxX(), borders.getMaxY()), new V(borders.getMinX(), borders.getMaxY())));
            continuum.getBodies().add(StaticBody.fromTo(
                    new V(borders.getMinX(), borders.getMaxY()), new V(borders.getMinX(), borders.getMinY())));

//            continuum.getBodies().add(new StaticBody(new V(borders.getMinX(),borders.getMinY()),new V(1,0)));
//            continuum.getBodies().add(new StaticBody(new V(borders.getMinX(),borders.getMinY()),new V(0,1)));
//            continuum.getBodies().add(new StaticBody(new V(borders.getMaxX(),borders.getMaxY()),new V(-1,0)));
//            continuum.getBodies().add(new StaticBody(new V(borders.getMaxX(),borders.getMaxY()),new V(0,-1)));
        }
    }

    public List<Checker> getCheckers() {
        return Collections.unmodifiableList(checkers);
    }

    public FrictionModel getFrictionModel() {
        return frictionModel;
    }

    public void add(Checker checker){
        checkers.add(checker);
        continuum.getBodies().add(checker.getDynamicBody());
    }

    public Continuum getContinuum() {
        return continuum;
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
        for(Checker checker: removeAllCheckers()){
            Disk disk = checker.getDisk();
            disk.setCenter(randomPos(this, disk.getRadius()));
            add(checker);
        }
    }

    private ArrayList<Checker> removeAllCheckers() {
        ArrayList<Checker> freeCheckers = newArrayList(checkers);
        for(Checker checker:checkers){
            continuum.getBodies().remove(checker.getDynamicBody());
        }
        checkers.clear();
        return freeCheckers;
    }

    public Checker getClosest(int filterColor, V pos, double maxD){
        Checker closest=null;
        double minD=maxD;
        for(Checker checker:getCheckers()){
            if(checker.getColor()==filterColor){
                double d = checker.getDisk().getCenter().subtract(pos).getLength();
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
                    Disk disk = checker.getDisk();
                    return !pos.inDistance(disk.getCenter(), r + disk.getRadius());
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
                return checker.getDynamicBody().getSpeed().getLength()>1;
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
            if(!isOutside(checker.getDisk())){
                set.add(checker.getColor());
            }
        }
        return set;
    }



    private boolean isOutside(Disk checker){
        return (checker.getCenter().getX()+checker.getRadius()<borders.getMinX()) ||
                (checker.getCenter().getX()-checker.getRadius()>borders.getMaxX()) ||
                (checker.getCenter().getY()+checker.getRadius()<borders.getMinY()) ||
                (checker.getCenter().getY()-checker.getRadius()>borders.getMaxY());
    }
}
