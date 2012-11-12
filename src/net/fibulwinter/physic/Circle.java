package net.fibulwinter.physic;

import net.fibulwinter.utils.V;

public class Circle extends Body{
    private double radius;

    public Circle(V center, double radius, double mass, V speed) {
        super(center, mass, speed);
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public void avoid(V touchPoint) {
        setCenter(touchPoint.addScaled(getCenter().subtract(touchPoint).normal(), radius));
    }

}
