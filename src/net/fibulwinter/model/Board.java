package net.fibulwinter.model;

import com.google.common.collect.Sets;
import net.fibulwinter.view.IModel;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;

public class Board implements IModel {
    private Rectangle borders;
    private boolean bouncing;
    private List<Checker> checkers = newArrayList();

    public Board(Rectangle borders, boolean bouncing) {
        this.borders = borders;
        this.bouncing = bouncing;
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

    public void generate(int count, Iterator<Integer> players){
        checkers.clear();
        for(int i=0;i<count;i++){
            double r = 50;
            V pos=randomPos(this,r);
            Checker checker = new Checker(pos.getX(), pos.getY(), r, 0, 0);
            checker.setColor(players.next());
            add(checker);
        }
    }

    private V randomPos(Board board, double r) {
        Rectangle borders = board.getBorders();
        boolean bad;
        V pos=null;
        do {
            pos = new V(rand(borders.getMinX() + r, borders.getMaxX() - r),
                    rand(borders.getMinY() + r, borders.getMaxY() - r));
            Checker candidate = new Checker(pos.getX(), pos.getY(), r, 0, 0);
            bad = false;
            for (Checker checker : board.getCheckers()) {
                if (checker.isTouched(candidate)) {
                    bad=true;
                }
            }
        } while (bad);
        return pos;
    }

    private double rand(double min, double max) {
        return Math.random() * (max-min) + min;
    }





    @Override
    public void simulate() {
        for(Checker checker:checkers){
            checker.move(1);
        }
        if(bouncing){
            for(Checker checker:checkers){
                if(checker.getPos().getX()-checker.getRadius()<borders.getMinX()){
                    checker.setPosX(borders.getMinX() + checker.getRadius());
                    checker.setSpeed(checker.getSpeed().scale(-1,1));
                }
                if(checker.getPos().getX()+checker.getRadius()>borders.getMaxX()){
                    checker.setPosX(borders.getMaxX()-checker.getRadius());
                    checker.setSpeed(checker.getSpeed().scale(-1,1));
                }
                if(checker.getPos().getY()-checker.getRadius()<borders.getMinY()){
                    checker.setPosY(borders.getMinY() + checker.getRadius());
                    checker.setSpeed(checker.getSpeed().scale(1,-1));
                }
                if(checker.getPos().getY()+checker.getRadius()>borders.getMaxY()){
                    checker.setPosY(borders.getMaxY() - checker.getRadius());
                    checker.setSpeed(checker.getSpeed().scale(1,-1));
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
