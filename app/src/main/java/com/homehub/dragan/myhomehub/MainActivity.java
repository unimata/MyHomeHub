package com.homehub.dragan.myhomehub;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mPlayer;
    private Context mContext;
    private RecyclerView recyclerView;
    private TextView mTextMessage;
    private FloatingActionButton mAddButton;

    private ArrayList<Object> getSampleArrayList() {
        ArrayList<Object> items = new ArrayList<>();

        items.add(new SliderBasedControl("Thermostat",23));
        items.add(new SwitchBasedControl("Master Bedroom Lights", true));
        items.add(new SwitchBasedControl("Foyer Lights", false));
        items.add(new SwitchBasedControl("TV Backlighting", false));

        //items.add("image");
        //items.add(new User("Jon Snow", "Castle Black"));
        //items.add("image");
        items.add(true);//Keep this at the end always

        return items;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {

                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_account:

                    intent = new Intent(MainActivity.this, AccountActivity.class);
                    //intent.putExtra("channel", "4lmrrOD8Ll2SkO2A");
                    startActivity(intent);
                    return true;
                case R.id.navigation_automation:

                    intent = new Intent(MainActivity.this, AutomationActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

       refreshDeviceList();

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



    }

    //used to initially load in device list, and to reload after adding new device to list
    public void refreshDeviceList(){
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recyclerView);
        rvContacts.setAdapter(new ComplexRecyclerViewAdapter(getSampleArrayList()));
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logout, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.lblEdit:
                Toast.makeText(mContext, "Edit", Toast.LENGTH_SHORT).show();
                //Intent i = new Intent(MainActivity.this, LoginActivity.class);
                //startActivity(i);
                return true;
            case R.id.lblLogout:
                Toast.makeText(mContext, "Logout", Toast.LENGTH_SHORT).show();
               // Intent i = new Intent(MainActivity.this, LoginActivity.class);
                //startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
