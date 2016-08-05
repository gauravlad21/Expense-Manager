package com.example.gauravlad.expense_daily;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button bDay, bData, bATM;

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
                Intent i = new Intent(getApplicationContext(), InputData.class);
                startActivity(i);
            }
        });

    }

    public void clicked(View view){
        Intent i = new Intent(MainActivity.this, DayData.class);
        startActivity(i);
    }
}
