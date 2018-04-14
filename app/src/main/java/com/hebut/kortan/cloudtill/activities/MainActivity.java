package com.hebut.kortan.cloudtill.activities;

import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.gson.Gson;
import com.hebut.kortan.cloudtill.R;
import com.hebut.kortan.cloudtill.fragment.fragmentInfo;
import com.hebut.kortan.cloudtill.fragment.fragmentOverview;
import com.hebut.kortan.cloudtill.utilities.JsonUtils;
import com.hebut.kortan.cloudtill.utilities.NetworkUtils;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView myContent;

    private fragmentInfo infoFrag = new fragmentInfo();
    private fragmentOverview overviewFrag = new fragmentOverview();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContent = (TextView) findViewById(R.id.web_data);
        new TestTask().execute();


        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, "Home"))
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, "Books"))
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, "Music"))
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, "Movies & TV"))
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, "Games"))
                .initialise();

        getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, overviewFrag).commit();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                if (position == 0) {
                    getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, overviewFrag).commit();
                }
                else if (position == 1) {
                    getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, infoFrag).commit();
                }
            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
            }
        });

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
