package com.example.gauth.android_newsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URL;

public class WebDisplay extends AppCompatActivity {
   WebView webView;
    String URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_display);

         webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        Intent intent=getIntent();
        if(-2==(intent.getIntExtra("commonTag", 0)))
        {Log.i("Web view","Online");
            URL = MainActivity.urlOnline.get(intent.getIntExtra("onlineNumber", 0));
        }
    if(-3==(intent.getIntExtra("commonTag", 0)))
    {
        Log.i("Web view","DataBase");
        URL = MainActivity.url.get(intent.getIntExtra("savedNumber", 0));
    }
        Log.i("SELECTED URL IS",URL);
        displayContent(URL);
    }
void displayContent(String URL)
{
    webView.loadUrl(URL);
}
}
