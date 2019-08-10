package gamecodeschool.com;

import android.content.Context;
import android.view.SurfaceView;

public class TDView extends SurfaceView implements Runnable {

    volatile boolean playing;
    Thread gameThread = null;

    public TDView(Context context) {
        super(context);
    }


    @Override
    public void run() {
        while (playing){
            update();
            draw();
            control();
        }
    }

    //clean up our thread when the game is interrupted
    //or the player quits
    public void pause()
    {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e)
        {

        }
    }

    //make a new thread and start it
    //execution moves to R
    public void resume()
    {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void update()
    {

    }

    private void draw()
    {

    }

    private void control()
    {

    }
}

