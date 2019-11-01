package com.taehoon.motionlayoutsample.TutorialActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.SeekBar;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.taehoon.motionlayoutsample.R;
import com.taehoon.motionlayoutsample.databinding.ViewTutorialBinding;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ClickableViewAccessibility")
public class SurfaceVideoView extends MotionLayout
        implements SurfaceHolder.Callback, View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener {

    private final float[] PLAY_SPEEDS = {0.5f, 1.0f, 1.5f, 2.0f};

    ViewTutorialBinding binding;
    private float mCurrPlaySpeed = 1.0f;
    private Context mContext;
    private MediaPlayer mPlayer;
    private Handler mControllerHandler = new Handler();
    private Runnable mControllerRunnable = new Runnable() {
        @Override
        public void run() {
            TransitionManager.beginDelayedTransition(SurfaceVideoView.this);
            binding.clController.setVisibility(INVISIBLE);
        }
    };
    private Timer mTimer;
    private TimerTask mSeekBarUpdateTask;
    private PlaySpeedAdapter mPlaySpeedAdapter;

    private boolean isTracking = false;
    private boolean isPlayBeforeTracking;

    public SurfaceVideoView(Context context) {
        super(context);
        this.mContext = context;
        initView();
        setListener();
    }

    public SurfaceVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
        setListener();
    }

    public SurfaceVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
        setListener();
    }

    private void setListener() {
        binding.ivClose.setOnClickListener(SurfaceVideoView.this);
//        binding.svVideo.getHolder().addCallback(TutorialVideoView.this);
        binding.textureViewVideo.setOnClickListener(SurfaceVideoView.this);
        binding.clController.setOnTouchListener(SurfaceVideoView.this);
        binding.ivTogglePlayPause.setOnClickListener(SurfaceVideoView.this);
        binding.sbProgress.setOnSeekBarChangeListener(SurfaceVideoView.this);
        binding.tvPlaySpeed.setOnClickListener(SurfaceVideoView.this);
        binding.ivReplay.setOnClickListener(SurfaceVideoView.this);
        binding.motionLayout.setTransitionListener(new TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {
                Log.d("TAG", "onTransitionStarted");
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {
                Log.d("TAG", "onTransitionChange");
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                Log.d("TAG", "onTransitionCompleted");
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {
                Log.d("TAG", "onTransitionTrigger");
            }
        });
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View view = inflater.inflate(R.layout.view_tutorial, SurfaceVideoView.this, false);
            addView(view);
            binding = DataBindingUtil.bind(view);

            if (binding != null) {
                binding.rvPlaySpeed.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                binding.rvPlaySpeed.setLayoutManager(new LinearLayoutManager(mContext));
                mPlaySpeedAdapter = new PlaySpeedAdapter(SurfaceVideoView.this, mCurrPlaySpeed, PLAY_SPEEDS);
                binding.rvPlaySpeed.setAdapter(mPlaySpeedAdapter);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPlayer = new MediaPlayer();
        Uri uri = Uri.parse("android.resource://" + mContext.getPackageName() + "/raw/tutorial");
        try {
            mPlayer.setDataSource(mContext, uri);
            mPlayer.setDisplay(holder);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    int videoLength = mPlayer.getDuration();
                    DateFormat format = new SimpleDateFormat("mm:ss", Locale.getDefault());
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    final String totalPlayTime = format.format(new Date(videoLength));
                    binding.tvPlayTime.setText("0:00/" + totalPlayTime);

                    binding.sbProgress.setMax(videoLength);

                    final Handler handler = new Handler();
                    mTimer = new Timer();
                    mSeekBarUpdateTask = new TimerTask() {
                        @Override
                        public void run() {
                            int currPosition = mPlayer.getCurrentPosition();
                            binding.sbProgress.setProgress(currPosition);
                            DateFormat format = new SimpleDateFormat("mm:ss", Locale.getDefault());
                            format.setTimeZone(TimeZone.getTimeZone("UTC"));
                            final String currPlayTime = format.format(new Date(currPosition));
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvPlayTime.setText(currPlayTime + "/" + totalPlayTime);
                                }
                            });
                        }
                    };
                    mTimer.schedule(mSeekBarUpdateTask, 0, 500);

                    mPlayer.start();
                    binding.ivTogglePlayPause.setImageResource(R.drawable.pause);
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    binding.ivReplay.setVisibility(View.VISIBLE);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mSeekBarUpdateTask != null) {
            mSeekBarUpdateTask.cancel();
            mSeekBarUpdateTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                this.setVisibility(GONE);
                break;
            case R.id.texture_view_video:
                toggleController();
                hidePlaySpeedList();
                break;
            case R.id.cl_controller:
                extensionControllerTime();
                hidePlaySpeedList();
                break;
            case R.id.iv_toggle_play_pause:
                extensionControllerTime();
                togglePlayPause();
                break;
            case R.id.tv_play_speed:
                cancelControllerHide();
                showPlaySpeedList();
                break;
            case R.id.cl_item:
                mCurrPlaySpeed = Float.parseFloat((String) v.getTag());
                setPlaySpeed(mCurrPlaySpeed);
                hidePlaySpeedList();
                hideControllerDelayed();
                break;
            case R.id.iv_replay:
                mPlayer.start();
                binding.ivReplay.setVisibility(View.GONE);
                break;
        }
    }

    private void toggleController() {
        ConstraintLayout controller = binding.clController;
        switch (controller.getVisibility()) {
            case View.VISIBLE:
                cancelControllerHide();
                TransitionManager.beginDelayedTransition(SurfaceVideoView.this);
                controller.setVisibility(INVISIBLE);
                break;
            case View.INVISIBLE:
                hideControllerDelayed();
                TransitionManager.beginDelayedTransition(SurfaceVideoView.this);
                controller.setVisibility(VISIBLE);
                break;
            case View.GONE:
                break;
        }
    }

    private void togglePlayPause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            binding.ivTogglePlayPause.setImageResource(R.drawable.play_arrow);
        } else {
            mPlayer.start();
            binding.ivTogglePlayPause.setImageResource(R.drawable.pause);
        }
    }

    private void cancelControllerHide() {
        mControllerHandler.removeCallbacks(mControllerRunnable);
    }

    private void hideControllerDelayed() {
        mControllerHandler.postDelayed(mControllerRunnable, 3000);
    }

    private void extensionControllerTime() {
        mControllerHandler.removeCallbacks(mControllerRunnable);
        mControllerHandler.postDelayed(mControllerRunnable, 3000);
    }

    private void showPlaySpeedList() {
        if (binding.rvPlaySpeed.getVisibility() == View.INVISIBLE) {
            mPlaySpeedAdapter.notifyDataSetChanged();
            TransitionManager.beginDelayedTransition(SurfaceVideoView.this);
            binding.rvPlaySpeed.setVisibility(VISIBLE);
        }
    }

    private void hidePlaySpeedList() {
        if (binding.rvPlaySpeed.getVisibility() == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(SurfaceVideoView.this);
            binding.rvPlaySpeed.setVisibility(INVISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setPlaySpeed(float speed) {
        if (mPlayer != null) {
            PlaybackParams params = new PlaybackParams();
            params.setSpeed(speed);
            if (mPlayer.isPlaying()) {
                mPlayer.setPlaybackParams(params);
            } else {
                mPlayer.setPlaybackParams(params);
                mPlayer.pause();
            }
            mPlaySpeedAdapter.changeCheckData(speed);

//            mPlaySpeedAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (isTracking) {
            mPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTracking = true;
        isPlayBeforeTracking = mPlayer.isPlaying();
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            binding.ivTogglePlayPause.setImageResource(R.drawable.play_arrow);
        }

        mControllerHandler.removeCallbacks(mControllerRunnable);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isTracking = false;
        if (isPlayBeforeTracking) {
            mPlayer.start();
            binding.ivTogglePlayPause.setImageResource(R.drawable.pause);
        }

        hideControllerDelayed();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mControllerHandler.removeCallbacks(mControllerRunnable);
                break;
            case MotionEvent.ACTION_UP:
                mControllerHandler.postDelayed(mControllerRunnable, 3000);
                break;
        }
        return true;
    }

    @Override
    public void transitionToStart() {
        super.transitionToStart();
        Log.d("TAG", "transitionToStart");
    }

    @Override
    public void transitionToEnd() {
        super.transitionToEnd();
        Log.d("TAG", "transitionToEnd");
    }

    @Override
    public void setTransitionListener(TransitionListener listener) {
        super.setTransitionListener(listener);
    }
}