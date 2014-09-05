package jp.gr.java_conf.androtaku.fightingbirds;

import android.util.Log;

import java.util.Random;

/**
 * Created by takuma on 2014/08/18.
 */
public class BirdsControl {
    //declare global variables
    private int dispWidth,dispHeight;

    //number of birds
    private int numBirds;

    //screen position of touching down
    private float touchStartY;

    //positions of birds
    private float[] birdsX;
    private float[] birdsY;

    //positions of falling birds
    private float[] fallingBirdsX;
    private float[] fallingBirdsY;

    //speed of cluming
    private float closerSpeed;

    //flags
    private boolean[] isAlive;
    private boolean[] isMoving;
    private boolean[] isCraming;
    private boolean[] isFalling;

    //frames
    private int[] fallingFrame;
    private int[] cramingFrame;

    //index of falling birds
    private int fallingIndex;

    public BirdsControl(int num,int dispWidth,int dispHeight){
        this.numBirds = num;
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;
        birdsX = new float[num];
        birdsY = new float[num];
        fallingBirdsX = new float[num];
        fallingBirdsY = new float[num];
        isAlive = new boolean[num];
        isMoving = new boolean[num];
        isCraming = new boolean[num];
        isFalling = new boolean[num];
        fallingFrame = new int[num];
        cramingFrame = new int[num];
        for(int i = 0;i < num;++i){
            birdsX[i] = dispWidth/3 - (i*dispWidth/18);
            birdsY[i] = dispHeight/2;
            isAlive[i] = true;
            isMoving[i] = false;
            isCraming[i] = false;
            isFalling[i] = false;
        }
        closerSpeed = dispHeight / 100;
        fallingIndex = 0;
    }

    //function of move birds
    public void moveBirds(){
        //position of front bird y position
        float frontBirdY;
        //vertical moving
        for(int i = 1;i < numBirds;++i){
            frontBirdY = birdsY[i - 1];
            //check if front bird is far
            if(birdsY[i] > frontBirdY + (closerSpeed*8)
                    || birdsY[i] < frontBirdY - (closerSpeed*8)){
                isMoving[i] = true;
            }
            //check bird moving
            if(isMoving[i]){
                //check if front bird is upper
                if(birdsY[i] < birdsY[i - 1]){
                    birdsY[i] += closerSpeed;
                }
                //check if front bird is downer
                else{
                    birdsY[i] -= closerSpeed;
                }
                //check if front bird is near
                if(birdsY[i] + closerSpeed >= birdsY[i - 1]
                        && birdsY[i] - closerSpeed <= birdsY[i - 1]){
                    isMoving[i] = false;
                    birdsY[i] = birdsY[i -1];
                }
            }
        }

        //horizontal moving
        for(int i = 0;i < numBirds;++i){
            //check bird closer
            if(isCraming[i]){
                birdsX[i] += dispWidth/180;
                ++cramingFrame[i];
                //check if front bird is near
                if(cramingFrame[i] == 10)
                    isCraming[i] = false;
            }
        }

        //moving falling birds
        for(int i = 0;i < fallingIndex;++i){
            //check bird falling
            if(isFalling[i]){
                fallingBirdsX[i] -= dispWidth/100;
                fallingBirdsY[i] -= dispHeight/100 - (dispHeight/400*fallingFrame[i]);
                ++fallingFrame[i];
                //check bird outside screen
                if(fallingBirdsY[i] > dispHeight)
                    isFalling[i] = false;
            }
        }
    }

    //function of craming array ids
    public void cramingBirds(int id){
        for(int i = id + 1;i < numBirds;++i){
            //check birds alive
            if(isAlive[i]) {
                isAlive[i - 1] = true;
                isAlive[i] = false;
                birdsY[i - 1] = birdsY[i];
                birdsX[i - 1] = birdsX[i];
                isCraming[i - 1] = true;
                cramingFrame[i - 1] = 0;
            }
        }
    }

    //function of getting position x
    public float[] getBirdsX(){
        return birdsX;
    }
    //function of getting position y
    public float[] getBirdsY(){
        return birdsY;
    }

    //function of getting flags of birds alive
    public boolean[] getIsAlive(){
        return  isAlive;
    }

    //function of getting falling birds position x
    public float[] getFallingBirdsX(){
        return fallingBirdsX;
    }
    //function of getting falling birds position y
    public float[] getFallingBirdsY(){
        return fallingBirdsY;
    }

    //function of gettin flags of birds falling
    public boolean[] getIsFalling(){
        return isFalling;
    }

    //function of treating hit birds
    public void hit(int id){
        isAlive[id] = false;
        isFalling[fallingIndex] = true;
        fallingFrame[fallingIndex] = 0;
        fallingBirdsX[fallingIndex] = birdsX[id];
        fallingBirdsY[fallingIndex] = birdsY[id];
        ++fallingIndex;
        cramingBirds(id);
    }

    //function of treating touch down
    public void touchDown(float y){
        touchStartY = y;
    }
    //function of treating touch move
    public void touchMove(float y){
        birdsY[0] -= touchStartY - y;
        touchStartY = y;
        if(birdsY[0] < 0){
            birdsY[0] = 0;
        }else if(birdsY[0] > dispHeight){
            birdsY[0] = dispHeight;
        }
    }
}
