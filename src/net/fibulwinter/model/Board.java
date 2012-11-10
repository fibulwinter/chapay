package net.fibulwinter.model;

import net.fibulwinter.view.IModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Board implements IModel {
    private Rectangle borders;
    private List<Checker> checkers = newArrayList();

    public Board(Rectangle borders) {
        this.borders = borders;
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

    @Override
    public void simulate() {
        for(Checker checker:checkers){
            checker.move(1);
        }
        for(Checker checker:checkers){
            if(checker.getPos().getX()-checker.getRadius()<borders.getMinX()){
                checker.setPosX(borders.getMinX()+checker.getRadius());
                checker.setSpeed(checker.getSpeed().scale(-1,1));
            }
            if(checker.getPos().getX()+checker.getRadius()>borders.getMaxX()){
                checker.setPosX(borders.getMaxX()-checker.getRadius());
                checker.setSpeed(checker.getSpeed().scale(-1,1));
            }
            if(checker.getPos().getY()-checker.getRadius()<borders.getMinY()){
                checker.setPosY(borders.getMinY()+checker.getRadius());
                checker.setSpeed(checker.getSpeed().scale(1,-1));
            }
            if(checker.getPos().getY()+checker.getRadius()>borders.getMaxY()){
                checker.setPosY(borders.getMaxY()-checker.getRadius());
                checker.setSpeed(checker.getSpeed().scale(1,-1));
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
