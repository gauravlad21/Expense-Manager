package com.example.gauravlad.expense_daily;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class DataOfATM extends Activity {

    Calendar calendar;
    int year, month,day;
    DBHelper dbHelper;
    TextView tvDateOfATM,tvATMshow;
    EditText etATM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atm);

        dbHelper = new DBHelper(getApplicationContext(), null, null, 1);
        tvDateOfATM = (TextView) findViewById(R.id.tvDateOfATM);
        tvATMshow = (TextView) findViewById(R.id.tvATMshow);
        etATM = (EditText) findViewById(R.id.etATM);
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
        tvDateOfATM.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    public void onClickATM(View view){
        if (etATM.getText().toString().length() != 0  ) {//why etInput.getText().toString() is not working?
            ATM atm = new ATM( Integer.parseInt(etATM.getText().toString()), tvDateOfATM.getText().toString() );
            dbHelper.addToATM(atm);
            tvATMshow.setText(dbHelper.shoToATM(atm));
            Toast.makeText(getApplicationContext(), "Added!!!", Toast.LENGTH_SHORT).show();

        }else{
            Snackbar snackbar = Snackbar.make(view, "Fill ATM money!!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }
}
