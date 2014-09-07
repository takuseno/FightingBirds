package jp.gr.java_conf.androtaku.fightingbirds;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;

import jp.basicinc.gamefeat.android.sdk.controller.GameFeatAppController;

/**
 * Created by takuma on 2014/08/21.
 */
public class ResultActivity extends Activity {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    GameHelper gameHelper;


    ResultGLSurfaceView resultGLSurfaceView;

    GameFeatAppController gfAppController;

    private boolean isNewRecord = false;
    private boolean initialized = false;

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

        final GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
            @Override
            public void onSignInFailed() {
                Log.i("signin","failed");
            }

            @Override
            public void onSignInSucceeded() {
                Log.i("signin","success");
                //submit score
                if(isNewRecord){
                    Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                            getString(R.string.lb_id), getIntent().getIntExtra("score",0));
                }
                //unlock achievements
                if(prefs.getInt("best_score",0) > 10000){
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_amateur));
                }
                if(prefs.getInt("best_score", 0) > 20000){
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_veteran));
                }
                if(prefs.getInt("best_score", 0)  > 25000){
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_professional));
                }
                if(prefs.getInt("balloons",0) > 99 && gameHelper.isSignedIn()){
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_balloon_enthusiast));
                }
                if(prefs.getInt("birds",0) > 99 && gameHelper.isSignedIn()){
                    Games.Achievements.unlock(gameHelper.getApiClient(),
                            getString(R.string.achievement_clow_killer));
                }
            }
        };

        prefs = getSharedPreferences("preference",MODE_PRIVATE);
        editor = prefs.edit();

        gameHelper = new GameHelper(ResultActivity.this,GameHelper.CLIENT_GAMES);
        gameHelper.setup(gameHelperListener);
        if(prefs.getBoolean("signIn",false)) {
            gameHelper.beginUserInitiatedSignIn();
        }

        setContentView(R.layout.layout_result);
        init();
    }

    public void init(){
        gfAppController = new GameFeatAppController();

        gfAppController.setPopupProbability(3);
//        gfAppController.showPopupAdDialog(ResultActivity.this);

        int previousBalloons = prefs.getInt("balloons",0);
        previousBalloons += getIntent().getIntExtra("balloons",0);
        editor.putInt("balloons",previousBalloons);
        editor.commit();

        int previousBirds = prefs.getInt("birds",0);
        previousBirds += getIntent().getIntExtra("birds",0);
        editor.putInt("birds",previousBirds);
        editor.commit();

        Button adButton = (Button)findViewById(R.id.wall_button2);
        adButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gfAppController.show(ResultActivity.this);
            }
        });

        if(getIntent().getIntExtra("score",0) > prefs.getInt("best_score",0)){
            editor.putInt("best_score",getIntent().getIntExtra("score",0));
            editor.commit();
            TextView bestRecord = (TextView)findViewById(R.id.newRecordText);
            bestRecord.setVisibility(View.VISIBLE);
            isNewRecord = true;
        }

        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.framelayout);

        TextView scoreText = (TextView)findViewById(R.id.score_text);
        scoreText.setText(""+getIntent().getIntExtra("score",0));
        Button titleButton = (Button)findViewById(R.id.title_button);
        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button twitterButton = (Button)findViewById(R.id.twitter);
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "twitter://post?message="+"Fighting Birdsで" + getIntent().getIntExtra("score",0)
                        +"点！ https://play.google.com/store/apps/details?id=jp.gr.java_conf.androtaku.fightingbirds";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                try {
                    startActivity(intent);
                }catch(ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.twitter.android")));
                }
            }
        });

        Button facebookButton = (Button)findViewById(R.id.facebook);
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Intent.ACTION_SEND );
                intent.setType("text/plain");
                intent.putExtra( Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=jp.gr.java_conf.androtaku.fightingbirds");
                intent.setPackage("com.facebook.katana");
                try {
                    startActivity(intent);
                } catch(android.content.ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.facebook.katana")));
                }
            }
        });

        resultGLSurfaceView = (ResultGLSurfaceView)findViewById(R.id.result_glview);
        if(!isNewRecord)
            frameLayout.removeView(resultGLSurfaceView);

        initialized = true;
    }

    @Override
    public void onStart(){
        super.onStart();
        gfAppController.activateGF(this,false,false,true);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(initialized){
            resultGLSurfaceView.onResume();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        resultGLSurfaceView.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return  true;
    }
}
