package net.fibulwinter.view;

import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import com.google.common.collect.Iterables;
import net.fibulwinter.model.Board;
import net.fibulwinter.model.Checker;
import net.fibulwinter.model.Rectangle;
import net.fibulwinter.model.V;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class VBoard implements IVisualizer{

    private Board board;
    private ScaleModel scaleModel;
    private final Iterator<Integer> players;
    private int actingColor;

    public VBoard(Board board, ScaleModel scaleModel, Iterator<Integer> players) {
        this.board = board;
        this.scaleModel = scaleModel;
        this.players = players;
        actingColor=players.next();
        if(Math.random()>0.5){
            actingColor=players.next();
        }
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
        if(actingColor==checker.getColor() && !isInProgress()){
            return actingColor;
        }else{
            float[] hsv = new float[3];
            Color.colorToHSV(checker.getColor(), hsv);
            hsv[1]=0.5f;
            hsv[2]=0.5f;
            return Color.HSVToColor(hsv);
        }
//        float h = (360f*board.getCheckers().indexOf(checker)) / board.getCheckers().size();
//        return Color.HSVToColor(new float[]{h,1,1});
    }

    private void clearCanvas(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(), paint);
    }

    private V startPos=null;

    private boolean isInProgress(){
        for(Checker checker:board.getCheckers()){
            if(checker.getSpeed().getLength()>1){
                return true;
            }
        }
        return false;
    }

    public void click(V pos, int action) {
        if(isInProgress())return;
        Set<Integer> remainers = board.remainingColors();
        if(remainers.size()<2){
            board.generate(board.getCheckers().size(),players);
            return;
        }
        if (action == MotionEvent.ACTION_DOWN) {
            startPos=pos;
        }else if (action == MotionEvent.ACTION_UP && startPos!=null) {
            Checker closest=null;
            double minD=100;
            for(Checker checker:board.getCheckers()){
                if(checker.getColor()==actingColor){
                    double d = checker.getPos().subtract(startPos).getLength();
                    if(d<minD){
                        minD=d;
                        closest=checker;
                    }
                }
            }
            if(closest!=null){
                closest.setSpeed(pos.subtract(startPos).scale(0.1));
                actingColor=players.next();
            }
            startPos=null;
        }
    }
}
