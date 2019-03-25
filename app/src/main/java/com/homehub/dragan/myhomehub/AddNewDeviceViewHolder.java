package com.homehub.dragan.myhomehub;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;


public class AddNewDeviceViewHolder extends RecyclerView.ViewHolder {

    private FloatingActionButton addButton;

    private Context mContext;
    private Intent intent;

    public AddNewDeviceViewHolder(View v) {
        super(v);


        addButton = (FloatingActionButton) v.findViewById(R.id.floatingActionButton);
        addButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
               mContext = v.getContext();
               Toast.makeText(mContext, "Add New Device", Toast.LENGTH_SHORT).show();
                intent = new Intent(v.getContext(), AddDeviceActivity.class);
                //intent.putExtra("channel", "4lmrrOD8Ll2SkO2A");
                mContext.startActivity(intent);
            }
        });

    }

    public FloatingActionButton getAddButton() {
        return addButton;
    }

    public void setAddButton(FloatingActionButton addButton) {
        this.addButton = addButton;
    }



}
