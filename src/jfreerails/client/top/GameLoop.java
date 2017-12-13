package jfreerails.client.top;

import java.awt.Toolkit;
import jfreerails.client.common.ScreenHandler;
import jfreerails.client.common.SynchronizedEventQueue;
import jfreerails.util.GameModel;


/**
 * This thread updates the GUI Client window.
 *
 */
final public class GameLoop implements Runnable {
    boolean gameNotDone = false;
    final ScreenHandler screenHandler;
    final static int TARGET_FPS = 60;
    private final GameModel model;
    private Integer loopMonitor = new Integer(0);

    public GameLoop(ScreenHandler s) {
        screenHandler = s;
        model = GameModel.NULL_MODEL;
    }

    public GameLoop(ScreenHandler s, GameModel gm) {
        screenHandler = s;
        model = gm;

        if (null == model) {
            throw new NullPointerException();
        }
    }

    /**
     * Stops the game loop.
     * Blocks until the loop is stopped.
     * Do not call this from inside the game loop!
     */
    public void stop() {
        synchronized (loopMonitor) {
            if (gameNotDone == false) {
                return;
            }

            gameNotDone = false;

            if (Thread.holdsLock(SynchronizedEventQueue.MUTEX)) {
                /*
                 * we might be executing in the event queue so give up the
                 * mutex temporarily to allow the loop to exit
                 */
                try {
                    SynchronizedEventQueue.MUTEX.wait();
                } catch (InterruptedException e) {
                    assert false;
                }
            }

            try {
                loopMonitor.wait();
            } catch (InterruptedException e) {
                assert false;
            }
        }
    }

    public void run() {
	long frameStartTime;
	int currentFrame = 0;
        gameNotDone = true;

        long baseTime = System.currentTimeMillis();

        /*
         * Reduce this threads priority to avoid starvation of the input thread
         * on Windows.
         */
        try {
            Thread.currentThread().setPriority(Thread.NORM_PRIORITY - 1);
        } catch (SecurityException e) {
            System.err.println("Couldn't lower priority of redraw thread");
        }

        while (true) {
            if (!screenHandler.isMinimised()) {
                /*
                 * Flush all redraws in the underlying toolkit.  This reduces
                 * X11 lag when there isn't much happening, but is expensive
                 * under Windows
                 */
                Toolkit.getDefaultToolkit().sync();

		frameStartTime = System.currentTimeMillis();
                synchronized (SynchronizedEventQueue.MUTEX) {
                    if (!gameNotDone) {
                        SynchronizedEventQueue.MUTEX.notify();

                        break;
                    }

                    if (model != null) {
                        model.update();
                    }
                }

		/*
		 * redraw the screen
		 */
		screenHandler.update();

		if (frameStartTime - baseTime > 10000) {
		    baseTime = frameStartTime;
		    currentFrame = 0;
		}
		currentFrame++;
		long nextFrameTime = baseTime + (1000 * currentFrame /
			TARGET_FPS);
		long timeToSleep = (nextFrameTime - System.currentTimeMillis());
		if (timeToSleep < 0) {
		    /* We dropped some frames */
		    currentFrame -= (timeToSleep * 60 / 1000);
		    timeToSleep = 1;
		}

		try {
		    Thread.sleep(timeToSleep);
		} catch (Exception e) {
		    e.printStackTrace();
		}
            } else {
                try {
                    //The window is minimised
                    Thread.sleep(200);
                } catch (Exception e) {
                }
            }
        }

        /* signal that we are done */
        synchronized (loopMonitor) {
            loopMonitor.notify();
        }
    }
}
