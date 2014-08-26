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
    private int[] textureNo;
    private int dispWidth,dispHeight;
    private float[] uvBuffer = {
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f
    };
    private int flyingFrame;
    private float stumblingFrame;

    private float[] enemyX;
    private float[] enemyY;
    private boolean[] isAlive;
    private int bornFrame;
    private float ENEMY_SPEED;
    private int bornIndex;
    private int BORN_FRAME_LIMIT;
    private int ENEMY_NUM;

    public final int CLOW = 0;
    public final int FAT_BIRD = 1;
    public final int STUMBLING_BIRD = 2;
    private int[] enemyTag;
    private int[] enemyLife;

    public float SIZE_CLOW;
    public float SIZE_FAT_BIRD;

    private int killedCounter;

    public Enemy(int dispWidth,int dispHeight){
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;
    }

    public void init(int loopCounter){
        ENEMY_NUM = (int)(20 * (1.0f + (0.1f*loopCounter)));
        enemyX = new float[ENEMY_NUM];
        enemyY = new float[ENEMY_NUM];
        isAlive = new boolean[ENEMY_NUM];
        enemyTag = new int[ENEMY_NUM];
        enemyLife = new int[ENEMY_NUM];
        Random random = new Random();
        for(int i = 0;i < ENEMY_NUM;++i){
            enemyX[i] = 4*dispWidth/3;
            enemyY[i] = random.nextFloat()*2*dispHeight/3 + (dispHeight/6);
            isAlive[i] = false;
            enemyTag[i] = random.nextInt(3);
            if(enemyTag[i] == CLOW || enemyTag[i] == STUMBLING_BIRD){
                enemyLife[i] = 1;
            }
            else if(enemyTag[i] == FAT_BIRD){
                enemyLife[i] = 2;
            }
        }
        bornIndex = 0;
        bornFrame = 0;
        ENEMY_SPEED = dispWidth/250 * (1.0f + (0.2f*loopCounter));
        BORN_FRAME_LIMIT = 60;
        for(int i = 0;i < loopCounter;++i){
            BORN_FRAME_LIMIT *= 0.9f;
        }

        SIZE_CLOW = dispWidth/8;
        SIZE_FAT_BIRD = dispWidth/6;

        killedCounter = 0;
    }

    public void setTexture(GL10 gl,Context context){
        int[] textureID = new int[2];
        textureNo = new int[2];
        gl.glGenTextures(2,textureID,0);

        Bitmap bitmap01 = BitmapFactory.decodeResource(context.getResources(), R.drawable.crow01);
        textureNo[0] = textureID[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo[0]);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,GL10.GL_REPLACE);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap01, 0);
        bitmap01.recycle();

        Bitmap bitmap02 = BitmapFactory.decodeResource(context.getResources(),R.drawable.crow02);
        textureNo[1] = textureID[1];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo[1]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap02, 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,GL10.GL_REPLACE);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,bitmap02,0);
        bitmap02.recycle();
    }

    //頂点バッファの生成
    private FloatBuffer makeVertexBuffer(int x,int y,int w,int h) {
        //ウィンドウ座標を正規化デバイス座標に変換
        float left  =((float)x/(float)dispWidth)*2.0f-1.0f;
        float top   =((float)y/(float)dispHeight)*2.0f-1.0f;
        float right =((float)(x+w)/(float)dispWidth)*2.0f-1.0f;
        float bottom=((float)(y+h)/(float)dispHeight)*2.0f-1.0f;
        top   =-top;
        bottom=-bottom;

        //頂点バッファの生成
        float[] vertexs={
                left, top,   0.0f,//頂点0
                left, bottom,0.0f,//頂点1
                right,top,   0.0f,//頂点2
                right,bottom,0.0f,//頂点3
        };
        return makeFloatBuffer(vertexs);
    }

    //float配列をFloatBufferに変換
    private FloatBuffer makeFloatBuffer(float[] array) {
        FloatBuffer fb= ByteBuffer.allocateDirect(array.length * 4).order(
                ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);
        return fb;
    }

    public void draw(GL10 gl){
        for(int i = 0;i < isAlive.length;++i) {
            if (isAlive[i]) {
                gl.glActiveTexture(GL10.GL_TEXTURE0);
                if (flyingFrame < 30) {
                    gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo[0]);
                } else {
                    gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo[1]);
                }
                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(uvBuffer));
                FloatBuffer vertexBuffer;
                if(enemyTag[i] == CLOW) {
                    vertexBuffer = makeVertexBuffer((int) enemyX[i] - (dispWidth / 16), (int) enemyY[i] - (dispWidth / 16), (int)SIZE_CLOW, (int)SIZE_CLOW);
                }
                else if(enemyTag[i] == FAT_BIRD){
                    vertexBuffer = makeVertexBuffer((int) enemyX[i] - (dispWidth / 16), (int) enemyY[i] - (dispWidth / 16), (int)SIZE_FAT_BIRD, (int)SIZE_FAT_BIRD);
                }
                else{
                    vertexBuffer = makeVertexBuffer((int) enemyX[i] - (dispWidth / 16), (int) enemyY[i] - (dispWidth / 16), (int)SIZE_CLOW, (int)SIZE_CLOW);
                }
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
                gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

                enemyX[i] -= ENEMY_SPEED;
                if(enemyTag[i] == STUMBLING_BIRD){
                    enemyY[i] += Math.sin(stumblingFrame/360*2*3.16)*dispHeight/150;
                    ++stumblingFrame;
                }
                if(enemyX[i] < -dispWidth/5){
                    enemyX[i] = 4*dispWidth/3;
                    isAlive[i] = false;
                    ++killedCounter;
                }
            }
        }

        if(bornFrame > BORN_FRAME_LIMIT){
            if(bornIndex < ENEMY_NUM) {
                bornFrame = 0;
                isAlive[bornIndex] = true;
                ++bornIndex;
            }
        }
        ++bornFrame;

        ++flyingFrame;
        if(flyingFrame > 60){
            flyingFrame = 0;
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
        enemyLife[id] -= 1;
        if(enemyLife[id] == 0) {
            isAlive[id] = false;
            enemyX[id] = 4 * dispWidth / 3;
            ++killedCounter;
        }
    }

    public boolean getIsOver(){
        if(killedCounter == ENEMY_NUM){
            return true;
        }
        return false;
    }

    public int[] getEnemyTag(){
        return enemyTag;
    }
}
