package com.homehub.dragan.myhomehub.Classes.model;

public class General_Form_User {
    private String first_name;
    private String last_name;
    private String email;
    private String phone_num;
    private String birthday;
    private String postal_code;

    public General_Form_User() {
    }

    public General_Form_User(String first_name, String last_name, String email,
                             String phone_num, String birthday, String postal_code) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone_num = phone_num;
        this.birthday = birthday;
        this.postal_code = postal_code;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }
}
