package com.homehub.dragan.myhomehub.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.homehub.dragan.myhomehub.Classes.DeviceList;
import com.homehub.dragan.myhomehub.DeviceDiscoveryUPnP;
import com.homehub.dragan.myhomehub.Fragments.AddSelectedDeviceFragment;
import com.homehub.dragan.myhomehub.R;
import com.homehub.dragan.myhomehub.UI.SwitchBasedControl;
import com.homehub.dragan.myhomehub.UPnPDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class AddDeviceActivity extends AppCompatActivity {

    public AddSelectedDeviceFragment addSelectedDeviceFrag;
    private RecyclerView mRecyclerView;
    private TextView mDeviceCounter;
    private TextView mScanningTextView;
    private ProgressBar mProgBar;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;

    //demo code
    private int foundDeviceCount = 3;
    public ArrayList<Object> items = DeviceList.getInstance().getDevices();

    ArrayList<String> rawDevices = new ArrayList<>();
    ArrayList<String> filteredDevices = new ArrayList<>(Arrays.asList("LiFX LED Light","Phillips Hue","WeMo Smart Outlet"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_devices);

        mContext = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mProgBar = (ProgressBar) findViewById(R.id.scanningDevicesBar);
        mScanningTextView = (TextView) findViewById(R.id.textViewScanning);
        mDeviceCounter = (TextView) findViewById(R.id.textViewDeviceCount);
        //foundDeviceCount = 0;

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
        mAdapter = new MyAdapter(filteredDevices);

        fillerDeviceList();

        mRecyclerView.setAdapter(mAdapter);

        //instance of the fragment
        addSelectedDeviceFrag = new AddSelectedDeviceFragment();



    }

    //@Override
    //protected void onStart() {
    //    super.onStart();
      //  new Thread(new Runnable() {
        //    public void run() {
          //      //demo build disables real discovery, remove this in prod.
            //    //discoverDevices();
            //}
        //}).start();
    //}

    public void addToDevices(String deviceName){
        items.remove(items.size()-1);
        items.add(new SwitchBasedControl(deviceName, false));
        items.add(true);//Keep this at the end always
        //Log.d("myTag", items.toString());
    }

    //this is for filler data for demo purposes
    public void fillerDeviceList(){

        // To do something
        mAdapter.notifyDataSetChanged();

        Toast.makeText(mContext, "Found new device", Toast.LENGTH_SHORT).show();

            /** when we add support for specific products, be sure to add code here that
             * re-filters the dataset for supported devices, as adding an unsupported
             * device may crash the app. This just shows everything on the network raw
             **/

            //hide the scanning elements
        mProgBar.setVisibility(View.GONE);
        mScanningTextView.setVisibility(View.GONE);
        mDeviceCounter.setText(Integer.toString(foundDeviceCount) + " Devices found on network.");
        mDeviceCounter.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        Toast.makeText(mContext, "Discovery finished", Toast.LENGTH_SHORT).show();
    }
    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main, fragment)
                    .commit();

            mDeviceCounter.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);

            return true;
        }
        return false;
    }

    public void discoverDevices(){
        DeviceDiscoveryUPnP.discoveryDevices(this, new DeviceDiscoveryUPnP.OnDiscoveryListener() {
            @Override
            public void OnStart() {
                Toast.makeText(mContext, "Start discovery", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFoundNewDevice(UPnPDevice device) {
                rawDevices.add(device.toString());
            }

            @Override
            public void OnFinish(HashSet<UPnPDevice> devices) {
                for (UPnPDevice device : devices) {
                    // To do something
                    if (!filteredDevices.contains(device.toString())) {
                        filteredDevices.add(device.toString());
                        mAdapter.notifyDataSetChanged();
                        foundDeviceCount++;
                        Toast.makeText(mContext, "Found new device", Toast.LENGTH_SHORT).show();

                        /** when we add support for specific products, be sure to add code here that
                         * re-filters the dataset for supported devices, as adding an unsupported
                         * device may crash the app. This just shows everything on the network raw
                         **/
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



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        
        private ArrayList<String> mDataset;

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView mTextView;
            ViewHolder(View view) {
                super(view);

                view.setOnClickListener(this);
                mTextView = (TextView) view.findViewById(R.id.FoundDeviceTextView);
            }

            @Override
            public void onClick(View v) {
                int itemPosition = mRecyclerView.getChildLayoutPosition(v);
                String item = mDataset.get(itemPosition);
                Toast.makeText(mContext, item, Toast.LENGTH_LONG).show();

                addToDevices(item);

                filteredDevices.remove(item);
                foundDeviceCount--;
                //load fragment
                //loadFragment(addSelectedDeviceFrag);

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
