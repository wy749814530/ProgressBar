package com.mcustom.progressbar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mian);

    }

    public void gotoWaterPregress(View view) {
        startActivity(new Intent(this, WaterWaveActivity.class));
    }

    public void gotoPregress(View view) {
        startActivity(new Intent(this, ProgressActivity.class));
    }

    public void gotoResultStatus(View view) {
        startActivity(new Intent(this, ResultStatusActivity.class));
    }

    public void gotoSegmentSlidButton(View view) {
        startActivity(new Intent(this, SegmentSlidActivity.class));
    }

    public void gotoRoundProgressStatusBar(View view) {
        startActivity(new Intent(this, RoundProgressStatusBarActivity.class));
    }
}