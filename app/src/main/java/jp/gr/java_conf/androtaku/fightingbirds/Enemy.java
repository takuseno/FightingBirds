package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

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
    private PlaySequence playSequence;

    //declare ids of texture
    private int[] textureIds = {R.drawable.crow01,R.drawable.crow02,R.drawable.balloon_red,R.drawable.balloon_green};

    //declare global variables
    private int dispWidth,dispHeight;

    //number of enemy
    private int ENEMY_NUM = 100;

    //positons of enemies
    private float[] enemyX;
    private float[] enemyY;

    //tags of enemyy kinds
    private int[] enemyTags;
    public int CLOW = 1;
    public int BALLOON_RED = 2;
    public int BALLOON_GREEN = 3;

    //size of enemy
    public float SIZE_CLOW;

    //speed of enemy speed
    private float ENEMY_SPEED;
    //rate of accelarating speed
    private float speedRate;
    //possibility of stumbling tag
    private int possibilityStumbling;
    //limit of frame waiting for born
    private int BORN_FRAME_LIMIT;
    //index of born enemy
    private int bornIndex;

    //flags
    private boolean[] isAlive;
    private boolean[] isFalling;
    public boolean isOutside;
    private boolean[] isStumbling;

    //frames
    private int flyingFrame;
    private int[] fallingFrame;
    private int bornFrame;
    private int[] stumblingFrame;

    public Enemy(Context context,GL10 gl,int dispWidth,int dispHeight){
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;
        drawTexture = new DrawTexture(context,4,dispWidth,dispHeight);
        drawTexture.setTexture(textureIds,gl);
    }

    //function of setting texture when resume
    public void setTexture(GL10 gl){
        drawTexture.setTexture(textureIds,gl);
    }

    //function of initialization
    public void init(PlaySequence playSequence){
        //set sequece
        this.playSequence = playSequence;
        //set arrays
        enemyX = new float[ENEMY_NUM];
        enemyY = new float[ENEMY_NUM];
        isAlive = new boolean[ENEMY_NUM];
        isFalling = new boolean[ENEMY_NUM];
        fallingFrame = new int[ENEMY_NUM];
        enemyTags = new int[ENEMY_NUM];
        isStumbling = new boolean[ENEMY_NUM];
        stumblingFrame = new int[ENEMY_NUM];
        for(int i = 0;i < ENEMY_NUM;++i){
            isAlive[i] = false;
            isFalling[i] = false;
            isStumbling[i] = false;
        }
        bornIndex = 0;
        bornFrame = 0;
        ENEMY_SPEED = dispWidth/160;
        BORN_FRAME_LIMIT = 25;
        SIZE_CLOW = dispWidth/10;
        isOutside = false;
        speedRate = 1.0f;
        possibilityStumbling = 0;
    }

    //function of drawing
    public void draw(GL10 gl){
        for(int i = 0;i < isAlive.length;++i) {
            if (isAlive[i] || isFalling[i]) {
                //draw clow
                if(enemyTags[i] == CLOW) {
                    if (flyingFrame < 30) {
                        drawTexture.drawTexture(gl, 0, (int) enemyX[i], (int) enemyY[i], (int) SIZE_CLOW, (int) SIZE_CLOW);
                    } else {
                        drawTexture.drawTexture(gl, 1, (int) enemyX[i], (int) enemyY[i], (int) SIZE_CLOW, (int) SIZE_CLOW);
                    }
                }
                //draw balloon
                else if(enemyTags[i] == BALLOON_RED)
                    drawTexture.drawTexture(gl, 2, (int) enemyX[i], (int) enemyY[i], (int) SIZE_CLOW, (int) SIZE_CLOW);
                else if(enemyTags[i] == BALLOON_GREEN)
                    drawTexture.drawTexture(gl, 3, (int) enemyX[i], (int) enemyY[i], (int) SIZE_CLOW, (int) SIZE_CLOW);
                //check enemy alive
                if(isAlive[i]) {
                    //move enemy
                    enemyX[i] -= ENEMY_SPEED * speedRate;
                    if(isStumbling[i]){
                        enemyY[i] += (float)Math.sin((float)stumblingFrame[i]/360*2*3.14)*(dispHeight/170);
                        ++stumblingFrame[i];
                        if(stumblingFrame[i] == 360){
                            stumblingFrame[i] = 0;
                        }
                    }
                    //check enemy outside
                    if (enemyX[i] < -dispWidth / 5) {
                        isAlive[i] = false;
                        isOutside = true;
                    }
                }
                //check enemy falling
                else if(isFalling[i]){
                    enemyX[i] += dispWidth/100;
                    enemyY[i] -= dispHeight/100 - (dispHeight/300*fallingFrame[i]);
                    ++fallingFrame[i];
                    //check enemy outside
                    if(enemyY[i] > dispHeight)
                        isFalling[i] = false;
                }
            }
        }

        //produce enemy
        bornBirds();

        //add frame
        ++flyingFrame;
        if(flyingFrame > 60){
            flyingFrame = 0;
        }
    }

    //function of producing enemy
    public void bornBirds(){
        if(bornFrame > BORN_FRAME_LIMIT){
            bornFrame = 0;
            isAlive[bornIndex] = true;
            enemyX[bornIndex] = 4*dispWidth/3;
            float[] birdsY = playSequence.bird.getBirdsY();
            Random random = new Random();
            int rand = random.nextInt(4);
            enemyY[bornIndex] = birdsY[0]  - (dispHeight/4) + (dispHeight/2*rand/4);
            rand = random.nextInt(80);
            if(rand == 0)
                enemyTags[bornIndex] = BALLOON_RED;
            else if(rand == 3)
                enemyTags[bornIndex] = BALLOON_GREEN;
            else
                enemyTags[bornIndex] = CLOW;
            rand = random.nextInt(80);
            for(int i = 0;i < possibilityStumbling;++i) {
                if (rand == i) {
                    isStumbling[bornIndex] = true;
                    stumblingFrame[bornIndex] = random.nextInt(360);
                    break;
                }
            }
            ++bornIndex;
            //loop index
            if(bornIndex == ENEMY_NUM){
                bornIndex = 0;
            }
            if(bornIndex%20 == 0){
                speedRate += 0.1;
                BORN_FRAME_LIMIT = (int)(25 / speedRate);
                ++possibilityStumbling;
                Log.i("up","" + bornIndex);
            }
        }
        //add frame
        ++bornFrame;
    }

    //function of getting ids of alive enemies
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

    //function of getting x positions
    public float[] getEnemyX(){
        return enemyX;
    }
    //function of getting y positions
    public float[] getEnemyY(){
        return  enemyY;
    }

    //function of getting tags
    public int[] getEnemyTags(){
        return enemyTags;
    }

    //function of hit enemy
    public void hit(int id){
        isAlive[id] = false;
        isFalling[id] = true;
        fallingFrame[id] = 0;
    }
}
