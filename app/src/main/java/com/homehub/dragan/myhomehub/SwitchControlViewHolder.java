package com.homehub.dragan.myhomehub;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;


public class SwitchControlViewHolder extends RecyclerView.ViewHolder {

    private TextView label1;
    private Switch control;
    private boolean State;

    public SwitchControlViewHolder(View v) {
        super(v);
        label1 = (TextView) v.findViewById(R.id.text1);
        control = (Switch) v.findViewById(R.id.switch1);

        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!State) {
                    control.setChecked(true);
                    setControl(true);
                }
                else if (State){
                    control.setChecked(false);
                    setControl(false);
                }
            }
        });

    }

    public TextView getLabel1() {
        return label1;
    }

    public Switch getControl() {
        return control;
    }

    public void setControl(Switch control) {
        this.control = control;
    }

    public void setLabel1(TextView label1) {
        this.label1 = label1;
    }

    public void setControl(boolean state) {
        State = state;
        if(state==true){
            control.setChecked(true);
            control.setText("On");
        }
        else{
            control.setChecked(false);
            control.setText("Off");
        }
    }
}
