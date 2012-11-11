package net.fibulwinter.model;

import net.fibulwinter.physic.Circle;
import net.fibulwinter.utils.V;

public class Checker {
    private int color;
    private Circle circle;

    public Checker(double x, double y, double radius, int color) {
        this.color = color;
        circle=new Circle(new V(x,y), radius, 1.0, new V(0,0));
    }

    public int getColor() {
        return color;
    }

    public Circle getCircle() {
        return circle;
    }

    public boolean isTouched(Checker checker) {
        return getCircle().getCenter().inDistance(checker.getCircle().getCenter(), checker.getCircle().getRadius()+getCircle().getRadius());
    }

    public V getCenter(){
        return circle.getCenter();
    }

    public double getRadius(){
        return circle.getRadius();
    }

}
