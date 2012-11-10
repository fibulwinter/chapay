package net.fibulwinter.view;

import android.graphics.PointF;
import net.fibulwinter.model.V;

public class ScaleModel {
    private double scale=1;

    public PointF fromModel(V v){
        return new PointF((float)(v.getX()*scale),(float)(v.getY()*scale));
    }

    public float fromModel(double d){
        return (float)(d*scale);
    }
}
