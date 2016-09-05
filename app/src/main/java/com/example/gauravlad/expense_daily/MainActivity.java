package com.example.gauravlad.expense_daily;

import android.Manifest;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    DBHelper dbHelper;
    Button bDay, bData, bATM;
    NotificationManager notificationManager;
    int notifyId = 33, value = 0 ;
    static boolean isNoticActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(getApplicationContext(), null, null, 1);

        bDay = (Button) findViewById(R.id.bByDay);
        bData = (Button) findViewById(R.id.bData);
        bATM = (Button) findViewById(R.id.bATM);

        bData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(getApplicationContext(), InputData.class);
                startActivity(i);
            }
        });

        bATM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(getApplicationContext(), DataOfATM.class);
                startActivity(i);
            }
        });


        SharedPreferences mSharedPreference= getSharedPreferences("BalanceFile", Context.MODE_PRIVATE);
        value=(mSharedPreference.getInt("Balance", 0));

        Log.d("d", "value of balance after getting in mainactivity!!!! = "+ value + "<---" );
        if( value == 0){ Toast.makeText(getApplicationContext(), "Your Balance is 0", Toast.LENGTH_LONG).show();}
        else if( value <= 500){
            showNotification();
            isNoticActive = true;
            Log.d("d", "showNoificaton() Area!!!");
        }else{
            Log.d("d", "Value is null or high then 500!!!" + isNoticActive);
            if(isNoticActive){
                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(notifyId);
            }
        }



        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(getApplicationContext(), "Ask for Permission!!!", Toast.LENGTH_SHORT).show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                try {
                    dbHelper.copyAppDbFromFolder();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    public void clicked(View view){
        Intent i = new Intent(MainActivity.this, DayData.class);
        startActivity(i);
    }



    public void showNotification(){
    // Builds a notification
    NotificationCompat.Builder notificBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
            .setContentTitle("Low Balance")
            .setContentText("Your Balance is : " + value)
            .setTicker("Alert New Message")
            .setSmallIcon(R.drawable.notification_icon);            ;

    // Define that we have the intention of opening MoreInfoNotification
        //[DELETED]

    // Used to stack tasks across activites so we go to the proper place when back is clicked
    TaskStackBuilder tStackBuilder = TaskStackBuilder.create(this);

    // Add all parents of this activity to the stack
    tStackBuilder.addParentStack(MainActivity.class);

    // Add our new Intent to the stack
        //[DELETED]

    // Define an Intent and an action to perform with it by another application
    // FLAG_UPDATE_CURRENT : If the intent exists keep it but update it if needed
        //[DELETED]

    // Defines the Intent to fire when the notification is clicked
        //[DELETED]

    // Gets a NotificationManager which is used to notify the user of the background event
    notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    //notificBuilder.setAutoCancel(true);//should be before  --> notificationManager.notify

    // Post the notification
    notificationManager.notify(notifyId, notificBuilder.build());

    // Used so that we can't stop a notification that has already been stopped
    isNoticActive = true;

}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {


        Log.d("d", "onRequestPermissionsResult!!!!");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(), "Permission granted!!!", Toast.LENGTH_SHORT).show();

                    try {
                        dbHelper.copyAppDbFromFolder();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(getApplicationContext(), "Denieded!!!", Toast.LENGTH_SHORT).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }

    }



}
