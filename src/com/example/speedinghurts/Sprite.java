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
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.graphics.BitmapFactory;

public class Sprite {
    //Reference to Activity Context
    private final Context mActivityContext;

    //Added for Textures
    private final FloatBuffer mCubeTextureCoordinates;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private final int mTextureCoordinateDataSize = 2;
    private int mTextureDataHandle;

    private final String vertexShaderCode =
        //Test
        "attribute vec2 a_TexCoordinate;" +
        "varying vec2 v_TexCoordinate;" +
        //End Test
        "uniform mat4 uMVPMatrix;" +
        "attribute vec4 vPosition;" +
        "void main() {" +
        "  gl_Position = uMVPMatrix * vPosition;" +
        //Test
        "v_TexCoordinate = a_TexCoordinate;" +
        //End Test
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 v_Color;" +
        //Test
        "uniform sampler2D u_Texture;" +
        "varying vec2 v_TexCoordinate;" +
        //End Test
        "void main() {" +
        //"gl_FragColor = v_Color;" +
        //"gl_FragColor = (v_Color * texture2D(u_Texture, v_TexCoordinate));" +
        // Just draw the texture, don't apply a color
        "gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" +
        "}";

    private final int shaderProgram;
    private final FloatBuffer vertexBuffer;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 2;
    // Ratio between width and height of the image
    private final float imageRatio = 1280f / 557;

    private float spriteCoords[] = {

        -imageRatio, 1f,   // top left
        -imageRatio, -1f,   // bottom left
        imageRatio, -1f,   // bottom right
        -imageRatio, 1f,   // top left
        imageRatio, -1f,   // bottom right
        imageRatio,  1f  //top right

    };

    private final int vertexStride = COORDS_PER_VERTEX * 4; //Bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    // Image to draw as a texture
    final int textureID = R.raw.streetview;

    public Sprite(final Context activityContext) {
        mActivityContext = activityContext;

        //Initialize Vertex Byte Buffer for Shape Coordinates / # of coordinate values * 4 bytes per float
        ByteBuffer bb = ByteBuffer.allocateDirect(spriteCoords.length * 4);
        //Use the Device's Native Byte Order
        bb.order(ByteOrder.nativeOrder());
        //Create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        //Add the coordinates to the FloatBuffer
        vertexBuffer.put(spriteCoords);
        //Set the Buffer to Read the first coordinate
        vertexBuffer.position(0);

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        final float[] cubeTextureCoordinateData =
            {
                //Front face
                /*0.0f, 0.0f,
                  0.0f, 1.0f,
                  1.0f, 0.0f,
                  0.0f, 1.0f,
                  1.0f, 1.0f,
                  1.0f, 0.0f*/
                /*
                -0.5f,  0.5f,
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f,  0.5f
                */
                /*
                0.5f, 0.5f,
                0.5f, -0.5f,
                -0.5f, -0.5f,
                -0.5f, 0.5f
                */
                /*
                -0.5f, 0.5f,   // top left
                -0.5f, -0.5f,   // bottom left
                0.5f, -0.5f,   // bottom right
                -0.5f, 0.5f,   // top left
                0.5f, -0.5f,   // bottom right
                0.5f,  0.5f  //top right
                */

                imageRatio, 1f,   // top left
                imageRatio, -1f,   // bottom left
                -imageRatio, -1f,   // bottom right
                imageRatio, 1f,   // top left
                -imageRatio, -1f,   // bottom right
                -imageRatio,  1f  //top right

            };

        mCubeTextureCoordinates = ByteBuffer
            .allocateDirect(cubeTextureCoordinateData.length * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);
        
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                                   vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                                     fragmentShaderCode);

        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);

        //Texture Code
        GLES20.glBindAttribLocation(shaderProgram, 0, "a_TexCoordinate");

        GLES20.glLinkProgram(shaderProgram);

        //Load the texture
        mTextureDataHandle = loadTexture(mActivityContext, textureID);
    }

    public void draw(float[] mvpMatrix) {
        //Add program to OpenGL ES Environment
        GLES20.glUseProgram(shaderProgram);

        //Get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");

        //Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        //Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle,
                                     COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT,
                                     false,
                                     vertexStride,
                                     vertexBuffer);

        //Get Handle to Fragment Shader's v_Color member
        mColorHandle = GLES20.glGetUniformLocation(shaderProgram, "v_Color");

        //Set the Color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        //Set Texture Handles and bind Texture
        mTextureUniformHandle = GLES20
            .glGetAttribLocation(shaderProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20
            .glGetAttribLocation(shaderProgram, "a_TexCoordinate");

        //Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        //Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        //Pass in the texture coordinate information
        mCubeTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
                                     mTextureCoordinateDataSize,
                                     GLES20.GL_FLOAT,
                                     false,
                                     0,
                                     mCubeTextureCoordinates);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        //Get Handle to Shape's Transformation Matrix
        mMVPMatrixHandle = GLES20
            .glGetUniformLocation(shaderProgram, "uMVPMatrix");

        //Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        //Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, spriteCoords.length);

        //Disable Vertex Array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
    }

    public static int loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
            {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;   // No pre-scaling

                // Read in the resource
                final Bitmap bitmap = BitmapFactory
                    .decodeResource(context.getResources(), resourceId, options);

                // Bind to the texture in OpenGL
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

                // Set filtering
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                                       GLES20.GL_TEXTURE_MIN_FILTER,
                                       GLES20.GL_NEAREST);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                                       GLES20.GL_TEXTURE_MAG_FILTER,
                                       GLES20.GL_NEAREST);
                /*
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                                       GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                                       GLES20.GL_CLAMP_TO_EDGE);
                */
                
                // Load the bitmap into the bound texture.
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

                // Recycle the bitmap, since its data has been loaded into OpenGL.
                bitmap.recycle();
            }

        if (textureHandle[0] == 0)
            {
                throw new RuntimeException("Error loading texture.");
            }

        return textureHandle[0];
    }
}