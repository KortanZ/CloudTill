package com.hebut.kortan.cloudtill.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hebut.kortan.cloudtill.R;
import com.hebut.kortan.cloudtill.utilities.JsonUtils;
import com.hebut.kortan.cloudtill.utilities.NetworkUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    MaterialEditText user;
    MaterialEditText passwd;

    TextView testLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = (MaterialEditText) findViewById(R.id.username);
        passwd = (MaterialEditText) findViewById(R.id.password);
        testLogin = (TextView) findViewById(R.id.testLogin);
    }

    public void tet(View view) {
        if (view.getId() == R.id.login) {
            new LoginTask().execute();
        }
    }

    public class LoginTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String response = null;
            try{
                HashMap<String, String> map = new HashMap<>();
                map.put("submit","appLogin");
                map.put("name",user.getText().toString());
                map.put("passwd",passwd.getText().toString());
                NetworkUtils handler = new NetworkUtils();
                response = handler.Http_Post(map,"/api/login.php");
            } catch(IOException e) {
                e.printStackTrace();
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!s.equals("ER")) {
                Toast.makeText(LoginActivity.this, "LoginDone!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", s);
                startActivity(intent);
            }
        }
    }
}


