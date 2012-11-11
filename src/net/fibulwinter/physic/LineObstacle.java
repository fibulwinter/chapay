package net.fibulwinter.physic;

import net.fibulwinter.utils.V;

public class LineObstacle extends Body{
    private V normal;

    public LineObstacle(V center, V normal) {
        super(center, 1e9, new V(0,0));
        this.normal = normal;
    }

    public V getNormal() {
        return normal;
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    public void setSpeed(V speed) {
    }

    @Override
    public void setCenter(V center) {
    }

    @Override
    public void avoid(V touchPoint) {
    }
}
