package net.fibulwinter.geometry;

import com.google.common.base.Optional;

public class Region {
    private Disk shape;
    private int color;
    private Optional<Double> friction=Optional.absent();

    public Region(Disk shape, double friction, int color) {
        this.shape = shape;
        this.friction = Optional.of(friction);
        this.color = color;
    }

    public Disk getShape() {
        return shape;
    }

    public int getColor() {
        return color;
    }

    public Optional<Double> getFriction() {
        return friction;
    }


}
