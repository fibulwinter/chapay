package net.fibulwinter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.google.common.collect.Iterables;
import net.fibulwinter.model.Board;
import net.fibulwinter.model.Checker;
import net.fibulwinter.model.Rectangle;
import net.fibulwinter.model.V;
import net.fibulwinter.view.ScaleModel;
import net.fibulwinter.view.SkyView;
import net.fibulwinter.view.VBoard;

import java.util.Iterator;
import java.util.List;

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

        Iterator<Integer> players= Iterables.cycle(newArrayList(Color.GREEN, Color.RED)).iterator();
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
        Rectangle borders = new Rectangle(10, 10, 310, 420);
        Board board = new Board(borders, Board.BouncingMode.PASS);
        board.generate(20, 10, players);
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
        final ScaleModel scaleModel = new ScaleModel();
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
