package net.fibulwinter;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.google.common.collect.Iterables;
import net.fibulwinter.geometry.Disk;
import net.fibulwinter.geometry.GeometryStack;
import net.fibulwinter.geometry.Region;
import net.fibulwinter.model.*;
import net.fibulwinter.geometry.Rectangle;
import net.fibulwinter.geometry.V;
import net.fibulwinter.view.ImageCache;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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

        Iterator<Integer> players= Iterables.cycle(newArrayList(Color.WHITE, Color.BLACK)).iterator();
        Rectangle borders = new Rectangle(new V(205,160), 410, 300);
        GeometryStack geometryStack = new GeometryStack();
//        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.5, 0.5), 100), Color.MAGENTA).friction(-1));
        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.5, 0.5), 300), Color.parseColor("#7CFC00")).flyType(FlyType.GRASS));
        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.5, 0.5), 30), Color.parseColor("#8B4513")).flyType(FlyType.PIT));
        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.3, 0.75), 40), Color.parseColor("#20B2AA")).flyType(FlyType.WATER));
        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.7, 0.75), 40), Color.parseColor("#20B2AA")).flyType(FlyType.WATER));
        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.3, 0.25), 40), Color.parseColor("#20B2AA")).flyType(FlyType.WATER));
        geometryStack.getRegions().add(new Region(new Disk(borders.getRelative(0.7, 0.25), 40), Color.parseColor("#20B2AA")).flyType(FlyType.WATER));
//        Placer placer = new RandomPlacer(10, 20, players);
        Level level = new Level(borders, geometryStack);
        Placer placer = new LinearPlacer(10, players);
        Board board = new Board(Board.BouncingMode.BOUNCE, level, placer);
//        frictionModel.getRegions().add(new FrictionModel.Region(
//                new Disk(new V(borders.getMidX(), borders.getMaxY()), borders.getWidth()/2), 3, Color.YELLOW));
//        frictionModel.getRegions().add(new FrictionModel.Region(
//                new Disk(new V(borders.getMidX(), borders.getMinY()), borders.getWidth()/2), -1, Color.MAGENTA));
        final ScaleModel scaleModel = new ScaleModel(borders);
        ImageCache.init(getResources());
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
