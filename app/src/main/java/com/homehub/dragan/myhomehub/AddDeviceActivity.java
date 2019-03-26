package com.homehub.dragan.myhomehub;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

public class AddDeviceActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TextView mDeviceCounter;
    private TextView mScanningTextView;
    private ProgressBar mProgBar;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;
    private int foundDeviceCount;

    ArrayList<String> rawDeviceDataset = new ArrayList<>();
    ArrayList<String> filteredDeviceDataset = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_devices);

        mContext = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mProgBar = (ProgressBar) findViewById(R.id.scanningDevicesBar);
        mScanningTextView = (TextView) findViewById(R.id.textViewScanning);
        mDeviceCounter = (TextView) findViewById(R.id.textViewDeviceCount);
        foundDeviceCount = 0;

        mProgBar.setVisibility(View.VISIBLE);
        mScanningTextView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mDeviceCounter.setVisibility(View.INVISIBLE);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(filteredDeviceDataset);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            public void run() {
                discoverDevices();
            }
        }).start();
        //discoverDevices();
    }

    public void discoverDevices(){
        DeviceDiscoveryUPnP.discoveryDevices(this, new DeviceDiscoveryUPnP.OnDiscoveryListener() {
            @Override
            public void OnStart() {
                Toast.makeText(mContext, "Start discovery", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFoundNewDevice(UPnPDevice device) {
                rawDeviceDataset.add(device.toString());
            }

            @Override
            public void OnFinish(HashSet<UPnPDevice> devices) {
                for (UPnPDevice device : devices) {
                    // To do something
                    if (!filteredDeviceDataset.contains(device.toString())) {
                        filteredDeviceDataset.add(device.toString());
                        mAdapter.notifyDataSetChanged();
                        foundDeviceCount++;
                        Toast.makeText(mContext, "Found new device", Toast.LENGTH_SHORT).show();
                    }

                }
                //hide the scanning elements
                mProgBar.setVisibility(View.GONE);
                mScanningTextView.setVisibility(View.GONE);

                mDeviceCounter.setText(Integer.toString(foundDeviceCount) + " Devices found on network.");
                mDeviceCounter.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);

                Toast.makeText(mContext, "Discovery finished", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnError(Exception e) {
                Toast.makeText(mContext, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<String> mDataset;

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;
            ViewHolder(View view) {
                super(view);
                mTextView = (TextView) view.findViewById(R.id.FoundDeviceTextView);
            }
        }

        MyAdapter(ArrayList<String> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_row_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mDataset.get(position));
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }


}
