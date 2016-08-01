package com.example.bobyk.myapplication;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.bobyk.myapplication.views.CustomProgressBar;

/**
 * Created by bobyk on 29.07.16.
 */
public class MainActivity extends AppCompatActivity {

    private CustomProgressBar progressCircleBar;
    private CustomProgressBar progressTomatoBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        /*progressCircleBar = (CustomProgressBar) findViewById(R.id.circle);
        progressCircleBar.setOnCustomBarClickListener(new CustomProgressBar.OnCustomBarClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(MainActivity.this, "Click on circle bar", Toast.LENGTH_SHORT).show();
            }
        });*/

        progressTomatoBar = (CustomProgressBar) findViewById(R.id.tomato);
        progressTomatoBar.setOnCustomBarClickListener(new CustomProgressBar.OnCustomBarClickListener() {
            @Override
            public void onClick() {
               // Toast.makeText(MainActivity.this, "Duration for tomato bar " + progressTomatoBar.getDuration() + "ms", Toast.LENGTH_SHORT).show();
            }
        });

        progressTomatoBar.setOnCustomBarAnimationListener(new CustomProgressBar.OnCustomBarAnimationListener() {
            @Override
            public void onAnimationEnd() {
                Toast.makeText(MainActivity.this, "Animated End", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
