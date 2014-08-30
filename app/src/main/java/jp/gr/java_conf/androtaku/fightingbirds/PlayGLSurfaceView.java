package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by takuma on 2014/08/18.
 */
public class PlayGLSurfaceView extends GLSurfaceView {
    PlayRenderer playRenderer;

    public PlayGLSurfaceView(Context context,AttributeSet attr) {
        super(context,attr);
        playRenderer = new PlayRenderer(context);
        setRenderer(playRenderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                playRenderer.touchDown(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                playRenderer.touchMove(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_UP:
                playRenderer.touchUp(event.getX(),event.getY());
                break;
        }

        return true;
    }
}
