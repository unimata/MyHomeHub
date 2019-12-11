package com.homehub.dragan.myhomehub.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.homehub.dragan.myhomehub.Activities.AddDeviceRegActivity;
import com.homehub.dragan.myhomehub.Classes.firebase.FirebaseDatabaseHelper;
import com.homehub.dragan.myhomehub.Classes.model.DevRegRecyclerView_Config;
import com.homehub.dragan.myhomehub.Classes.model.Device_Registration;
import com.homehub.dragan.myhomehub.R;

import java.util.List;

public class DeviceRegistrationsFragment extends Fragment {

    private Activity activity;
    private RecyclerView deviceRegRecyclerView;
    private Button btnNewDeviceReg;

    public static DeviceRegistrationsFragment newInstance() { return new DeviceRegistrationsFragment(); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_device_registrations, container, false);

        //set up the add device reg button
        btnNewDeviceReg = view.findViewById(R.id.btnNewDeviceReg);

        //set up event listener for button
        btnNewDeviceReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //disable button to prevent double click
                btnNewDeviceReg.setEnabled(false);

                Intent intent;
                intent = new Intent(getActivity(), AddDeviceRegActivity.class);
                intent.putExtra("newDeviceRegKey", Integer.toString(deviceRegRecyclerView.getAdapter().getItemCount()));
                startActivity(intent);

            }
        });

        //recycler view
        deviceRegRecyclerView = view.findViewById(R.id.recyclerViewDeviceReg);

        //attaching swipe ability to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(deviceRegRecyclerView);

        //get data from firebase (but also maintain connection)
        new FirebaseDatabaseHelper().readDeviceRegs(new FirebaseDatabaseHelper.DeviceDataStatus() {
            @Override
            public void DeviceDataIsLoaded(List<Device_Registration> deviceRegs, List<String> keys) {
                view.findViewById(R.id.loading_device_reg_pb).setVisibility(View.GONE);
                new DevRegRecyclerView_Config().setConfig(deviceRegRecyclerView, getActivity().getApplicationContext(), deviceRegs, keys);
            }

            @Override
            public void DeviceDataIsUpdated() {

            }

            @Override
            public void DeviceDataIsInserted() {

            }


            @Override
            public void DeviceDataIsDeleted() {

            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        //re-enable button
        btnNewDeviceReg.setEnabled(true);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            //get key

            String key = ((TextView) deviceRegRecyclerView.findViewHolderForAdapterPosition(viewHolder.getAdapterPosition()).itemView.findViewById(R.id.keyTxtView)).getText().toString();


            Log.println(1, "lol", key);

            new FirebaseDatabaseHelper().deleteDeviceReg(key , new FirebaseDatabaseHelper.DeviceDataStatus() {
                @Override
                public void DeviceDataIsLoaded(List<Device_Registration> deviceRegistrations, List<String> keys) {

                }

                @Override
                public void DeviceDataIsInserted() {

                }

                @Override
                public void DeviceDataIsUpdated() {

                }

                @Override
                public void DeviceDataIsDeleted() {
                    Toast.makeText(getContext(), "Entry was deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            Bitmap trash_icon;

            Paint p = new Paint();
            p.setColor(Color.parseColor("#D32121"));

            if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                View itemView = viewHolder.itemView;
                float height = itemView.getBottom() - itemView.getTop();
                float width = height / 3;

                if(dX > 0){
                    //background rect and color
                    RectF background = new RectF(itemView.getLeft(), itemView.getTop(),
                            dX, itemView.getBottom());
                    c.drawRect(background,p);

                    //icon placement
                    trash_icon = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_delete_white_24dp);
                    RectF icon_dest = new RectF(itemView.getLeft() + width , itemView.getTop() + width,
                            itemView.getLeft()+ 2*width,itemView.getBottom() - width);
                    c.drawBitmap(trash_icon,null,icon_dest, p);
                } else {
                    //background rect and color
                    RectF background = new RectF( itemView.getRight() + dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                    c.drawRect(background,p);

                    //icon placement
                    trash_icon = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_delete_white_24dp);
                    RectF icon_dest = new RectF( itemView.getRight() - 2*width , itemView.getTop() + width,
                            itemView.getRight() - width,itemView.getBottom() - width);
                    c.drawBitmap(trash_icon,null,icon_dest, p);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}
