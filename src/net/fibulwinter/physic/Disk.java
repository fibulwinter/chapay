package net.fibulwinter.physic;

import net.fibulwinter.utils.V;

public class Disk extends Shape{
    private final double radius;

    public Disk(V center, double radius) {
        super(center);
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public void avoid(V touchPoint) {
        setCenter(touchPoint.addScaled(getCenter().subtract(touchPoint).normal(), getRadius()));
    }

}
