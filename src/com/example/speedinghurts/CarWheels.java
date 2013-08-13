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

class CarWheels {

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    // Defined in constructor
    static float carWheelsCoords[];
    private int vertexCount;// = circleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.5f, 0.5f, 0.5f, 1.0f };

    private final float SIDES = 20f;
    private final float RADIUS = 0.2f;
    private final float WIDTH = 0.1f;

    public CarWheels() {
        // Create the array of coordinates using math, and set the vertex count
        ArrayList<Float> coords = new ArrayList<Float>();

        // Non-drive side front wheel
        float sin1, sin2, cos1, cos2;
        for (int i = 0; i < SIDES; i++) {
            sin1 = (float) Math.sin((i / SIDES) * 2 * Math.PI);
            cos1 = (float) Math.cos((i / SIDES) * 2 * Math.PI);
            i++;
            sin2 = (float) Math.sin((i / SIDES) * 2 * Math.PI);
            cos2 = (float) Math.cos((i / SIDES) * 2 * Math.PI);
            i--;
            
            coords.add(-0.3f); coords.add(-0.2f); coords.add(0.05f);
            coords.add(-0.3f);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + 0.05f);
            coords.add(-0.3f);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + 0.05f);
            // Cylinder addition
            coords.add(-0.3f);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + 0.05f);
            coords.add(-0.3f + WIDTH);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + 0.05f);
            coords.add(-0.3f + WIDTH);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + 0.05f);
            coords.add(-0.3f);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + 0.05f);
            coords.add(-0.3f + WIDTH);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + 0.05f);
            coords.add(-0.3f);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + 0.05f);
            // Opposite side
            coords.add(-0.3f + WIDTH); coords.add(-0.2f); coords.add(0.05f);
            coords.add(-0.3f + WIDTH);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + 0.05f);
            coords.add(-0.3f + WIDTH);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + 0.05f);
        }

        // Drive side front wheel
        for (int i = 0; i < SIDES; i++) {
            sin1 = (float) Math.sin((i / SIDES) * 2 * Math.PI);
            cos1 = (float) Math.cos((i / SIDES) * 2 * Math.PI);
            i++;
            sin2 = (float) Math.sin((i / SIDES) * 2 * Math.PI);
            cos2 = (float) Math.cos((i / SIDES) * 2 * Math.PI);
            i--;
            
            coords.add(0.2f); coords.add(-0.2f); coords.add(0.05f);
            coords.add(0.2f);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + 0.05f);
            coords.add(0.2f);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + 0.05f);
            // Cylinder addition
            coords.add(0.2f);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + 0.05f);
            coords.add(0.2f + WIDTH);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + 0.05f);
            coords.add(0.2f + WIDTH);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + 0.05f);
            coords.add(0.2f);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + 0.05f);
            coords.add(0.2f + WIDTH);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + 0.05f);
            coords.add(0.2f);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + 0.05f);
            // Opposite side
            coords.add(0.2f + WIDTH); coords.add(-0.2f); coords.add(0.05f);
            coords.add(0.2f + WIDTH);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + 0.05f);
            coords.add(0.2f + WIDTH);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + 0.05f);
        }

        // Non-drive side back wheel
        for (int i = 0; i < SIDES; i++) {
            sin1 = (float) Math.sin((i / SIDES) * 2 * Math.PI);
            cos1 = (float) Math.cos((i / SIDES) * 2 * Math.PI);
            i++;
            sin2 = (float) Math.sin((i / SIDES) * 2 * Math.PI);
            cos2 = (float) Math.cos((i / SIDES) * 2 * Math.PI);
            i--;
            
            coords.add(-0.3f); coords.add(-0.2f); coords.add(-0.95f);
            coords.add(-0.3f);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + -0.95f);
            coords.add(-0.3f);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + -0.95f);
            // Cylinder addition
            coords.add(-0.3f);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + -0.95f);
            coords.add(-0.3f + WIDTH);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + -0.95f);
            coords.add(-0.3f + WIDTH);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + -0.95f);
            coords.add(-0.3f);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + -0.95f);
            coords.add(-0.3f + WIDTH);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + -0.95f);
            coords.add(-0.3f);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + -0.95f);
            // Opposite side
            coords.add(-0.3f + WIDTH); coords.add(-0.2f); coords.add(-0.95f);
            coords.add(-0.3f + WIDTH);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + -0.95f);
            coords.add(-0.3f + WIDTH);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + -0.95f);
        }

        // Drive side back wheel
        for (int i = 0; i < SIDES; i++) {
            sin1 = (float) Math.sin((i / SIDES) * 2 * Math.PI);
            cos1 = (float) Math.cos((i / SIDES) * 2 * Math.PI);
            i++;
            sin2 = (float) Math.sin((i / SIDES) * 2 * Math.PI);
            cos2 = (float) Math.cos((i / SIDES) * 2 * Math.PI);
            i--;
            
            coords.add(0.2f); coords.add(-0.2f); coords.add(-0.95f);
            coords.add(0.2f);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + -0.95f);
            coords.add(0.2f);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + -0.95f);
            // Cylinder addition
            coords.add(0.2f);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + -0.95f);
            coords.add(0.2f + WIDTH);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + -0.95f);
            coords.add(0.2f + WIDTH);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + -0.95f);
            coords.add(0.2f);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + -0.95f);
            coords.add(0.2f + WIDTH);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + -0.95f);
            coords.add(0.2f);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + -0.95f);
            // Opposite side
            coords.add(0.2f + WIDTH); coords.add(-0.2f); coords.add(-0.95f);
            coords.add(0.2f + WIDTH);
            coords.add(RADIUS * sin2 + -0.2f);
            coords.add(RADIUS * cos2 + -0.95f);
            coords.add(0.2f + WIDTH);
            coords.add(RADIUS * sin1 + -0.2f);
            coords.add(RADIUS * cos1 + -0.95f);
        }

        carWheelsCoords = new float[coords.size()];
        for (int i = 0; i < carWheelsCoords.length; i++) {
            carWheelsCoords[i] = coords.get(i);
        }
        vertexCount = carWheelsCoords.length / COORDS_PER_VERTEX;
        
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                carWheelsCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(carWheelsCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                                   MyGLRenderer.vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                                     MyGLRenderer.fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
