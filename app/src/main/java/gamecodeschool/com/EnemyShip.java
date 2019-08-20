package gamecodeschool.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

public class EnemyShip {
    private Bitmap bitmap;
    private int x , y;
    private int speed;

    //detect enemies leaving the screen
    private int maxX;
    private int minX;

    //respawn enemies within screen bounds
    private int maxY;
    private int minY;

    //a HitBox for collision detection
    private Rect hitBox;

    public EnemyShip(Context context , int screenX , int screenY)
    {
        Random generator = new Random();

        //which enemy ship to appear on screen
        int whichBitmap = generator.nextInt(3);
        switch (whichBitmap) {
            case 0:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
                break;

            case 1:
                bitmap = BitmapFactory.decodeResource(context.getResources() , R.drawable.enemy1);
                break;

            case 2:
                bitmap = BitmapFactory.decodeResource(context.getResources() , R.drawable.enemy2);
                break;

        }
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        speed = generator.nextInt(6) + 10;

        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();

        //initialize the hit box
        hitBox = new Rect(x , y , bitmap.getWidth() , bitmap.getHeight());
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    //this is used by the TDView's update method to
    //make an enemy out of bounds and force a re-spawn
    public void setX(int x) {this.x = x;}

    public int getY() {
        return y;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public void update(int playerSpeed)
    {
        //move to the left
        x -= playerSpeed;
        x -= speed;

        //respawn when off screen
        if (x < minX - bitmap.getHeight())
        {
            Random generator = new Random();
            speed = generator.nextInt(10) + 10;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }

        //refresh hit box location
        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }
}
