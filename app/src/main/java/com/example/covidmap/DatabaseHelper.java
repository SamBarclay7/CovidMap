package com.example.covidmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Scanner;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "cases";
    private static final String TABLE_NAME = "casesPostcode";
    private static final String KEY_POSTCODE = "postcode";
    private static final String KEY_POPULATION = "population";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_CASES = "cases";
    private static final String KEY_RATE = "rate";
    private static final String KEY_NEW = "new";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CASES_TABLE = "CREATE TABLE " + TABLE_NAME +  "(" +
                KEY_POSTCODE + " INTEGER PRIMARY KEY," +
                KEY_POPULATION + " INTEGER," +
                KEY_ACTIVE + " INTEGER,"+
                KEY_CASES + " INTEGER," +
                KEY_RATE + " REAL," +
                KEY_NEW + " INTEGER" + ")";
        db.execSQL(CREATE_CASES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public void addBulk(String data) {
        //Send all the data to this and it'll add it to the database. Discards the first line and then
        //Adds the rest to one long transaction
        SQLiteDatabase db = this.getWritableDatabase();

        //Drop the table if we have new data
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

        Scanner sc = new Scanner(data);
        sc.nextLine();

        db.beginTransaction();
        try {
            while (sc.hasNextLine()) {
                ContentValues values = new ContentValues();
                String[] split = sc.nextLine().split(",");
                values.put(KEY_POSTCODE, split[0]);
                values.put(KEY_POPULATION, split[1]);
                values.put(KEY_ACTIVE, split[2]);
                values.put(KEY_CASES, split[3]);
                values.put(KEY_RATE, split[4]);
                values.put(KEY_NEW, split[5]);
                db.insert(TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public Postcode getRow(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{KEY_POSTCODE,KEY_POPULATION,KEY_ACTIVE, KEY_CASES,KEY_RATE,KEY_NEW},
                KEY_POSTCODE + "=?",
                new String[]{String.valueOf(id)},null,null,null,null);
        if(cursor != null) {
            cursor.moveToFirst();
        }

        assert cursor != null;
        Postcode postcode = new Postcode(cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5));
        cursor.close();

        return postcode;
    }
}
