package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.DWebView;

public class MainActivity extends AppCompatActivity {

    private DWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        webView.addJavascriptObject(new JsApi(), null);
//        webView.loadUrl("http://10.43.101.59:8080");
        webView.loadUrl("file:////android_asset/index.html");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        changeTheme(0xFFFF0000);
        return true;
    }

    // 换肤
    private void changeTheme (int color) {
       // 状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(color);

        // 标题栏
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));

        // 导航栏
        getWindow().setNavigationBarColor(color);

        // Web 网页的背景
        webView.callHandler("changeTheme", new Object[]{color});
    }

    public class JsApi {
        @JavascriptInterface
        public void nativeRequest(Object params, CompletionHandler handler) {
            try {
                String url = ((JSONObject)params).getString("url");
                String data = request(url);
                handler.complete(data);
            } catch (Exception e) {
                handler.complete(e.getMessage());
                e.printStackTrace();
            }
        }

        private String request(String urlSpec) throws Exception {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlSpec).openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer result = new StringBuffer();
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            connection.disconnect();
            return result.toString();
        }
    }
}
