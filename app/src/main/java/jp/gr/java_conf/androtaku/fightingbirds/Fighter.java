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
 * Created by takuma on 2014/08/20.
 */
public class Fighter {
    private int textureNo;
    private int dispWidth,dispHeight;

    private float fighterX;
    private float fighterY;

    private boolean isEntering;
    private float enteringSpeed;
    private boolean isUp;
    private boolean isDown;
    private float upDownSpeed;
    private int hitCounter;

    Missile missile;

    private float[] uvBuffer = {
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f
    };

    public Fighter(int dispWidth,int dispHeight){
        this.dispWidth = dispWidth;
        this.dispHeight = dispHeight;
        init();
        missile = new Missile(dispWidth,dispHeight);
    }

    public void init(){
        fighterX = 2*dispWidth;
        fighterY = dispHeight/2 - (dispWidth/8);
        enteringSpeed = dispWidth/120;
        upDownSpeed = dispHeight/240;
        isEntering = true;
        isUp = true;
        isDown = false;
        hitCounter = 0;
    }

    public void setTexture(GL10 gl,Context context){
        int[] textureID = new int[1];
        gl.glGenTextures(1,textureID,0);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fighter);
        textureNo = textureID[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,GL10.GL_REPLACE);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        missile.setTexture(gl,context);
    }

    //頂点バッファの生成
    private FloatBuffer makeVertexBuffer(int x,int y,int w,int h) {
        //ウィンドウ座標を正規化デバイス座標に変換
        float left  =((float)x/(float)dispWidth)*2.0f-1.0f;
        float top   =((float)y/(float)dispHeight)*2.0f-1.0f;
        float right =((float)(x+w)/(float)dispWidth)*2.0f-1.0f;
        float bottom=((float)(y+h)/(float)dispHeight)*2.0f-1.0f;
        top   =-top;
        bottom=-bottom;

        //頂点バッファの生成
        float[] vertexs={
                left, top,   0.0f,//頂点0
                left, bottom,0.0f,//頂点1
                right,top,   0.0f,//頂点2
                right,bottom,0.0f,//頂点3
        };
        return makeFloatBuffer(vertexs);
    }

    //float配列をFloatBufferに変換
    private FloatBuffer makeFloatBuffer(float[] array) {
        FloatBuffer fb= ByteBuffer.allocateDirect(array.length * 4).order(
                ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(array).position(0);
        return fb;
    }

    public void draw(GL10 gl){
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNo);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(uvBuffer));
        FloatBuffer vertexBuffer = makeVertexBuffer((int)fighterX - (dispWidth/4),(int)fighterY - (dispWidth/8), dispWidth/2, dispWidth/4);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        enter();
        upDown();

        missile.draw(gl);
        missile.fire(fighterY);
    }

    public void enter(){
        if(isEntering){
            fighterX -= enteringSpeed;
            if(fighterX < dispWidth){
                isEntering = false;
            }
        }
    }

    public void upDown(){
        if(!isEntering){
            if(isUp && !isDown){
                fighterY -= upDownSpeed;
                if(fighterY < dispWidth/24){
                    isUp = false;
                    isDown = true;
                }
            }
            else{
                fighterY += upDownSpeed;
                if(fighterY > dispHeight - (dispWidth/24)){
                    isUp = true;
                    isDown = false;
                }
            }
        }
    }

    public float getFighterX(){
        return fighterX;
    }

    public float getFighterY(){
        return fighterY;
    }

    public void hit(){
        ++hitCounter;
    }

    public boolean isOver(){
        if(hitCounter > 3){
            return true;
        }
        return false;
    }

    public float getMissileX(){
        return missile.getMissileX();
    }

    public float getMissileY(){
        return missile.getMissileY();
    }

    public boolean getLaunching(){
        return missile.getLaunching();
    }

    public void missileHit(){
        missile.hit();
    }
}
