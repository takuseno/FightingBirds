package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

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
    Context context;

    private float startX,startY;
    private float endX,endY;

    private int dispWidth,dispHeight;

    public PlayRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        drawSky.draw(gl);
        bird.draw(gl);
        fighter.draw(gl);
        checkFighterColison();
        checkMissileColison();
        //enemy.draw(gl);
        //checkEnemyColison();
        explosion.draw(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        dispWidth = width;
        dispHeight = height;
        gl.glViewport(0, 0, width, height);
        drawSky = new DrawSky(width,height);
        drawSky.setTexture(gl,context);
        bird = new Bird(width,height);
        bird.setTexture(gl,context);
        enemy = new Enemy(width,height);
        enemy.setTexture(gl,context);
        fighter = new Fighter(width,height);
        fighter.setTexture(gl,context);
        explosion = new Explosion(width,height,dispWidth/10);

        explosion.setTexture(gl,context);
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

    public void checkEnemyColison(){
        float[] birdsX = bird.getBirdsX();
        float[] birdsY = bird.getBirdsY();
        int[] birdsId = bird.getAliveBirdsId();
        float[] enemyX = enemy.getEnemyX();
        float[] enemyY = enemy.getEnemyY();
        int[] enemyId = enemy.getAliveEnemyId();
        for(int i = 0;i < birdsId.length;++i){
            for(int j = 0;j < enemyId.length;++j){
                if(Math.sqrt(((birdsX[birdsId[i]] - enemyX[enemyId[j]])
                        * (birdsX[birdsId[i]] - enemyX[enemyId[j]] ))
                        + ((birdsY[birdsId[i]] - enemyY[enemyId[j]])
                        * ((birdsY[birdsId[i]]  - enemyY[enemyId[j]] ))))
                        <  dispWidth/18
                        ){
                    bird.hit(birdsId[i]);
                    enemy.hit(enemyId[j]);
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
                    explosion.explode(birdsX[birdsId[i]],birdsY[birdsId[i]]);
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
                    if (birdsY[birdsId[i]] < missileY + (dispWidth / 70)
                            && birdsY[birdsId[i]] > missileY - (dispWidth / 70)) {
                        bird.hit(birdsId[i]);
                        fighter.missileHit();
                        explosion.explode(missileX,missileY);
                    }
                }
            }
        }
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
        float angle = (float)Math.atan((endY - startY)/(endX - startX));
        bird.startFlying(angle);
    }
}
