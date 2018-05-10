package com.hebut.kortan.cloudtill.activities;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.gson.Gson;
import com.hebut.kortan.cloudtill.R;
import com.hebut.kortan.cloudtill.dummy.DummyContent;
import com.hebut.kortan.cloudtill.fragment.ClientFragment;
import com.hebut.kortan.cloudtill.fragment.FarmManage;
import com.hebut.kortan.cloudtill.fragment.MyClientPagerAdapter;
import com.hebut.kortan.cloudtill.fragment.RealTimeData;
import com.hebut.kortan.cloudtill.fragment.fragmentInfo;
import com.hebut.kortan.cloudtill.fragment.fragmentOverview;
import com.hebut.kortan.cloudtill.utilities.JsonUtils;
import com.hebut.kortan.cloudtill.utilities.NetworkUtils;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends FragmentActivity implements ClientFragment.OnListFragmentInteractionListener, RealTimeData.OnFragmentInteractionListener, FarmManage.OnFragmentInteractionListener, ClientFragment.ClientFragCallBackInterface {

    private TextView myContent;

    private fragmentInfo infoFrag = new fragmentInfo();
    private fragmentOverview overviewFrag = new fragmentOverview();

    private ClientFragment clientFrag = new ClientFragment();
    private RealTimeData realTimeData = new RealTimeData();

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    public void onListFragmentInteraction(DummyContent.DummyItem item){

    }

    public void onFragmentInteraction(Uri uri){

    }

    @Override
    public void myCallBack(View view) {
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager)view.findViewById(R.id.pager);
        mPagerAdapter = new MyClientPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContent = (TextView) findViewById(R.id.web_data);

        //new TestTask().execute();

        //导航栏初始化
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_watch_later_white_24dp, "实时数据"))
                .addItem(new BottomNavigationItem(R.drawable.ic_cloud_white_24dp, "云数据"))
                .initialise();

        //显示初始Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, clientFrag).commit();

        //为导航栏添加Listener
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                if (position == 0) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, clientFrag).commit();
                }
                else if (position == 1) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, infoFrag).commit();
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
