package com.example.gauravlad.expense_daily;

import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button bDay, bData, bATM;
    NotificationManager notificationManager;
    int notifyId = 33, value = 0 ;
    static boolean isNoticActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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




}
