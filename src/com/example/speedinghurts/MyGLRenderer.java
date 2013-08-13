package com.example.speedinghurts;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private final int NUM_CARS = 21;
    private final int NUM_LANES = 3;
    // Try to make sure this doesn't truncate a decimal ever
    private final int CARS_PER_LANE = NUM_CARS / NUM_LANES;
    
    private static final String TAG = "MyGLRenderer";
    private CarBody[] mCarBody = new CarBody[NUM_CARS];
    private CarWheels[] mCarWheels = new CarWheels[NUM_CARS];
    // X and Z offsets, to implement the "stream of traffic" effect
    private float[] mCarXOffsets = new float[NUM_CARS];
    private float[] mCarZOffsets = new float[NUM_CARS];
    private Square road;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mMMatrix = new float[16];
    private float[] temp = new float[16];

    // Declare as volatile because we are updating it from another thread
    public volatile float mAngleY = 0;
    public volatile float mAngleX = 0;
    // Car position
    public float carPos = 0;
    // Car speed
    public volatile float carSpeed = 0.02f;
    // Constant to multiply speeds by in setSpeed()
    public static final float speedScale = .0015f;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Do backface culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glFrontFace(GLES20.GL_CCW);
        // Why using GL_BACK won't work is a mystery
        GLES20.glCullFace(GLES20.GL_BACK);
        // Enable depth buffer
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);

        for (int i = 0; i < NUM_CARS; i++) {
            mCarBody[i] = new CarBody((float) Math.random(),
                                      (float) Math.random(),
                                      (float) Math.random());
            mCarWheels[i] = new CarWheels();
        }
        
        // Create transformation matrices for each car
        for (int i = 0; i < NUM_CARS; i++) {
            mCarXOffsets[i] = i % NUM_LANES;
            mCarZOffsets[i] = (i / NUM_LANES) * -2;
        }

        road = new Square(0.9f, 0.9f, 0.9f);
        
    }

    @Override
    public void onDrawFrame(GL10 unused) {

        // Draw background color
        GLES20.glClearDepthf(1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

        // Clear the model matrix
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.rotateM(mMMatrix, 0, 35, 0f, 1f, 0f);
        Matrix.rotateM(mMMatrix, 0, 20, 1f, 0f, 0f);
        Matrix.rotateM(mMMatrix, 0, mAngleY, 0f, 1f, 0f);
        Matrix.rotateM(mMMatrix, 0, mAngleX, 1f, 0f, 0f);

        // Apply the model matrix
        float[] cmMVPMatrix = mMVPMatrix.clone();
        float[] cmMMatrix = mMMatrix.clone();

        for (int i = 0; i < NUM_CARS; i++) {
            Matrix.setIdentityM(temp, 0);
            Matrix.translateM(temp, 0, mCarXOffsets[i], 0, mCarZOffsets[i]);
            Matrix.multiplyMM(mMMatrix, 0, cmMMatrix, 0, temp, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, cmMVPMatrix, 0, mMMatrix, 0);
            // Allow Z coordinates of [-2 * CARS_PER_LANE + 2, 2]
            mCarZOffsets[i] = ((mCarZOffsets[i] + (2 * CARS_PER_LANE - 2) + carSpeed) %
                               (2 * CARS_PER_LANE)) -
                (2 * CARS_PER_LANE - 2);
            mCarWheels[i].draw(mMVPMatrix);
            mCarBody[i].draw(mMVPMatrix);
        }

        Matrix.setIdentityM(temp, 0);
        Matrix.translateM(temp, 0, 0f, -0.2f, 0f);
        //Matrix.rotateM(temp, 0, 20f, 0f, 0f, 1f);
        //Matrix.scaleM(temp, 0, 3, 10, 1);
        Matrix.multiplyMM(mMMatrix, 0, cmMMatrix, 0, temp, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, cmMVPMatrix, 0, mMMatrix, 0);
        road.draw(mMVPMatrix);
        
        // Create a rotation for the triangle
        //long time = SystemClock.uptimeMillis() % 4000L;
        //float angle = 0.090f * ((int) time);

    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        /*
          Near / far clipping planes, in terms of units away from the camera
          Cannot be negative or 0, should not be close to 0
        */
        //Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 21);

    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /*
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}
