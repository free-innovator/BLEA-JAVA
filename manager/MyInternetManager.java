package com.practice.myapplication.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hagtfms on 2016-05-05.
 */
public class MyInternetManager {
    private static final String SERVER_DOMAIN = "1.244.226.109";
    private static final String HTTP_ADDRESS = "http://"+SERVER_DOMAIN;

    /**
     * 반환된 문자열이 원하는 문자열인지 확인하여야 합니다.
     * @param url
     * @return
     */
    public static String getStringFromURL(String url){
        PhpStringDownloadThread phpDownloadThread = new PhpStringDownloadThread(HTTP_ADDRESS + url);
        phpDownloadThread.start();
        try{
            phpDownloadThread.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        return phpDownloadThread.getResult();
    }
    public static Bitmap getBitmapFromURL(String url){
        PhpBitmapDownloadThread phpDownloadThread = new PhpBitmapDownloadThread(HTTP_ADDRESS + url);
        phpDownloadThread.start();
        try{
            phpDownloadThread.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        return phpDownloadThread.getResult();
    }

    private static class PhpStringDownloadThread extends Thread{
        private String mResultString = null;
        private String mDownloadURLString = null;

        private String getResult(){ return mResultString; }

        private PhpStringDownloadThread(@NonNull String url){
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

    private static class PhpBitmapDownloadThread extends Thread{
        private Bitmap mResultBitmap = null;
        private String mDownloadURLString = null;

        private Bitmap getResult(){ return mResultBitmap; }

        private PhpBitmapDownloadThread(@NonNull String url){
            mDownloadURLString = url;
        }

        @Override
        public void run(){
            try{
                URL url = new URL(mDownloadURLString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                if(conn != null){
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    mResultBitmap = BitmapFactory.decodeStream(is);
                }
            }
            catch(MalformedURLException e){
                e.printStackTrace();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
