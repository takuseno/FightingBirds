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
public class DrawSky {
    private DrawTexture drawTexture;
    private int[] textureId = {R.drawable.sky};

    private int dispWidth,dispHeight;

    public DrawSky(Context context,GL10 gl,int dispWidth,int dispHeight){
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;
        drawTexture = new DrawTexture(context,1,dispWidth,dispHeight);
        drawTexture.setTexture(textureId,gl);
    }

    public void setTexture(GL10 gl){
        drawTexture.setTexture(textureId,gl);
    }

    public void draw(GL10 gl){
        drawTexture.drawTexture(gl,0,dispWidth/2,dispHeight/2,dispWidth,dispHeight);
    }
}
