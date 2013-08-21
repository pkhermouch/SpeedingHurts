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

public class DataGraphActivity extends Activity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);

        ImageView image = new ImageView(this);
        image.setImageResource(R.raw.graph_static);
        setContentView(image);
        
    }
    
}
