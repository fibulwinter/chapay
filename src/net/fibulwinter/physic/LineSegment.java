package net.fibulwinter.physic;

import net.fibulwinter.utils.V;

public class LineSegment extends Shape{
    private final V normal;
    private final double length;

    public LineSegment(V center, V normal, double length) {
        super(center);
        this.normal = normal;
        this.length = length;
    }

    public V getNormal() {
        return normal;
    }

    public double getLength() {
        return length;
    }

    public V getP1() {
        return getCenter().addScaled(getNormal().left(),getLength()/2);
    }

    public V getP2() {
        return getCenter().addScaled(getNormal().right(),getLength()/2);
    }

    @Override
    public void avoid(V touchPoint) {
    }
}
