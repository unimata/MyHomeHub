package com.homehub.dragan.myhomehub.UI;


import android.widget.SeekBar;

public class SliderBasedControl {

    private String LinkedDeviceId;
    private float Progress;
    private SeekBar Slider;

    public SliderBasedControl(String deviceId, float p){

        setLinkedDeviceId(deviceId);
        setProgress(p);
    }


    public float getProgress() {
        return Progress;
    }

    public void setProgress(float p) {
        Progress = p;
    }

    public String getLinkedDeviceId() {
        return LinkedDeviceId;
    }

    public void setLinkedDeviceId(String linkedDeviceId) {
        LinkedDeviceId = linkedDeviceId;
    }

    public void setSlider(SeekBar slider) { Slider = slider; }

    public SeekBar getSlider() { return Slider; }
}
