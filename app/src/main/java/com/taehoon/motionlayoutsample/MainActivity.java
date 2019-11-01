package com.taehoon.motionlayoutsample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.taehoon.motionlayoutsample.TutorialActivity.TutorialVideoActivity;
import com.taehoon.motionlayoutsample.databinding.ActivityRectangleLayoutBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityRectangleLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_rectangle_layout);
        binding.btStartTutorialActivity.setOnClickListener(this);
        binding.btStartSampleActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start_tutorial_activity:
                goActivity(TutorialVideoActivity.class);
                break;
            case R.id.bt_start_sample_activity:
                goActivity(SampleActivity.class);
                break;
        }
    }

    private void goActivity(@NonNull Class targetActivity) {
        Intent intent = new Intent(MainActivity.this, targetActivity);
        startActivity(intent);
    }
}