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
public class DrawImage {
    private final FloatBuffer mVertexBuffer,mTexBuffer;

    Context mContext;

    int[] buffers;

    public DrawImage(Context context){
        mContext = context;

        float left = 0f;
        float top = 1.0f;
        float right = 1.0f;
        float bottom = 0f;

        float[] uv = new float[]{
                right, bottom,
                right, top,
                left, bottom,
                left, top
        };

        ByteBuffer bb = ByteBuffer.allocateDirect(uv.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mTexBuffer = bb.asFloatBuffer();
        mTexBuffer.put(uv);
        mTexBuffer.position(0);

        left = -1.0f;
        top = 1.0f;
        right = 1.0f;
        bottom = -1.0f;

        float[] vertics = new float[]{
                left,top,0,
                left,bottom,0,
                right,top,0,
                right,bottom,0
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertics.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertics);
        mVertexBuffer.position(0);
    }

    public void draw(GL10 gl,int imageNumber){
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, buffers[imageNumber]);

        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
    }

    public void createBuffers(GL10 gl,int number){
        //number���̖����̃��������m��
        buffers = new int[number];
        gl.glGenTextures(number, buffers,0);
    }

    public void setImage(GL10 gl,int imageId,int imageNumber){
        gl.glBindTexture(GL10.GL_TEXTURE_2D, buffers[imageNumber]);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), imageId);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
    }
}
