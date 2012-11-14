package net.fibulwinter.physic;

import net.fibulwinter.geometry.Shape;
import net.fibulwinter.geometry.V;

public abstract class Body {
    private Shape shape;
    private V speed;
    private double mass;

    public Body(Shape shape, double mass, V speed) {
        this.shape = shape;
        this.mass = mass;
        this.speed = speed;
    }

    public Shape getShape() {
        return shape;
    }

    public V getCenter(){
        return shape.getCenter();
    }

    public V getSpeed() {
        return speed;
    }

    public void setSpeed(V speed) {
        this.speed = speed;
    }

    public abstract boolean isFixed();

    public abstract void move(double timeStep, double friction);


    public double getMass() {
        return mass;
    }
}
