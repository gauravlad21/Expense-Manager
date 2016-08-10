package com.example.gauravlad.expense_daily;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class DataOfATM extends Activity {

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
                onShowATM();
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("Balance", balance);
        editor.commit();
        Log.d("d", "This balance is in onCreate method!!!");
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
        tvDateOfATM.setText(new StringBuilder().append(dbHelper.doubleDigit(""+year)).append("/").append(dbHelper.doubleDigit(""+month)).append("/").append(dbHelper.doubleDigit(""+day)));
    }

    public void onClickATM(View view){

                if (etATM.getText().toString().length() != 0  ) {//why etInput.getText().toString() is not working?

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("Are you sure you want to increse amount in ATM???");

                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    ATM atm = new ATM( etATM.getText().toString(), tvDateOfATM.getText().toString() );
                                    dbHelper.addToATM(atm);
                                    tvATMshow.setText(dbHelper.shoToATM( 1 ));
                                    Toast.makeText(getApplicationContext(), "Added in ATM", Toast.LENGTH_SHORT).show();
                                    //Log.d("d","Enters in onClickATM!!!");

                                    balance = dbHelper.MyBalance();
                                    tv.setText("Your Current Balance is :"+ balance);

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putInt("Balance", balance);
                                    editor.commit();

                                    //Toast.makeText(getApplicationContext(), "Msg Should be disappear!!!", Toast.LENGTH_LONG).show();

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
