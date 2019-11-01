package com.taehoon.motionlayoutsample.TutorialActivity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.taehoon.motionlayoutsample.R;
import com.taehoon.motionlayoutsample.databinding.ActivityTutorialBinding;

public class TutorialVideoActivity extends AppCompatActivity {

    ActivityTutorialBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(TutorialVideoActivity.this, R.layout.activity_tutorial);
    }
}
