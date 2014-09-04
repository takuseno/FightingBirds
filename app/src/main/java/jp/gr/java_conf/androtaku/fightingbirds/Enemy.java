package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by takuma on 2014/08/19.
 */
public class Enemy {
    //declare class
    private DrawTexture drawTexture;

    //declare ids of texture
    private int[] textureIds = {R.drawable.crow01,R.drawable.crow02};

    //declare global variables
    private int dispWidth,dispHeight;

    //number of enemy
    private int ENEMY_NUM = 20;

    //positons of enemies
    private float[] enemyX;
    private float[] enemyY;

    //size of enemy
    public float SIZE_CLOW;

    //speed of enemy speed
    private float ENEMY_SPEED;
    //rate of accelarating speed
    private float speedRate;

    //limit of frame waiting for born
    private int BORN_FRAME_LIMIT;
    //index of born enemy
    private int bornIndex;

    //flags
    private boolean[] isAlive;
    private boolean[] isFalling;
    public boolean isOutside;

    //frames
    private int flyingFrame;
    private int[] fallingFrame;
    private int bornFrame;

    //counter of enemy outside screen
    private int throughCounter;

    public Enemy(Context context,GL10 gl,int dispWidth,int dispHeight){
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;
        drawTexture = new DrawTexture(context,2,dispWidth,dispHeight);
        drawTexture.setTexture(textureIds,gl);
    }

    //function of initialization
    public void init(){
        //set arrays
        enemyX = new float[ENEMY_NUM];
        enemyY = new float[ENEMY_NUM];
        isAlive = new boolean[ENEMY_NUM];
        isFalling = new boolean[ENEMY_NUM];
        fallingFrame = new int[ENEMY_NUM];
        for(int i = 0;i < ENEMY_NUM;++i){
            isAlive[i] = false;
            isFalling[i] = false;
        }
        bornIndex = 0;
        bornFrame = 0;
        ENEMY_SPEED = dispWidth/200;
        BORN_FRAME_LIMIT = 30;
        SIZE_CLOW = dispWidth/10;
        isOutside = false;
        throughCounter = 0;
        speedRate = 1.0f;
    }

    //function of drawing
    public void draw(GL10 gl){
        for(int i = 0;i < isAlive.length;++i) {
            if (isAlive[i] || isFalling[i]) {
                if (flyingFrame < 30) {
                    drawTexture.drawTexture(gl,0,(int)enemyX[i],(int)enemyY[i],(int)SIZE_CLOW,(int)SIZE_CLOW);
                } else {
                    drawTexture.drawTexture(gl,1,(int)enemyX[i],(int)enemyY[i],(int)SIZE_CLOW,(int)SIZE_CLOW);
                }

                if(isAlive[i]) {
                    enemyX[i] -= ENEMY_SPEED * speedRate;
                    if (enemyX[i] < -dispWidth / 5) {
                        isAlive[i] = false;
                        isOutside = true;
                        ++throughCounter;
                        if (throughCounter > 20) {
                            throughCounter = 0;
                            speedRate += 0.1;
                            BORN_FRAME_LIMIT *= 0.9;
                        }
                    }
                }

                else if(isFalling[i]){
                    enemyX[i] += dispWidth/100;
                    enemyY[i] -= dispHeight/100 - (dispHeight/300*fallingFrame[i]);
                    ++fallingFrame[i];
                    if(enemyY[i] > dispHeight)
                        isFalling[i] = false;
                }
            }
        }

        bornBirds();

        ++flyingFrame;
        if(flyingFrame > 60){
            flyingFrame = 0;
        }
    }

    public void bornBirds(){
        if(bornFrame > BORN_FRAME_LIMIT){
            if(bornIndex < ENEMY_NUM) {
                bornFrame = 0;
                isAlive[bornIndex] = true;
                enemyX[bornIndex] = 4*dispWidth/3;
                Random random = new Random();
                enemyY[bornIndex] = (float)random.nextInt(8)*dispHeight/7;
                ++bornIndex;
            }
        }
        ++bornFrame;

        if(bornIndex == ENEMY_NUM){
            bornIndex = 0;
        }
    }

    public int[] getAliveEnemyId(){
        int num = 0;
        for(int i = 0;i < ENEMY_NUM;++i){
            if(isAlive[i]){
                ++num;
            }
        }
        int[] temp = new int[num];
        int count = 0;
        for(int i = 0;i < ENEMY_NUM;++i){
            if(isAlive[i]){
                temp[count] = i;
                ++count;
            }
        }
        return temp;
    }

    public float[] getEnemyX(){
        return enemyX;
    }

    public float[] getEnemyY(){
        return  enemyY;
    }

    public void hit(int id){
        isAlive[id] = false;
        isFalling[id] = true;
        fallingFrame[id] = 0;
    }
}
