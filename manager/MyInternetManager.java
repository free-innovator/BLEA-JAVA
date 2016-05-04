package com.practice.myapplication.manager;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
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

    public static String getStringFromURL(String url){
        PhpDownloadThread phpDownloadThread = new PhpDownloadThread(HTTP_ADDRESS + url);
        phpDownloadThread.start();
        try{
            phpDownloadThread.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        return phpDownloadThread.getResult();
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
