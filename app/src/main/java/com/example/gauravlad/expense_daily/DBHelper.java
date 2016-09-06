package com.example.gauravlad.expense_daily;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VIRSON = 1;
    private static final String DATABASE_NAME = "expense.db";
    private static final String TABLE_EXPENSE = "expense";
    private static final String TABLE_INCOME = "income";
    private static final String COLUMN_ATM = "_atm";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATE = "_date";
    private static final String COLUMN_COMMENT = "_comment";
    private static final String COLUMN_EXP = "_exp";//manual input
    private static boolean exist = false;
    private static boolean interalExist = false;
    File internalFile, file;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VIRSON);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "expense.db");
        internalFile= new File ("/data/user/0/com.example.gauravlad.expense_daily/databases/expense.db");

        exist = file.exists();
        interalExist = internalFile.exists();

        Log.d("d", exist + " <---currentDB.exists()");
        if( exist ) {////Toughest THING!!!! :P
            try {
                copyAppDbFromFolder();
                onUpgrade(db, 1, 1);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String s = " CREATE TABLE " + TABLE_EXPENSE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE + " DATETIME, " + COLUMN_COMMENT + " TEXT, " +  COLUMN_EXP + " INTEGER " + ");";
        String newstring = "CREATE TABLE " + TABLE_INCOME + " ( " + COLUMN_DATE + " Date, " +COLUMN_ATM + " INTEGER );";
        db.execSQL(s);
        db.execSQL(newstring);

        Log.d("d", "DatabaseOncreateDirectory!!!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_INCOME);
        onCreate(db);
    }

    //Add new Row to database
    public void addMoney(Expense product) {

        if (!exist){
            if (interalExist) {
                if (!isExistDate(String.valueOf(product.getDate()), product.getComment(), TABLE_EXPENSE)) {
                    SQLiteDatabase db = getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_DATE, String.valueOf(product.getDate()));
                    values.put(COLUMN_EXP, (product.getMoney()));
                    values.put(COLUMN_COMMENT, product.getComment());
                    db.insert(TABLE_EXPENSE, null, values);
                } else {
                    Log.d("d", String.valueOf(product.getMoney()) + " new");
                    SQLiteDatabase db = getWritableDatabase();

                    String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + " ='" + product.getDate() + "' AND " + COLUMN_COMMENT + " = '" + product.getComment() + "';";

                    Cursor c = db.rawQuery(query, null);
                    c.moveToFirst();

                    int va = (product.getMoney() + Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_EXP))));
                    String s = "UPDATE " + TABLE_EXPENSE + " SET " + COLUMN_EXP + " = '" + va + "' WHERE " + COLUMN_DATE + " ='" + product.getDate() + "' AND " + COLUMN_COMMENT + " = '" + product.getComment() + "';";
                    c = db.rawQuery(s, null);
                    c.moveToFirst();//It is important

                    // Log.d("d", va+"<----new!!!");
                }
            }
        }else{
            try {
                copyAppDbFromFolder();
                //onUpgrade((SQLiteDatabase) internalFile, 1, 1);
                //return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            addMoney(product);
        }
    }

    public void MinusMoney(Expense product){

        if(isExistDate(changeDateOrder(String.valueOf(product.getDate())), product.getComment() , TABLE_EXPENSE )  ) {

            Log.d("d", "Already Exist!!!");
            Log.d("d", String.valueOf(product.getMoney())+" new");
            SQLiteDatabase db =  getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + "='" + changeDateOrder(product.getDate()) + "' AND " + COLUMN_COMMENT + "='" + product.getComment() + "';";

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            int ans = (product.getMoney() - Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_EXP)))) *(-1);  //Question!!!!!!!!!!!!!!!!!!!!!
            Log.d("d", ans+"<--This is anwer!!!");
            if(ans >=0) {
                String s = "UPDATE " + TABLE_EXPENSE + " SET " + COLUMN_EXP + "='" + ans + "' WHERE " + COLUMN_DATE + "='" + changeDateOrder(product.getDate()) + "' AND " + COLUMN_COMMENT + "='" + product.getComment() + "';";
                c = db.rawQuery(s, null);
                c.moveToFirst();//It is important
            }else {
                Log.d("d", "Couldn't be done because subtraction is more!!!");
                //do nothing
            }
        }
    }

    public void deleteMoney(String input , String inputcomment){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(" DELETE FROM " +  TABLE_EXPENSE + " WHERE " + COLUMN_DATE + "='" + changeDateOrder(input) + "' AND " + COLUMN_COMMENT + "='" + inputcomment + "'");
        Log.d("d", "DELETED FROM SQLite DATABASE!!!");
    }

    public int allMoney(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COLUMN_EXP + " FROM " + TABLE_EXPENSE , null);
        c.moveToFirst();
        int ans = 0 ;

        while(!c.isAfterLast()){
            if (c.getString(c.getColumnIndex(COLUMN_EXP)) != null){
                Log.d("d", Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_EXP)))+"<<<<--------ans++");
                ans += Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_EXP)));
            }
            c.moveToNext();
        }
        c.close();
        return ans;
    }

    public String databsetoString(int x){

        int ans = 0;
        String dbString = "";

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSE + " ORDER BY " + COLUMN_DATE + " DESC";

        //CURSOR POINT TO A LOCATION IN YOUR RESULTS
        Cursor c = db.rawQuery(query, null);
        //Move to first row in your result
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_EXP)) != null && c.getString(c.getColumnIndex(COLUMN_COMMENT)) != null && c.getString(c.getColumnIndex(COLUMN_DATE)) != null) {
                dbString += c.getString(c.getColumnIndex(COLUMN_EXP)) + "..."+ changeDateOrder(c.getString(c.getColumnIndex(COLUMN_DATE))) + "..." + c.getString(c.getColumnIndex(COLUMN_COMMENT))  ;
                dbString += "\n";
                ans += Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_EXP)));
            }
            c.moveToNext();
        }
        c.close();

        return (x == 1) ? dbString : "" + ans;
    }
    // if record is exist then it will return true otherwise this method returns false
    public boolean isExistDate(String date, String comment , String Table) {
        // Log.d("d", "Entered in isExist--->>>" + date + " " + comment + " ;");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + Table + " WHERE " + COLUMN_DATE + "='" + date + "' AND " + COLUMN_COMMENT + "='" + comment + "';", null);/////DO NOT PUT SPACE AFTER & BEFORE ='<TEXT>'----------------
        boolean exist = (cur.getCount() > 0);
        cur.close();

        // Log.d("d", exist+"");
        return exist;
    }

    public boolean isExistATM(String date,  String Table) {
        // Log.d("d", "Entered in isExist--->>>" + date + " ;");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + Table + " WHERE " + COLUMN_DATE + "='" + date + "'" , null);/////DO NOT PUT SPACE AFTER & BEFORE ='<TEXT>'----------------
        boolean exist = (cur.getCount() > 0);
        cur.close();

        //Log.d("d", exist+"");
        return exist;
    }

    String showDetail(String date){
        ////Log.d("d", date+" received!!!");

        date =  changeDateOrder(date);
        String s = "";
        int ans=0;
        if(isExistATM(date  ,TABLE_EXPENSE) ) {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + "='" + date + "';";
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while(!c.isAfterLast()){
                s += c.getString(c.getColumnIndex(COLUMN_EXP)) +"..."+ c.getString(c.getColumnIndex(COLUMN_COMMENT));
                s += "\n";
                ans += Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_EXP)));
                c.moveToNext();
            }
            c.moveToFirst();
            return " Your amount on " + date + " is: " + ans + "\n Your detail is: \n  " + s;
        }else{
            //Log.d("d", "No data found!!!");
        }
        return "No data found!!!";
    }

    double dataFromMonth(String dataMonth, int x){

        double sum = 0;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ TABLE_EXPENSE + " WHERE 1=1 ";

        //CURSOR POINT TO A LOCATION IN YOUR RESULTS
        Cursor c = db.rawQuery(query, null);
        //Move to first row in your result
        c.moveToFirst();

        while(!c.isAfterLast()){

            if (c.getString(c.getColumnIndex(COLUMN_DATE)).contains(dataMonth) ){
                //Log.d("d", c.getString(c.getColumnIndex(COLUMN_EXP)) + "<-- This is expense!!!" );
                sum += Double.parseDouble( c.getString(c.getColumnIndex(COLUMN_EXP)) ) ;
            }

            c.moveToNext();
        }

        c.close();

        Double z = (sum * 100)/allMoney();
        return x==0?sum:z;
        //textView.setText(sum);
        // WHY IT IS NOT HAPPENING?????
    }

    double showDataBetweenTwoDates(String from, String to, int q){

        double returnString = 0;

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + " between '" + changeDateOrder(from) + "' AND '" + changeDateOrder(to) + "'";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_EXP)) != null && c.getString(c.getColumnIndex(COLUMN_COMMENT)) != null && c.getString(c.getColumnIndex(COLUMN_DATE)) != null) {

                //Log.d("d", "Enter in showDataBetweenTwoDates if condition.......!!!!");

                returnString += Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_EXP)));
            }
            c.moveToNext();
        }

        Log.d("d", returnString + " <---> " + allMoney());
        double z = (returnString * 100)/allMoney();

        Log.d("d", z+"<<-Z!!!!");
        return q==0 ? returnString : z;
    }

    public void addToATM(ATM product) {
        SQLiteDatabase db = getWritableDatabase();

        if(!isExistATM(String.valueOf(product.getDate()), TABLE_INCOME) ){

            ContentValues values = new ContentValues();
            values.put(COLUMN_DATE, String.valueOf(product.getDate()));
            values.put(COLUMN_ATM, String.valueOf(product.getMoney()));

            // //Log.e("d", "insert!!!!");
            db.insert(TABLE_INCOME, null, values);
        } else {
            Cursor c=null;
            // //Log.d("d", "Already Exist!!!");
            ////Log.d("d", String.valueOf(product.getMoney()) + " new");

            String s = "UPDATE " + TABLE_INCOME + " SET " + COLUMN_ATM + "='" + product.getMoney()  + "' WHERE " + COLUMN_DATE + "='" + product.getDate() + "';";
            c = db.rawQuery(s, null);
            c.moveToFirst();//It is important
            //Log.d("d", "UPDATED!!!");
        }
    }

    public String shoToATM( int x) {

        File MyDatabase = new File ("/data/user/0/com.example.gauravlad.expense_daily/databases/expense.db");
        String returnString = "...\n";
        int totalSum = 0;

        if (!exist){

            if (MyDatabase.exists()) {
                SQLiteDatabase db = getWritableDatabase();
                Cursor c = null;

                //Log.d("d", "isExist-->");
                String query = " SELECT * FROM " + TABLE_INCOME + " WHERE 1=1 ";

                c = db.rawQuery(query, null);
                c.moveToFirst();

                while (!c.isAfterLast()) {
                    if (c.getString(c.getColumnIndex(COLUMN_ATM)) != null && c.getString(c.getColumnIndex(COLUMN_DATE)) != null) {
                        returnString += c.getString(c.getColumnIndex(COLUMN_ATM)) + "<-->" + changeDateOrder(c.getString(c.getColumnIndex(COLUMN_DATE))) + "\n";
                        totalSum += Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ATM)));
                    }
                    c.moveToNext();
                }
            }
            // //Log.d("d", returnString+"<-- This should be displayed!!!");
        }else{
            try {
                copyAppDbFromFolder();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // return databsetoString(x);

        }
        if (x == 1) return returnString;
        else return "" + totalSum;

    }

    //For Date
    String doubleDigit(String x){
        return (x.length() == 1)?"0"+x:x;
    }

    public int MyBalance(){
        DataOfATM.balance = Integer.parseInt(shoToATM(0)) - Integer.parseInt(databsetoString(0));
        Log.d("d", DataOfATM.balance+ "     "+ shoToATM(0) + " " +databsetoString(0)  +"<<<----------DataOfATM.balance!!!!!   ");
        return DataOfATM.balance;
    }

    public void copyAppDbToDownloadFolder() throws IOException {

        File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "expense.db"); // for example "my_data_backup.db"
        //File currentDB = context.getDatabasePath("expense.db");
        File currentDB = new File ("/data/user/0/com.example.gauravlad.expense_daily/databases/expense.db");

        Log.d("d", (currentDB.exists()) + "<--currentDB.exists()");

        if (currentDB.exists()) {
            Log.d("d", "copyAppDbToDownloadFolder--->>if condition<<<---");
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();

            Log.d("d", "FileChannel!!!!copyAppDbToDownloadFolder");

            dst.transferFrom(src, 0, src.size());
            Log.d("s", "transformcopyAppDbToDownloadFolder");
            src.close();
            dst.close();
        }
    }

    public void copyAppDbFromFolder() throws IOException {
        File currentDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "expense.db"); // for example "my_data_backup.db"
        File backupDB = new File ("/data/user/0/com.example.gauravlad.expense_daily/databases/expense.db");

        Log.d("d", "copyAppDbFromFolder-->>"+currentDB.exists());
        Log.d("d", currentDB.toString()+" <-documents<<-- " + currentDB.exists() + " -->>expense.db-> "+backupDB.toString());

        if (currentDB.exists()) {
            Log.d("d", "copyAppDbFromFolder--->>if condition<<<---");
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();

            Log.d("d", currentDB.toString()+" <-documents<<---->>expense.db-> "+backupDB.toString());
            Log.d("d", "FileChannel!!!!copyAppDbFromFolder");

            dst.transferFrom(src, 0, src.size());

            Log.d("d", "transform...copyAppDbFromFolder");
            src.close();
            dst.close();
        }
    }


    public String changeDateOrder(String s){
        String newString[]  = s.split("/");
        return newString[2]+"/"+newString[1]+"/"+newString[0];
    }

}