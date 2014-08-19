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

    private float[] enemyX;
    private float[] enemyY;
    private boolean[] isAlive;
    private int bornFrame = 0;
    private final float ENEMY_SPEED = 5;
    private int bornIndex = 0;

    public Enemy(int dispWidth,int dispHeight){
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;

        enemyX = new float[20];
        enemyY = new float[20];
        isAlive = new boolean[20];
        Random random = new Random();
        for(int i = 0;i < 20;++i){
            enemyX[i] = 4*dispWidth/3;
            enemyY[i] = random.nextFloat()*dispHeight;
            isAlive[i] = false;
        }
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
                FloatBuffer vertexBuffer = makeVertexBuffer((int)enemyX[i], (int) enemyY[i], dispWidth / 5, dispWidth / 5);
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
                gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

                enemyX[i] -= ENEMY_SPEED;
                if(enemyX[i] < -dispWidth/5){
                    enemyX[i] = 4*dispWidth/3;
                    isAlive[i] = false;
                }
            }
        }

        if(bornFrame > 90){
            bornFrame = 0;
            isAlive[bornIndex] = true;
            ++bornIndex;
            if(bornIndex == 20){
                bornIndex = 0;
            }
        }
        ++bornFrame;

        ++flyingFrame;
        if(flyingFrame > 60){
            flyingFrame = 0;
        }
    }
}
