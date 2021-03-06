package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by takuma on 2014/08/21.
 */
public class FeatherScatter {
    private int textureNo;
    private int dispWidth,dispHeight;

    private int frame;
    private float size;
    private boolean isScatter;
    private float expX,expY;



    public FeatherScatter(int dispWidth,int dispHeight){
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;
    }

    public void scatter(float x,float y,float size){
        frame = 0;
        isScatter = true;
        expX = x;
        expY = y;
        this.size = size;
    }

    public void setTexture(GL10 gl,Context context){
        int[] textureID = new int[1];
        gl.glGenTextures(1,textureID,0);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.feather);
        textureNo = textureID[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,GL10.GL_REPLACE);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
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
        if(isScatter) {
            float indexX;
            float indexY;
            if(frame < 4){
                indexX = ((float)frame)/4;
                indexY = 0;
            }
            else if(frame >= 4 && frame < 8){
                indexX = ((float)(frame - 4))/4;
                indexY = 0.25f;
            }
            else if(frame >= 8 && frame < 12){
                indexX = ((float)(frame - 8))/4;
                indexY = 0.5f;
            }
            else{
                indexX = ((float)(frame - 12))/4;
                indexY = 0.75f;
            }

            float[] uvBuffer = {
                    indexX,indexY,
                    indexX,indexY + 0.25f,
                    indexX + 0.25f,indexY,
                    indexX + 0.25f,indexY + 0.25f
            };
            Log.i("x", "" + indexX);
            gl.glActiveTexture(GL10.GL_TEXTURE0);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(uvBuffer));
            FloatBuffer vertexBuffer = makeVertexBuffer((int) expX - (int)(size/2), (int) expY - (int)(size/2),(int) size, (int)size);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
            ++frame;
            if(frame == 16){
                frame = 0;
                isScatter = false;
            }
        }
    }
}
