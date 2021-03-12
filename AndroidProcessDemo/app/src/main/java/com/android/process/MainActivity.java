package com.android.process;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    private Timer mTimer = new Timer();

    int value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SportProgressView sportProgressView = findViewById(R.id.view_sport_progress);
        DownloadProgressButton downloadProgressButton = findViewById(R.id.dpb);
        CustomCircleProgress customCircleProgress = findViewById(R.id.ccp);

        sportProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sportProgressView.startAnimation(100);
            }
        });


        downloadProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                value = 0;
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        downloadProgressButton.setProgressAnDownloadStatus(++value, GameDownloadStatus.DOWNLOADING);
                        if (value >= downloadProgressButton.getMaxProgress()) {
                            cancel();
                        }
                    }
                }, 0, 100);
            }
        });

        customCircleProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                value = 0;
                customCircleProgress.setMax(100);
                customCircleProgress.setStatus(CustomCircleProgress.Status.STARTING);
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        customCircleProgress.setProgress(++value);
                        if (value >= downloadProgressButton.getMaxProgress()) {
                            cancel();
                        }
                    }
                }, 0, 50);
            }
        });


    }
}