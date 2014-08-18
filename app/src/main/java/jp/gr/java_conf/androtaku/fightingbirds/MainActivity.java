package jp.gr.java_conf.androtaku.fightingbirds;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    PlayGLSurfaceView playGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        playGLSurfaceView = new PlayGLSurfaceView(this);
        setContentView(playGLSurfaceView);
    }

    @Override
    public void onResume(){
        super.onResume();
        playGLSurfaceView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        playGLSurfaceView.onPause();
    }
}
