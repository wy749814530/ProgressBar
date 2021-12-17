package com.mcustom.progressbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mcustom.library.SegmentSlidBar;
import com.mcustom.progressbar.databinding.ActivityRoundProgressStatusBarBinding;

import java.util.ArrayList;

/**
 * @WYU-WIN
 * @date 2021/12/15 0015.
 * description：
 */
public class RoundProgressStatusBarActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private ActivityRoundProgressStatusBarBinding mBinding;
    private ArrayList contexts = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRoundProgressStatusBarBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        contexts.add("111");
        contexts.add("222");
        contexts.add("333");
        contexts.add("444");
        contexts.add("555");
        contexts.add("666");
        contexts.add("777");
        contexts.add("888");
        mBinding.segmentSlidBar.updataGradientPoints(contexts);

        mBinding.segmentSlidBar.setSegmentSlidListener(new SegmentSlidBar.SegmentSlidListener() {
            @Override
            public void onSectionValue(String sectionText) {
                Log.i(TAG, "onSectionValue( " + sectionText + " )");
            }
        });

        mBinding.segmentSlidBar.setCurrentSection("BBB");
    }

    public void onClick15View(View view) {
        mBinding.segmentSlidBar.setCurrentSection("111");
    }

    public void onClick30View(View view) {
        mBinding.segmentSlidBar.setCurrentSection("333");
    }

    public void onClick45View(View view) {
        mBinding.segmentSlidBar.setCurrentSection("555");
    }

    public void onClick60View(View view) {
        mBinding.segmentSlidBar.setCurrentSection("777");
    }
}
