package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by takuma on 2014/08/18.
 */
public class PlayRenderer implements GLSurfaceView.Renderer {
    PlaySequence playSequence;

    SoundPool soundPool;

    private Context context;
    private boolean initialized;

    public PlayRenderer(Context context){
        playSequence = new PlaySequence();
        this.context = context;
        initialized = false;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);

        if(!initialized) {
            playSequence.init(context, gl, width, height);
            initialized = true;
        }
        else{
            playSequence.setTexture(gl);
        }

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//            pafu = soundPool.load(context,R.raw.pafu,0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //背景色をクリア
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //頂点配列の有効化
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        //テクスチャ機能ON
        gl.glEnable(GL10.GL_TEXTURE_2D);
        //ブレンド可能に
        gl.glEnable(GL10.GL_ALPHA_TEST);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        playSequence.draw(gl);
    }

    public void touchDown(float x,float y){
        playSequence.touchDown(x,y);
    }

    public void touchMove(float x,float y){
        playSequence.touchMove(x,y);
    }

    public void touchUp(float x,float y){
        playSequence.touchUp(x,y);
    }
}
