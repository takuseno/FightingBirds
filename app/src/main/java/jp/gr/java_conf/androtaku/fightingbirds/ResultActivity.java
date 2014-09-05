package jp.gr.java_conf.androtaku.fightingbirds;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;

/**
 * Created by takuma on 2014/08/21.
 */
public class ResultActivity extends Activity {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    GameHelper gameHelper;

    ResultGLSurfaceView resultGLSurfaceView;

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

            }

            @Override
            public void onSignInSucceeded() {

            }
        };
        gameHelper = new GameHelper(ResultActivity.this,GameHelper.CLIENT_GAMES);
        gameHelper.setMaxAutoSignInAttempts(0);
        gameHelper.setup(gameHelperListener);

        setContentView(R.layout.layout_result);
        init();
    }

    public void init(){
        prefs = getSharedPreferences("preference",MODE_PRIVATE);
        editor = prefs.edit();
        if(getIntent().getIntExtra("score",0) > prefs.getInt("best_score",0)){
            editor.putInt("best_score",getIntent().getIntExtra("score",0));
            editor.commit();
            TextView bestRecord = (TextView)findViewById(R.id.newRecordText);
            bestRecord.setVisibility(View.VISIBLE);
            isNewRecord = true;
            if(gameHelper.isSignedIn()){
                Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(R.string.lb_id), getIntent().getIntExtra("score",0));
            }
        }

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
                String url = "twitter://post?message="+"Fighting Birdsで" + getIntent().getIntExtra("score",0)+"点！ https://play.google.com/store/apps/details?id=jp.gr.java_conf.androtaku.fightingbirds";
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
        resultGLSurfaceView.setNewRecord(isNewRecord);

        initialized = true;
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
