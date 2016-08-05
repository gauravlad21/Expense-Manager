package com.example.gauravlad.expense_daily;

import android.util.Log;

import java.util.regex.Pattern;

public class Expense {

    private String date;
    private int money;
    private String comment;

    String sum[];

    public Expense(String date, String money, String comment) {
        this.date = date;
        sum = money.split(" ");
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
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
