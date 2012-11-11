package net.fibulwinter.view;

import android.graphics.PointF;
import net.fibulwinter.utils.Rectangle;
import net.fibulwinter.utils.V;

public class ScaleModel {
    private double scale=1;
    private double vdx=0;
    private double vdy=0;
    private final Rectangle modelBorder;

    public ScaleModel(Rectangle modelBorder) {
        this.modelBorder = modelBorder;
    }

    public void updateViewSize(int vw, int vh) {
        scale=Math.max(1e-5,Math.min(vw/modelBorder.getWidth(),vh/modelBorder.getHeight()));
        vdx=(vw-modelBorder.getWidth()*scale)/2;
        vdy=(vh-modelBorder.getHeight()*scale)/2;
    }

    public PointF fromModel(V mv){
        return new PointF((float)(vdx+(mv.getX()-modelBorder.getMinX())*scale),
                (float)(vdy+(mv.getY()-modelBorder.getMinY())*scale));
    }

    public float fromModel(double d){
        return (float)(d*scale);
    }

    public V fromView(float x, float y) {
        return new V((x-vdx)/scale+modelBorder.getMinX(),(y-vdy)/scale+modelBorder.getMinY());
    }
}
