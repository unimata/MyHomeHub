package com.homehub.dragan.myhomehub.UI;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import com.homehub.dragan.myhomehub.Activities.CreateRoutineActivity;
import com.homehub.dragan.myhomehub.Classes.RoutineList;
import com.homehub.dragan.myhomehub.R;

public class RoutineViewHolder extends RecyclerView.ViewHolder {
    private TextView deviceName;
    private TextView actionTv;
    private TextView activatorTv;

    public Button btnDelete;
    public Button btnEdit;

    public RoutineViewHolder(View v) {
        super(v);

        deviceName = (TextView) v.findViewById(R.id.deviceNameTv);
        actionTv = (TextView) v.findViewById(R.id.actionTv);
        activatorTv = (TextView) v.findViewById(R.id.activatorTv);

        btnDelete = (Button) v.findViewById(R.id.deleteBtn);
        btnEdit = (Button) v.findViewById(R.id.editBtn);

        //btnDelete.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {

                // need to make this button delete stuff
        //    }
        //});
    }

    public void setDeviceName(TextView deviceName) {
        this.deviceName = deviceName;
    }

    public TextView getDeviceName() { return deviceName; }

    public TextView getActionTv() { return actionTv; }

    public void setActionTv(TextView actionTv) { this.actionTv = actionTv; }

    public TextView getActivatorTv() { return activatorTv; }

    public void setActivatorTv(TextView activatorTv) { this.activatorTv = activatorTv; }
}
