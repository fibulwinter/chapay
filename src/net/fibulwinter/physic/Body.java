package net.fibulwinter.physic;

import net.fibulwinter.geometry.Shape;
import net.fibulwinter.geometry.V;

public abstract class Body {
    private Shape shape;
    private V velocity;
    private double mass;

    public Body(Shape shape, double mass, V velocity) {
        this.shape = shape;
        this.mass = mass;
        this.velocity = velocity;
    }

    public Shape getShape() {
        return shape;
    }

    public V getCenter(){
        return shape.getCenter();
    }

    public V getVelocity() {
        return velocity;
    }

    public void setVelocity(V velocity) {
        this.velocity = velocity;
    }

    public abstract boolean isFixed();

    public abstract void move(double timeStep, double friction);


    public double getMass() {
        return mass;
    }
}
