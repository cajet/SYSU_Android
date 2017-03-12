package com.example.cajet.lab3;


import java.io.Serializable;

/**
 * Created by cajet on 2016/10/9.
 */

public class Contactor implements Serializable{

    private String c_name;

    private String c_mobilenumber;

    private String c_type;

    private String c_location;

    private int c_bgcolor;

    public Contactor() {}

    public Contactor(String name, String mobilenumber, String type, String location, int bgcolor) {
        c_name= name;
        c_mobilenumber= mobilenumber;
        c_type= type;
        c_location= location;
        c_bgcolor= bgcolor;
    }

    public String getName() {
        return c_name;
    }

    public String getFirstWord() {
        return c_name.substring(0, 1);
    }

    public String getMobilenumber() {
        return c_mobilenumber;
    }

    public String getType() {
        return c_type;
    }

    public String getLocation() {
        return c_location;
    }

    public int getBGcolor() {
        return c_bgcolor;
    }
}
