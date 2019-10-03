package com.homehub.dragan.myhomehub.Classes.shared;

import android.app.FragmentManager;
import android.content.Context;

import com.homehub.dragan.myhomehub.Classes.model.rest.CallServiceRequest;

public interface EntityProcessInterface {
    void callService(final String domain, final String service, CallServiceRequest serviceRequest);

    FragmentManager getFragmentManager();

    Context getActivityContext();

    void showToast(String message);
}
