package com.taehoon.motionlayoutsample.TutorialActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taehoon.motionlayoutsample.R;
import com.taehoon.motionlayoutsample.databinding.ItemPlaySpeedBinding;

import lombok.Getter;
import lombok.Setter;

public class PlaySpeedAdapter extends RecyclerView.Adapter {

    private final View.OnClickListener mListener;
    private ItemData[] mData;

    PlaySpeedAdapter(@NonNull View.OnClickListener listener, float currSpeed, @NonNull float[] playSpeedArr) {
        this.mListener = listener;
        mData = new ItemData[playSpeedArr.length];
        for (int index = 0; index < playSpeedArr.length; index++) {
            mData[index] = new ItemData();
            mData[index].setValue(String.valueOf(playSpeedArr[index]));
            mData[index].setChecked(currSpeed == playSpeedArr[index]);
        }
    }

    void changeCheckData(float playSpeed) {
        for (ItemData data : mData) {
            if (playSpeed == Float.parseFloat(data.getValue())) {
                data.setChecked(true);
            } else {
                data.setChecked(false);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_speed, parent, false);
        return new PlaySpeedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlaySpeedHolder playSpeedHolder = (PlaySpeedHolder) holder;
        ItemPlaySpeedBinding binding = playSpeedHolder.binding;
        ItemData data = mData[position];
        binding.tvPlaySpeedItem.setText(data.getValue());
        if (data.isChecked()) {
            binding.ivCheck.setVisibility(View.VISIBLE);
        } else {
            binding.ivCheck.setVisibility(View.INVISIBLE);
        }
        binding.clItem.setTag(data.getValue());
        binding.clItem.setOnClickListener(mListener);
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    private class PlaySpeedHolder extends RecyclerView.ViewHolder {

        ItemPlaySpeedBinding binding;

        PlaySpeedHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    private class ItemData {
        @Setter
        @Getter
        private boolean isChecked;
        @Setter
        @Getter
        private String value;
    }
}
