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
    //declare class
    private DrawTexture drawTexture;
    private BirdsControl birdsControl;

    //declare ids of texture
    private int[] textureIds = {R.drawable.bird01,R.drawable.bird02};

    //size of birds
    public float SIZE_BIRD;
    //frame
    public int flyingFrame = 0;


    public Bird(Context context,GL10 gl,int dispWidth,int dispHeight){
        birdsControl = new BirdsControl(6,dispWidth,dispHeight);
        drawTexture = new DrawTexture(context,2,dispWidth,dispHeight);
        drawTexture.setTexture(textureIds,gl);
        SIZE_BIRD = dispWidth/18;
    }

    //function of setting texture when resume
    public void setTexture(GL10 gl){
        drawTexture.setTexture(textureIds,gl);
    }

    //function of drawing birds
    public void draw(GL10 gl){
        //get birds positions
        float[] birdsX = birdsControl.getBirdsX();
        float[] birdsY = birdsControl.getBirdsY();
        //get flags of birds lives
        boolean[] isAlive = birdsControl.getIsAlive();
        //draw alive birds
        for(int i = 0;i < isAlive.length;++i) {
            //check birds alive
            if(isAlive[i]) {
                //draw
                if (flyingFrame < 30) {
                    drawTexture.drawTexture(gl,0,(int)birdsX[i],(int)birdsY[i],(int)SIZE_BIRD,(int)SIZE_BIRD);
                } else {
                    drawTexture.drawTexture(gl,1,(int)birdsX[i],(int)birdsY[i],(int)SIZE_BIRD,(int)SIZE_BIRD);
                }
            }
        }

        //get falling birds positions
        float[] fallingBirdsX = birdsControl.getFallingBirdsX();
        float[] fallingBirdsY = birdsControl.getFallingBirdsY();
        //get flags of birds falling
        boolean[] isFalling = birdsControl.getIsFalling();
        //draw falling birds
        for(int i = 0;i < isFalling.length;++i) {
            //check birds falling
            if(isFalling[i]) {
                //draw
                drawTexture.drawTexture(gl,0,(int)fallingBirdsX[i],(int)fallingBirdsY[i],(int)SIZE_BIRD,(int)SIZE_BIRD);
            }
        }

        //move birds
        birdsControl.moveBirds();

        //add frame
        ++flyingFrame;
        if(flyingFrame > 60){
            flyingFrame = 0;
        }
    }

    //function of getting flags of alive birds
    public boolean[] getIsAlive(){
        return  birdsControl.getIsAlive();
    }
    //function of getting number of alive birds
    public int getNumAlive(){
        int counter = 0;
        boolean[] isAlive = birdsControl.getIsAlive();
        for(int i = 0;i < isAlive.length;++i){
            if(isAlive[i])
                ++counter;
        }
        return counter;
    }

    //function of getting x positions
    public float[] getBirdsX(){
        return  birdsControl.getBirdsX();
    }
    //function of getting y positions
    public float[] getBirdsY(){
        return birdsControl.getBirdsY();
    }

    //function of treating hit birds
    public void hit(int id){
        birdsControl.hit(id);
    }

    //function of treating touch down
    public void touchDown(float y){
        birdsControl.touchDown(y);
    }
    //function of treating touch move
    public void touchMove(float y){
        birdsControl.touchMove(y);
    }
}
