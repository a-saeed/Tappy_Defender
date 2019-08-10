package gamecodeschool.com;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    TDView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //create an instance of Tappy defender view(TDView)
        //passing "this" as the context of our app
        gameView = new TDView(this);

        //make gameView the view for this activity
        setContentView(gameView);
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
