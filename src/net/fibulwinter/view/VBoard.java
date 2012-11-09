package net.fibulwinter.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import net.fibulwinter.model.Board;
import net.fibulwinter.model.Checker;

public class VBoard implements IVisualizer{

    private Board board;
    private ScaleModel scaleModel=new ScaleModel();

    public VBoard(Board board) {
        this.board = board;
    }

    @Override
    public void doDraw(Canvas canvas) {
        clearCanvas(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        for(Checker checker:board.getCheckers()){
            PointF centerPoint = scaleModel.fromModel(checker.getPos());
            float r = scaleModel.fromModel(checker.getRadius());
            canvas.drawOval(new RectF(centerPoint.x-r,centerPoint.y-r,centerPoint.x+r,centerPoint.y+r), paint);
        }
    }

    private void clearCanvas(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(), paint);
    }
}
