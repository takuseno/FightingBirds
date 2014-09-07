package jp.gr.java_conf.androtaku.fightingbirds;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.GameHelper;

import java.io.IOException;

import jp.basicinc.gamefeat.android.sdk.controller.GameFeatAppController;
import jp.basicinc.gamefeat.android.sdk.view.GameFeatIconView;


public class MainActivity extends Activity {

    GameHelper gameHelper;
    IInAppBillingService billingServices;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            billingServices = IInAppBillingService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            billingServices = null;
        }
    };

    PlayGLSurfaceView playGLSurfaceView;
    MediaPlayer bgm;

    GameFeatAppController gfAppCntroller;

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

        bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"),
                serviceConnection,Context.BIND_AUTO_CREATE);

        //game feat ads
        gfAppCntroller = new GameFeatAppController();
        gfAppCntroller.setRefreshInterval(30);
        ((GameFeatIconView)findViewById(R.id.gf_icon1)).addLoader(gfAppCntroller);
        ((GameFeatIconView)findViewById(R.id.gf_icon2)).addLoader(gfAppCntroller);
        Button adButton = (Button)findViewById(R.id.wall_button);
        adButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gfAppCntroller.show(MainActivity.this);
            }
        });

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
                Log.i("signin","failed");
            }

            @Override
            public void onSignInSucceeded() {
                Log.i("signin","success");
                Games.Achievements.unlock(gameHelper.getApiClient(),
                        getString(R.string.achievement_follow_me));
                Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                        getString(R.string.lb_id), prefs.getInt("best_score",0));
                editor.putBoolean("signIn",true);
                editor.commit();
            }
        };
        gameHelper = new GameHelper(MainActivity.this,GameHelper.CLIENT_GAMES);
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
        setContentView(R.layout.play_layout);
        playGLSurfaceView = (PlayGLSurfaceView)findViewById(R.id.play_view);
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
        if(Mode == START){
            gfAppCntroller.startIconAd();
        }
        else if(Mode == PLAY) {
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
        gfAppCntroller.activateGF(this,false,true,true);
        if(isSignIn) {
            gameHelper.onStart(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(Mode == START){
            gfAppCntroller.stopIconAd();
        }
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
