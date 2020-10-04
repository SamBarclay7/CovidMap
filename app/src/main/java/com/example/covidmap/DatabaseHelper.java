package com.example.covidmap;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 15;
    private static final String DATABASE_NAME = "cases";
    private static final String TABLE_NAME = "casesPostcode";
    private static final String KEY_POSTCODE = "postcode";
    private static final String KEY_POPULATION = "population";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_CASES = "cases";
    private static final String KEY_RATE = "rate";
    private static final String KEY_NEW = "new";
    private static final String LOCATION_TABLE_NAME = "location";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LOCATION_POSTCODE = "postcode";

    private int locationSize = 0;

    //https://www.corra.com.au/australian-postcode-location-data/

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        Log.d("TEST", "made to onCreate");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        String CREATE_CASES_TABLE = "CREATE TABLE " + TABLE_NAME +  "(" +
                KEY_POSTCODE + " INTEGER PRIMARY KEY," +
                KEY_POPULATION + " INTEGER," +
                KEY_ACTIVE + " INTEGER,"+
                KEY_CASES + " INTEGER," +
                KEY_RATE + " REAL," +
                KEY_NEW + " INTEGER" + ")";
        db.execSQL(CREATE_CASES_TABLE);
//        Log.d("TEST", "made to onCreate2");
        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);
        String CREATE_LOCATION_TABLE = "CREATE TABLE " + LOCATION_TABLE_NAME + "(" +
                KEY_LOCATION_POSTCODE + " INTEGER PRIMARY KEY," +
                KEY_LATITUDE + " REAL," +
                KEY_LONGITUDE + " REAL" + ")";
//        Log.d("TEST", "made to onCreate3");
        db.execSQL(CREATE_LOCATION_TABLE);
