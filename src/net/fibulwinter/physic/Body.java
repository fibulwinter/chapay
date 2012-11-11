package net.fibulwinter.physic;

import com.google.common.base.Optional;
import net.fibulwinter.utils.V;

public abstract class Body {
    private V center;
    private V speed;
    private double mass;

    public Body(V center, double mass, V speed) {
        this.center = center;
        this.mass = mass;
        this.speed = speed;
    }

    public V getCenter(){
        return center;
    }

    public void setCenter(V center) {
        this.center = center;
    }

    public V getSpeed() {
        return speed;
    }

    public void setSpeed(V speed) {
        this.speed = speed;
    }

    public boolean isFixed(){
        return false;
    }

    public void move(double timeStep) {
        if(!isFixed()){
            center=center.addScaled(speed, timeStep);
            speed = speed.addLength(-0.5*timeStep);
        }
    }

    public abstract void avoid(V touchPoint);

    public double getMass() {
        return mass;
    }
}
