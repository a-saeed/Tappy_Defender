package gamecodeschool.com;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TDView extends SurfaceView implements Runnable {

    volatile boolean playing;
    Thread gameThread = null;
    //game object
    private PlayerShip player;
    //for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder holder;

    public TDView(Context context , int x , int y) {
        super(context);
        //initialize player ship
        player = new PlayerShip(context , x , y);
        //initialize drawing objects
        holder = getHolder();
        paint = new Paint();
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
        player.update();
    }

    private void draw()
    {
        if (holder.getSurface().isValid())
        {
            //first we lock the area of memory we will be drawing to
            canvas = holder.lockCanvas();
            //rub out the last frame (clear the screen)
            canvas.drawColor(Color.argb(255 , 50 , 50 , 50));
            //draw the player
            canvas.drawBitmap(player.getBitmap() , player.getX() , player.getY() , paint);
            //unlock and draw the scene
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void control()
    {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e)
        {

        }
    }

    //surfaceView allows us to handle the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        //there are many different events in MotionEvent
        //we care about just 2 - for now
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
        {
            //has the player lifted their finger up?
            case MotionEvent.ACTION_UP:
                //do some
                player.stopBoosting();
                break;

                //has the player touched the screen?
            case MotionEvent.ACTION_DOWN:
                //do some
                player.setBoosting();
                break;
        }

        return true;
    }
}

