package jp.gr.java_conf.androtaku.fightingbirds;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.GameHelper;


public class MainActivity extends Activity {

    GameHelper gameHelper;

    PlayGLSurfaceView playGLSurfaceView;

    private int START = 0;
    private int PLAY = 1;
    private int RESULT = 2;
    private int Mode = START;

    private boolean isSignIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT > 10) {
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }

        setStart();

    }

    public void setStart(){
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.start_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGame();
            }
        });

        final GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
            @Override
            public void onSignInFailed() {

            }

            @Override
            public void onSignInSucceeded() {
                isSignIn = true;
            }
        };
        gameHelper = new GameHelper(MainActivity.this,GameHelper.CLIENT_GAMES);
        gameHelper.setup(gameHelperListener);

        SignInButton signInButton = (SignInButton)findViewById(R.id.sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameHelper.beginUserInitiatedSignIn();
            }
        });
    }

    public void setGame(){
        Mode = PLAY;
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gameView = layoutInflater.inflate(R.layout.play_layout, null, false);
        setContentView(gameView);
        playGLSurfaceView = (PlayGLSurfaceView)gameView.findViewById(R.id.play_view);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(Mode == PLAY) {
            //playGLSurfaceView.onResume();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(Mode == PLAY) {
           // playGLSurfaceView.onPause();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isSignIn) {
            gameHelper.onStart(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isSignIn) {
            gameHelper.onStop();
        }
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if(isSignIn) {
            gameHelper.onActivityResult(request, response, data);
        }
    }

}
