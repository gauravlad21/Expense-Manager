package com.example.gauravlad.expense_daily;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class DataOfATM extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    static int balance;
    Calendar calendar;
    int year, month,day;
    DBHelper dbHelper;
    TextView tvDateOfATM,tvATMshow, tv;
    EditText etATM;
    SharedPreferences sharedpreferences;
    Button bShowAtm, bAddATM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atm);

        Log.e("Important", balance+"<--- HERE US THE BALANCE!!!!");

        dbHelper = new DBHelper(getApplicationContext(), null, null, 1);
        tvDateOfATM = (TextView) findViewById(R.id.tvDateOfATM);
        tvATMshow = (TextView) findViewById(R.id.tvATMshow);
        etATM = (EditText) findViewById(R.id.etATM);
        calendar = Calendar.getInstance();
        bAddATM = (Button)findViewById(R.id.bAddAtm);
        bShowAtm = (Button)findViewById(R.id.bshowAtm);

        sharedpreferences = getSharedPreferences("BalanceFile", Context.MODE_PRIVATE);

        tvATMshow.setMovementMethod(new ScrollingMovementMethod());
        tv = (TextView) findViewById(R.id.tv);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDate(year, month+1, day);

        bAddATM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickATM(v);
            }
        });

        bShowAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    onShowATM();
                }catch (Exception e){
                    e.printStackTrace();
                    Snackbar snackbar = Snackbar.make(v, "Fill the ATM or Expense!!!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("Balance", balance);
        editor.commit();
        Log.d("d", "This balance is in onCreate method!!!");


        try {
            dbHelper.copyAppDbFromFolder();
            Log.d("d", "dbHelper.copyAppDbFromFolder(getApplicationContext());<<---- in DataOfATM!!!");
        } catch (IOException e) {
            e.printStackTrace();
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

            }
        }

    }

    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            //return new DatePickerDialog(this, myDateListener, year, month, day);

            //cant select date after today!!!!
            DatePickerDialog dialog = new DatePickerDialog(this, myDateListener, year, month, day);
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            return dialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year// arg2 = month // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        String s = new StringBuilder().append(dbHelper.doubleDigit(dbHelper.doubleDigit(""+day))).append("/").append(dbHelper.doubleDigit(""+month)).append("/").append(year).toString();
        tvDateOfATM.setText(s);
    }

    public void onClickATM(View view){

        if (etATM.getText().toString().length() != 0  ) {//why etInput.getText().toString() is not working?

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure you want to increse amount in ATM???");

                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                ATM atm = new ATM( etATM.getText().toString(), dbHelper.changeDateOrder(tvDateOfATM.getText().toString()) );
                                dbHelper.addToATM(atm);
                                tvATMshow.setText(dbHelper.shoToATM( 1 ));
                                Toast.makeText(getApplicationContext(), "Added in ATM", Toast.LENGTH_SHORT).show();
                                //Log.d("d","Enters in onClickATM!!!");

                                Log.d("d", dbHelper.shoToATM(0)+" <<<<<<<<<<<<<-----------added in ATM");
                                balance = dbHelper.MyBalance();
                                tv.setText("Your Current Balance is :"+ balance);

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putInt("Balance", balance);
                                editor.commit();

                                try {
                                    dbHelper.copyAppDbToDownloadFolder();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(getApplicationContext(), "You Safe!!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
        }else{
            Snackbar snackbar = Snackbar.make(view, "Fill ATM money!!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void onShowATM(){
        tvATMshow.setText(dbHelper.shoToATM(1));
        tv.setText("Your balance is : "+dbHelper.MyBalance());
    }

    @Override
    public void onBackPressed() {

        finish();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

    }

}
