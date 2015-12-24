package com.sigrideducation.englishlearning.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sigrideducation.englishlearning.R;
import com.sigrideducation.englishlearning.database.ELDatabaseHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class HomeActivity extends AppCompatActivity {

    private static String url = "http://getcult.com/static/Data/data";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        new JSONParse().execute();
    }

    private class JSONParse extends AsyncTask<String, Void, String> {
        ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        @Override
        protected void onPreExecute() {

            dialog.setMessage("Please wait..");
            dialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... args) {
            String response;

            if(hasActiveInternetConnection(getApplicationContext()))
            {
                try {
                    sharedPreferences = getSharedPreferences("MY_PREF",MODE_PRIVATE);
                    if(sharedPreferences.getBoolean("IS_FIRST_LAUNCH",true))
                    {
                        sharedPreferences.edit().putBoolean("IS_FIRST_LAUNCH",false).apply();
                    }
                    else {
                        url=url+sharedPreferences.getInt("CURRENT_VERSION",1);
                    }
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(url);
                    HttpResponse httpResponse = httpclient.execute(httppost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    response = EntityUtils.toString(httpEntity);

                    JSONArray jsonArray = new JSONArray(response);

                    final int CURRENT_VERSION=jsonArray.getJSONObject(0).getInt("version");
                    Log.i("File to download","data"+CURRENT_VERSION);
                    sharedPreferences.edit().putInt("CURRENT_VERSION",CURRENT_VERSION).apply();

                    final JSONArray resultArray = jsonArray.getJSONObject(0).getJSONArray("chapters");
                    return resultArray.toString();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else {

            }

            return null;
        }


        @Override
        protected void onPostExecute(String json) {
            if(json !=null)
            {
                ELDatabaseHelper elDatabaseHelper = new ELDatabaseHelper(getApplicationContext());
                elDatabaseHelper.update(getApplicationContext(), json);
            }
            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            dialog.hide();
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    String LOG_TAG ="EL";

    public boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(LOG_TAG, "No network available!");
        }
        return false;
    }
}
