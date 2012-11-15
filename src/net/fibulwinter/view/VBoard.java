package net.fibulwinter.view;

import android.graphics.*;
import android.view.MotionEvent;
import com.google.common.collect.Iterables;
import net.fibulwinter.geometry.Disk;
import net.fibulwinter.geometry.LineSegment;
import net.fibulwinter.geometry.Shape;
import net.fibulwinter.model.Board;
import net.fibulwinter.model.Checker;
import net.fibulwinter.physic.*;
import net.fibulwinter.geometry.Region;
import net.fibulwinter.geometry.Rectangle;
import net.fibulwinter.geometry.V;
import net.fibulwinter.utils.ColorUtils;
import net.fibulwinter.utils.RandUtils;

import java.util.Iterator;
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
        if(RandUtils.getBoolean()){
            actingColor=players.next();
        }
    }

    @Override
    public void doDraw(Canvas canvas) {
        scaleModel.updateViewSize(canvas.getWidth(), canvas.getHeight());
        clearCanvas(canvas);
//        drawBorder(canvas);
        drawFrictions(canvas);
        drawObstacles(canvas);
        Paint paint = new Paint();
        for(Checker checker:board.getCheckers()){
            paint.setColor(getColor(checker));
            Disk disk = checker.getDisk();
            PointF centerPoint = scaleModel.fromModel(disk.getCenter());
            float r = scaleModel.fromModel(disk.getRadius());
            canvas.drawOval(new RectF(centerPoint.x-r,centerPoint.y-r,centerPoint.x+r,centerPoint.y+r), paint);
        }
    }

    private void drawBorder(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        Rectangle borders = board.getBorders();
        PointF minPoint = scaleModel.fromModel(borders.getRelative(0,0));
        PointF maxPoint = scaleModel.fromModel(borders.getRelative(1,1));
        canvas.drawRect(minPoint.x,minPoint.y,maxPoint.x-1,maxPoint.y-1,paint);
    }

    private void drawObstacles(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        for(Body body:board.getContinuum().getBodies()){
            if(body instanceof StaticBody){
                Shape shape = body.getShape();
                if(shape instanceof LineSegment){
                    LineSegment lineObstacle = (LineSegment) shape;
                    PointF pf1 = scaleModel.fromModel(lineObstacle.getP1());
                    PointF pf2 = scaleModel.fromModel(lineObstacle.getP2());
                    canvas.drawLine(pf1.x,pf1.y,pf2.x,pf2.y,paint);
                }
            }
        }
    }

    private void drawFrictions(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        for(Region region:board.getGeometryStack().getRegions()){
            Disk disk = region.getShape();
            PointF c = scaleModel.fromModel(disk.getCenter());
            float r = scaleModel.fromModel(disk.getRadius());
            paint.setColor(region.getColor());
            canvas.drawOval(new RectF(c.x-r,c.y-r,c.x+r,c.y+r), paint);
        }
    }

    private int getColor(Checker checker){
        if(actingColor==checker.getColor() && !board.isAnyMoving()){
            return actingColor;
        }else{
            return ColorUtils.createDisabledColor(checker.getColor());
        }
    }

    private void clearCanvas(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(), paint);
    }

    private V startPos=null;

    public void click(V pos, int action) {
        if(board.isAnyMoving())return;
        Set<Integer> remainingColors = board.remainingColors();
        if(remainingColors.size()<2){
            board.regenerate();
            if(remainingColors.size()==1){
                actingColor=Iterables.getOnlyElement(remainingColors);
            }
            while (players.next()!=actingColor);
            return;
        }
        if (action == MotionEvent.ACTION_DOWN) {
            startPos=pos;
        }else if (action == MotionEvent.ACTION_UP && startPos!=null) {
            V subtract = pos.subtract(startPos);
            Checker closest=board.getClosest(actingColor, startPos, 100);
            if(closest!=null){
                if(subtract.getLength()>20){
                    closest.getDynamicBody().setSpeed(subtract.scale(0.1).limitLength(30));
                    actingColor=players.next();
                }
            }
            startPos=null;
        }
    }
}
