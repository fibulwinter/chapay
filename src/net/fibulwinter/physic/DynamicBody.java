package net.fibulwinter.physic;

import net.fibulwinter.utils.V;

public class DynamicBody extends Body{
    public DynamicBody(Disk disk, double mass, V speed) {
        super(disk, mass, speed);
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    @Override
    public void move(double timeStep) {
        getShape().setCenter(getCenter().addScaled(getSpeed(), timeStep));
        setSpeed(getSpeed().addLength(-0.5*timeStep));
    }
}
