package jp.gr.java_conf.androtaku.fightingbirds;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by takuma on 2014/08/21.
 */
public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT > 10) {
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }

        setContentView(R.layout.layout_result);
        init();
    }

    public void init(){
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
                String url = "twitter://post?message="+getIntent().getIntExtra("score",0)+" app url";
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
                intent.putExtra( Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.facebook.katana");
                intent.setPackage("com.facebook.katana");
                try {
                    startActivity(intent);
                } catch(android.content.ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.facebook.katana")));
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return  true;
    }
}
