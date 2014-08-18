package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by takuma on 2014/08/18.
 */
public class PlayGLSurfaceView extends GLSurfaceView {
    PlayRenderer playRenderer;

    public PlayGLSurfaceView(Context context) {
        super(context);
        playRenderer = new PlayRenderer(context);
        setRenderer(playRenderer);
    }
}
