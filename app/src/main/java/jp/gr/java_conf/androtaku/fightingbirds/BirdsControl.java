package jp.gr.java_conf.androtaku.fightingbirds;

import android.util.Log;

import java.util.Random;

/**
 * Created by takuma on 2014/08/18.
 */
public class BirdsControl {
    private int numBirds;
    private float[] birdsX;
    private float[] birdsY;

    private boolean[] isAlive;
    private boolean[] isMoving;

    private float followSpeed;

    public BirdsControl(int num,int dispWidth,int dispHeight){
        this.numBirds = num;

        birdsX = new float[num];
        birdsY = new float[num];
        isAlive = new boolean[num];
        isMoving = new boolean[num];
        for(int i = 0;i < num;++i){
            birdsX[i] = dispWidth/3 - (i*dispWidth/10);
            birdsY[i] = dispHeight/2;
            isAlive[i] = true;
            isMoving[i] = false;
        }

        followSpeed = dispHeight / 100;
    }

    public void touchDown(float y){
        birdsY[0] = y;
    }

    public void touchMove(float y){
        birdsY[0] = y;
    }

    public void moveBirds(){
        float startBirdY;
        for(int i = 1;i < numBirds;++i){
            startBirdY = birdsY[i - 1];
            if(birdsY[i] > startBirdY + (followSpeed*10)
                    || birdsY[i] < startBirdY - (followSpeed*10)){
                isMoving[i] = true;
                Log.i("moving","start:" + i);
            }
            if(isMoving[i]){
                if(birdsY[i] < birdsY[i - 1]){
                    birdsY[i] += followSpeed;
                }
                else{
                    birdsY[i] -= followSpeed;
                }
                Log.i("moving","moving:" + i);
                if(birdsY[i] == birdsY[i - 1]){
                    isMoving[i] = false;
                    Log.i("moving","stop:" + i);
                }
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
    }
}
