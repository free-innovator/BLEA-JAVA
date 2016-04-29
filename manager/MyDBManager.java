package com.practice.myapplication.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.practice.myapplication.data.ClubData;

import java.util.ArrayList;

/**
 * Created by hagtfms on 2016-04-18.
 */
public class MyDBManager {
    private static ClubDbHelper mClubDbHelper = null;

    public static ArrayList<ClubData> getClubList(Context context){
        if(mClubDbHelper == null){
            mClubDbHelper = new ClubDbHelper(context);
        }

        SQLiteDatabase db = mClubDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from "+ClubData.TABLE_NAME, null);

        ArrayList<ClubData> clubList = new ArrayList<ClubData>();
        if(c.getCount() != 0){
            c.moveToFirst();
            do{
                clubList.add(new ClubData(
                        c.getString(c.getColumnIndexOrThrow(ClubData.COLUMN_NAME_NAME)),
                        c.getInt(c.getColumnIndexOrThrow(ClubData.COLUMN_NAME_METER))));
            }while(c.moveToNext());
        }

        c.close();
        db.close();
        return clubList;
    }
    public static void updateClubList(Context context, ClubData clubData){
        if(mClubDbHelper == null){
            mClubDbHelper = new ClubDbHelper(context);
        }

        SQLiteDatabase db = mClubDbHelper.getWritableDatabase();
        db.execSQL("update "+ ClubData.TABLE_NAME +
                " set "+ ClubData.COLUMN_NAME_METER + "=" + clubData.getMeter() +
                " where "+ ClubData.COLUMN_NAME_NAME + "='" + clubData.getName() + "'");
    }

    @Override
    protected void finalize() throws Throwable{
        if(mClubDbHelper != null){
            mClubDbHelper.close();
            mClubDbHelper = null;
        }
        super.finalize();
    }
}

class ClubDbHelper extends SQLiteOpenHelper{
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE "+ClubData.TABLE_NAME+"(" +
                    "_id integer auto_increment," +
                    ClubData.COLUMN_NAME_NAME + " varchar(20) not null," +
                    ClubData.COLUMN_NAME_METER + " integer," +
                    "primary key(_id)" +
                    ")";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS tbl_club";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Club.db";

    public ClubDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL("insert into tbl_club("+ClubData.COLUMN_NAME_NAME+", "+ClubData.COLUMN_NAME_METER+") values" +
                "('1st', null)," +
                "('2st', null)," +
                "('3st', null)," +
                "('4st', null)," +
                "('5st', null)");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}