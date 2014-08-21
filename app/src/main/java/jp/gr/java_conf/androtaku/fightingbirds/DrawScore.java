package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by takuma on 2014/08/21.
 */
public class DrawScore {
    private int textureNo;
    private int dispWidth,dispHeight;

    private float[] uvBuffer = {
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f
    };

    public DrawScore(int dispWidth,int dispHeight){
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;
    }

    public void setTexture(GL10 gl,int score){
        int[] textureID = new int[1];
        gl.glGenTextures(1,textureID,0);

        Bitmap bitmap = Bitmap.createBitmap(256,256, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(256/10);
        paint.setAntiAlias(true);
        canvas.drawColor(0);
        canvas.drawText("SCORE:" + score,0,256/10,paint);
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
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(uvBuffer));
        FloatBuffer vertexBuffer = makeVertexBuffer(0,0, dispWidth/3, dispWidth/3);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    }
}
