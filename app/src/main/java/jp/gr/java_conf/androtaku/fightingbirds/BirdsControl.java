package jp.gr.java_conf.androtaku.fightingbirds;

import android.util.Log;

import java.util.Random;

/**
 * Created by takuma on 2014/08/18.
 */
public class BirdsControl {
    private int dispWidth,dispHeight;

    private int numBirds;
    private float[] birdsX;
    private float[] birdsY;

    private boolean[] isAlive;
    private boolean[] isMoving;

    private boolean[] isCluming;
    private int[] clumingFrame;

    private float followSpeed;

    private float touchStartY;

    public BirdsControl(int num,int dispWidth,int dispHeight){
        this.numBirds = num;
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;

        birdsX = new float[num];
        birdsY = new float[num];
        isAlive = new boolean[num];
        isMoving = new boolean[num];
        isCluming = new boolean[num];
        clumingFrame = new int[num];
        for(int i = 0;i < num;++i){
            birdsX[i] = dispWidth/3 - (i*dispWidth/18);
            birdsY[i] = dispHeight/2;
            isAlive[i] = true;
            isMoving[i] = false;
            isCluming[i] = false;
        }

        followSpeed = dispHeight / 100;

    }

    public void touchDown(float y){
        touchStartY = y;
    }

    public void touchMove(float y){
        birdsY[0] = touchStartY - y;
        if(birdsY[0] < 0){
            birdsY[0] = 0;
        }else if(birdsY[0] > dispHeight){
            birdsY[0] = dispHeight;
        }
    }

    public void moveBirds(){
        float startBirdY;
        for(int i = 1;i < numBirds;++i){
            startBirdY = birdsY[i - 1];
            if(birdsY[i] > startBirdY + (followSpeed*8)
                    || birdsY[i] < startBirdY - (followSpeed*8)){
                isMoving[i] = true;
            }
            if(isMoving[i]){
                if(birdsY[i] < birdsY[i - 1]){
                    birdsY[i] += followSpeed;
                }
                else{
                    birdsY[i] -= followSpeed;
                }
                if(birdsY[i] + followSpeed >= birdsY[i - 1] && birdsY[i] - followSpeed <= birdsY[i - 1]){
                    isMoving[i] = false;
                    birdsY[i] = birdsY[i -1];
                }
            }
        }
        for(int i = 0;i < numBirds;++i){
            if(isCluming[i]){
                birdsX[i] += dispWidth/180;
                ++clumingFrame[i];
                if(clumingFrame[i] == 10){
                    isCluming[i] = false;
                }
            }
        }
    }

    public void clumingBirds(int id){
        for(int i = id + 1;i < numBirds;++i){
            if(isAlive[i]) {
                isAlive[i - 1] = true;
                isAlive[i] = false;
                birdsY[i - 1] = birdsY[i];
                birdsX[i - 1] = birdsX[i];
                isCluming[i - 1] = true;
                clumingFrame[i - 1] = 0;
                Log.i("bird", "cluming:" + i);
            }
        }
    }

    public float[] getBirdsX(){
        return birdsX;
    }

    public float[] getBirdsY(){
        return birdsY;
    }

    public boolean[] getIsAlive(){
        return  isAlive;
    }

    public void hit(int id){
        isAlive[id] = false;
        clumingBirds(id);
    }
}
