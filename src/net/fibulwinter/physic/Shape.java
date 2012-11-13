package net.fibulwinter.physic;

import net.fibulwinter.utils.V;

public abstract class Shape {
    private V center;

    public Shape(V center) {
        this.center = center;
    }

    public V getCenter() {
        return center;
    }

    public void setCenter(V center) {
        this.center = center;
    }

    public abstract void avoid(V touchPoint);

}
