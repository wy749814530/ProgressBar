package com.mcustom.progressbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mcustom.library.LineProgressbar;
import com.mcustom.library.RoundProgressBar;

/**
 * @WYU-WIN
 * @date 2020/8/5 0005.
 * description：
 */
public class ProgressActivity extends AppCompatActivity {
    LineProgressbar progressbar;
    RoundProgressBar roundProgressBar2;
    EditText edMinProgress, edMaxProgress, edTextSize, edUnit, edInnerRadius, edOuterRadius;
    Button btnMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        progressbar = findViewById(R.id.progressbar);
        roundProgressBar2 = findViewById(R.id.roundProgressBar2);
        edMinProgress = findViewById(R.id.edMinProgress);
        edMaxProgress = findViewById(R.id.edMaxProgress);
        edTextSize = findViewById(R.id.edTextSize);
        edUnit = findViewById(R.id.edUnit);
        edInnerRadius = findViewById(R.id.edInnerRadius);
        edOuterRadius = findViewById(R.id.edOuterRadius);
        progressbar.setOnProgressbarChangeListener(new LineProgressbar.OnProgressbarChangeListener() {
            @Override
            public void onProgressChanged(LineProgressbar progressbar, int progress) {
                Log.i("MainActivity", "progress : " + progress);
            }

            @Override
            public void onDragging(LineProgressbar progressbar, int progress) {

            }
        });
        progressbar.setProgressBgColor(ContextCompat.getColor(this, R.color.gray_light));
        progressbar.setProgressSpendColor(ContextCompat.getColor(this, R.color.green_25d1d3));


        btnMin = findViewById(R.id.btnMin);
        btnMin.setTextSize(16);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
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

    int progress = 0;

    public void refreshPregress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roundProgressBar2.setProgress(progress);
                if (progress == 100) {
                    progress = 0;
                } else {
                    progress++;
                }
            }
        });
    }

    public void setMinProgress(View view) {
        progressbar.setMinProgress(Integer.parseInt(edMinProgress.getText().toString()));
    }

    public void setMaxProgress(View view) {
        progressbar.setMaxProgress(Integer.parseInt(edMaxProgress.getText().toString()));
    }

    public void setTextSize(View view) {
        progressbar.setTextSize(Integer.parseInt(edTextSize.getText().toString()));
    }

    public void setUnit(View view) {
        progressbar.setUnit(edUnit.getText().toString());
    }

    public void setInnerRadius(View view) {
        progressbar.setInnerRadius(Integer.parseInt(edInnerRadius.getText().toString()));
    }

    public void setOuterRadius(View view) {
        progressbar.setOuterRadius(Integer.parseInt(edOuterRadius.getText().toString()));
    }
}
