package com.example.cajet.text_contact;

import java.io.Serializable;

class User implements Serializable {
    int _id;
    int imageId;
    String imagePath;
    String username;
    String zipCode;
    String birthday;
    String mobilePhone;
    String familyPhone;
    String address;
    String company;
    String email;
    String otherContact;
    String remark;
    String position;

    User() {
        imageId = R.drawable.icon1;
        imagePath = "";
        username = "";
        zipCode = "";
        birthday = "";
        mobilePhone = "";
        familyPhone = "";
        address = "";
        company = "";
        email = "";
        otherContact = "";
        remark = "";
        position = "";
    }
}
