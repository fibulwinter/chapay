package net.fibulwinter.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import net.fibulwinter.utils.RandUtils;
import net.fibulwinter.view.IModel;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;

public class Board implements IModel {

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
    }

    public List<Checker> getCheckers() {
        return Collections.unmodifiableList(checkers);
    }

    public void add(Checker checker){
        checkers.add(checker);
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
        for(Checker checker: freeCheckers){
            checker.setPos(randomPos(this,checker.getRadius()));
            add(checker);
        }
    }

    public Checker getClosest(int filterColor, V pos, double maxD){
        Checker closest=null;
        double minD=maxD;
        for(Checker checker:getCheckers()){
            if(checker.getColor()==filterColor){
                double d = checker.getPos().subtract(pos).getLength();
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
                    return !pos.inDistance(checker.getPos(), r + checker.getRadius());
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
                return checker.getSpeed().getLength()>1;
            }
        });
    }

    @Override
    public void simulate() {
        for(Checker checker:checkers){
            checker.move(1);
        }
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
        return (checker.getPos().getX()+checker.getRadius()<borders.getMinX()) ||
                (checker.getPos().getX()-checker.getRadius()>borders.getMaxX()) ||
                (checker.getPos().getY()+checker.getRadius()<borders.getMinY()) ||
                (checker.getPos().getY()-checker.getRadius()>borders.getMaxY());
    }

    private void bounce(Checker checkerA, Checker checkerB) {
        V vAB = checkerB.getPos().subtract(checkerA.getPos()).normal();
        double speedA1 = checkerA.getSpeed().dot(vAB);
        double speedB1 = checkerB.getSpeed().dot(vAB);
        double speedA2 = speedA1-speedB1;
        double speedB2 = speedB1-speedB1;
        double mass = checkerA.getMass() + checkerB.getMass();
        double speedA3 = speedA2*(checkerA.getMass()-checkerB.getMass())/mass;
        double speedB3 = 2*speedA2*checkerA.getMass()/mass;
        double speedA4 = speedA3+speedB1;
        double speedB4 = speedB3+speedB1;
        V psA=checkerA.getSpeed().subtract(vAB.scale(speedA1));
        V psB=checkerB.getSpeed().subtract(vAB.scale(speedB1));
        checkerA.setSpeed(psA.addScaled(vAB, speedA4));
        checkerB.setSpeed(psB.addScaled(vAB, speedB4));
    }
}
