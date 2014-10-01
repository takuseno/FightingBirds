package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by takuma on 2014/09/04.
 */
public class PlaySequence {
    //declare classes for drawing
    public Bird bird;
    public Enemy enemy;
    public DrawSky drawSky;
    public DrawScore drawScore;

    //declare global variables
    private int score;
    private Context context;
    private int balloonCounter;
    private int birdCounter;
    private SoundPool soundPool;
    int soundHit;
    int soundItem;

    //function of initialization
    public void init(Context context,GL10 gl,int dispWidth,int dispHeight){
        this.context = context;
        drawSky = new DrawSky(context,gl,dispWidth, dispHeight);
        bird = new Bird(context,gl,dispWidth, dispHeight);
        enemy = new Enemy(context,gl,dispWidth, dispHeight);
        enemy.init(this);
        drawScore = new DrawScore(dispWidth, dispHeight);
        drawScore.setTexture(gl, 0);
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,1);
        soundHit = soundPool.load(context,R.raw.hit,1);
        soundItem = soundPool.load(context,R.raw.up1,1);
        score = 0;
        balloonCounter = 0;
        birdCounter = 0;
    }

    //function of setting texture when resume
    public void setTexture(GL10 gl){
        drawSky.setTexture(gl);
        bird.setTexture(gl);
        enemy.setTexture(gl);
    }

    //function of drawing
    public void draw(GL10 gl){
        //draw objects
        drawSky.draw(gl);
        bird.draw(gl);
        enemy.draw(gl);
        drawScore.draw(gl);

        //check if an enemy go outside screen
        if(enemy.isOutside){
            //add score
            score += 10 * bird.getNumAlive();
            //restore flag
            enemy.isOutside = false;
            //remake score texture
            drawScore.setTexture(gl,score);
        }

        //check collision between birds and enemies
        checkEnemyColison(gl);

        //check gameover
        if(checkOver()){
            //transition to result screen
            Intent intent = new Intent(context,ResultActivity.class);
            intent.putExtra("score",score);
            intent.putExtra("balloons",balloonCounter);
            intent.putExtra("birds",birdCounter);
            ((MainActivity)context).finish();
            context.startActivity(intent);
        }
    }

    //function of collision between birds and enemies
    public void checkEnemyColison(GL10 gl){
        //get birds positions
        float[] birdsX = bird.getBirdsX();
        float[] birdsY = bird.getBirdsY();
        boolean[] birdsId = bird.getIsAlive();
        //get enemies positions
        float[] enemyX = enemy.getEnemyX();
        float[] enemyY = enemy.getEnemyY();
        int[] enemyId = enemy.getAliveEnemyId();
        int[] enemyTags = enemy.getEnemyTags();
        //loop for checking collisions
        for(int i = 0;i < birdsId.length;++i){
            for(int j = 0;j < enemyId.length;++j) {
                if (birdsId[i]) {
                    float enemyRadius = enemy.SIZE_CLOW / 2;
                    if (Math.sqrt(Math.pow(birdsX[i] - enemyX[enemyId[j]],2)
                            + Math.pow(birdsY[i] - enemyY[enemyId[j]],2))
                                < (bird.SIZE_BIRD + enemyRadius)/2.5) {
                        //processing of a hit bird
                        if(enemyTags[enemyId[j]] == enemy.CLOW) {
                            soundPool.play(soundHit,20,20,0,0,1.0f);
                            bird.hit(i);
                            //add score
                            score += 10 * bird.getNumAlive() * 2;
                            //remake score texture
                            drawScore.setTexture(gl,score);
                            ++birdCounter;
                        }
                        else if(enemyTags[enemyId[j]] == enemy.BALLOON_RED) {
                            soundPool.play(soundItem,1.0f,1.0f,0,0,1.0f);
                            bird.hitBalloonRed();
                            ++balloonCounter;
                        }
                        else if(enemyTags[enemyId[j]] == enemy.BALLOON_GREEN){
                            soundPool.play(soundItem,1.0f,1.0f,0,0,1.0f);
                            bird.hitBalloonGreen();
                            ++balloonCounter;
                        }
                        //processing of a hit enemy
                        enemy.hit(enemyId[j]);
                        //soundPool.play(pafu,10.0f,10.0f,0,0,1.0f);
                        break;
                    }
                }
            }
        }
    }

    //function of checking gameover
    public boolean checkOver(){
        //get flags of birds' lives
        boolean[] isAlive = bird.getIsAlive();
        for(int i = 0;i < isAlive.length;++i){
            if(isAlive[i]) {
                return false;
            }
        }
        return true;
    }

    //function of treating touch down
    public void touchDown(float x,float y){
        bird.touchDown(y);
    }
    //function of treating touch move
    public void touchMove(float x,float y){
        bird.touchMove(y);
    }
    //function of treating touch up
    public void touchUp(float x,float y){
    }
}
