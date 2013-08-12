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

class CarBody {

    private final String vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +

        "attribute vec4 vPosition;" +
        "void main() {" +
        // the matrix must be included as a modifier of gl_Position
        "  gl_Position = uMVPMatrix * vPosition;" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "void main() {" +
        "  gl_FragColor = vColor;" +
        "}";

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    // Defined in constructor
    static float carBodyCoords[];
    private int vertexCount;// = circleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 1f, 0f, 0f, 1.0f };

    public CarBody() {
        // Create the array of coordinates using math, and set the vertex count
        ArrayList<Float> coords = new ArrayList<Float>();

        // Front
        coords.add(0.4f); coords.add(0.2f); coords.add(0.4f);
        coords.add(-0.4f); coords.add(-0.2f); coords.add(0.4f);
        coords.add(0.4f); coords.add(-0.2f); coords.add(0.4f);
        coords.add(0.4f); coords.add(0.2f); coords.add(0.4f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(0.4f);
        coords.add(-0.4f); coords.add(-0.2f); coords.add(0.4f);
        // Hood
        coords.add(0.4f); coords.add(0.2f); coords.add(0.4f);
        coords.add(0.4f); coords.add(0.2f); coords.add(0f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(0.4f);
        coords.add(0.4f); coords.add(0.2f); coords.add(0f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(0f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(0.4f);
        // Windshield
        coords.add(0.4f); coords.add(0.2f); coords.add(0f);
        coords.add(0.4f); coords.add(0.5f); coords.add(-0.2f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(0f);
        coords.add(0.4f); coords.add(0.5f); coords.add(-0.2f);
        coords.add(-0.4f); coords.add(0.5f); coords.add(-0.2f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(0f);
        // Triangles to accomodate slant
        coords.add(-0.4f); coords.add(0.2f); coords.add(0f);
        coords.add(-0.4f); coords.add(0.5f); coords.add(-0.2f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(-0.2f);
        coords.add(0.4f); coords.add(0.2f); coords.add(-0.2f);
        coords.add(0.4f); coords.add(0.5f); coords.add(-0.2f);
        coords.add(0.4f); coords.add(0.2f); coords.add(0f);
        // Top
        coords.add(0.4f); coords.add(0.5f); coords.add(-0.2f);
        coords.add(0.4f); coords.add(0.5f); coords.add(-0.7f);
        coords.add(-0.4f); coords.add(0.5f); coords.add(-0.2f);
        coords.add(0.4f); coords.add(0.5f); coords.add(-0.7f);
        coords.add(-0.4f); coords.add(0.5f); coords.add(-0.7f);
        coords.add(-0.4f); coords.add(0.5f); coords.add(-0.2f);
        // Back
        coords.add(0.4f); coords.add(0.5f); coords.add(-0.7f);
        coords.add(0.4f); coords.add(0.2f); coords.add(-0.9f);
        coords.add(-0.4f); coords.add(0.5f); coords.add(-0.7f);
        coords.add(0.4f); coords.add(0.2f); coords.add(-0.9f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(-0.9f);
        coords.add(-0.4f); coords.add(0.5f); coords.add(-0.7f);
        // Triangles to accomodate slant
        coords.add(-0.4f); coords.add(0.2f); coords.add(-0.7f);
        coords.add(-0.4f); coords.add(0.5f); coords.add(-0.7f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(-0.9f);
        coords.add(0.4f); coords.add(0.2f); coords.add(-0.9f);
        coords.add(0.4f); coords.add(0.5f); coords.add(-0.7f);
        coords.add(0.4f); coords.add(0.2f); coords.add(-0.7f);
        // Trunk
        coords.add(0.4f); coords.add(0.2f); coords.add(-0.9f);
        coords.add(0.4f); coords.add(0.2f); coords.add(-1.3f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(-0.9f);
        coords.add(0.4f); coords.add(0.2f); coords.add(-1.3f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(-1.3f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(-0.9f);
        // back
        coords.add(0.4f); coords.add(0.2f); coords.add(-1.3f);
        coords.add(0.4f); coords.add(-0.2f); coords.add(-1.3f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(-1.3f);
        coords.add(0.4f); coords.add(-0.2f); coords.add(-1.3f);
        coords.add(-0.4f); coords.add(-0.2f); coords.add(-1.3f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(-1.3f);
        // Non-drive side bottom
        coords.add(-0.4f); coords.add(-0.2f); coords.add(0.4f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(0.4f);
        coords.add(-0.4f); coords.add(-0.2f); coords.add(-1.3f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(0.4f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(-1.3f);
        coords.add(-0.4f); coords.add(-0.2f); coords.add(-1.3f);
        // Non-drive side window
        coords.add(-0.4f); coords.add(0.2f); coords.add(-0.2f);
        coords.add(-0.4f); coords.add(0.5f); coords.add(-0.2f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(-0.7f);
        coords.add(-0.4f); coords.add(0.5f); coords.add(-0.2f);
        coords.add(-0.4f); coords.add(0.5f); coords.add(-0.7f);
        coords.add(-0.4f); coords.add(0.2f); coords.add(-0.7f);
        // Drive side bottom
        coords.add(0.4f); coords.add(-0.2f); coords.add(-1.3f);
        coords.add(0.4f); coords.add(0.2f); coords.add(-1.3f);
        coords.add(0.4f); coords.add(-0.2f); coords.add(0.4f);
        coords.add(0.4f); coords.add(0.2f); coords.add(-1.3f);
        coords.add(0.4f); coords.add(0.2f); coords.add(0.4f);
        coords.add(0.4f); coords.add(-0.2f); coords.add(0.4f);
        // Drive side window
        coords.add(0.4f); coords.add(0.2f); coords.add(-0.7f);
        coords.add(0.4f); coords.add(0.5f); coords.add(-0.7f);
        coords.add(0.4f); coords.add(0.2f); coords.add(-0.2f);
        coords.add(0.4f); coords.add(0.5f); coords.add(-0.7f);
        coords.add(0.4f); coords.add(0.5f); coords.add(-0.2f);
        coords.add(0.4f); coords.add(0.2f); coords.add(-0.2f);
        // Bottom
        coords.add(0.4f); coords.add(-0.2f); coords.add(-1.3f);
        coords.add(0.4f); coords.add(-0.2f); coords.add(0.4f);
        coords.add(-0.4f); coords.add(-0.2f); coords.add(-1.3f);
        coords.add(0.4f); coords.add(-0.2f); coords.add(0.4f);
        coords.add(-0.4f); coords.add(-0.2f); coords.add(0.4f);
        coords.add(-0.4f); coords.add(-0.2f); coords.add(-1.3f);

        carBodyCoords = new float[coords.size()];
        for (int i = 0; i < carBodyCoords.length; i++) {
            carBodyCoords[i] = coords.get(i);
        }
        vertexCount = carBodyCoords.length / COORDS_PER_VERTEX;
        
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                carBodyCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(carBodyCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                                   vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                                     fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

    }

    public CarBody(float r, float g, float b) {

        this();
        color = new float[4];
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = 1f;

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
