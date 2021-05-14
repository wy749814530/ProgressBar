package com.mcustom.progressbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mcustom.library.SegmentSlidButton;

public class SegmentSlidActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    SegmentSlidButton slidButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_slid);
        slidButton = findViewById(R.id.segment_slid_button);
        slidButton.setSlidButtonListener(new SegmentSlidButton.SlidButtonListener() {
            @Override
            public void onSlidSectionValue(String sectionText) {
                Log.i(TAG, "sectionText : " + sectionText);
            }
        });
    }

    public void onClick15View(View view) {
        slidButton.setCurrentSection("15S");
    }

    public void onClick30View(View view) {
        slidButton.setCurrentSection("30S");
    }

    public void onClick45View(View view) {
        slidButton.setCurrentSection("45S");
    }

    public void onClick60View(View view) {
        slidButton.setCurrentSection("60S");
    }
}