package com.example.gauth.android_newsapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gauth on 6/18/2018.
 */

public class DownloadTask  extends AsyncTask<String,Void,String> {
    IData iData;
    public DownloadTask(IData iData)
    {
        this.iData=iData;
    }
    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        URL url;//This a special type of string which should be in the specific URL format and type URL
        HttpURLConnection urlConnection = null; //This is bit like a browser
        try {
            url = new URL(urls[0]); //The try catch is here because there can be an exception if url is not correct
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream(); //this is for holding the incomming data
            InputStreamReader reader = new InputStreamReader(in); //this is for reading data
            int data = reader.read(); //this keeps track of current data we are currently on
            while (data != -1)  //the dat will be read and once done it becomes -1
            {

                char current = (char) data; //this will convert the value data is pointing to a charectar
                result += current;
                data = reader.read();
            }
            //Log.i("Complete Data",result);
            return result; //result back to task to be printed out
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }

    @Override
    protected void onPostExecute(String result) {

        iData.handleData(result);
    }
public static interface IData
{
    public void handleData(String data);
}
}
