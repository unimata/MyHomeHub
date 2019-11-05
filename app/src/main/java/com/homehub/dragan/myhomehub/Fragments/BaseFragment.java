package com.homehub.dragan.myhomehub.Fragments;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.homehub.dragan.myhomehub.Activities.AppController;
import com.homehub.dragan.myhomehub.Activities.MainActivity;
import com.homehub.dragan.myhomehub.Classes.model.rest.CallServiceRequest;

public class BaseFragment extends Fragment {

    public void setSubtitle(String title) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(title);
            }
        }
    }

    public AppController getAppController() {
        Application app = getActivity().getApplication();
        if (app instanceof AppController) {
            return (AppController) app;
        }
        throw new RuntimeException("Unsupported Activity");
    }

    public void callService(final String domain, final String service, CallServiceRequest serviceRequest) {
        Activity app = getActivity();
        if (app instanceof MainActivity) {
//            ((MainActivity) app).callService(domain, service, serviceRequest);
            return;
        }
        throw new RuntimeException("Unsupported Activity");
    }

    public boolean isActiveFragment() {
        Activity app = getActivity();
        if (app instanceof MainActivity) {
            return false;//((MainActivity) app).getCurrentEntityFragment() == this;
        }

        if (app == null) {

        }
        return false;
        //throw new RuntimeException("Unsupported Activity");
    }

    public void showToast(String message) {
        Activity app = getActivity();
        if (app instanceof MainActivity) {
//            ((MainActivity) app).showToast(message);
            return;
        }
        throw new RuntimeException("Unsupported Activity");
    }

}

