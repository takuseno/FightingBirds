package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by takuma on 2014/08/18.
 */
public class Bird {
    private int[] textureNo;
    private int dispWidth,dispHeight;
    private float[] uvBuffer = {
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f
    };

    public int flyingFrame = 0;

    BirdsControl birdsControl;

    private int selectedBird;

    public Bird(int dispWidth,int dispHeight){
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;
        birdsControl = new BirdsControl(30,dispWidth,dispHeight);
    }

    public void setTexture(GL10 gl,Context context){
        int[] textureID = new int[2];
        textureNo = new int[2];
        gl.glGenTextures(2,textureID,0);

        Bitmap bitmap01 = BitmapFactory.decodeResource(context.getResources(),R.drawable.bird01);
        textureNo[0] = textureID[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo[0]);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,GL10.GL_REPLACE);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,bitmap01,0);
        bitmap01.recycle();

        Bitmap bitmap02 = BitmapFactory.decodeResource(context.getResources(),R.drawable.bird02);
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
        float[] birdsX = birdsControl.getBirdsX();
        float[] birdsY = birdsControl.getBirdsY();
        boolean[] isAlive = birdsControl.getIsAlive();
        for(int i = 0;i < isAlive.length;++i) {
            if(isAlive[i]) {
                gl.glActiveTexture(GL10.GL_TEXTURE0);
                if (flyingFrame < 30) {
                    gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo[0]);
                } else {
                    gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo[1]);
                }
                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(uvBuffer));
                FloatBuffer vertexBuffer = makeVertexBuffer((int) birdsX[i] - (dispWidth / 20), (int) birdsY[i] - (dispWidth / 20), dispWidth / 10, dispWidth / 10);
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
                gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
            }
        }

        birdsControl.flying();
        birdsControl.returning();

        ++flyingFrame;
        if(flyingFrame > 60){
            flyingFrame = 0;
        }
    }

    public void getNearestBird(float x,float y){
        selectedBird = birdsControl.getNearestBird(x,y);
    }

    public void startFlying(float angle){
        birdsControl.startFlying(selectedBird,angle);
    }

    public int[] getAliveBirdsId(){
        return birdsControl.getAliveBirdsId();
    }

    public boolean[] getIsAlive(){
        return  birdsControl.getIsAlive();
    }

    public float[] getBirdsX(){
        return  birdsControl.getBirdsX();
    }

    public float[] getBirdsY(){
        return birdsControl.getBirdsY();
    }

    public void hit(int id){
        birdsControl.hit(id);
    }
}
