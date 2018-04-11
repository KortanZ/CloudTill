package com.hebut.kortan.cloudtill.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hebut.kortan.cloudtill.R;
import com.hebut.kortan.cloudtill.utilities.JsonUtils;
import com.hebut.kortan.cloudtill.utilities.NetworkUtils;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView myContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContent = (TextView) findViewById(R.id.web_data);
        new TestTask().execute();

    }

    public class TestTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            String response = null;
            try{
                HashMap<String, String> map = new HashMap<>();
                Gson gson = new Gson();
                JsonUtils js = new JsonUtils();
                JsonUtils.LoginJsonFormat login = js.new LoginJsonFormat();
                login.name = "app";
                login.passwd = "123";
                String info = gson.toJson(login);
                map.put("submit","appLogin");
                map.put("appLoginInfo", info);
//                map.put("name","app");
//                map.put("passwd","123");
                NetworkUtils handler = new NetworkUtils();
                response = handler.Http_Post(map,"/api/login.php");
//                response = handler.Http_Get("/api/logout.php");
            } catch(IOException e) {
                e.printStackTrace();
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            myContent.setText(s);
        }
    }
}
