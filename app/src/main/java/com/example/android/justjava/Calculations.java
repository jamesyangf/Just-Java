package com.example.android.justjava;

/**
 * Created by Jae Yang on 8/9/2016.
 */
public class Calculations {


    public double calculateCost(int qty, double cost){
        return (qty * cost);
    }

    public double totalPrice(double subTotal, double taxPercent){
        double tax = subTotal * taxPercent;
        return (subTotal+tax);
    }
}
