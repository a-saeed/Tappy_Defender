package gamecodeschool.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PlayerShip {

    private Bitmap bitmap;
    private int x , y ;
    private int speed = 0;

    public PlayerShip(Context context)
    {
        x = 50;
        y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources() , R.drawable.ship);
    }
}
