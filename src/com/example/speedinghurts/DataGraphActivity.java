package com.example.speedinghurts;

import android.app.Activity;
import android.os.Bundle;

import android.view.*;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.os.Build;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
/*
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
*/

public class DataGraphActivity extends Activity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);

        ImageView image = new ImageView(this);
        
        // Import the SVG graph
        /*
        SVG svg = SVGParser.getSVGFromResource(getResources(),
                                               R.raw.graph_scalable);
        // Get a drawable from the parsed SVG and set it as the drawable for the ImageView
        image.setImageDrawable(svg.createPictureDrawable());
        */
        
        image.setImageResource(R.raw.graph_static);
        
        // Stretch to fill screen
        // image.setScaleType(ScaleType.FIT_XY);
        
        setContentView(image);
        
    }
    
}
