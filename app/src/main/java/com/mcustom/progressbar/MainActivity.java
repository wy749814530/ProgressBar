package com.mcustom.progressbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.mcustom.library.Progressbar;

public class MainActivity extends AppCompatActivity {
    Progressbar progressbar;
    EditText edMinProgress, edMaxProgress, edTextSize, edUnit, edInnerRadius, edOuterRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressbar = findViewById(R.id.progressbar);
        edMinProgress = findViewById(R.id.edMinProgress);
        edMaxProgress = findViewById(R.id.edMaxProgress);
        edTextSize = findViewById(R.id.edTextSize);
        edUnit = findViewById(R.id.edUnit);
        edInnerRadius = findViewById(R.id.edInnerRadius);
        edOuterRadius = findViewById(R.id.edOuterRadius);
        progressbar.setOnProgressbarChangeListener(new Progressbar.OnProgressbarChangeListener() {
            @Override
            public void onProgressChanged(Progressbar progressbar, int progress) {
                Log.i("MainActivity", "progress : " + progress);
            }
        });
        progressbar.setPrpgressBgColor(ContextCompat.getColor(this, R.color.gray_light));
        progressbar.setProgressSpendColor(ContextCompat.getColor(this, R.color.green_25d1d3));
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

    public void setInnerRadius (View view) {
        progressbar.setInnerRadius(Integer.parseInt(edInnerRadius.getText().toString()));
    }

    public void setOuterRadius(View view) {
        progressbar.setOuterRadius(Integer.parseInt(edOuterRadius.getText().toString()));
    }
}