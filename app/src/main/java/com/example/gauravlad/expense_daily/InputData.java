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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class InputData extends Activity implements  AdapterView.OnItemSelectedListener {

    DBHelper dbHelper;
    Button bAdd, bMinus;
    EditText etInput;
    Calendar calendar;
    int month, day, year;
    TextView tv, etInputDate,tvForbalance;
    SharedPreferences sharedPreferences;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_file);

        bAdd = (Button) findViewById(R.id.bAdd);
        bMinus = (Button) findViewById(R.id.bMinus);
        etInputDate = (TextView)findViewById(R.id.etInputDate);
        etInput = (EditText)findViewById(R.id.etInput);
        tv = (TextView)findViewById(R.id.tv);
        tvForbalance = (TextView)findViewById(R.id.tvForBalance);
        sharedPreferences = getSharedPreferences("BalanceFile", Context.MODE_PRIVATE);

        dbHelper = new DBHelper(getApplicationContext(), null, null, 1);

        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDate(year, month+1, day);

        tv.setMovementMethod(new ScrollingMovementMethod());

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener( this);

        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Food");
        categories.add("Movie");
        categories.add("Daily Usage");
        categories.add("Travel");
        categories.add("Hotel/Restaurant");
        categories.add("Shopping/Online Shopping");
        categories.add("Medical");
        categories.add("Other");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.sppinner_title , categories);

        adapter.setDropDownViewResource(R.layout.spinner_layout);

        spinner.setAdapter(adapter);

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
        etInputDate.setText(new StringBuilder().append(dbHelper.doubleDigit(""+day)).append("/")
                .append(dbHelper.doubleDigit(""+month)).append("/").append(year));
    }

    public void addButtonClick(View view){
        if (etInput.getText().toString().length() != 0  ) {//why etInput.getText().toString() is not working?
            Expense expense = new Expense(dbHelper.changeDateOrder(etInputDate.getText().toString()), etInput.getText().toString(), spinner.getSelectedItem().toString());
            dbHelper.addMoney(expense);
            Toast.makeText(getApplicationContext(), "Added!!!", Toast.LENGTH_SHORT).show();

            Log.d("d", dbHelper.MyBalance()+"<---This is value after adding expense!!!");

            DataOfATM.balance = dbHelper.MyBalance();
            tvForbalance.setText("Your Balance is: "+dbHelper.MyBalance()+"");
            printDatabase();

            try {
                dbHelper.copyAppDbToDownloadFolder();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            Snackbar snackbar = Snackbar.make(view, "Fill Expense!!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void MinusButtonClicked(View view){

        if (etInput.getText().toString().length() != 0 ) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure you want decrease the expense?");

            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Expense expense = new Expense(etInputDate.getText().toString(), etInput.getText().toString(), spinner.getSelectedItem().toString());
                            dbHelper.MinusMoney(expense);
                            Toast.makeText(getApplicationContext(), "Minus!!!", Toast.LENGTH_SHORT).show();
                            DataOfATM.balance = dbHelper.MyBalance();
                            tvForbalance.setText("Your Balance is: "+dbHelper.MyBalance()+"");
                            printDatabase();

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
            Snackbar snackbar = Snackbar.make(view, "Fill Expense!!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void deleteButtonClick(View view){
        final String input = etInputDate.getText().toString();
        final String inputComment = spinner.getSelectedItem().toString();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want delete data from "+ input +" date and comment -->" + inputComment + "?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        dbHelper.deleteMoney(input, inputComment);
                        Toast.makeText(getApplicationContext(), "Deleted!!!", Toast.LENGTH_SHORT).show();
                        DataOfATM.balance = dbHelper.MyBalance();
                        tvForbalance.setText("Yoyr Balance is:"+dbHelper.MyBalance()+"");
                        printDatabase();

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

        DataOfATM.balance = dbHelper.MyBalance();
        tvForbalance.setText("Your Balance is: "+dbHelper.MyBalance()+"");

    }

    public void printDatabase(){
        String s = dbHelper.databsetoString(1);
        tv.setText(s);
    }

    @Override
    public void onBackPressed() {
/*
        try {
            dbHelper.copyAppDbToDownloadFolder(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Balance", DataOfATM.balance);
        editor.commit();
        Log.d("d", "This balance is in onCreate method!!!");
        finish();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item = parent.getItemAtPosition(position).toString();

        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
