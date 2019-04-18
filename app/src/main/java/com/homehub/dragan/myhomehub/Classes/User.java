package com.homehub.dragan.myhomehub.Classes;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

class User {
    private String Name;
    private String Location;

    public User(String name, String location) {
        setName(name);
        setLocation(location);
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public String getLocation() {
        return Location;
    }

    public String getName() {
        return Name;
    }

    public void firebaseGetUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
    }



}
