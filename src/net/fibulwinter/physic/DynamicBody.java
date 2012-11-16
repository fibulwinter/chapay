package net.fibulwinter.physic;

import net.fibulwinter.geometry.Disk;
import net.fibulwinter.geometry.V;

public class DynamicBody extends Body{
    private double maxSpeed=Double.POSITIVE_INFINITY;

    public DynamicBody(Disk disk, double mass, V speed) {
        super(disk, mass, speed);
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @Override
    public void move(double timeStep, double friction) {
        getShape().setCenter(getCenter().addScaled(getVelocity(), timeStep));
        setVelocity(getVelocity().addLength(-friction * timeStep).limitLength(maxSpeed));
    }
}
