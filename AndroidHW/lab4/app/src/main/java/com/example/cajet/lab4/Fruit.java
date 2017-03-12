package com.example.cajet.lab4;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by cajet on 2016/10/16.
 */

public class Fruit {

    private int F_image_id;

    private String F_name;

    public Fruit() {}

    public Fruit(int id, String name) {
        F_image_id= id;
        F_name= name;
    }

    public int getFruitImageId() {
        return F_image_id;
    }

    public String getFruitName() {
        return F_name;
    }
}
