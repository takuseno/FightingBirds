package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by takuma on 2014/08/26.
 */
public class ResultGLSurfaceView extends GLSurfaceView {

    ResultRenderer resultRenderer;

    public ResultGLSurfaceView(Context context,AttributeSet attr){
        super(context,attr);
        resultRenderer = new ResultRenderer(context);
        setRenderer(resultRenderer);
    }


}
