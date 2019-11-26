package com.homehub.dragan.myhomehub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.homehub.dragan.myhomehub.Fragments.AccountMenuFragment;
import com.homehub.dragan.myhomehub.R;

public class AccountActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Fragment accMenuFrag;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        intent = new Intent(AccountActivity.this, MainActivity.class);
                        //intent.putExtra("channel", "4lmrrOD8Ll2SkO2A");
                        startActivity(intent);
                        AccountActivity.super.finish();
                        return true;
                    case R.id.navigation_account:
                        return true;
                    case R.id.navigation_automation:
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

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_account);

        //instance of the fragment
        accMenuFrag = new AccountMenuFragment();

        //load fragment
        loadFragment(accMenuFrag);

    }

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }

}
