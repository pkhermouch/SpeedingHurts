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
import android.os.SystemClock;
import android.content.Context;

import java.util.ArrayList;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private final int NUM_CARS = 21;
    private final int NUM_LANES = 3;
    // Try to make sure this doesn't truncate a decimal ever
    private final int CARS_PER_LANE = NUM_CARS / NUM_LANES;
    private final float SPACE_BETWEEN_LANES = 1.7f;
    private final float SPACE_BETWEEN_CARS = 3f;
    // Z Offset between lanes of traffic so it looks more natural
    private final float CAR_OFFSET = 2f;

    public static final String vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +

        "attribute vec4 vPosition;" +
        "void main() {" +
        // the matrix must be included as a modifier of gl_Position
        "  gl_Position = uMVPMatrix * vPosition;" +
        "}";
    public static final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "void main() {" +
        "  gl_FragColor = vColor;" +
        "}";

    private static final String TAG = "MyGLRenderer";
    private CarBody[] mCarBody = new CarBody[NUM_CARS];
    private CarWheels[] mCarWheels = new CarWheels[NUM_CARS];
    // X and Z offsets, to implement the "stream of traffic" effect
    private float[] mCarXOffsets = new float[NUM_CARS];
    private float[] mCarZOffsets = new float[NUM_CARS];
    private Sprite background;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mMMatrix = new float[16];
    private float[] temp = new float[16];

    // Car position
    public float carPos = 0;
    // Car speed
    public volatile float carSpeed = 0.005f;
    // Constant to multiply speeds by in setSpeed()
    public static final double speedScale = 0.00025;
    // Variable for transitioning between different speeds smoothly
    // Old Z offset
    private float oldOffset = 0f;
    // Old SystemClock.uptimeMillis()
    private int oldTime = 0;

    // Used to draw the texture
    private final Context mActivityContext;
    public MyGLRenderer(final Context activityContext) {
        super();
        mActivityContext = activityContext;
    }
    
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
            mCarXOffsets[i] = (i % NUM_LANES) * SPACE_BETWEEN_LANES;
            mCarZOffsets[i] = ((i / NUM_LANES) * SPACE_BETWEEN_CARS) +
                ((i % NUM_LANES) * CAR_OFFSET);
        }

        background = new Sprite(mActivityContext);

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
        // Make the cars a bit bigger
        Matrix.scaleM(mMMatrix, 0, 2.4f, 3f, 1f);
        // Orient the plane so that it matches up with the texture
        // Mainly pay attention to making the vanishing points match up, then
        // rotate about the Z axis if necessary
        Matrix.rotateM(mMMatrix, 0, 33, 0f, 1f, 0f);
        Matrix.rotateM(mMMatrix, 0, 18, 1f, 0f, 0f);
        Matrix.rotateM(mMMatrix, 0, 4, 0f, 0f, 1f);
        // Make the cars even bigger
        Matrix.scaleM(mMMatrix, 0, 2.5f, 2.5f, 2f);
        // Move them away from the viewer
        Matrix.translateM(mMMatrix, 0, 0.5f, -0.7f, 0f);
        
        // Apply the model matrix
        float[] cmMVPMatrix = mMVPMatrix.clone();
        float[] cmMMatrix = mMMatrix.clone();

        for (int i = 0; i < NUM_CARS; i++) {
            Matrix.setIdentityM(temp, 0);
            
            // Allow Z coordinates of [val * CARS_PER_LANE + 2, 2]
            // Value of val found through experimentation
            int newTime = (int) SystemClock.uptimeMillis();
            oldOffset += (newTime - oldTime) * carSpeed;
            oldTime = newTime;
            float timeOffset = ((mCarZOffsets[i] + oldOffset) %
                                (3f * CARS_PER_LANE)) -
                (3f * CARS_PER_LANE - 2);

            Matrix.translateM(temp, 0, mCarXOffsets[i], 0, timeOffset);
            Matrix.multiplyMM(mMMatrix, 0, cmMMatrix, 0, temp, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, cmMVPMatrix, 0, mMMatrix, 0);
            mCarWheels[i].draw(mMVPMatrix);
            mCarBody[i].draw(mMVPMatrix);
        }

        // Draw the background
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.scaleM(mMMatrix, 0, 280f, 170f, 1f);
        Matrix.translateM(mMMatrix, 0, -0.5f, 0.5f, -37f);
        Matrix.rotateM(mMMatrix, 0, 180f, 0f, 0f, 1f);
        Matrix.multiplyMM(mMVPMatrix, 0, cmMVPMatrix, 0, mMMatrix, 0);
        background.draw(mMVPMatrix);

    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        // The last two parameters determine how close and far objects can be
        // from the viewer and still be drawn, respectively. These values must
        // be greater than 0, and the near value should not be close to 0
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 0.5f, 40);

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
