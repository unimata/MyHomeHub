package com.homehub.dragan.myhomehub.Classes.shared;

import android.support.v4.app.FragmentActivity;

public interface OnBackPressedInterface {
    /**
     * @param activity
     * @return true to indicate action is consumed.
     */
    boolean onBackPressed(FragmentActivity activity);
}
