package gamecodeschool.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class PlayerShip {

    public int shieldStrength;
    private Bitmap bitmap;
    private int x , y ;
    private int speed ;
    private boolean boosting;

    private final int GRAVITY = -12;

    //stop the ship from leaving the screen
    private int minY;
    private int maxY;

    //limit the bounds of the ship's speed
    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

    //a HitBox for collision detection
    private Rect hitBox;


    public PlayerShip(Context context , int screenX , int screenY)
    {
        x = 50;
        y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources() , R.drawable.ship);
        shieldStrength = 2;

        //initially the ship is not boosting
        boosting=false;

        //initialize max and min y
        minY = 0;
        maxY = screenY - bitmap.getHeight();

        //initialize the hit box
        hitBox = new Rect(x , y , bitmap.getWidth() , bitmap.getHeight());
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public int getShieldStrength() {
        return shieldStrength;
    }

    public void update() {
        //are we boosting ?
        if (boosting)
            //speed up
            speed += 3;
        else
            //slow down
            speed -= 5;

        //constrain top speed
        if (speed > MAX_SPEED)
            speed = MAX_SPEED;

        //never stop completely
        if (speed < MIN_SPEED)
            speed = MIN_SPEED;

        //move the ship up or down
        y -= speed + GRAVITY;

        //but don't let the ship stray off screen
        if (y < minY)
            y = minY;

        if (y > maxY)
            y = maxY;


        //refresh hit box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    public void setBoosting() {boosting = true;}

    public void stopBoosting() {boosting = false;}

    public void reduceShieldStrength()
    {
        shieldStrength-- ;
    }
}
