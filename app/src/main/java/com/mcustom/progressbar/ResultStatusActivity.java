package com.mcustom.progressbar;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mcustom.library.RoundProgressStatusBar;

/**
 * @WYU-WIN
 * @date 2020/8/5 0005.
 * description：
 */
public class ResultStatusActivity extends AppCompatActivity {

    RoundProgressStatusBar roundProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_status);
        roundProgressBar = findViewById(R.id.roundProgressBar);
        startUpdataPregress();
    }

    int progress = 0;

    public void refreshPregress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roundProgressBar.setProgress(progress);
                if (progress == 100) {
                    runing = false;
                    roundProgressBar.loadSuccess();
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

    public void loadSuccessStatus(View view) {
        runing = false;
        roundProgressBar.loadSuccess();
    }


    public void loadFailedStatus(View view) {
        runing = false;
        roundProgressBar.loadFailure();
    }


    public void loadingStatus(View view) {
        startUpdataPregress();
    }

}
