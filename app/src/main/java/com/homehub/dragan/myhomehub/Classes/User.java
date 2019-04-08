package com.homehub.dragan.myhomehub.Classes;

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

}
