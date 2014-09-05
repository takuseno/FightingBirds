package jp.gr.java_conf.androtaku.fightingbirds;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by takuma on 2014/08/26.
 */
public class ResultGLSurfaceView extends GLSurfaceView {

    ResultRenderer resultRenderer;

    private boolean isNewRecord = false;

    public ResultGLSurfaceView(Context context,AttributeSet attr){
        super(context,attr);
        resultRenderer = new ResultRenderer(context);
        resultRenderer.setNewRecord(isNewRecord);
        setRenderer(resultRenderer);
    }

    public void setNewRecord(boolean isNewRecord){
        this.isNewRecord = isNewRecord;
    }

}
