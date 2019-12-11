package com.homehub.dragan.myhomehub.Classes.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.homehub.dragan.myhomehub.R;

import org.w3c.dom.Text;

import java.util.List;

public class DevRegRecyclerView_Config {
    private Context mContext;
    private DeviceRegAdapter mDeviceRegsAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Device_Registration> deviceRegs, List<String> keys){

        mContext = context;
        mDeviceRegsAdapter = new DeviceRegAdapter(deviceRegs, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mDeviceRegsAdapter);
    }


    class DeviceRegItemView extends RecyclerView.ViewHolder {
        private TextView mDevice;
        private TextView mDeviceName;
        private TextView mDeviceKey;

        public DeviceRegItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext).inflate(R.layout.device_reg_rec_list_item, parent, false));

            mDevice = (TextView) itemView.findViewById(R.id.device_txtview);
            mDeviceName = (TextView) itemView.findViewById(R.id.deviceName_txtView);
            mDeviceKey = (TextView) itemView.findViewById(R.id.keyTxtView);
        }

        public void bind(Device_Registration device, String key){
            mDevice.setText(device.getDevice());
            mDeviceName.setText(device.getDevice_Name());
            mDeviceKey.setText(key);
        }
    }

    class DeviceRegAdapter extends RecyclerView.Adapter<DeviceRegItemView> {
        private List<Device_Registration> mDeviceRegList;
        private List<String> mKeys;

        public DeviceRegAdapter(List<Device_Registration> mDeviceRegList, List<String> mKeys) {
            this.mDeviceRegList = mDeviceRegList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public DeviceRegItemView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new DeviceRegItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceRegItemView holder, int position) {
            holder.bind(mDeviceRegList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mDeviceRegList.size();
        }
    }

}
