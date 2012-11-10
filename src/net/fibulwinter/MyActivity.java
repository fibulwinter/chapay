package net.fibulwinter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import net.fibulwinter.model.Board;
import net.fibulwinter.model.Checker;
import net.fibulwinter.model.Rectangle;
import net.fibulwinter.model.V;
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

        Board board = new Board(new Rectangle(10,10,310,420));
        board.add(new Checker(100, 100, 20, 0,10));
        board.add(new Checker(130,200, 20,0,0));
        board.add(new Checker(52,52, 40,0,0));
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
