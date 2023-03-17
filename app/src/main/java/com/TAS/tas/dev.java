package com.TAS.tas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class dev extends AppCompatActivity {

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);
        webView = findViewById(R.id.devv);
        webView.loadUrl("https://anudeb2001.wixsite.com/my-site-4");
    }
}