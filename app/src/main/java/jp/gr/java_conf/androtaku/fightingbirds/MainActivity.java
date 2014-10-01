package jp.gr.java_conf.androtaku.fightingbirds;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.GameHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import jp.basicinc.gamefeat.android.sdk.controller.GameFeatAppController;
import jp.basicinc.gamefeat.android.sdk.view.GameFeatIconView;


public class MainActivity extends Activity{
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    GameHelper gameHelper;
    IInAppBillingService billingServices;
    String DELETE_ADS_ID = "delete_ads";
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            billingServices = IInAppBillingService.Stub.asInterface(service);
            if(!prefs.getBoolean("delete_ads",false)) {
                try {
                    Bundle ownedItems = billingServices.getPurchases(3, getPackageName(), "inapp", null);
                    int responce = ownedItems.getInt("RESPONSE_CODE");
                    if(responce == 0){
                        ArrayList ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                        for(int i = 0;i < ownedSkus.size();++i){
                            if(ownedSkus.get(i).equals(DELETE_ADS_ID)){
                                editor.putBoolean("delete_ads",true);
                                editor.commit();
                                showAdsExplanation();
                            }
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            billingServices = null;
        }
    };

    PlayGLSurfaceView playGLSurfaceView;
    MediaPlayer bgm;
    SoundPool soundPool;
    int soundId;

    GameFeatAppController gfAppCntroller;

    private int START = 0;
    private int PLAY = 1;
    private int Mode = START;

    private boolean isSignIn = false;
    private boolean isBGMInitialized = false;

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

        bgm = MediaPlayer.create(this,R.raw.main_bgm);
        bgm.setLooping(true);
        bgm.setVolume(0.1f,0.1f);
        bgm.start();
        isBGMInitialized = true;

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        soundId = soundPool.load(this,R.raw.start_button,1);

        setStart();
    }

    public void setStart(){
        setContentView(R.layout.activity_main);

        //setting billing service
        bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"),
                serviceConnection,Context.BIND_AUTO_CREATE);

        //game feat ads
        gfAppCntroller = new GameFeatAppController();
        Button adButton = (Button)findViewById(R.id.wall_button);
        adButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gfAppCntroller.show(MainActivity.this);
            }
        });
        gfAppCntroller.setRefreshInterval(30);
        GameFeatIconView iconView1 = (GameFeatIconView)findViewById(R.id.iconView1);
        iconView1.addLoader(gfAppCntroller);
        GameFeatIconView iconView2 = (GameFeatIconView)findViewById(R.id.iconView2);
        iconView2.addLoader(gfAppCntroller);
        GameFeatIconView iconView3 = (GameFeatIconView)findViewById(R.id.iconView3);
        iconView3.addLoader(gfAppCntroller);

        //play game services
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

        //setting view
        Button button = (Button)findViewById(R.id.start_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId,1.0f,1.0f,0,0,1);
                setGame();
            }
        });
        Button rankingButton = (Button)findViewById(R.id.ranking_button);
        rankingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameHelper.isSignedIn()){
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
                            getString(R.string.lb_id)), 5001);
                }
                else{
                    gameHelper.setConnectOnStart(true);
                    gameHelper.beginUserInitiatedSignIn();

                }
            }
        });
        Button adDeleteButton = (Button)findViewById(R.id.ad_delete_button);
        adDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bundle buyIntentBundle = billingServices.getBuyIntent(3, getPackageName(),
                            DELETE_ADS_ID, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                    if(buyIntentBundle.getInt("RESPONSE_CODE") == 0){
                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                        try {
                            startIntentSenderForResult(pendingIntent.getIntentSender(),
                                    1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                                    Integer.valueOf(0));
                        }catch(IntentSender.SendIntentException e){
                            e.printStackTrace();
                        }
                    }
                }catch(RemoteException e){
                    e.printStackTrace();
                }
            }
        });

        if(prefs.getBoolean("delete_ads",false)){
            RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.main_layout);
            relativeLayout.removeView(adButton);
            relativeLayout.removeView(adDeleteButton);
            relativeLayout.removeView(iconView1);
            relativeLayout.removeView(iconView2);
            relativeLayout.removeView(iconView3);
        }
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
                .setTitle("遊び方")
                .setMessage("画面を上下にスワイプして、画面右から次々に出てくる鳥達を避けてください。残っている自分たちの鳥の数が多いほどスコアが高くなります。風船にぶつかるといいことが起こります。");
        builder.create().show();
    }

    public void showAdsExplanation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setTitle("購入を確認しました")
                .setMessage("次のアプリの起動時から完全に広告は表示されません。");
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
        gfAppCntroller.startIconAd();
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
        gfAppCntroller.activateGF(this,false,true,false);
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
        gfAppCntroller.stopIconAd();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        bgm.release();
        soundPool.release();
        if(billingServices != null){
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if(request == 1001){
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            if(response == RESULT_OK){
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    if(DELETE_ADS_ID.equals(sku)){
                        editor.putBoolean("delete_ads",true);
                        editor.commit();
                        showAdsExplanation();
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
        gameHelper.onActivityResult(request, response, data);
    }
}
