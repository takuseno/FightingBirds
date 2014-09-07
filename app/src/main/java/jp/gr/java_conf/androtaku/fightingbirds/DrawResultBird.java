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
 * Created by takuma on 2014/08/26.
 */
public class DrawResultBird {
    private DrawTexture drawTexture;
    private int[] textureIds = {R.drawable.bird01,R.drawable.bird02};

    private int dispWidth,dispHeight;

    public int flyingFrame = 0;
    private float birdY;

    public DrawResultBird(Context context,GL10 gl,int dispWidth,int dispHeight){
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;
        drawTexture = new DrawTexture(context,2,dispWidth,dispHeight);
        drawTexture.setTexture(textureIds,gl);
        birdY = dispHeight/2;
    }

    public void draw(GL10 gl){
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        if (flyingFrame < 30) {
            drawTexture.drawTexture(gl,0,dispWidth/5,(int)birdY,dispWidth/6,dispWidth/6);
            drawTexture.drawTexture(gl,0,4*dispWidth/5,(int)birdY,dispWidth/6,dispWidth/6);
        } else {
            drawTexture.drawTexture(gl,1,dispWidth/5,(int)birdY,dispWidth/6,dispWidth/6);
            drawTexture.drawTexture(gl,1,4*dispWidth/5,(int)birdY,dispWidth/6,dispWidth/6);
        }

        ++flyingFrame;
        if(flyingFrame > 60){
            flyingFrame = 0;
        }
    }
}
