package com.taehoon.motionlayoutsample;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.taehoon.motionlayoutsample.databinding.ActivitySampleBinding;

public class SampleActivity extends AppCompatActivity {

    ActivitySampleBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(SampleActivity.this, R.layout.activity_sample);
    }
}
