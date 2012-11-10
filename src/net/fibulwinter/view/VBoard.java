package net.fibulwinter.view;

import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import net.fibulwinter.model.Board;
import net.fibulwinter.model.Checker;
import net.fibulwinter.model.Rectangle;
import net.fibulwinter.model.V;

public class VBoard implements IVisualizer{

    private Board board;
    private ScaleModel scaleModel;

    public VBoard(Board board, ScaleModel scaleModel) {
        this.board = board;
        this.scaleModel = scaleModel;
    }

    @Override
    public void doDraw(Canvas canvas) {
        clearCanvas(canvas);
        drawBorder(canvas);
        Paint paint = new Paint();
        for(Checker checker:board.getCheckers()){
            paint.setColor(getColor(checker));
            PointF centerPoint = scaleModel.fromModel(checker.getPos());
            float r = scaleModel.fromModel(checker.getRadius());
            canvas.drawOval(new RectF(centerPoint.x-r,centerPoint.y-r,centerPoint.x+r,centerPoint.y+r), paint);
        }
    }

    private void drawBorder(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        Rectangle borders = board.getBorders();
        PointF minPoint = scaleModel.fromModel(new V(borders.getMinX(), borders.getMinY()));
        PointF maxPoint = scaleModel.fromModel(new V(borders.getMaxX(), borders.getMaxY()));
        canvas.drawRect(minPoint.x,minPoint.y,maxPoint.x,maxPoint.y,paint);
    }

    private int getColor(Checker checker){
        float h = (360f*board.getCheckers().indexOf(checker)) / board.getCheckers().size();
        return Color.HSVToColor(new float[]{h,1,1});
    }

    private void clearCanvas(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(), paint);
    }

    private V startPos=null;

    public void click(V pos, int action) {
        if (action == MotionEvent.ACTION_DOWN) {
            startPos=pos;
        }else if (action == MotionEvent.ACTION_UP) {
            Checker closest=null;
            double minD=50;
            for(Checker checker:board.getCheckers()){
                double d = checker.getPos().subtract(startPos).getLength();
                if(d<minD){
                    minD=d;
                    closest=checker;
                }
            }
            if(closest!=null){
                closest.setSpeed(pos.subtract(startPos).scale(0.1));
            }
            startPos=null;
        }
    }
}
