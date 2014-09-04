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
    private DrawTexture drawTexture;
    private int[] textureIds = {R.drawable.bird01,R.drawable.bird02};

    private int dispWidth,dispHeight;

    public int flyingFrame = 0;

    BirdsControl birdsControl;

    public float SIZE_BIRD;

    public Bird(Context context,GL10 gl,int dispWidth,int dispHeight){
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;
        birdsControl = new BirdsControl(6,dispWidth,dispHeight);
        drawTexture = new DrawTexture(context,2,dispWidth,dispHeight);
        drawTexture.setTexture(textureIds,gl);
        SIZE_BIRD = dispWidth/18;
    }

    public void draw(GL10 gl){
        float[] birdsX = birdsControl.getBirdsX();
        float[] birdsY = birdsControl.getBirdsY();
        boolean[] isAlive = birdsControl.getIsAlive();
        for(int i = 0;i < isAlive.length;++i) {
            if(isAlive[i]) {
                if (flyingFrame < 30) {
                    drawTexture.drawTexture(gl,0,(int)birdsX[i],(int)birdsY[i],(int)SIZE_BIRD,(int)SIZE_BIRD);
                } else {
                    drawTexture.drawTexture(gl,1,(int)birdsX[i],(int)birdsY[i],(int)SIZE_BIRD,(int)SIZE_BIRD);
                }
            }
        }

        float[] fallingBirdsX = birdsControl.getFallingBirdsX();
        float[] fallingBirdsY = birdsControl.getFallingBirdsY();
        boolean[] isFalling = birdsControl.getIsFalling();
        for(int i = 0;i < isFalling.length;++i) {
            if(isFalling[i]) {
                drawTexture.drawTexture(gl,0,(int)fallingBirdsX[i],(int)fallingBirdsY[i],(int)SIZE_BIRD,(int)SIZE_BIRD);
            }
        }

        birdsControl.moveBirds();

        ++flyingFrame;
        if(flyingFrame > 60){
            flyingFrame = 0;
        }
    }

    public void touchDown(float y){
        birdsControl.touchDown(y);
    }

    public void touchMove(float y){
        birdsControl.touchMove(y);
    }

    public boolean[] getIsAlive(){
        return  birdsControl.getIsAlive();
    }

    public int getAliveNum(){
        int counter = 0;
        boolean[] isAlive = birdsControl.getIsAlive();
        for(int i = 0;i < isAlive.length;++i){
            if(isAlive[i])
                ++counter;
        }
        return counter;
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
