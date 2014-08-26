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
    Fighter fighter;
    Explosion explosion;
    FeatherScatter featherScatter;
    DrawScore drawScore;
    Context context;

    SoundPool soundPool;
    int bomb_missile,bomb_fighter,pafu;

    private float startX,startY;
    private float endX,endY;

    private int dispWidth,dispHeight;

    private final int NORMAL_ENEMY = 1;
    private final int BOSS = 2;
    private int sequence = NORMAL_ENEMY;

    private int score = 0;
    private int loopCounter = 0;

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
            if(enemy.getIsOver()){
                fighter.init(loopCounter);
                sequence = BOSS;
            }
        }
        else if(sequence == BOSS) {
            fighter.draw(gl);
            checkFighterColison();
            checkMissileColison();
            if(fighter.isOver()){
                enemy.init(loopCounter);
                sequence = NORMAL_ENEMY;
                score += 100;
                drawScore.setTexture(gl,score);
                explosion.explode(fighter.getFighterX() - dispWidth/10,fighter.getFighterY(),dispWidth/5);
                ++loopCounter;
            }
        }
        explosion.draw(gl);
        featherScatter.draw(gl);

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

            enemy.init(loopCounter);
            fighter = new Fighter(width, height);

            fighter.init(loopCounter);
            explosion = new Explosion(width, height);

            featherScatter = new FeatherScatter(width, height);

            drawScore = new DrawScore(dispWidth, dispHeight);

            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            bomb_missile = soundPool.load(context, R.raw.bomb2, 0);
            bomb_fighter = soundPool.load(context, R.raw.bomb1, 0);
            pafu = soundPool.load(context,R.raw.pafu,0);
            initialized = true;
        }
        drawSky.setTexture(gl, context);
        bird.setTexture(gl, context);
        enemy.setTexture(gl, context);
        fighter.setTexture(gl, context);
        explosion.setTexture(gl, context);
        featherScatter.setTexture(gl, context);
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
        int[] birdsId = bird.getAliveBirdsId();
        float[] enemyX = enemy.getEnemyX();
        float[] enemyY = enemy.getEnemyY();
        int[] enemyId = enemy.getAliveEnemyId();
        int[] enemyTag = enemy.getEnemyTag();
        for(int i = 0;i < birdsId.length;++i){
            for(int j = 0;j < enemyId.length;++j){
                float enemyRadius = 0;
                if(enemyTag[enemyId[j]] == enemy.CLOW){
                    enemyRadius = enemy.SIZE_CLOW/2;
                }
                else if(enemyTag[enemyId[j]] == enemy.FAT_BIRD){
                    enemyRadius = enemy.SIZE_FAT_BIRD/2;
                }
                if(Math.sqrt(((birdsX[birdsId[i]] - enemyX[enemyId[j]])
                        * (birdsX[birdsId[i]] - enemyX[enemyId[j]] ))
                        + ((birdsY[birdsId[i]] - enemyY[enemyId[j]])
                        * ((birdsY[birdsId[i]]  - enemyY[enemyId[j]] ))))
                        <  (dispWidth/5 + enemyRadius)/3.5
                        ){
                    featherScatter.scatter(enemyX[enemyId[j]],enemyY[enemyId[j]],dispWidth/8);
                    bird.hit(birdsId[i]);
                    enemy.hit(enemyId[j]);
                    score += 10;
                    drawScore.setTexture(gl,score);
                    //soundPool.play(pafu,10.0f,10.0f,0,0,1.0f);
                    break;
                }
            }
        }
    }

    public void checkFighterColison(){
        float[] birdsX = bird.getBirdsX();
        float[] birdsY = bird.getBirdsY();
        int[] birdsId = bird.getAliveBirdsId();
        float fighterX = fighter.getFighterX();
        float fighterY = fighter.getFighterY();
        for(int i = 0;i < birdsId.length;++i){
            if(birdsX[birdsId[i]] < fighterX + (dispWidth/4)
                    && birdsX[birdsId[i]] > fighterX - (dispWidth/4)){
                if(birdsY[birdsId[i]] < fighterY + (dispWidth/20)
                        && birdsY[birdsId[i]] > fighterY - (dispWidth/20)){
                    bird.hit(birdsId[i]);
                    fighter.hit();
                    explosion.explode(birdsX[birdsId[i]],birdsY[birdsId[i]],dispWidth/10);
                    soundPool.play(bomb_fighter,0.0f,1.0f,0,0,1.0f);
                }
            }
        }
    }

    public void checkMissileColison(){
        if(fighter.getLaunching()) {
            float[] birdsX = bird.getBirdsX();
            float[] birdsY = bird.getBirdsY();
            int[] birdsId = bird.getAliveBirdsId();
            float missileX = fighter.getMissileX();
            float missileY = fighter.getMissileY();
            for (int i = 0; i < birdsId.length; ++i) {
                if (birdsX[birdsId[i]] < missileX + (dispWidth / 8)
                        && birdsX[birdsId[i]] > missileX - (dispWidth / 8)) {
                    if (birdsY[birdsId[i]] < missileY + (dispWidth / 20)
                            && birdsY[birdsId[i]] > missileY - (dispWidth / 20)) {
                        bird.hit(birdsId[i]);
                        fighter.missileHit();
                        explosion.explode(missileX,missileY,dispWidth/10);
                        soundPool.play(bomb_missile,1.0f,1.0f,0,0,1.0f);
                    }
                }
            }
        }
    }

    public boolean checkOver(){
        boolean[] isAlive = bird.getIsAlive();
        for(int i = 0;i < isAlive.length;++i){
            if(isAlive[i]) {
                return false;
            }
        }
        return true;
    }

    public void touchDown(float x,float y){
        startX = x;
        startY = y;
        bird.getNearestBird(x,y);
    }

    public void touchMove(float x,float y){

    }

    public void touchUp(float x,float y){
        endX = x;
        endY = y;
        if(((startX - x)*(startX - x)) + ((startY - y)*(startY - y)) > 4) {
            float angle = (float) Math.atan((endY - startY) / (endX - startX));
            if (endX < startX) {
                bird.startFlying(angle, false);
            } else {
                bird.startFlying(angle, true);
            }
        }
    }
}
