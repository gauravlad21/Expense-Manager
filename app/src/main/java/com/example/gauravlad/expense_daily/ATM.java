package com.example.gauravlad.expense_daily;

/**
 * Created by gauravlad on 6/8/16.
 */
public class ATM {
    int money;
    String date;

    public ATM(int money, String date) {
        this.money = money;
        this.date = date;
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
