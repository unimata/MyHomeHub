package com.homehub.dragan.myhomehub;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;


public class SliderControlViewHolder extends RecyclerView.ViewHolder {

    private TextView label1;
    private TextView CurrentValue;
    private SeekBar setValue;

    public SliderControlViewHolder(View v) {
        super(v);
        label1 = (TextView) v.findViewById(R.id.text1);
        setValue = (SeekBar) v.findViewById(R.id.seekBar);
        CurrentValue = (TextView) v.findViewById(R.id.currentValue);


        setValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setCurrentValue(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void setSeekBar(SeekBar s){
        setValue = s;
    }



    public TextView getLabel1() {
        return label1;
    }

    public TextView getValue() {
        return CurrentValue;
    }
    public void setCurrentValue(float f){
        CurrentValue.setText(Float.toString(f));
    }

    public void setValue(SeekBar setValue) {
        this.setValue = setValue;
    }

    public void setLabel1(TextView label1) {
        this.label1 = label1;
    }





}

