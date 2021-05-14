package com.mcustom.progressbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.mcustom.library.SegmentSlidButton;

public class SegmentSlidActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_slid);
        SegmentSlidButton slidButton = findViewById(R.id.segment_slid_button);
        slidButton.setSlidButtonListener(new SegmentSlidButton.SlidButtonListener() {
            @Override
            public void onSlidSectionValue(String sectionText) {
                Log.i(TAG, "sectionText : " + sectionText);
            }
        });
    }
}