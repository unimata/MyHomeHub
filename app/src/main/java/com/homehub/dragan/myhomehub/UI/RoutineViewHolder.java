package com.homehub.dragan.myhomehub.UI;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.TextView;

import com.homehub.dragan.myhomehub.R;

public class RoutineViewHolder extends RecyclerView.ViewHolder {
    private TextView deviceName;
    public RoutineViewHolder(View v) {
        super(v);

        deviceName = (TextView) v.findViewById(R.id.deviceNameTv);
        //gotta figure out how to link the viewholder Java file with a viewholder res file
    }

    public void setDeviceName(TextView deviceName) {
        this.deviceName = deviceName;
    }

    public TextView getDeviceName() {
        return deviceName;

    }
}
