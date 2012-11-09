package net.fibulwinter;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import net.fibulwinter.R;
import net.fibulwinter.model.Board;
import net.fibulwinter.model.Checker;
import net.fibulwinter.model.Pos;
import net.fibulwinter.view.IModel;
import net.fibulwinter.view.IVisualizer;
import net.fibulwinter.view.SkyView;
import net.fibulwinter.view.VBoard;

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

        Board board = new Board();
        Checker checker1 = new Checker(new Pos(100, 100), 20);
        checker1.getSpeed().setXY(1,2);
        board.add(checker1);
        board.add(new Checker(new Pos(200,400), 10));
        board.add(new Checker(new Pos(0,0), 40));
        VBoard vBoard = new VBoard(board);

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
