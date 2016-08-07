package com.example.gauravlad.expense_daily;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class DayData extends Activity{

    DBHelper dbHelper;
    Button bShow, bshowMonth, bSSDB, bShooow;
    Calendar calendar;
    int year, month, day;
    EditText etForDate, etForMonth, etFrom, etTo;
    TextView tvData, tvData2, tvSDB, tvShooow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.by_day);

        etForMonth = (EditText) findViewById(R.id.etForMonth);
        etForDate = (EditText)findViewById(R.id.etForDay);
        etTo = (EditText)findViewById(R.id.etTo);
        etFrom = (EditText)findViewById(R.id.etFrom);
        bshowMonth = (Button) findViewById(R.id.bShow);
        bShow = (Button) findViewById(R.id.bShoow);
        bShooow = (Button) findViewById(R.id.bShoooow);
        bSSDB = (Button) findViewById(R.id.bSDB);
        tvData = (TextView) findViewById(R.id.tvData);
        tvShooow = (TextView) findViewById(R.id.tvShooow);
        tvData2 = (TextView)findViewById(R.id.tvData2);
        tvSDB = (TextView) findViewById(R.id.tvsdb);

        dbHelper = new DBHelper(getApplicationContext(), null, null, 1);

        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDate(year, month+1, day);

        bShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.showDetail(etForDate.getText().toString(), tvData);
            }
        });

        bshowMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataMonth = etForMonth.getText().toString();
                if ( dataMonth.length() == 0 || Integer.parseInt(dataMonth) <=0 || Integer.parseInt(dataMonth) >12 ){
                    Toast.makeText(getApplicationContext(), "Please Put Correct Month!!!", Toast.LENGTH_SHORT).show();
                }else{
                    String s = "/"+dataMonth+"/";
                    Log.d("d", s);
                    tvData2.setText( "Your Expenses in " + whichMonth(dataMonth) +" month is : " + dbHelper.dataFromMonth(s) ); //why i cant sent textview in other class method??
                }
            }
        });

        bShooow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = etFrom.getText().toString();
                String to = etTo.getText().toString();

                tvShooow.setText( dbHelper.showDataBetweenTwoDates(from , to) );
            }
        });

    }
    //----------end of onCreate Method!!!


    public void setDate(View view) {
        showDialog(99);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 99) {
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
        etForDate.setText(new StringBuilder().append(year).append("/")
                .append(month).append("/").append(day));
    }

    //--------------------end of date dialogue!!!

    String whichMonth(String s){
        if (s.equals("1")) return "January";
        else if (s.equals("2")) return "February";
        else if (s.equals("3")) return "March";
        else if (s.equals("4")) return "April";
        else if (s.equals("5")) return "May";
        else if (s.equals("6")) return "June";
        else if (s.equals("7")) return "July";
        else if (s.equals("8")) return "August";
        else if (s.equals("9")) return "September";
        else if (s.equals("10")) return "October";
        else if (s.equals("11")) return "November";
        else return "December";
    }



}
