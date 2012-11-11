package net.fibulwinter.physic;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import net.fibulwinter.utils.CollectUtils;
import net.fibulwinter.utils.V;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class RectangleBody extends Body{
    private double witdh;
    private double height;

    public RectangleBody(V center, double witdh, double height, double mass, V speed) {
        super(center, mass, speed);
        this.witdh = witdh;
        this.height = height;
    }

    public double getWitdh() {
        return witdh;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public void avoid(V touchPoint) {
/*
        List<V> shifts=newArrayList(
                new V((touchPoint.getX()-getWitdh()/2)-getCenter().getX(),0),
                new V((touchPoint.getX()+getWitdh()/2)-getCenter().getX(),0),
                new V(0, (touchPoint.getY()-getHeight()/2)-getCenter().getY()),
                new V(0, (touchPoint.getY()+getHeight()/2)-getCenter().getY())
        );
        setCenter(getCenter().add(CollectUtils.getMin(shifts, new Function<V, Double>() {
            @Override
            public Double apply(V input) {
                return input.getLength();
            }
        })));
*/
    }
}
