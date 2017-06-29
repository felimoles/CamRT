package com.example.dopa.opengltest21api;

import android.content.Context;
import android.opengl.GLES20;

import com.example.dopa.opengltest21api.MyGLUtils;
import com.example.dopa.opengltest21api.R;


public class filterCRT_v1_0  extends CameraFilter{


    private int program;

    public filterCRT_v1_0(Context context) {
        super(context);

        // Build shaders
        program = MyGLUtils.buildProgram(context, R.raw.vertext, R.raw.crt_v1_0);
    }

    @Override
    public void onDraw(int cameraTexId, int canvasWidth, int canvasHeight) {
        setupShaderInputs(program,
                new int[]{canvasWidth, canvasHeight},
                new int[]{cameraTexId},
                new int[][]{});
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
