package gamecodeschool.com;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    TDView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /**getting screen details information */
        //get a display object to access the screen details.
        Display display = getWindowManager().getDefaultDisplay();
        //load the resolution into a point object
        Point size = new Point();
        display.getSize(size);

        /**setting the view */
        //create an instance of Tappy defender view(TDView)
        //passing "this" as the context of our app
        //also passing the screen resolution to the constructor
        gameView = new TDView(this , size.x , size.y);

        //make gameView the view for this activity
        setContentView(gameView);

        // Hide the status bar and other OS-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    //if activity is paused , pause the thread
    @Override
    public void onPause()
    {
        super.onPause();
        gameView.pause();
    }

    //if activity is resumed , resume the thread
    @Override
    public void onResume()
    {
        super.onResume();
        gameView.resume();
    }
}
