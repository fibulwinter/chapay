package net.fibulwinter.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import net.fibulwinter.geometry.GeometryStack;
import net.fibulwinter.physic.Continuum;
import net.fibulwinter.geometry.Disk;
import net.fibulwinter.physic.StaticBody;
import net.fibulwinter.geometry.Rectangle;
import net.fibulwinter.geometry.V;
import net.fibulwinter.view.IModel;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static net.fibulwinter.utils.RandUtils.mix;

public class Board implements IModel {

    private Continuum continuum;
    private final Level level;

    private BouncingMode bouncingMode=BouncingMode.STOP;
    private List<Checker> checkers = newArrayList();
    private Placer placer;

    public Board(BouncingMode bouncingMode, Level level, Placer placer) {
        this.bouncingMode = bouncingMode;
        this.level = level;
        this.placer = placer;
        continuum = new Continuum(level);
//        continuum.getBodies().addAll(StaticBody.asClosed(
//                borders.getRelative(0.4, 0.5),
//                borders.getRelative(0.5, 0.6),
//                borders.getRelative(0.6, 0.5),
//                borders.getRelative(0.5, 0.4)
//        ));
        if(bouncingMode==BouncingMode.BOUNCE){
            Rectangle borders = level.getBorders();
            continuum.getBodies().add(StaticBody.fromTo(
                    borders.getRelative(0, 0), borders.getRelative(1, 0)));
            continuum.getBodies().add(StaticBody.fromTo(
                    borders.getRelative(1,0), borders.getRelative(1,1)));
            continuum.getBodies().add(StaticBody.fromTo(
                    borders.getRelative(1, 1), borders.getRelative(0, 1)));
            continuum.getBodies().add(StaticBody.fromTo(
                    borders.getRelative(0,1), borders.getRelative(0,0)));

//            continuum.getBodies().add(new StaticBody(new V(borders.getMinX(),borders.getMinY()),new V(1,0)));
//            continuum.getBodies().add(new StaticBody(new V(borders.getMinX(),borders.getMinY()),new V(0,1)));
//            continuum.getBodies().add(new StaticBody(new V(borders.getMaxX(),borders.getMaxY()),new V(-1,0)));
//            continuum.getBodies().add(new StaticBody(new V(borders.getMaxX(),borders.getMaxY()),new V(0,-1)));

        }
        removeAllCheckers();
        placer.setupCheckers(this);
    }

    public List<Checker> getCheckers() {
        return Collections.unmodifiableList(checkers);
    }

    public Level getLevel() {
        return level;
    }

    public void add(Checker checker){
        checkers.add(checker);
        continuum.getBodies().add(checker.getDynamicBody());
    }

    public Continuum getContinuum() {
        return continuum;
    }

    public Rectangle getBorders() {
        return getLevel().getBorders();
    }

    public void regenerate(){
        removeAllCheckers();
        placer.setupCheckers(this);
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

    public boolean isAnyMoving(){
        return Iterables.any(getCheckers(), new Predicate<Checker>() {
            @Override
            public boolean apply(Checker checker) {
                return checker.isMoving();
            }
        });
    }

    @Override
    public void simulate() {
        continuum.simulate(1.0);
        for (Iterator<Checker> iterator = checkers.iterator(); iterator.hasNext(); ) {
            Checker checker = iterator.next();
            boolean kill=false;
            switch (level.getFlyType(checker.getDisk().getCenter())) {
                case PIT:
                    kill=true;
                    break;
                case WATER:
                    kill=!checker.isMoving();
                    break;
                case GRASS:
                    break;
            }
            if(kill){
                checker.die();
                continuum.getBodies().remove(checker.getDynamicBody());
                iterator.remove();
            }
        }
/*
        if(bouncingMode!=BouncingMode.PASS){
            for(Checker checker:checkers){
                if(checker.getPos().getX()-checker.getRadius()<borders.getMinX()){
                    checker.setPosX(borders.getMinX() + checker.getRadius());
                    checker.setVelocity(checker.getVelocity().scale(bouncingMode.bounceFactor,1));
                }
                if(checker.getPos().getX()+checker.getRadius()>borders.getMaxX()){
                    checker.setPosX(borders.getMaxX()-checker.getRadius());
                    checker.setVelocity(checker.getVelocity().scale(bouncingMode.bounceFactor,1));
                }
                if(checker.getPos().getY()-checker.getRadius()<borders.getMinY()){
                    checker.setPosY(borders.getMinY() + checker.getRadius());
                    checker.setVelocity(checker.getVelocity().scale(1,bouncingMode.bounceFactor));
                }
                if(checker.getPos().getY()+checker.getRadius()>borders.getMaxY()){
                    checker.setPosY(borders.getMaxY() - checker.getRadius());
                    checker.setVelocity(checker.getVelocity().scale(1,bouncingMode.bounceFactor));
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
        Rectangle borders = getLevel().getBorders();
        return (checker.getCenter().getX()+checker.getRadius()<borders.getMinX()) ||
                (checker.getCenter().getX()-checker.getRadius()>borders.getMaxX()) ||
                (checker.getCenter().getY()+checker.getRadius()<borders.getMinY()) ||
                (checker.getCenter().getY()-checker.getRadius()>borders.getMaxY());
    }

    public enum BouncingMode{
        PASS(0),
        STOP(0),
        BOUNCE(-1);

        private double bounceFactor;

        private BouncingMode(double bounceFactor) {
            this.bounceFactor = bounceFactor;
        }

    }

}
