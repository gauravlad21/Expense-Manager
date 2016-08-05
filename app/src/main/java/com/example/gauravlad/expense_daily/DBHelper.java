package com.example.gauravlad.expense_daily;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VIRSON = 1;
    private static final String DATABASE_NAME = "expense.db";
    private static final String TABLE_EXPENSE = "expense";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATE = "_date";
    private static final String COLUMN_COMMENT = "_comment";
    private static final String COLUMN_EXP = "_exp";//manual input

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VIRSON);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String s = " CREATE TABLE " + TABLE_EXPENSE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE + " DATE, " + COLUMN_COMMENT + " TEXT, " +  COLUMN_EXP + " INTEGER " + ");";
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        onCreate(db);
    }

    //Add new Row to database
    public void addMoney(Expense product){

     if(!isExist(String.valueOf(product.getDate()))) {
         SQLiteDatabase db = getWritableDatabase();
         ContentValues values = new ContentValues();
         values.put(COLUMN_DATE, String.valueOf(product.getDate()));
         values.put(COLUMN_EXP, (product.getMoney()));
         values.put(COLUMN_COMMENT, product.getComment());
         db.insert(TABLE_EXPENSE, null, values);

         sortByDate();

         db.close();
     }else{
         Log.d("d", "Already Exist!!!");
         Log.d("d", String.valueOf(product.getMoney())+" new");
         SQLiteDatabase db =  getWritableDatabase();

         String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + "='" + product.getDate() + "'";

         Cursor c = db.rawQuery(query, null);
         c.moveToFirst();
         Log.d("d", c.getString(3)+" old");

         String s = "UPDATE " + TABLE_EXPENSE + " SET " + COLUMN_EXP + "='" + (product.getMoney() + Integer.parseInt(c.getString(3))) + "' WHERE " + COLUMN_DATE + "='" + product.getDate() + "';";
         c = db.rawQuery(s, null);
         c.moveToFirst();//It is important

     }
    }

    public void MinusMoney(Expense product){

        if(isExist(String.valueOf(product.getDate()))) {

            Log.d("d", "Already Exist!!!");
            Log.d("d", String.valueOf(product.getMoney())+" new");
            SQLiteDatabase db =  getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + "='" + product.getDate() + "'";

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            int ans = (product.getMoney() - Integer.parseInt(c.getString(3))) *(-1);  //Question!!!!!!!!!!!!!!!!!!!!!
            if(ans >=0) {
                String s = "UPDATE " + TABLE_EXPENSE + " SET " + COLUMN_EXP + "='" + ans + "' WHERE " + COLUMN_DATE + "='" + product.getDate() + "';";
                c = db.rawQuery(s, null);
                c.moveToFirst();//It is important
            }else
                Log.d("d", "Couldn't be done because substraction is more!!!");
        }
    }

    public void deleteMoney(String input){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(" DELETE FROM " +  TABLE_EXPENSE + " WHERE " + COLUMN_DATE + "='" + input + "'");
        Log.d("d", "DELETED FROM SQLite DATABASE!!!");
    }

    public String databsetoString(){

        Log.d("d", "DatabaseToString!!!");

        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ TABLE_EXPENSE + " ORDER BY " + COLUMN_DATE;

        //CURSOR POINT TO A LOCATION IN YOUR RESULTS
        Cursor c = db.rawQuery(query, null);
        //Move to first row in your result
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_EXP)) != null && c.getString(c.getColumnIndex(COLUMN_COMMENT)) != null && c.getString(c.getColumnIndex(COLUMN_DATE)) != null) {
                dbString += c.getString(c.getColumnIndex(COLUMN_EXP)) +"..."+ c.getString(c.getColumnIndex(COLUMN_COMMENT)) +"..."+  c.getString(c.getColumnIndex(COLUMN_DATE));
                dbString += "\n";
            }
            c.moveToNext();
        }
        Log.d("d", "...\n"+dbString+"...");
        c.close();
        return dbString;
    }

    // if record is exist then it will return true otherwise this method returns false
    public boolean isExist(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + " = '" + date + "'", null);
        boolean exist = (cur.getCount() > 0);
        cur.close();
        db.close();
        return exist;
    }

    void showDetail(String date, TextView textView){
        Log.d("d", date+" received!!!");

        if(isExist(date)) {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + " = '" + date + "'";
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            textView.setText(" Your amount on " + date + " is: " + c.getString(c.getColumnIndex(COLUMN_EXP)) + " \nComment:  " + c.getString(c.getColumnIndex(COLUMN_COMMENT)));
        }else{
            Log.d("d", "No data found!!!");
        }

    }

    String dataFromMonth(String dataMonth){

        int sum = 0;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ TABLE_EXPENSE + " WHERE 1=1 ";

        //CURSOR POINT TO A LOCATION IN YOUR RESULTS
        Cursor c = db.rawQuery(query, null);
        //Move to first row in your result
        c.moveToFirst();

        while(!c.isAfterLast()){

            if (c.getString(c.getColumnIndex(COLUMN_DATE)).contains(dataMonth) ){
                Log.d("d", c.getString(c.getColumnIndex(COLUMN_EXP)) + "<-- This is expense!!!" );
                sum += Integer.parseInt( c.getString(c.getColumnIndex(COLUMN_EXP)) ) ;
            }

            c.moveToNext();
        }
        Log.d("d", "Month-->"+sum+"<--");
        c.close();
        return ""+sum;
        //textView.setText(sum);
        // WHY IT IS NOT HAPPENING?????
    }

    /*----------> No Use at ALL!!!!
    void sortByDate(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "Error In Query";
        Cursor c = null;
        try {
            //query = "SELECT * FROM " + TABLE_EXPENSE + " ORDER BY " + COLUMN_DATE ;
            c = db.query(TABLE_EXPENSE, null, null, null, null, null, COLUMN_DATE+" DESC");
            Log.d("d", "sortByDate!!!");
        }catch(Exception e){
            e.printStackTrace();
        }
        //SELECT * FROM Table ORDER BY date(dateColumn)

        //Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Log.d("d", "After Query sorted");
        databsetoString();
    }
*/
    String showDataBetweenTwoDates(String from, String to){

        String returnString = "";

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + " = '" + from + " '";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_EXP)) != null && c.getString(c.getColumnIndex(COLUMN_COMMENT)) != null && c.getString(c.getColumnIndex(COLUMN_DATE)) != null) {

                Log.d("d", "Enter in showDataBetweenTwoDates if condition.......!!!!");

                returnString += c.getString(c.getColumnIndex(COLUMN_EXP)) + c.getString(c.getColumnIndex(COLUMN_COMMENT)) + c.getString(c.getColumnIndex(COLUMN_DATE));
                returnString += "\n";
            }
            c.moveToNext();
        }

        return returnString+"gauravlad";
    }

}
