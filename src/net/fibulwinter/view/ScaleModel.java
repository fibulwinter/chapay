package net.fibulwinter.view;

import android.graphics.PointF;
import net.fibulwinter.model.Pos;

public class ScaleModel {
    private double scale=1;

    public PointF fromModel(Pos pos){
        return new PointF((float)(pos.getX()*scale),(float)(pos.getY()*scale));
    }

    public float fromModel(double d){
        return (float)(d*scale);
    }
}
