package net.fibulwinter.geometry;

import com.google.common.base.Optional;
import net.fibulwinter.model.FlyType;

public class Region {
    private Disk shape;
    private int color;
    private Optional<Double> friction=Optional.absent();
    private Optional<FlyType> flyType=Optional.absent();

    public Region(Disk shape, int color) {
        this.shape = shape;
        this.color = color;
    }

    public Region friction(double friction) {
        this.friction = Optional.of(friction);
        return this;
    }

    public Region flyType(FlyType flyType) {
        this.flyType = Optional.of(flyType);
        return this;
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

    public Optional<FlyType> getFlyType() {
        return flyType;
    }
}
