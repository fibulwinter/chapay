package net.fibulwinter.view;

import android.graphics.PointF;
import net.fibulwinter.geometry.Rectangle;
import net.fibulwinter.geometry.V;

public class ScaleModel {
    private static final double SCALE_X=1;
    private static final double SCALE_Y=0.75;
    private double scale=1;
    private double vdx=0;
    private double vdy=0;
    private final Rectangle modelBorder;

    public ScaleModel(Rectangle modelBorder) {
        this.modelBorder = modelBorder;
    }

    public void updateViewSize(int vw, int vh) {
        scale=Math.max(1e-5,Math.min(vw/(modelBorder.getWidth()*SCALE_X),vh/(modelBorder.getHeight()*SCALE_Y)));
        vdx=(vw-modelBorder.getWidth()*SCALE_X*scale)/2;
        vdy=(vh-modelBorder.getHeight()*SCALE_Y*scale)/2;
    }

    public PointF fromModel(V mv){
        return new PointF((float)(vdx+(mv.getX()-modelBorder.getMinX())*SCALE_X*scale),
                (float)(vdy+(mv.getY()-modelBorder.getMinY())*SCALE_Y*scale));
    }

    public float fromModelWidth(double dx){
        return (float)(dx*SCALE_X*scale);
    }

    public float fromModelHeight(double dy){
        return (float)(dy*SCALE_Y*scale);
    }

    public V fromView(float x, float y) {
        return new V(((x-vdx)/scale+modelBorder.getMinX())/SCALE_X,((y-vdy)/scale+modelBorder.getMinY())/SCALE_Y);
    }
}
