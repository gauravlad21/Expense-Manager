package com.example.gauravlad.expense_daily;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


public class InputData extends Activity {

    DBHelper dbHelper;
    Button bAdd, bMinus;
    EditText etInput,  etComment;
    Calendar calendar;
    int month, day, year;
    TextView tv, etInputDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_file);

        bAdd = (Button) findViewById(R.id.bAdd);
        bMinus = (Button) findViewById(R.id.bMinus);
        etInputDate = (TextView)findViewById(R.id.etInputDate);
        etInput = (EditText)findViewById(R.id.etInput);
        etComment = (EditText)findViewById(R.id.etComment);
        tv = (TextView)findViewById(R.id.tv);

        dbHelper = new DBHelper(getApplicationContext(), null, null, 1);

        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDate(year, month+1, day);
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
        etInputDate.setText(new StringBuilder().append(dbHelper.doubleDigit(""+year)).append("/")
                .append(dbHelper.doubleDigit(""+month)).append("/").append(dbHelper.doubleDigit(""+day)));
    }

    public void addButtonClick(View view){
        if (etInput.getText().toString().length() != 0  ) {//why etInput.getText().toString() is not working?
            Expense expense = new Expense(etInputDate.getText().toString(), etInput.getText().toString(), etComment.getText().toString());
            dbHelper.addMoney(expense);
            Toast.makeText(getApplicationContext(), "Added!!!", Toast.LENGTH_SHORT).show();
            printDatabase();
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
                            Expense expense = new Expense(etInputDate.getText().toString(), etInput.getText().toString(), etComment.getText().toString());
                            dbHelper.MinusMoney(expense);
                            Toast.makeText(getApplicationContext(), "Minus!!!", Toast.LENGTH_SHORT).show();
                            printDatabase();
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want delete data from "+ input +" date?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        dbHelper.deleteMoney(input);
                        Toast.makeText(getApplicationContext(), "Deleted!!!", Toast.LENGTH_SHORT).show();
                        printDatabase();
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

    }

    public void printDatabase(){
        String s = dbHelper.databsetoString();
        tv.setText(s);
    }

}
