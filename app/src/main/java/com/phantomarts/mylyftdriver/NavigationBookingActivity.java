package com.phantomarts.mylyftdriver;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class NavigationBookingActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private Button btnPlaceBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_booking);
        initActionBar();

        btnPlaceBooking=findViewById(R.id.btnPlaceBooking);
        btnPlaceBooking.setOnClickListener(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Placing Booking ...");

    }

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.drawermenu_booklater);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        final Activity activity=this;
        if(v==btnPlaceBooking){
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    AlertDialog dialog=new AlertDialog.Builder(activity)
                            .setTitle("Booking")
                            .setMessage("Your Booking is Placed We will notify when driver accepts your Booking ")

                            .setPositiveButton(android.R.string.ok, null)

                            .setIcon(R.drawable.ic_menu_booking)
                            .show();
                }
            },2000);
        }
    }
}
