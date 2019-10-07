package com.homehub.dragan.myhomehub.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;


import com.homehub.dragan.myhomehub.Fragments.AppSettingsFragment;
import com.homehub.dragan.myhomehub.Fragments.DeviceRegistrationsFragment;
import com.homehub.dragan.myhomehub.Fragments.GeneralFormFragment;
import com.homehub.dragan.myhomehub.R;

public class AccountSubActivity extends AppCompatActivity {

    private Fragment accSelectedListFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //show action bar
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        //show acc_settings
        setContentView(R.layout.activity_acc_settings);

        //get selected list item
        int selectedListItem = getIntent().getIntExtra("selectedListItem", 0);

        //generate necessary fragment
        switch (selectedListItem) {
            case 0:
                //set instance of the fragment
                accSelectedListFrag = GeneralFormFragment.newInstance();
                break;
            case 1:
                //set instance of the fragment
                accSelectedListFrag = DeviceRegistrationsFragment.newInstance();
                break;
            case 2:
                //set instance of the fragment
                accSelectedListFrag = AppSettingsFragment.newInstance();
                break;
        }

        //load fragment
        loadFragment(accSelectedListFrag);

    }

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

            return true;
        }
        return false;
    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
