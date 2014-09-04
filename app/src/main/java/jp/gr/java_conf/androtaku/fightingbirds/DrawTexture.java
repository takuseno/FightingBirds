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
 * Created by takuma on 2014/09/04.
 */
public class DrawTexture {
    private float[] uvBuffer = {
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f
    };

    private int[] textureNo;
    private Context context;
    private int dispWidth,dispHeight;

    public DrawTexture(Context context,int textureNum,int dispWidth,int dispHeight){
        this.context = context;
        textureNo = new int[textureNum];
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;
    }

    public void setTexture(int[] textureIds,GL10 gl){
        int[] tempIds = new int[textureNo.length];
        gl.glGenTextures(textureNo.length,tempIds,0);
        for(int i = 0;i < textureNo.length;++i){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), textureIds[i]);
            textureNo[i] = tempIds[i];
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo[i]);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,GL10.GL_REPLACE);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
    }

    public void drawTexture(GL10 gl,int id,int x,int y,int width,int height){
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo[id]);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(uvBuffer));
        FloatBuffer vertexBuffer;
        vertexBuffer = makeVertexBuffer(x - (width/2), y - (height/2), width, height);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
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
}
