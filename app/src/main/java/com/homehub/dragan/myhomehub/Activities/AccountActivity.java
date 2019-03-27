package com.homehub.dragan.myhomehub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.homehub.dragan.myhomehub.R;

public class AccountActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    //intent = new Intent(MainActivity.this, MainActivity.class);
                    //startActivity(intent);
                    AccountActivity.super.finish();
                    return true;
                case R.id.navigation_account:
                    mTextMessage.setText(R.string.title_account);
                    intent = new Intent(AccountActivity.this, AccountActivity.class);
                    //intent.putExtra("channel", "4lmrrOD8Ll2SkO2A");
                    startActivity(intent);
                    return true;
                case R.id.navigation_automation:
                    mTextMessage.setText(R.string.title_automation);
                    intent = new Intent(AccountActivity.this, AutomationActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
