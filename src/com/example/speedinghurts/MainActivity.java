package com.example.speedinghurts;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import android.content.res.Configuration;

public class MainActivity extends Activity {

    private MyGLSurfaceView mGLView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
            
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_portrait);
        mGLView = (MyGLSurfaceView) findViewById(R.id.the_surface_view);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }

    public void switchSpeed(View view) {

        TextView speeding = (TextView) findViewById(R.id.percent_speeding);
        TextView killed = (TextView) findViewById(R.id.percent_killed);
        
        switch (view.getId()) {
        case R.id.button_20:
            mGLView.getRenderer().carSpeed = (float) (20f * MyGLRenderer.speedScale);
            // Update widgets
            speeding.setText(getString(R.string.speeding_20));
            killed.setText(getString(R.string.killed_20));
            break;
        case R.id.button_30:
            mGLView.getRenderer().carSpeed = (float) (30f * MyGLRenderer.speedScale);
            // Update widgets
            speeding.setText(getString(R.string.speeding_30));
            killed.setText(getString(R.string.killed_30));
            break;
        case R.id.button_40:
            mGLView.getRenderer().carSpeed = (float) (40f * MyGLRenderer.speedScale);
            // Update widgets
            speeding.setText(getString(R.string.speeding_40));
            killed.setText(getString(R.string.killed_40));
            break;
        }

    }

    public void showGraph(View view) {

        Intent intent = new Intent(this, DataGraphActivity.class);
        // Start the new intent
        startActivity(intent);

    }

    // Change the layout when the user changes the orientation of the screen
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.main_landscape);
            mGLView = (MyGLSurfaceView) findViewById(R.id.the_surface_view);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.main_portrait);
            mGLView = (MyGLSurfaceView) findViewById(R.id.the_surface_view);
        }

    }

}

class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        // Enable depth buffer
        setEGLConfigChooser(true);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        // Enable depth buffer
        setEGLConfigChooser(true);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public MyGLRenderer getRenderer() {

        return mRenderer;

    }
    
}
