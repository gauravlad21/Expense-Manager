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

    File direct;
    private static final int DATABASE_VIRSON = 1;
    private static final String DATABASE_NAME = "expense.db";
    private static final String TABLE_EXPENSE = "expense";
    private static final String TABLE_INCOME = "income";
    private static final String COLUMN_ATM = "_atm";
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
                + COLUMN_DATE + " DATETIME, " + COLUMN_COMMENT + " TEXT, " +  COLUMN_EXP + " INTEGER " + ");";
        String newstring = "CREATE TABLE " + TABLE_INCOME + " ( " + COLUMN_DATE + " Date, " +COLUMN_ATM + " INTEGER );";
        db.execSQL(s);
        db.execSQL(newstring);

        Log.d("d", "DatabaseOncreateDirectory!!!");
/*
        direct = new File(Environment.getExternalStorageDirectory() + "/BackUpFolder Creator");

       // Environment.isExternalStorageEmulated(direct);
        Log.d("d", "DatabaseOncreateDirectory!!!");

        if(!direct.exists())
        {
            direct.mkdir();
            Log.d("d", "Directory has been created!!!");
        }
        importDB();
        //exportDB();*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_INCOME);
        onCreate(db);
    }

    //Add new Row to database
    public void addMoney(Expense product){
        if(!isExistDate(String.valueOf(product.getDate()), product.getComment(), TABLE_EXPENSE ) ) {
             SQLiteDatabase db = getWritableDatabase();
             ContentValues values = new ContentValues();
             values.put(COLUMN_DATE, String.valueOf(product.getDate()));
             values.put(COLUMN_EXP, (product.getMoney()));
             values.put(COLUMN_COMMENT, product.getComment());
             db.insert(TABLE_EXPENSE, null, values);

          
     }else{
        Log.d("d", String.valueOf(product.getMoney())+" new");
         SQLiteDatabase db =  getWritableDatabase();

         String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + " ='" + product.getDate() + "' AND " + COLUMN_COMMENT + " = '" + product.getComment() + "';";

         Cursor c = db.rawQuery(query, null);
         c.moveToFirst();

         String s = "UPDATE " + TABLE_EXPENSE + " SET " + COLUMN_EXP + " = '" + (product.getMoney() + Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_EXP)))) + "' WHERE " + COLUMN_DATE + " ='" + product.getDate() + "' AND " + COLUMN_COMMENT + " = '" + product.getComment() + "';";
         c = db.rawQuery(s, null);
         c.moveToFirst();//It is important

     }

    }

    public void MinusMoney(Expense product){

        if(isExistDate(String.valueOf(product.getDate()), product.getComment() , TABLE_EXPENSE )  ) {

          Log.d("d", "Already Exist!!!");
           Log.d("d", String.valueOf(product.getMoney())+" new");
            SQLiteDatabase db =  getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + "='" + product.getDate() + "' AND " + COLUMN_COMMENT + "='" + product.getComment() + "';";

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            int ans = (product.getMoney() - Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_EXP)))) *(-1);  //Question!!!!!!!!!!!!!!!!!!!!!
            Log.d("d", ans+"<--This is anwer!!!");
            if(ans >=0) {
                String s = "UPDATE " + TABLE_EXPENSE + " SET " + COLUMN_EXP + "='" + ans + "' WHERE " + COLUMN_DATE + "='" + product.getDate() + "' AND " + COLUMN_COMMENT + "='" + product.getComment() + "';";
                c = db.rawQuery(s, null);
                c.moveToFirst();//It is important
            }else {
               Log.d("d", "Couldn't be done because substraction is more!!!");
                //do nothing
            }
        }
    }

    public void deleteMoney(String input , String inputcomment){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(" DELETE FROM " +  TABLE_EXPENSE + " WHERE " + COLUMN_DATE + "='" + input + "' AND " + COLUMN_COMMENT + "='" + inputcomment + "'");
        Log.d("d", "DELETED FROM SQLite DATABASE!!!");
    }

    public String databsetoString(int x){

        Log.d("d", "DatabaseToString!!!");

        int ans=0;
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ TABLE_EXPENSE + " ORDER BY " + COLUMN_DATE + " DESC";

        //CURSOR POINT TO A LOCATION IN YOUR RESULTS
        Cursor c = db.rawQuery(query, null);
        //Move to first row in your result
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_EXP)) != null && c.getString(c.getColumnIndex(COLUMN_COMMENT)) != null && c.getString(c.getColumnIndex(COLUMN_DATE)) != null) {
                dbString += c.getString(c.getColumnIndex(COLUMN_EXP)) +"..."+ c.getString(c.getColumnIndex(COLUMN_COMMENT)) +"..."+  c.getString(c.getColumnIndex(COLUMN_DATE));
                dbString += "\n";
                ans += Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_EXP)));
            }
            c.moveToNext();
        }
        Log.d("d", "...\n"+dbString+"...");
        c.close();

        return (x==1)?dbString:""+ans;
    }
    // if record is exist then it will return true otherwise this method returns false
    public boolean isExistDate(String date, String comment , String Table) {
        Log.d("d", "Entered in isExist--->>>" + date + " " + comment + " ;");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + Table + " WHERE " + COLUMN_DATE + "='" + date + "' AND " + COLUMN_COMMENT + "='" + comment + "';", null);/////DO NOT PUT SPACE AFTER & BEFORE ='<TEXT>'----------------
        boolean exist = (cur.getCount() > 0);
        cur.close();

        Log.d("d", exist+"");
        return exist;
    }

    public boolean isExistATM(String date,  String Table) {
        Log.d("d", "Entered in isExist--->>>" + date + " ;");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + Table + " WHERE " + COLUMN_DATE + "='" + date + "'" , null);/////DO NOT PUT SPACE AFTER & BEFORE ='<TEXT>'----------------
        boolean exist = (cur.getCount() > 0);
        cur.close();

        Log.d("d", exist+"");
        return exist;
    }


    String showDetail(String date){
        ////Log.d("d", date+" received!!!");

        String s = "";
        int ans=0;
        if(isExistATM(date  ,TABLE_EXPENSE) ) {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + "='" + date + "';";
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while(!c.isAfterLast()){
                Log.d("d", c.getString(c.getColumnIndex(COLUMN_EXP))+"<--Exp && comment-->"+c.getString(c.getColumnIndex(COLUMN_COMMENT)));
               // if(c.getString(c.getColumnIndex(COLUMN_EXP)) != null && c.getString(c.getColumnIndex(COLUMN_COMMENT)) != null ) {
                    s += c.getString(c.getColumnIndex(COLUMN_EXP)) +"..."+ c.getString(c.getColumnIndex(COLUMN_COMMENT));
                    s += "\n";
                    ans += Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_EXP)));
                //}
                c.moveToNext();
            }
        c.moveToFirst();
           return " Your amount on " + date + " is: " + ans + "\n Your detaile is: \n  " + s;
        }else{
            //Log.d("d", "No data found!!!");
        }
        return "No data found!!!";
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
                //Log.d("d", c.getString(c.getColumnIndex(COLUMN_EXP)) + "<-- This is expense!!!" );
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

    int showDataBetweenTwoDates(String from, String to){

        int returnString = 0;

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSE + " WHERE " + COLUMN_DATE + " between '" + from + "' AND '" + to + "'";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_EXP)) != null && c.getString(c.getColumnIndex(COLUMN_COMMENT)) != null && c.getString(c.getColumnIndex(COLUMN_DATE)) != null) {

                //Log.d("d", "Enter in showDataBetweenTwoDates if condition.......!!!!");

                returnString += Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_EXP)));
            }
            c.moveToNext();
        }

        //Log.d("d", returnString+"Returned!!!");
        return returnString;
    }

    public void addToATM(ATM product) {
        SQLiteDatabase db = getWritableDatabase();

        if(!isExistATM(String.valueOf(product.getDate()), TABLE_INCOME) ){

            ContentValues values = new ContentValues();
            values.put(COLUMN_DATE, String.valueOf(product.getDate()));
            values.put(COLUMN_ATM, String.valueOf(product.getMoney()));

           // //Log.e("d", "insert!!!!");
            db.insert(TABLE_INCOME, null, values);

    }
        else {
            Cursor c=null;
           // //Log.d("d", "Already Exist!!!");
            ////Log.d("d", String.valueOf(product.getMoney()) + " new");

            String s = "UPDATE " + TABLE_INCOME + " SET " + COLUMN_ATM + "='" + product.getMoney()  + "' WHERE " + COLUMN_DATE + "='" + product.getDate() + "';";
            c = db.rawQuery(s, null);
            c.moveToFirst();//It is important
            //Log.d("d", "UPDATED!!!");
        }
    }

    public String shoToATM( int x){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        String returnString = "...\n";
        int totalSum=0;

            //Log.d("d", "isExist-->");
            String query = " SELECT * FROM " + TABLE_INCOME + " WHERE 1=1 ";

                c = db.rawQuery(query, null);
                c.moveToFirst();

            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_ATM)) != null && c.getString(c.getColumnIndex(COLUMN_DATE)) != null) {
                    returnString += c.getString(c.getColumnIndex(COLUMN_ATM)) + "<-->" + c.getString(c.getColumnIndex(COLUMN_DATE)) + "\n";
                    totalSum += Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ATM)));
                }
                c.moveToNext();
            }

       // //Log.d("d", returnString+"<-- This should be displayed!!!");

        if(x==1) return returnString;
        else return ""+totalSum;
    }

    //For Date
    String doubleDigit(String x){
        return (x.length() == 1)?"0"+x:x;
    }

    public int MyBalance(){
        DataOfATM.balance = Integer.parseInt(shoToATM(0)) - Integer.parseInt(databsetoString(0))  ;
        return  DataOfATM.balance;
    }

/*
    //importing database
     void importDB() {
        // TODO Auto-generated method stub

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data  = Environment.getDataDirectory();

            Log.d("d", "Entered in import try part!!!!");

            if (true) {
                String  currentDBPath= "\\data\\" + "com.example.gauravlad.expense_daily"
                        + "\\databases\\" + "expense.db";
                String backupDBPath  = Environment.DIRECTORY_DOCUMENTS+"BackUpFolder.db";
                File  backupDB= new File(data, currentDBPath);
                File currentDB  = new File(sd, backupDBPath);

                Log.d("d", "if-->true-->file has been made!!!");

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Log.e("d", "Imported!!!!");
            }
        } catch (Exception e) {Log.e("d", "Exception!!!Import");}
    }

*/
    public void copyAppDbToDownloadFolder(Context context) throws IOException {
        Log.d("d", "copyAppDbToDownloadFolder");
        File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BackUpFolder.db"); // for example "my_data_backup.db"
        File currentDB = context.getDatabasePath("expense.db"); //databaseName=your current application database name, for example "my_data.db"
        if (currentDB.exists()) {
            Log.d("d", "copyAppDbToDownloadFolder--->>if condition<<<---");
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();

            Log.d("d", "FileChannel!!!!");

            dst.transferFrom(src, 0, src.size());
            Log.d("s", "transform");
            src.close();
            dst.close();
        }
    }

    public void copyAppDbFromFolder(Context context) throws IOException {
        File currentDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BackUpFolder.db"); // for example "my_data_backup.db"
        File backupDB = context.getDatabasePath("expense.db"); //databaseName=your current application database name, for example "my_data.db"

        Log.d("d", "copyAppDbFromFolder-->>"+currentDB.exists());

        if (currentDB.exists()) {
            Log.d("d", "copyAppDbFromFolder--->>if condition<<<---");
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();

            Log.d("d", "FileChannel!!!!copyAppDbFromFolder");

            dst.transferFrom(src, 0, src.size());

            Log.d("d", "transform...copyAppDbFromFolder");
            src.close();
            dst.close();
        }
    }
}

