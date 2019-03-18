package com.homehub.dragan.myhomehub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
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

    private Context mContext;
    private RecyclerView recyclerView;
    private TextView mTextMessage;

    private ArrayList<Object> getSampleArrayList() {
        ArrayList<Object> items = new ArrayList<>();

        items.add(new User("Dany Targaryen", "Valyria"));
        items.add(new SliderBasedControl("Nest Thermostat",23));
        items.add(new SwitchBasedControl("hue bulb 1", true));
        items.add(new User("Rob Stark", "Winterfell"));
        items.add(new SwitchBasedControl("hue bulb 2", false));
        items.add("image");
        items.add(new User("Jon Snow", "Castle Black"));
        items.add("image");
        return items;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_account:
                    mTextMessage.setText(R.string.title_account);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_automation);
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

        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.recyclerView);
        rvContacts.setAdapter(new ComplexRecyclerViewAdapter(getSampleArrayList()));
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



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
