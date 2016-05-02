package com.practice.myapplication.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.practice.myapplication.data.ClubData;
import com.practice.myapplication.data.StoreData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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
    private static final String SERVER_DOMAIN = "1.244.226.109";
    private static final String PHP_ADDRESS = "http://"+SERVER_DOMAIN+"/php/";
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
    public static ArrayList<StoreData> getStoreList(){
        String json = getString(PHP_ADDRESS + "getStore.php");

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
    public static String getTactic(final int groundId){
        return getString(PHP_ADDRESS + "getTactic.php?id="+groundId);
    }

    private static String getString(String url){
        PhpDownloadThread phpDownloadThread = new PhpDownloadThread(url);
        phpDownloadThread.start();
        try{
            phpDownloadThread.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        return phpDownloadThread.getResult();
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

    private static class ClubDbHelper extends SQLiteOpenHelper{
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

    private static class PhpDownloadThread extends Thread{
        private String mResultString = null;
        private String mDownloadURLString = null;

        private String getResult(){ return mResultString; }

        private PhpDownloadThread(@NonNull String url){
            mDownloadURLString = url;
        }

        @Override
        public void run(){
            StringBuilder jsonHtml = new StringBuilder();
            try{
                URL url = new URL(mDownloadURLString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while(true){
                            String line = bufferedReader.readLine();
                            if(line == null) break;

                            jsonHtml.append(line + '\n');
                        }
                        bufferedReader.close();
                    }
                    conn.disconnect();
                }
            }
            catch(MalformedURLException e){
                e.printStackTrace();
            }
            catch(IOException e){
                e.printStackTrace();
            }

            mResultString = jsonHtml.toString();
        }
    }
}