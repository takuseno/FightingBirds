package jp.gr.java_conf.androtaku.fightingbirds;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.GameHelper;

import java.io.IOException;


public class MainActivity extends Activity {

    GameHelper gameHelper;

    PlayGLSurfaceView playGLSurfaceView;
    MediaPlayer bgm;

    private int START = 0;
    private int PLAY = 1;
    private int Mode = START;

    private boolean isSignIn = false;
    private boolean isBGMInitialized = false;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT > 10) {
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }
        else{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        prefs = getSharedPreferences("preference",MODE_PRIVATE);
        editor = prefs.edit();
        if(prefs.getBoolean("first_activated",true)){
            showGuide();
        }

        bgm = MediaPlayer.create(this,R.raw.result_bgm);
        bgm.setLooping(true);
        bgm.setVolume(0.2f,0.2f);
        bgm.start();
        isBGMInitialized = true;

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
                editor.putBoolean("signIn",true);
                editor.commit();
            }
        };
        gameHelper = new GameHelper(MainActivity.this,GameHelper.CLIENT_GAMES);
        gameHelper.setMaxAutoSignInAttempts(0);
        gameHelper.setup(gameHelperListener);
        if(prefs.getBoolean("signIn",false)){
            gameHelper.beginUserInitiatedSignIn();
        }

        Button rankingButton = (Button)findViewById(R.id.ranking_button);
        rankingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameHelper.isSignedIn()){
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
                            getString(R.string.lb_id)), 5001);
                }
                else{
                    gameHelper.beginUserInitiatedSignIn();
                }
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

    public void showGuide(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean("first_activated",false);
                        editor.commit();
                    }
                })
                .setTitle("How to play")
                .setMessage("Many clows come from right to left. You should make birds avoid them by swiping up or down. The more you have remains, the higher you get the score.");
        builder.create().show();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(isBGMInitialized) {
            bgm.start();
        }
        if(Mode == PLAY) {
            playGLSurfaceView.onResume();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        bgm.stop();
        try {
            bgm.prepare();
        }catch(IOException e){
            e.printStackTrace();
        }
        if(Mode == PLAY) {
            playGLSurfaceView.onPause();
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
    public void onDestroy(){
        super.onDestroy();
        bgm.release();
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if(isSignIn) {
            gameHelper.onActivityResult(request, response, data);
        }
    }
}
