package com.example.android.loginfirebase;

/**
 * Created by sudha on 18-Mar-18.
 */

public class UserInformation {

    public String name, email, gender, dob, bloodGroup, mobile, address;

    UserInformation() {
    }

    public UserInformation(String name, String email, String gender, String dob, String bloodGroup, String mobile, String address) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.bloodGroup = bloodGroup;
        this.mobile = mobile;
        this.address = address;
    }
}
