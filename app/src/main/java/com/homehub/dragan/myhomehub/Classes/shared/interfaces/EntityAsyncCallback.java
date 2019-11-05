package com.homehub.dragan.myhomehub.Classes.shared.interfaces;

import com.homehub.dragan.myhomehub.Classes.model.Entity;

import java.util.ArrayList;

public interface EntityAsyncCallback {
    void onFinished(ArrayList<Entity> entities);
}
