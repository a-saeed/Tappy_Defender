package gamecodeschool.com;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

public class TDView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private boolean gameEnded;
    Thread gameThread = null;
    private Context context;

    /* game objects */
    private PlayerShip player;
    private EnemyShip enemy1;
    private EnemyShip enemy2;
    private EnemyShip enemy3;
    private EnemyShip enemy4;
    private EnemyShip enemy5;
    public ArrayList<SpaceDust> dustList = new ArrayList<SpaceDust>();

    /* for drawing */
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder holder;

    /* in-game info (HUD) */
    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;
    private int screenX;
    private int screenY;

    /* in-game sound effects */
    private SoundPool soundPool;
    int start = -1;
    int bump = -1;
    int destroyed = -1;
    int win = -1;

    /* saving and loading data to local files */
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public TDView(final Context context , int x , int y) {
        super(context);
        this.context = context;

        /* initialize drawing objects */
        holder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;

        /*
        initializing sounds
        this SoundPool is deprecated
        */
        soundPool = new SoundPool(10 , AudioManager.STREAM_MUSIC , 0);

        try {
            //create objects for the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            //create our FX in memory ready for use
            descriptor = assetManager.openFd("start.ogg");
            start = soundPool.load(descriptor , 0);

            descriptor = assetManager.openFd("win.ogg");
            win = soundPool.load(descriptor , 0);

            descriptor = assetManager.openFd("destroyed.ogg");
            destroyed = soundPool.load(descriptor , 0);

            descriptor = assetManager.openFd("bump.ogg");
            bump = soundPool.load(descriptor , 0);

        } catch (IOException e)
        {
            Log.e("error" , "failed to load sound files") ;
        }

        /*dealing with local files*/

        //get a reference to a file called HiScores
        //if id doesn't exist create one
        prefs = context.getSharedPreferences("HiScores",
            Context.MODE_PRIVATE);

        //initialize the editor ready
        editor = prefs.edit();

        //load fastest time from an entry in the file
        //labeled "fastestTime
        //if not available highScore = 1000000
        fastestTime = prefs.getLong("fastestTime" , 1000000);

        startGame();
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
        boolean hitDetected = false;

        if (Rect.intersects(player.getHitBox() , enemy1.getHitBox())){
            hitDetected = true;
            enemy1.setX(-100);
        }

        if (Rect.intersects(player.getHitBox() , enemy2.getHitBox())){
            hitDetected = true;
            enemy2.setX(-100);
        }

        if (Rect.intersects(player.getHitBox() , enemy3.getHitBox())){
            hitDetected = true;
            enemy3.setX(-100);
        }

        if (screenX > 1000){
            if (Rect.intersects(player.getHitBox() , enemy4.getHitBox())){
                hitDetected = true;
                enemy4.setX(-100);
            }
        }

        if (screenX > 1200){
            if (Rect.intersects(player.getHitBox() , enemy5.getHitBox())){
                hitDetected = true;
                enemy5.setX(-100);
            }
        }

        if (hitDetected)
        {
            soundPool.play(bump , 1 , 1 , 0 , 0 , 1);
            player.reduceShieldStrength();
            if (player.shieldStrength < 0) {
                //game over so do something
                soundPool.play(destroyed , 1 , 1 , 0 , 0 , 1);
                gameEnded = true;
            }
        }

        //update the player
        player.update();

        //update the enemy
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());

        if (screenX > 1000)
            enemy4.update(player.getSpeed());

        if (screenX > 1200)
            enemy5.update(player.getSpeed());

        //update the space dust
        for (SpaceDust spec : dustList)
        {
            spec.update(player.getSpeed());
        }

        //if player didn't crash or hasn't yet reached home
        if (! gameEnded)
        {
            //subtract distance to home planet based on current speed
            distanceRemaining -= player.getSpeed();

            //how long has the player been flying
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        //completed the game
        if (distanceRemaining < 0)
        {
            soundPool.play(win , 1 , 1 , 0 , 0 , 1);

            //check for new fastest time
            if (timeTaken < fastestTime) {
                //save highScore
                editor.putLong("fastestTime" , timeTaken);
                editor.commit();
                fastestTime = timeTaken;
            }

            //avoid -ve numbers in the HUD
            distanceRemaining = 0;

            //now end the game
            gameEnded =true;
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

            /*=====================================================================
            for debugging
            draw a rectangle around ships to visualize hit boxes
            switch to white pixels
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
            draw white specs of dust
            ==========================================================================
            */

            for (SpaceDust spec : dustList)
            {
                canvas.drawPoint(spec.getX() , spec.getY() , paint);
            }

            //draw the player & enemy ships
            canvas.drawBitmap(player.getBitmap() , player.getX() , player.getY() , paint);
            canvas.drawBitmap(enemy1.getBitmap() , enemy1.getX() , enemy1.getY() , paint);
            canvas.drawBitmap(enemy2.getBitmap() , enemy2.getX() , enemy2.getY() , paint);
            canvas.drawBitmap(enemy3.getBitmap() , enemy3.getX() , enemy3.getY() , paint);

            if (screenX > 1000)
                canvas.drawBitmap(enemy4.getBitmap() , enemy4.getX() , enemy4.getY() , paint);

            if (screenX > 1200)
                canvas.drawBitmap(enemy5.getBitmap() , enemy5.getX() , enemy5.getY() , paint);

            //displaying the HUD
            if (! gameEnded)
               playingHUD();

            else
                //this happens when the game is ended
                gameOverHUD();


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
                //if we're currently in the pause screen , start a new game
                if (gameEnded)
                    startGame();
                break;
        }

        return true;
    }

    //start the game and/or restart it whenever the the player dies
    private void startGame() {
        /* initialize game objects */
        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);

        //scale up the size of two new enemyShips based on given resolution
        if (screenX > 1000) {
            enemy4 = new EnemyShip(context, screenX, screenY);
            enemy4.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy3));
        }

        if (screenX > 1200) {
            enemy5 = new EnemyShip(context, screenX, screenY);
            enemy5.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy4));
        }


        /* initialize SpaceDust */
        int numSpecs = 1000;

        for (int i = 0 ; i < numSpecs ; i++ )
        {
            //where will the dust spawn?
            //avoid adding +500 of dust every time the game restarts
            if (dustList.size() < 1000)
            {
                SpaceDust spec = new SpaceDust(screenX, screenY);
                dustList.add(spec);
            }
        }

        /* reset time and distance */
        distanceRemaining = 10000; //10km
        timeTaken = 0;

        /* get start time */
        timeStarted = System.currentTimeMillis();

        gameEnded = false;

        soundPool.play(start , 1 , 1 , 0 , 0 ,1);
    }

    public void playingHUD()
    {
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.argb(255, 255, 255, 255));
        paint.setTextSize(25);

        canvas.drawText("Fastest:" + fastestTime + "s", 10, 20, paint);
        canvas.drawText("Time:" + timeTaken + "s", screenX / 2, 20, paint);

        canvas.drawText("Distance:" +
                        distanceRemaining / 1000 + "KM",
                screenX / 3, screenY - 20, paint);

        canvas.drawText("Shield:" +
                        player.getShieldStrength(),
                10, screenY - 20, paint);

        canvas.drawText("Speed:" +
                player.getSpeed() * 60 +
                "MPS", (screenX / 3) * 2, screenY - 20, paint);
    }

    public void gameOverHUD()
    {
        //show pause screen
        paint.setTextSize(80);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Game over" , screenX / 2 , 100 , paint);

        paint.setTextSize(25);
        canvas.drawText("Fastest:" + fastestTime + "s",
                screenX / 2 , 160 , paint);

        canvas.drawText("Time:" + timeTaken + "s" ,
                screenX / 2 , 200 , paint);

        canvas.drawText("Distance remaining" + distanceRemaining / 1000 + "KM",
                screenX / 2 , 240 , paint);

        paint.setTextSize(80);
        canvas.drawText("Tap to replay!" , screenX / 2 , 350 , paint);
    }

}

