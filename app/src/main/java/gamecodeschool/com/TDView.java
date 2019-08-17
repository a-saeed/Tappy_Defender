package gamecodeschool.com;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class TDView extends SurfaceView implements Runnable {

    volatile boolean playing;
    Thread gameThread = null;

    //game objects
    private PlayerShip player;
    private EnemyShip enemy1;
    private EnemyShip enemy2;
    private EnemyShip enemy3;
    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    //for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder holder;

    public TDView(Context context , int x , int y) {
        super(context);

        //initialize player ship
        player = new PlayerShip(context , x , y);
        enemy1 = new EnemyShip(context , x ,y);
        enemy2 = new EnemyShip(context , x ,y);
        enemy3 = new EnemyShip(context , x ,y);

        //initialize drawing objects
        holder = getHolder();
        paint = new Paint();

        //initialize SpaceDust
        int numSpecs = 500;

        for (int i =0 ; i<numSpecs ; i++ )
        {
            //where will the dust spawn?
            SpaceDust spec = new SpaceDust(x , y);
            dustList.add(spec);
        }
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
        //collision detection on new positions
        //if an enemy ship hits the player
        //instantly transport enemy to a location off of the left of screen
        //EnemyShip class takes care of the rest by re-spawning the enemy.
        if (Rect.intersects(player.getHitBox() , enemy1.getHitBox()))
            enemy1.setX(-100);

        if (Rect.intersects(player.getHitBox() , enemy2.getHitBox()))
            enemy2.setX(-100);

        if (Rect.intersects(player.getHitBox() , enemy3.getHitBox()))
            enemy3.setX(-100);

        //update the player
        player.update();

        //update the enemy
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());

        //update the space dust
        for (SpaceDust spec : dustList)
        {
            spec.update(player.getSpeed());
        }

    }

    private void draw()
    {
        if (holder.getSurface().isValid())
        {
            //first we lock the area of memory we will be drawing to
            canvas = holder.lockCanvas();

            //rub out the last frame (clear the screen)
            canvas.drawColor(Color.argb(255 , 20 , 20 , 35));

            //for debugging
            //draw a rectangle around ships to visualize hit boxes
            //switch to white pixels
            paint.setColor(Color.argb(255 , 255 , 255 , 255));

            canvas.drawRect(player.getHitBox().left ,
                    player.getHitBox().top ,
                    player.getHitBox().right ,
                    player.getHitBox().bottom ,
                    paint);

            canvas.drawRect(enemy1.getHitBox().left ,
                    enemy1.getHitBox().top ,
                    enemy1.getHitBox().right ,
                    enemy1.getHitBox().bottom ,
                    paint);

            canvas.drawRect(enemy2.getHitBox().left ,
                    enemy2.getHitBox().top ,
                    enemy2.getHitBox().right ,
                    enemy2.getHitBox().bottom ,
                    paint);

            canvas.drawRect(enemy3.getHitBox().left ,
                    enemy3.getHitBox().top ,
                    enemy3.getHitBox().right ,
                    enemy3.getHitBox().bottom ,
                    paint);

            //draw white specs of dust
            paint.setColor(Color.argb(255 , 255 , 255 , 255));
            for (SpaceDust spec : dustList)
            {
                canvas.drawPoint(spec.getX() , spec.getY() , paint);
            }

            //draw the player
            canvas.drawBitmap(player.getBitmap() , player.getX() , player.getY() , paint);
            canvas.drawBitmap(enemy1.getBitmap() , enemy1.getX() , enemy1.getY() , paint);
            canvas.drawBitmap(enemy2.getBitmap() , enemy2.getX() , enemy2.getY() , paint);
            canvas.drawBitmap(enemy3.getBitmap() , enemy3.getX() , enemy3.getY() , paint);

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

