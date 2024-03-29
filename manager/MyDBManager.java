package com.practice.myapplication.manager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.practice.myapplication.data.ClubData;
import com.practice.myapplication.data.GroundData;
import com.practice.myapplication.data.GroundMapData;
import com.practice.myapplication.data.ScoreData;
import com.practice.myapplication.data.StoreData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hagtfms on 2016-04-18.
 */
public class MyDBManager {
    private static MyDbOpenHelper mDBOpenHelper = null;

    public static boolean initSetting(@NonNull Activity context) {
        mDBOpenHelper = new MyDbOpenHelper(context);
        return (mDBOpenHelper != null)? true : false;
    }

    public static ArrayList<ClubData> getClubList(){
        ArrayList<ClubData> clubList = null;
        if(mDBOpenHelper != null){
            SQLiteDatabase db = mDBOpenHelper.getReadableDatabase();
            Cursor c = db.rawQuery("select * from "+ClubData.TABLE_NAME+
                    " order by "+ClubData.COLUMN_NAME_METER + " DESC", null);

            clubList = new ArrayList<ClubData>();
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
        }
        return clubList;
    }
    public static ArrayList<StoreData> getStoreList(){
        String json = MyInternetManager.getStringFromURL("/php/getStore.php");

        ArrayList<StoreData> storeList = new ArrayList<StoreData>();
        try{
            JSONObject root = new JSONObject(json);
            JSONArray jsonArray = root.getJSONArray("results");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                storeList.add(new StoreData(
                        jsonObject.getInt("id"),
                        jsonObject.getString("name")));
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        return storeList;
    }
    /*
    public static String getTactic(final int groundId){
        return MyInternetManager.getStringFromURL("/php/getTactic.php?id="+groundId);
    }*/
    public static ScoreData getScoreData(final int gameid){
        String json = MyInternetManager.getStringFromURL("/php/getScore.php?id="+gameid);

        ScoreData scoreData = null;
        try{
            JSONObject root = new JSONObject(json);
            JSONArray jsonArray = root.getJSONArray("results");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                scoreData = new ScoreData(
                        jsonObject.getInt("courseid"),
                        jsonObject.getInt("s1"),
                        jsonObject.getInt("s2"),
                        jsonObject.getInt("s3"),
                        jsonObject.getInt("s4"),
                        jsonObject.getInt("s5"),
                        jsonObject.getInt("s6"),
                        jsonObject.getInt("s7"),
                        jsonObject.getInt("s8"),
                        jsonObject.getInt("s9"),
                        jsonObject.getInt("s10"),
                        jsonObject.getInt("s11"),
                        jsonObject.getInt("s12"),
                        jsonObject.getInt("s13"),
                        jsonObject.getInt("s14"),
                        jsonObject.getInt("s15"),
                        jsonObject.getInt("s16"),
                        jsonObject.getInt("s17"),
                        jsonObject.getInt("s18")
                );
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        return scoreData;
    }
    public static GroundData getGroundData(final int storeid, final int groundid){
        String json = MyInternetManager.getStringFromURL("/php/getGround.php?" +
                "sid="+storeid+"&" +
                "gid="+groundid);

        GroundData groundData = null;
        try{
            JSONObject root = new JSONObject(json);
            JSONArray jsonArray = root.getJSONArray("results");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                groundData = new GroundData(
                        jsonObject.getInt("regFigure"),
                        jsonObject.getString("tactic"),
                        jsonObject.getString("imageUrl"),
                        jsonObject.getInt("imageVer")
                );
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        return groundData;
    }
    public static GroundMapData getGroundMapData(final int storeid, final int groundid){
        GroundMapData groundMapData = null;
        if(mDBOpenHelper != null){
            SQLiteDatabase db = mDBOpenHelper.getReadableDatabase();
            Cursor c = db.rawQuery("select * from "+ GroundMapData.TABLE_NAME +" where "+
                    GroundMapData.COLUMN_NAME_STOREID+"="+storeid+" and "+
                    GroundMapData.COLUMN_NAME_GROUNDID+"="+groundid, null);

            if(c.getCount() != 0){
                c.moveToFirst();

                byte[] bytes = c.getBlob(c.getColumnIndexOrThrow(GroundMapData.COLUMN_NAME_MAPSTREAM));
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                groundMapData = new GroundMapData(
                        c.getInt(c.getColumnIndexOrThrow(GroundMapData.COLUMN_NAME_STOREID)),
                        c.getInt(c.getColumnIndexOrThrow(GroundMapData.COLUMN_NAME_GROUNDID)),
                        bitmap);
            }

            c.close();
            db.close();
        }
        return groundMapData;
    }

    public static boolean insertGroundMap(@NonNull Bitmap bitmap, final int storeid, final int groundid){
        if(mDBOpenHelper != null && bitmap != null){
            SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            ContentValues values = new ContentValues();
            values.put(GroundMapData.COLUMN_NAME_STOREID, storeid);
            values.put(GroundMapData.COLUMN_NAME_GROUNDID, groundid);
            values.put(GroundMapData.COLUMN_NAME_MAPSTREAM, stream.toByteArray());

            db.insert(GroundMapData.TABLE_NAME, null, values);
            return true;
        }
        return false;
    }

    public static boolean updateClubList(Context context, ClubData clubData){
        if(mDBOpenHelper != null){
            SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
            db.execSQL("update "+ ClubData.TABLE_NAME +
                    " set "+ ClubData.COLUMN_NAME_METER + "=" + clubData.getMeter() +
                    " where "+ ClubData.COLUMN_NAME_NAME + "='" + clubData.getName() + "'");
            return true;
        }
        else
            return false;
    }

    public static boolean setScore(final int i){
        MyInternetManager.getStringFromURL("/php/setScore.php?score="+i);
        return true;
    }

    @Override
    protected void finalize() throws Throwable{
        super.finalize();
    }

    private static class MyDbOpenHelper extends SQLiteOpenHelper{
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "Golf.db";

        public MyDbOpenHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db){
            db.execSQL("CREATE TABLE "+ClubData.TABLE_NAME+"(" +
                    "_id integer auto_increment," +
                    ClubData.COLUMN_NAME_NAME + " varchar(20) not null," +
                    ClubData.COLUMN_NAME_METER + " integer," +
                    "primary key(_id)" +
                    ")");
            db.execSQL("insert into "+ClubData.TABLE_NAME+"("+ClubData.COLUMN_NAME_NAME+", "+ClubData.COLUMN_NAME_METER+") values" +
                    "('Driver', 200)," +
                    "('3 Wood', 180)," +
                    "('4 Iron', 170)," +
                    "('5 Iron', 160)," +
                    "('6 Iron', 150)," +
                    "('7 Iron', 140)," +
                    "('8 Iron', 130)," +
                    "('9 Iron', 120)," +
                    "('PW', 100)," +
                    "('SW', 70)," +
                    "('PT', 20)");

            db.execSQL("CREATE TABLE "+GroundMapData.TABLE_NAME+" (" +
                    GroundMapData.COLUMN_NAME_STOREID + " integer not null, " +
                    GroundMapData.COLUMN_NAME_GROUNDID + " integer not null, " +
                    GroundMapData.COLUMN_NAME_MAPSTREAM + " blob not null, " +
                    "primary key("+GroundMapData.COLUMN_NAME_STOREID+","+GroundMapData.COLUMN_NAME_GROUNDID+"))");
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS tbl_club");
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}