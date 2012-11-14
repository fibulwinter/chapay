package net.fibulwinter.geometry;

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

    public boolean contains(V pos) {
        return getCenter().inDistance(pos, getRadius());
    }
}
