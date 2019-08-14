package gamecodeschool.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PlayerShip {

    private Bitmap bitmap;
    private int x , y ;
    private int speed = 0;
    private boolean boosting;

    public PlayerShip(Context context)
    {
        x = 50;
        y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources() , R.drawable.ship);
        //initially the ship is not boosting
        boosting=false;
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

    public void update() {
        x+=5;
    }

    public void setBoosting() {boosting = true;}

    public void stopBoosting() {boosting = false;}
}
