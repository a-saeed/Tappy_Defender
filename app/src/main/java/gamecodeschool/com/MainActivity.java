package gamecodeschool.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* prepare to load fastest time */
        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        prefs = getSharedPreferences("HiScores" , MODE_PRIVATE);

        /* wiring the play button */
        Button buttonPlay = (Button)findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this , GameActivity.class);
                startActivity(i);
                finish();
            }
        });

        /* wiring the textView */
        final TextView textFastestTime = (TextView)findViewById(R.id.textHighScore);

        //load fastest time
        //if not available then default = 1000000
        long fastestTime = prefs.getLong("fastestTime" , 1000000);

        //put high score in textView
        textFastestTime.setText("Fastest Time:" + fastestTime);

    }
}
