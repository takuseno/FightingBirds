package jp.gr.java_conf.androtaku.fightingbirds;

import android.util.Log;

import java.util.Random;

/**
 * Created by takuma on 2014/08/18.
 */
public class BirdsControl {
    private int numBirds;
    private int dispWidth,dispHeight;
    private float[] birdsX;
    private float[] birdsY;
    private int[] flyingFrame;
    private float[] flyingAngle;
    private boolean[] isFlying;
    private boolean[] isAlive;

    private float FLYING_SPEED = 30;

    public BirdsControl(int num,int dispWidth,int dispHeight){
        this.numBirds = num;
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;

        birdsX = new float[num];
        birdsY = new float[num];
        isAlive = new boolean[num];
        flyingFrame = new int[num];
        isFlying = new boolean[num];
        flyingAngle = new float[num];
        Random random = new Random();
        for(int i = 0;i < num;++i){
            birdsX[i] = dispWidth/5 * random.nextFloat();
            birdsY[i] = dispHeight * random.nextFloat();
            isAlive[i] = true;
            isFlying[i] = false;
        }
    }

    public int getNearestBird(float x,float y){
        float distance = 10000;
        int num = 0;
        for(int i = 0;i < numBirds;++i){
            if(!isFlying[i]) {
                float temp = (float) Math.sqrt(((x - birdsX[i]) * (x - birdsX[i])) + ((y - birdsY[i]) * (y - birdsY[i])));
                if (temp < distance) {
                    distance = temp;
                    num = i;
                }
            }
        }
        Log.i("select",""+num);
        return num;
    }

    public void flying(){
        for(int i = 0;i < numBirds;++i){
            if(isFlying[i]){
                birdsX[i] += Math.cos(flyingAngle[i])*FLYING_SPEED;
                birdsY[i] += Math.sin(flyingAngle[i])*FLYING_SPEED;
            }
        }
    }

    public void startFlying(int id,float angle){
        isFlying[id] = true;
        flyingAngle[id] = angle;
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

}
