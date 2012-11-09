package net.fibulwinter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * View that draws, takes keystrokes, etc. for a simple LunarLander game.
 * <p/>
 * Has a mode which RUNNING, PAUSED, etc. Has a x, y, dx, dy, ... capturing the
 * current ship physics. All x/y etc. are measured with (0,0) at the lower left.
 * updatePhysics() advances the physics based on realtime. draw() renders the
 * ship, and does an invalidate() to prompt another draw() as soon as possible
 * by the system.
 */
public class SkyView extends SurfaceView implements SurfaceHolder.Callback {
    public class SkyThread extends Thread {
        private SurfaceHolder mSurfaceHolder;

        private boolean mPaused = true;
        private boolean mRun;

        private IModel model;
        private IVisualizer visualizer;
        private Handler handler;
        private Runnable mainRunnable;

        public SkyThread(SurfaceHolder surfaceHolder) {
// get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mainRunnable = new Runnable() {
                public void run() {
                    Canvas c = null;
                    try {
                        c = mSurfaceHolder.lockCanvas(null);
                        synchronized (mSurfaceHolder) {
                            //todo

                            if (!mPaused && model != null) {
                                model.simulate();
                            }
                            if (visualizer != null) {
                                visualizer.doDraw(c);
                            }
                        }
                    } finally {
                        // do this in a finally so that if an exception is thrown
                        // during the above, we don't leave the Surface in an
                        // inconsistent state
                        if (c != null) {
                            mSurfaceHolder.unlockCanvasAndPost(c);
                        }
                    }
                    if (mRun) {
                        handler.postDelayed(mainRunnable, 20);
                    }
                }
            };
        }

        public void setModelVisualizer(IModel model, IVisualizer visualizer) {
            synchronized (mSurfaceHolder) {
                this.model = model;
                this.visualizer = visualizer;
            }
        }

        public void doInThread(Runnable runnable) {
            handler.post(runnable);
        }

        public void setPause(boolean pause) {
            synchronized (mSurfaceHolder) {
                this.mPaused = pause;
            }
        }

        @Override
        public void run() {
            Looper.prepare();
            handler = new Handler();
            handler.postDelayed(mainRunnable, 20);
            Looper.loop();
        }


        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         *
         * @param b true to run, false to shut down
         */
        private void setRunning(boolean b) {
            Log.e("Chapay",String.valueOf(mRun));
            if (!mRun && b) {
                thread.start();
            } else if (mRun && !b) {
                handler.getLooper().quit();
            }
            mRun = b;
        }

        public void restoreState(Bundle savedInstanceState) {
            //todo later
        }

        public void saveState(Bundle state) {
            //todo later
        }

        public boolean isPaused() {
            return mPaused;
        }
    }

    /**
     * The thread that actually draws the animation
     */
    private SkyThread thread;

    public SkyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new SkyThread(holder);

//        setFocusable(true); // make sure we get key events
    }

    /**
     * Fetches the animation thread corresponding to this LunarView.
     *
     * @return the animation thread
     */
    public SkyThread getThread() {
        return thread;
    }

    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.setPause(true);
    }

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}