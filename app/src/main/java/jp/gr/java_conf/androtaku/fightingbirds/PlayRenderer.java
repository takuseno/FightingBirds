package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by takuma on 2014/08/18.
 */
public class PlayRenderer implements GLSurfaceView.Renderer {
    Bird bird;
    Enemy enemy;
    DrawSky drawSky;
    DrawScore drawScore;
    Context context;

    SoundPool soundPool;

    private int dispWidth,dispHeight;

    private final int NORMAL_ENEMY = 1;
    private int sequence = NORMAL_ENEMY;

    private int score = 0;

    private boolean initialized = false;

    public PlayRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        drawSky.draw(gl);
        bird.draw(gl);
        if(sequence == NORMAL_ENEMY) {
            enemy.draw(gl);

            checkEnemyColison(gl);
        }

        drawScore.draw(gl);

        if(checkOver()){
            Log.i("result","result");
            Intent intent = new Intent(context,ResultActivity.class);
            intent.putExtra("score",score);
            ((MainActivity)context).finish();
            context.startActivity(intent);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        dispWidth = width;
        dispHeight = height;
        gl.glViewport(0, 0, width, height);
        if(!initialized) {
            drawSky = new DrawSky(width, height);

            bird = new Bird(width, height);

            enemy = new Enemy(width, height);
            enemy.init();

            drawScore = new DrawScore(dispWidth, dispHeight);

            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//            bomb_missile = soundPool.load(context, R.raw.bomb2, 0);
//            bomb_fighter = soundPool.load(context, R.raw.bomb1, 0);
//            pafu = soundPool.load(context,R.raw.pafu,0);
            initialized = true;
        }
        drawSky.setTexture(gl, context);
        bird.setTexture(gl, context);
        enemy.setTexture(gl, context);
        drawScore.setTexture(gl, 0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //背景色をクリア
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //頂点配列の有効化
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        //テクスチャ機能ON
        gl.glEnable(GL10.GL_TEXTURE_2D);
        //ブレンド可能に
        gl.glEnable(GL10.GL_ALPHA_TEST);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void checkEnemyColison(GL10 gl){
        float[] birdsX = bird.getBirdsX();
        float[] birdsY = bird.getBirdsY();
        boolean[] birdsId = bird.getIsAlive();
        float[] enemyX = enemy.getEnemyX();
        float[] enemyY = enemy.getEnemyY();
        int[] enemyId = enemy.getAliveEnemyId();
        int[] enemyTag = enemy.getEnemyTag();

        for(int i = 0;i < birdsId.length;++i){
            for(int j = 0;j < enemyId.length;++j) {
                if (birdsId[i]) {
                    float enemyRadius = 0;
                    if (enemyTag[enemyId[j]] == enemy.CLOW) {
                        enemyRadius = enemy.SIZE_CLOW / 2;
                    } else if (enemyTag[enemyId[j]] == enemy.FAT_BIRD) {
                        enemyRadius = enemy.SIZE_FAT_BIRD / 2;
                    }
                    if (Math.sqrt(((birdsX[i] - enemyX[enemyId[j]])
                            * (birdsX[i] - enemyX[enemyId[j]]))
                            + ((birdsY[i] - enemyY[enemyId[j]])
                            * ((birdsY[i] - enemyY[enemyId[j]]))))
                            < (dispWidth / 5 + enemyRadius) / 3.5
                            ) {
                        bird.hit(i);
                        enemy.hit(enemyId[j]);
                        //soundPool.play(pafu,10.0f,10.0f,0,0,1.0f);
                        break;
                    }
                }
            }
        }
    }

    public boolean checkOver(){
        boolean[] isAlive = bird.getIsAlive();
        for(int i = 1;i < isAlive.length;++i){
            if(isAlive[i]) {
                return false;
            }
        }
        return true;
    }

    public void touchDown(float x,float y){
        bird.touchDown(y);
    }

    public void touchMove(float x,float y){
        bird.touchMove(y);
    }

    public void touchUp(float x,float y){
    }
}
