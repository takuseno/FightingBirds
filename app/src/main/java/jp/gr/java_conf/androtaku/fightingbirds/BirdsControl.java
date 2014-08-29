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

    private float[] flyingAngle;
    private boolean[] isFlying;

    private boolean[] isAlive;

    private boolean[] isReturning;
    private float[] returnAngle;
    private int[] returnFrame;
    private float[] returnSpeed;

    private float FLYING_SPEED;

    private boolean[] isForward;

    private boolean isFever;

    public BirdsControl(int num,int dispWidth,int dispHeight){
        this.numBirds = num;
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;

        birdsX = new float[num];
        birdsY = new float[num];
        isAlive = new boolean[num];
        isFlying = new boolean[num];
        flyingAngle = new float[num];
        isReturning = new boolean[num];
        returnAngle = new float[num];
        returnFrame = new int[num];
        returnSpeed = new float[num];
        isForward = new boolean[num];
        Random random = new Random();
        for(int i = 0;i < num;++i){
            birdsX[i] = dispWidth/5 * random.nextFloat() + (dispWidth/20);
            birdsY[i] = 2*dispHeight/3 * random.nextFloat() + (dispHeight/6);
            isAlive[i] = true;
            isFlying[i] = false;
            isReturning[i] = false;
            isForward[i] = true;
        }

        FLYING_SPEED = dispWidth/30;
        isFever = false;
    }

    public int getNearestBird(float x,float y){
        float distance = 10000;
        int num = 0;
        for(int i = 0;i < numBirds;++i){
            if(!isFlying[i] && isAlive[i]) {
                float temp = (float) Math.sqrt(((x - birdsX[i]) * (x - birdsX[i])) + ((y - birdsY[i]) * (y - birdsY[i])));
                if (temp < distance) {
                    distance = temp;
                    num = i;
                }
            }
        }
        return num;
    }

    public void flying(){
        for(int i = 0;i < numBirds;++i){
            if(isFlying[i]){
                if(isForward[i]) {
                    birdsX[i] += Math.cos(flyingAngle[i]) * FLYING_SPEED;
                    birdsY[i] += Math.sin(flyingAngle[i]) * FLYING_SPEED;
                }
                else{
                    birdsX[i] -= Math.cos(flyingAngle[i]) * FLYING_SPEED;
                    birdsY[i] -= Math.sin(flyingAngle[i]) * FLYING_SPEED;
                }
                if(birdsX[i] > dispWidth || birdsX[i] < 0 || birdsY[i] < -dispWidth/10 || birdsY[i] > dispHeight){
                    if(!isFever) {
                        isAlive[i] = false;
                        isFlying[i] = false;
                    }
                    else{
                        isReturning[i] = true;
                        returnFrame[i] = 0;
                        Random random = new Random();
                        float returnX = dispWidth/5 * random.nextFloat() + (dispWidth/20);
                        float returnY = 2*dispHeight/3 * random.nextFloat() + (dispHeight/6);
                        returnAngle[i] = (float)Math.atan((birdsY[i] - returnY)/(birdsX[i] - returnX));
                        float distance = (float)Math.sqrt(((returnX - birdsX[i])*(returnX - birdsX[i])) + ((returnY - birdsY[i])*(returnY - birdsY[i])));
                        returnSpeed[i] = distance/20;
                        isFlying[i] = false;
                    }
                }
            }
        }
    }

    public void returning(){
        for(int i = 0;i < numBirds;++i){
            if(isReturning[i]){
                birdsX[i] -= Math.cos(returnAngle[i])*returnSpeed[i];
                birdsY[i] -= Math.sin(returnAngle[i])*returnSpeed[i];
                ++returnFrame[i];
                if(returnFrame[i] == 19){
                    isReturning[i] = false;
                }
            }
        }
    }

    public int[] getAliveBirdsId(){
        int num = 0;
        for(int i = 0;i < numBirds;++i){
            if(isAlive[i] && !isReturning[i]){
                ++num;
            }
        }
        int[] temp = new int[num];
        int count = 0;
        for(int i = 0;i < numBirds;++i){
            if(isAlive[i] && !isReturning[i]){
                temp[count] = i;
                ++count;
            }
        }
        return temp;
    }

    public void startFlying(int id,float angle,boolean forward){
        isFlying[id] = true;
        flyingAngle[id] = angle;
        isForward[id] = forward;
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

    public boolean[] getIsFlying(){
        return isFlying;
    }

    public void hit(int id){
        if(isFlying[id]){
            isReturning[id] = true;
            returnFrame[id] = 0;
            Random random = new Random();
            float returnX = dispWidth/5 * random.nextFloat() + (dispWidth/20);
            float returnY = 2*dispHeight/3 * random.nextFloat() + (dispHeight/6);
            returnAngle[id] = (float)Math.atan((birdsY[id] - returnY)/(birdsX[id] - returnX));
            float distance = (float)Math.sqrt(((returnX - birdsX[id])*(returnX - birdsX[id])) + ((returnY - birdsY[id])*(returnY - birdsY[id])));
            returnSpeed[id] = distance/20;
            isFlying[id] = false;
        }
        else if(!isFever) {
            isAlive[id] = false;
        }
    }

    public void enterFever(){
        isFever = true;
    }

    public void overFever(){
        isFever = false;
    }
}
