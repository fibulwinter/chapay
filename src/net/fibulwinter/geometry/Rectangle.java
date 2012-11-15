package net.fibulwinter.geometry;

import static com.google.common.base.Preconditions.checkArgument;
import static net.fibulwinter.utils.RandUtils.mix;

public class Rectangle extends Shape{
    private double halfWidth;
    private double halfHeigth;

    public Rectangle(V center, double width, double height) {
        super(center);
        this.halfWidth=width/2;
        this.halfHeigth=height/2;
    }

    public double getMinX() {
        return getCenter().getX()-halfWidth;
    }

    public double getMinY() {
        return getCenter().getY()-halfHeigth;
    }

    public double getMaxX() {
        return getCenter().getX()+halfWidth;
    }

    public double getMaxY() {
        return getCenter().getY()+halfHeigth;
    }

    public double getWidth() {
        return halfWidth*2;
    }

    public double getHeight() {
        return halfHeigth*2;
    }

    public V getRelative(double rx, double ry){
        return new V(mix(getMinX(),getMaxX(),rx),mix(getMinY(),getMaxY(),ry));
    }

    @Override
    public void avoid(V touchPoint) {
    }
}
