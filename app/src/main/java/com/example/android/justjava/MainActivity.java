package com.example.android.justjava;
/*
 * Author: James Yang
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View; //the View object file
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView; //the TextView object file
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private int n = 0;
    private double c = 3.24;
    private double taxPercent = 0.095;
    private String coffeeType = "Java";
    private String priceMessage = "t";
    private ListView listView; //our list view
    private Calculations calculate = new Calculations();

    //list of orders will be saved in this list
    private ArrayList<String> orders = new ArrayList<String>();
    //list of all the cost
    private ArrayList<Double> totalPriceList = new ArrayList<Double>();
    //list of all the qty
    private ArrayList<Integer> totalQtyList = new ArrayList<Integer>();

    //Handle the data of the listView, this will update the view when the list is changed
    private ArrayAdapter<String> adapter; //adapter kinda like what updatess the view

    @Override //initial start of app
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sets it to our list view by finding the id
        listView = (ListView) findViewById(R.id.list);
        //context, layout, and arraylist
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, orders);
        listView.setAdapter(adapter);//sets the listViews adapter
    }

    /*
     * This method is called when the + button is clicked
     */
    public void addQuantity(View view){
        n++;
        priceMessage = "";
        displayQuantity(n);
        displayPrice(calculate.calculateCost(n,c));
    }

    /*
     * This method is called when the - button is clicked
     */
    public void subQuantity(View view){
        n--;
        priceMessage = "";
        if(n < 0){
            n = 0;
        }
        displayQuantity(n);
        displayPrice(calculate.calculateCost(n,c));
    }

    /*
     * This method is called when the clear button is clicked
     */
    public void clearQuantity(View view){
        n = 0;
        priceMessage = "";
        displayQuantity(n);
        displayPrice(0);
    }

    /*
     * This is the method for the clear All Dialog
     */

    public void clearAllDialog() {
        //Instantiate an AlertDialog.Builder with its contructor
        AlertDialog builder = new AlertDialog.Builder(MainActivity.this).create();

        builder.setTitle("Alert");

        //Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Are you sure you want to clear all orders?");//sets the message of the alert Dialog
        builder.setButton(builder.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                orders.clear();
                totalPriceList.clear();
                displayTotal(0);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setButton(builder.BUTTON_NEGATIVE,"Cancel",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        //Create the AlertDialog Object and return it
        builder.show();

    }

    public void orderConformationDialog() {
        //Instantiate an AlertDialog.Builder with its contructor
        AlertDialog builder = new AlertDialog.Builder(MainActivity.this).create();

        //Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Are you done ordering?");//sets the message of the alert Dialog
        builder.setButton(builder.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                EditText nameView = (EditText) findViewById(R.id.name_view);
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, "Java Order");
                intent.putExtra(Intent.EXTRA_TEXT, createOrderSummary(nameView.getText().toString(),
                        getTotalQty(),
                            calculate.totalPrice(getTotalPrice(),
                                taxPercent)));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        builder.setButton(builder.BUTTON_NEGATIVE,"Cancel",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        });

        //Create the AlertDialog Object and return it
        builder.show();

    }

    public void clearOrder(View view){
       clearAllDialog();
    }

    /*
     * This method displays the given quantity value on the screen.
     */
    private void displayQuantity(int number){
        //referenceing the quantity_text_view and calling it quantityTextView
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);

        //Calling the objects method setText
        quantityTextView.setText(""+number);
    }

    /*
     * This method displays the given price on the screen
     */
    private void displayPrice(double number){
        TextView priceTextView = (TextView)findViewById(R.id.price_text_view);
        priceTextView.setText("Cost: "+NumberFormat.getCurrencyInstance().format(number));
    }

    /*
     * This method displays the total price
     */
    private void displayTotal(double number){
        TextView totalTextView = (TextView) findViewById(R.id.total_text_view);
        totalTextView.setText("Subtotal: "+ NumberFormat.getCurrencyInstance().format(number));
    }

    /*
     * This method is called when the add button is called
     */
    public void addItems(View view){
        if(n != 0) {
            //adds data to array list
            orders.add("Coffee: "+coffeeType+"   Cost: " +NumberFormat.getCurrencyInstance().format(n * c) +
                    "   Qty: " + n);

            displayTotal(getTotalPrice());
            totalQtyList.add(n);
            //check if your adapter changed
            adapter.notifyDataSetChanged();
        }

        n=0;
        displayQuantity(0);
        displayPrice(0);
    }
    /*
     * This method gets the total Price or subtotal actually
     */
    private double getTotalPrice(){
        double totalPrice = 0;
        //adds up the total price
        totalPriceList.add(calculate.calculateCost(n,c));
        for(int i=0; i < totalPriceList.size(); i++){
            totalPrice += totalPriceList.get(i);

        }
        return totalPrice;

    }
    /*
     * This method gets the total Price or subtotal actually
     */
    private int getTotalQty(){
        int totalQty = 0;
        //adds up the total qty
        for(int i=0; i < totalQtyList.size(); i++){
            totalQty += totalQtyList.get(i);

        }
        return totalQty;

    }

    /*
     * This method is called when the order button is called
     */
    public void orderButton(View view){

        orderConformationDialog();
    }

    /*
     * This is the order summary
     */
    public String createOrderSummary(String name, int totalQty,double totalPrice){
        return ("Name: "+ name +"\n"
                +"Qty: "+ totalQty+"\n"
                    +"Total: "+ NumberFormat.getCurrencyInstance().format(totalPrice)+"\n"
                        +"Thank You!");

    }



}
