package jp.gr.java_conf.androtaku.fightingbirds;

import java.util.Random;

/**
 * Created by takuma on 2014/08/18.
 */
public class BirdsControl {
    private int numBirds;
    private int dispWidth,dispHeight;
    private float[] birdsX;
    private float[] birdsY;
    private boolean[] isAlive;

    public BirdsControl(int num,int dispWidth,int dispHeight){
        this.numBirds = num;
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;

        birdsX = new float[num];
        birdsY = new float[num];
        isAlive = new boolean[num];
        Random random = new Random();
        for(int i = 0;i < num;++i){
            birdsX[i] = dispWidth/5 * random.nextFloat();
            birdsY[i] = dispHeight * random.nextFloat();
            isAlive[i] = true;
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

}
