package com.roomr.dragan.myhomehub;

class User {
    private String name;
    private String location;

    public User(String name, String location) {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

}
