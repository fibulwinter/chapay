package net.fibulwinter.view;

import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import net.fibulwinter.R;
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

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

public class VBoard implements IVisualizer{

    private Board board;
    private ScaleModel scaleModel;
    private final Iterator<Integer> players;
    private int actingColor;
    private long pressedTime;

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
//        drawFrictions(canvas);
        drawObstacles(canvas);
        ArrayList<Checker> checkers = newArrayList(board.getCheckers());
        Collections.sort(checkers, new Comparator<Checker>() {
            @Override
            public int compare(Checker checker, Checker checker1) {
                return Double.compare(checker.getDynamicBody().getCenter().getY(),checker1.getDynamicBody().getCenter().getY());
            }
        });
        for(Checker checker:checkers){
            int imageResource = checker.getColor() == Color.BLACK ? R.drawable.black2 : R.drawable.white2;
            //            paint.setColor(getColor(checker));

            Disk disk = checker.getDisk();
            PointF centerPoint = scaleModel.fromModel(disk.getCenter());
            float rx = scaleModel.fromModelWidth(disk.getRadius());
            float ry = scaleModel.fromModelHeight(disk.getRadius());
//            canvas.drawOval(new RectF(centerPoint.x-r,centerPoint.y-r,centerPoint.x+r,centerPoint.y+r), paint);
/*
            if(actingColor==checker.getColor() && !board.isAnyMoving()){
                float k = 1.4f;
                canvas.drawBitmap(ImageCache.get().get(R.drawable.sel,
                        (int) scaleModel.fromModelWidth(checker.getDisk().getRadius() * 2*k),
                        (int) scaleModel.fromModelHeight(checker.getDisk().getRadius() * 2*k)
                ), centerPoint.x - rx* k, centerPoint.y - ry* k, null);
            }
*/
            Paint paint = new Paint();
            if(actingColor==checker.getColor() && !board.isAnyMoving()){
                paint.setColor(Color.RED);
                ColorFilter filter = new LightingColorFilter(Color.RED, 1);
                paint.setColorFilter(filter);
            }else{
            }
            Bitmap bitmap = ImageCache.get().get(imageResource, (int) rx * 2);
            canvas.drawBitmap(bitmap, centerPoint.x - rx, centerPoint.y + ry-bitmap.getHeight(), paint);
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
        Rectangle borders = board.getLevel().getBorders();
        Bitmap logicalBitmap=board.getLevel().getLogicalBitmap();
//        canvas.drawBitmap(logicalBitmap, 0,0, null);

        PointF pMin = scaleModel.fromModel(borders.getRelative(0,0));
        PointF pMax = scaleModel.fromModel(borders.getRelative(1,1));
        canvas.drawBitmap(logicalBitmap, new Rect(0,0,logicalBitmap.getWidth(),logicalBitmap.getHeight()),
                new Rect((int)pMin.x,(int)pMin.y,(int)pMax.x,(int)pMax.y), null);
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
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        Rectangle borders = board.getBorders();
        PointF pMin = scaleModel.fromModel(borders.getRelative(0, 0));
        PointF pMax = scaleModel.fromModel(borders.getRelative(1, 1));
        Rect viewRect = new Rect((int) pMin.x, (int) pMin.y, (int) pMax.x, (int) pMax.y);

        Bitmap bitmap = ImageCache.get().get(R.drawable.field3, viewRect.width(), viewRect.height());
        canvas.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),
                viewRect, null);
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
            pressedTime = System.currentTimeMillis();
        }else if (action == MotionEvent.ACTION_UP && startPos!=null) {
            V subtract = pos.subtract(startPos);
            double timeSec = (System.currentTimeMillis() - pressedTime+1)/1000.0;
            Checker closest=board.getClosest(actingColor, startPos, 100);
            if(closest!=null){
                if(subtract.getLength()>Checker.RADIUS){
                    double speed=subtract.getLength()/timeSec*0.03;
                    if(speed>5){
                        Log.e("Chapay","speed="+speed);
                        closest.getDynamicBody().setVelocity(subtract.normal().scale(speed).limitLength(40));
                        actingColor=players.next();
                    }
                }
            }
            startPos=null;
        }
    }
}
