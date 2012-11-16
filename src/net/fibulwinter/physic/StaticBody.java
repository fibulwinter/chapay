package net.fibulwinter.physic;

import net.fibulwinter.geometry.Disk;
import net.fibulwinter.geometry.LineSegment;
import net.fibulwinter.geometry.Shape;
import net.fibulwinter.geometry.V;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class StaticBody extends Body{

    public static List<Body> asClosed(V... ps) {
        List<Body> list=newArrayList();
        for (int i = 0, psLength = ps.length; i < psLength; i++) {
            V p1 = ps[i];
            V p2 = ps[(i+1)%ps.length];
            list.add(fromTo(p1,p2));
            list.add(new StaticBody(new Disk(p1, 1e-2)));
        }
        return list;
    }

    public static StaticBody fromTo(V p1, V p2) {
        return new StaticBody(new LineSegment(V.middle(p1, p2), new V(p2.subtract(p1).right().normal()),p1.subtract(p2).getLength()));
    }

    public StaticBody(Shape shape) {
        super(shape, 1e9, new V(0,0));
    }

    @Override
    public boolean isFixed() {
        return true;
    }

    @Override
    public void setVelocity(V velocity) {
    }

    @Override
    public void move(double timeStep, double friction) {
    }
}
