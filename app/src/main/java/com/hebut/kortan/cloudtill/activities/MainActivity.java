package com.hebut.kortan.cloudtill.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.gson.Gson;
import com.hebut.kortan.cloudtill.R;
import com.hebut.kortan.cloudtill.data.SensorData;
import com.hebut.kortan.cloudtill.fragment.ClientFragment;
import com.hebut.kortan.cloudtill.fragment.FarmManage;
import com.hebut.kortan.cloudtill.fragment.MyClientPagerAdapter;
import com.hebut.kortan.cloudtill.fragment.RealTimeData;
import com.hebut.kortan.cloudtill.fragment.fragmentInfo;
import com.hebut.kortan.cloudtill.utilities.NetworkUtils;
import com.hebut.kortan.cloudtill.utilities.SocketUtils;
import com.hebut.kortan.cloudtill.utilities.wifiutils.WifiApManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends FragmentActivity implements ClientFragment.OnListFragmentInteractionListener, FarmManage.OnFragmentInteractionListener, RealTimeData.OnFragmentInteractionListener, ClientFragment.ClientFragCallBackInterface, RealTimeData.OnFragmentMessageDeliverer {

    private TextView username;

    private fragmentInfo infoFrag = new fragmentInfo();
    private ClientFragment clientFrag = new ClientFragment();

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private int pointPos = 0;

    WifiApManager wifiApManager;

    SocketUtils soc;

    private TextView originDataView;
    private TextView parsedDataView;
    private ArcProgress teProgress;
    private ArcProgress hrProgress;

    private TextView stateText;
    private Button stateButton;

    private String rawData;

    public void onListFragmentInteraction(int position){
        pointPos = position;
        Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
    }

    public void onFragmentInteraction(TextView v, Button b){
        stateText = v;
        stateButton = b;
    }

    @Override
    public void controlDev(String cmd) {
        final String temp = cmd;
        new Thread(){
            @Override
            public void run(){
                try {
                    soc.SocketSendMsg(temp, pointPos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void onFragmentMessage() {
        new Thread(){
            @Override
            public void run(){
                try {
                    soc.SocketSendMsg("Sync Request!", pointPos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void onFragmentInteraction(List<TextView> v, List<ArcProgress> arc) {
        originDataView = v.get(0);
        parsedDataView = v.get(1);
        teProgress = arc.get(0);
        hrProgress = arc.get(1);
    }

    @Override
    public void doSth() {
        new UploadTask().execute();
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
        username = (TextView) findViewById(R.id.username);

        Intent intent = getIntent();
        username.setText("当前用户：" + intent.getStringExtra("username"));

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

        //wifi and socket
        wifiApManager = new WifiApManager(this);
        if (!wifiApManager.IsApOn()) {
            wifiApManager.showWritePermissionSettings();
            wifiApManager.OpenAp("CloudTill", "12345678");
        }

        Handler myHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //socket消息为msg.obj.toString()
                rawData = msg.obj.toString();
                Gson gson = new Gson();
                SensorData da = gson.fromJson(msg.obj.toString(),SensorData.class);
                if (originDataView != null && parsedDataView != null && stateText != null && stateButton != null) {
                    originDataView .setText("原始数据: " + msg.obj.toString() + "\n");
                    parsedDataView.setText("TE: " + da.getTE() + "\n");
                    parsedDataView.append("HR: " + da.getHR() + "\n");
                    parsedDataView.append("WT: " + da.getWT() + "\n");
                    parsedDataView.append("WP: " + da.getWP() + "\n");
                    parsedDataView.append("WD: " + da.getWD() + "\n");
                    parsedDataView.append("RF: " + da.getRF() + "\n");
                    parsedDataView.append("CD: " + da.getCD() + "\n");
                    parsedDataView.append("SS: " + da.getSS() + "\n");

                    if (da.getSS().equals("1")) {
                        stateText.setText("电磁阀开启");
                        stateButton.setText("关闭");
                    } else {
                        stateText.setText("电磁阀关闭");
                        stateButton.setText("开启");
                    }
                }

                ObjectAnimator teAnim = ObjectAnimator.ofInt(teProgress, "progress", 0, Float.valueOf(da.getTE()).intValue());
                teAnim.setInterpolator(new DecelerateInterpolator());
                teAnim.setDuration(500);
                teAnim.start();

                ObjectAnimator hrAnim = ObjectAnimator.ofInt(hrProgress, "progress", 0, Float.valueOf(da.getHR()).intValue());
                hrAnim.setInterpolator(new DecelerateInterpolator());
                hrAnim.setDuration(500);
                hrAnim.start();
                super.handleMessage(msg);
            }
        };
        soc = new SocketUtils(5600, myHandler);
        StartSocket();
    }


    public class UploadTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String response = "ER";
            if (rawData != null) {
                try {
                    HashMap<String, String> map = new HashMap<>();
                    Gson gson = new Gson();
                    SensorData da = gson.fromJson(rawData,SensorData.class);
                    map.put("TE", da.getTE());
                    map.put("HR", da.getHR());
                    map.put("WT", da.getWT());
                    map.put("WP", da.getWP());
                    map.put("WD", da.getWD());
                    map.put("RF", da.getRF());
                    map.put("CD", da.getCD());
                    NetworkUtils handler = new NetworkUtils();
                    response = handler.Http_Post(map,"/api/upload.php");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("OK")) {
                Toast.makeText(MainActivity.this, "UploadDone!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public  void StartSocket() {
        while (wifiApManager.IsApOn());
        try {
            new Thread(){
                @Override
                public void run(){
                    try {
                        if (soc != null) {
                            soc.SocketServerStart();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
                Toast.makeText(this, "SocketStarted", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}
