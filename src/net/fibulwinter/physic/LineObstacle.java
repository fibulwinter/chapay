package net.fibulwinter.physic;

import net.fibulwinter.utils.V;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class LineObstacle extends Body{
    private V normal;
    private double a=Double.POSITIVE_INFINITY;

    public static List<Body> asClosed(V... ps) {
        List<Body> list=newArrayList();
        for (int i = 0, psLength = ps.length; i < psLength; i++) {
            V p1 = ps[i];
            V p2 = ps[(i+1)%ps.length];
            list.add(fromTo(p1,p2));
            Circle circle = new Circle(p1, 1e-2, 1e9, new V(0, 0));
            circle.setFixed();
            list.add(circle);
        }
        return list;
    }

    public static LineObstacle fromTo(V p1, V p2) {
        LineObstacle lineObstacle = new LineObstacle(V.middle(p1, p2), new V(p2.subtract(p1).right().normal()));
        lineObstacle.a=p1.subtract(p2).getLength()/2;
        return lineObstacle;
    }

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

    public double getA() {
        return a;
    }

    public V getP1() {
        return getCenter().addScaled(getNormal().left(),getA());
    }

    public V getP2() {
        return getCenter().addScaled(getNormal().right(),getA());
    }
}
