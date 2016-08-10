package com.example.gauravlad.expense_daily;

import android.util.Log;

import java.util.regex.Pattern;

/**
 * Created by gauravlad on 6/8/16.
 */
public class ATM {
    int money;
    String date;
    String sum[];

    public ATM(String mny, String date) {
        this.date = date;

        sum = mny.split(" ");
        for (int i = 0 ; i < sum.length ; i++){
            //Log.d("d", sum[i] + "<--this is it!!! and sum.length: "+sum.length);
            if (Pattern.matches("[a-zA-Z]+", sum[i]) == false && sum[i].length() > 0) {
                try {
                    this.money += Integer.parseInt(sum[i]);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                this.money = -1;
                Log.d("d", "cant do");
                break;
            }
        }

    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