//        Log.d("TEST", "made to onCreate4");
        try {
            insertLocationData(db);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertLocationData(SQLiteDatabase db) throws IOException
    {
//        Log.d("TEST", "made it to insertLocationData");
//        SQLiteDatabase db = this.getWritableDatabase();
        //Drop the table if we have new data

//        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);
//        onCreate(db);

        File file = new File("app.src.main.assets.postcode_locations.csv");
        AssetManager am = App.getmContext().getAssets();


        Scanner sc = new Scanner(am.open("postcode_locations.csv"));
//        Log.d("TEST", "made it to insertLocationData22");



        db.beginTransaction();
        try
        {
            String last = "";
            while(sc.hasNextLine())
            {
//                Log.d("TEST", "made it to insertLocationData2");
                ContentValues values = new ContentValues();
                String[] split = sc.nextLine().split(",");
                if(!last.equals(split[0])) {

                    locationSize ++;

                    last = split[0];

//                    Log.d("TEST2", split[0]);

                    values.put(KEY_LOCATION_POSTCODE, split[0]);
                    values.put(KEY_LATITUDE, split[1]);
                    values.put(KEY_LONGITUDE, split[2]);
                    if(Integer.parseInt(split[0]) == 3000 || Integer.parseInt(split[0]) == 3001)
                    {
                        Log.d("TEST23", "\n\nlat: " + split[1] + "\nlng: " + split[2] + "yeet");
                    }

                    db.insert(LOCATION_TABLE_NAME, null, values);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }






    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);

        onCreate(db);
    }

    public void addBulk(String data) {
        //Send all the data to this and it'll add it to the database. Discards the first line and then
        //Adds the rest to one long transaction
        SQLiteDatabase db = this.getWritableDatabase();
//        Log.d("TEST", "made it to add bulk");

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

    public PCLocation getPCLocation(int id)
    {
        //make cursor
        //loop through one by one

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(LOCATION_TABLE_NAME,
                new String[]{KEY_LOCATION_POSTCODE,KEY_LATITUDE,KEY_LONGITUDE},
                KEY_LOCATION_POSTCODE + " = ? ",
                new String[]{String.valueOf(id)},null,null,null,null);
        if(cursor != null) {
            cursor.moveToFirst();
        }

        PCLocation PCL = new PCLocation(cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2));
        cursor.close();

        return PCL;
    }




    // code to get all contacts in a list view
    public List<Postcode> getAllPostcodes() {
        return getAllPostcodes(Order.POSTCODE);
    }

    public List<Postcode> getAllPostcodes(Order order) {
        return getAllPostcodes(order, false);
    }

    public List<Postcode> getAllPostcodes(Order order, boolean descending) {
        List<Postcode> postcodeList = new ArrayList<Postcode>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{KEY_POSTCODE, KEY_POPULATION, KEY_ACTIVE, KEY_CASES, KEY_RATE, KEY_NEW},
                null, null, null, null,
                String.valueOf(order).toLowerCase() + (descending? " DESC":""), null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Postcode contact = new Postcode();
                contact.setPostcode((cursor.getInt(0)));
                contact.setPopulation((cursor.getInt(1)));
                contact.setActive((cursor.getInt(2)));
                contact.setCases((cursor.getInt(3)));
                contact.setRate(cursor.getDouble(4));
                contact.setNewCases((cursor.getInt(5)));

                // Adding contact to list
               postcodeList.add(contact);

               Log.d("Working message", "THis is working");
            } while (cursor.moveToNext());
        }

        // return contact list
        return postcodeList;
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



    public PCLocation getRow2(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("TEST", "getRow2");
        Cursor cursor = db.query(LOCATION_TABLE_NAME,
                new String[]{KEY_LOCATION_POSTCODE,KEY_LATITUDE,KEY_LONGITUDE},
                KEY_LOCATION_POSTCODE + "=?",
                new String[]{String.valueOf(id)},null,null,null,null);
        if(cursor != null) {
            cursor.moveToFirst();
        }

        assert cursor != null;


        PCLocation PCL = new PCLocation(cursor.getInt(0),
                cursor.getDouble(1),
                cursor.getDouble(2));

        cursor.close();

        return PCL;
    }


    public int getLocationSize()
    {
        return locationSize;
    }

    public List<PCLocation> getAllPostcodeLocations() {
        List<PCLocation> postcodeList = new ArrayList<PCLocation>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + LOCATION_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PCLocation contact = new PCLocation();

                contact.setPc((cursor.getInt(0)));
                contact.setLat(cursor.getDouble(1));
                contact.setLng(cursor.getDouble(2));
                if(cursor.getInt(0) == 3000 || cursor.getInt(0) == 3001)
                {
                    Log.d("TEST23", "\n\nlat: " + cursor.getDouble(1) + "\nlng: " + cursor.getDouble(2) + "yeetyeet");
                }



                // Adding contact to list
                postcodeList.add(contact);

                Log.d("Working message", "THis is working");
            } while (cursor.moveToNext());
        }

        // return contact list
        return postcodeList;
    }


    public boolean checkId(int id)
    {
//        SQLiteDatabase db = this.getReadableDatabase();
////        String select = "SELECT " + KEY_LOCATION_POSTCODE + " FROM " + LOCATION_TABLE_NAME;
//        for(int i = 0; i < locationSize; ++i)
//        {
//
//            if(PCLocation.getPc() == id)
//            {
//                //SEARCH DATABASE FOR ID TO CHECK IF IT EXISTS
//                //IF IT DOES EXIST RETURN TRUE
//
//            }
//        }
//
//        return false;



        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("TEST23", "checkId1");
        Cursor cursor = db.query(LOCATION_TABLE_NAME,
                new String[]{KEY_LOCATION_POSTCODE,KEY_LATITUDE,KEY_LONGITUDE},
                KEY_LOCATION_POSTCODE + "=?",
                new String[]{String.valueOf(id)},null,null,null,null);
        Log.d("TEST23", "checkId2");
        if(cursor != null) {
            cursor.moveToFirst();
        }

        assert cursor != null;


//       if(cursor.getInt(0) == null)
//       {
//
//       }

        cursor.close();
        return true;
    }
}
