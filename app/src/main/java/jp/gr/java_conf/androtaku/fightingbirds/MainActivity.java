package jp.gr.java_conf.androtaku.fightingbirds;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    PlayGLSurfaceView playGLSurfaceView;
    Button button;

    private int START = 0;
    private int PLAY = 1;
    private int Mode = START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT > 10) {
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.start_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mode = PLAY;
                playGLSurfaceView = new PlayGLSurfaceView(MainActivity.this);
                setContentView(playGLSurfaceView);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        if(Mode == PLAY) {
            playGLSurfaceView.onResume();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(Mode == PLAY) {
            playGLSurfaceView.onPause();
        }
    }
}
