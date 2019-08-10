package gamecodeschool.com;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    TDView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
