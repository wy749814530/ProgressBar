package com.mcustom.progressbar;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mcustom.library.WaterWaveProgress;

/**
 * @WYU-WIN
 * @date 2020/8/5 0005.
 * description：
 */
public class WaterWaveActivity extends AppCompatActivity {
    WaterWaveProgress asWater;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_wave);

        asWater = findViewById(R.id.as_water);


        startUpdataPregress();
    }

    int progress = 0;

    public void refreshPregress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                asWater.setProgress(progress);

                if (progress == 100) {
//                    runing = false;
                    progress = 0;
                } else {
                    progress++;
                }
            }
        });
    }

    boolean runing = true;

    public void startUpdataPregress() {
        runing = true;
        progress = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (runing) {
                    refreshPregress();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
