package net.fibulwinter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.google.common.collect.Iterables;
import net.fibulwinter.geometry.Disk;
import net.fibulwinter.geometry.GeometryStack;
import net.fibulwinter.geometry.Region;
import net.fibulwinter.model.Board;
import net.fibulwinter.model.FlyType;
import net.fibulwinter.model.LinearPlacer;
import net.fibulwinter.model.Placer;
import net.fibulwinter.physic.FrictionModel;
import net.fibulwinter.geometry.Rectangle;
import net.fibulwinter.geometry.V;
import net.fibulwinter.view.ScaleModel;
import net.fibulwinter.view.SkyView;
import net.fibulwinter.view.VBoard;

import java.util.Iterator;

import static com.google.common.collect.Lists.newArrayList;

public class MyActivity extends Activity {
    private SkyView mLunarView;
    private SkyView.SkyThread mLunarThread;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // get handles to the LunarView from XML, and its LunarThread
        mLunarView = (SkyView) findViewById(R.id.lunar);
        mLunarThread = mLunarView.getThread();

        if (savedInstanceState == null) {
            // we were just launched: set up a new game
            mLunarThread.setPause(true);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            // we are being restored: resume a previous game
            mLunarThread.restoreState(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nonnull");
        }

        mLunarThread.setPause(false);

        Iterator<Integer> players= Iterables.cycle(newArrayList(Color.YELLOW, Color.MAGENTA)).iterator();
/*
        Rectangle borders = new Rectangle(-1e10, -1e10, 1e10, 1e10);
        Board board = new Board(borders);
        for(int i=0;i<5;i++){
            Checker checker = new Checker(310/5*i+20, 10+20, 20, 0, 0);
            checker.setColor(players.get(0));
            board.add(checker);
        }
        for(int i=0;i<5;i++){
            Checker checker = new Checker(310/5*i+20, 420-10-20, 20, 0, 0);
            checker.setColor(players.get(1));
            board.add(checker);
        }
*/
//        Rectangle borders = new Rectangle(10, 10, 780, 1100);
        Rectangle borders = new Rectangle(new V(160,205), 300, 410);
        GeometryStack geometryStack = new GeometryStack();
//        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.5, 0.5), 100), Color.MAGENTA).friction(-1));
        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.5, 0.5), 300), Color.GREEN).flyType(FlyType.GRASS));
/*
        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.5, 0.5), 30), Color.RED).flyType(FlyType.PIT));
        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.25, 0.70), 50), Color.BLUE).flyType(FlyType.WATER));
        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.75, 0.70), 50), Color.BLUE).flyType(FlyType.WATER));
        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.25, 0.3), 50), Color.BLUE).flyType(FlyType.WATER));
        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.75, 0.3), 50), Color.BLUE).flyType(FlyType.WATER));
*/
        FrictionModel frictionModel = new FrictionModel(0.5, geometryStack);
//        Placer placer = new RandomPlacer(10, 20, players);
        Placer placer = new LinearPlacer(2, 20, players);
        Board board = new Board(borders, Board.BouncingMode.BOUNCE, geometryStack, frictionModel, placer);
//        frictionModel.getRegions().add(new FrictionModel.Region(
//                new Disk(new V(borders.getMidX(), borders.getMaxY()), borders.getWidth()/2), 3, Color.YELLOW));
//        frictionModel.getRegions().add(new FrictionModel.Region(
//                new Disk(new V(borders.getMidX(), borders.getMinY()), borders.getWidth()/2), -1, Color.MAGENTA));
/*
        Rectangle borders = new Rectangle(10, 10, 310, 420);
        Board board = new Board(borders);
        for(int i=0;i<10;i++){
            double r = rand(10,20);
            V pos=randomPos(board,r);
            Checker checker = new Checker(pos.getX(), pos.getY(), r, 0, 0*/
/*rand(-5, 5), rand(-5, 5)*//*
);
            board.add(checker);
        }
*/
//        board.add(new Checker(100, 100, 20, 0,10));
//        board.add(new Checker(130,200, 20,0,0));
//        board.add(new Checker(52,52, 40,0,0));
        final ScaleModel scaleModel = new ScaleModel(borders);
        final VBoard vBoard = new VBoard(board, scaleModel, players);
        mLunarView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, final MotionEvent motionEvent) {
                final V pos = scaleModel.fromView(motionEvent.getX(), motionEvent.getY());
                mLunarThread.doInThread(new Runnable() {
                    public void run() {
                        vBoard.click(pos, motionEvent.getAction());
                    }
                });
                return true;
            }
        });


        mLunarThread.setModelVisualizer(board, vBoard);
    }

/*    private void reset() {
        skyController.reset();
        mLunarThread.setPause(false);
    }*/

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mLunarView.getThread().setPause(true); // pause game when Activity pauses
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLunarView.getThread().setPause(false); // resume game when Activity resumes
    }

    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state.
     *
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        mLunarThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }
}
