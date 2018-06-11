package com.hebut.kortan.cloudtill.activities;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.hebut.kortan.cloudtill.R;
import com.hebut.kortan.cloudtill.utilities.SocketUtils;

import com.hebut.kortan.cloudtill.utilities.wifiutils.ClientScanResult;
import com.hebut.kortan.cloudtill.utilities.wifiutils.FinishScanListener;
import com.hebut.kortan.cloudtill.utilities.wifiutils.WifiApManager;

import java.util.ArrayList;


public class SensorActivity extends AppCompatActivity {

    TextView textView1;
    TextView textView2;

    WifiApManager wifiApManager;

    SocketUtils soc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        textView1 = (TextView) findViewById(R.id.textView);

        textView2 = (TextView) findViewById(R.id.textView2);

        wifiApManager = new WifiApManager(this);

//        Handler myHandler = new Handler(getMainLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                textView2.setText(msg.obj.toString());
//                super.handleMessage(msg);
//            }
//        };
//
//        soc = new SocketUtils(5600, myHandler);
//        scan();
    }

    private void scan() {
        wifiApManager.GetClientList(false, 20, new FinishScanListener() {

            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {

                textView1.setText("WifiApState: " + wifiApManager.GetWifiApState() + "\n\n");
                textView1.append("Clients: \n");
                for (ClientScanResult clientScanResult : clients) {
                    textView1.append("####################\n");
                    textView1.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");
                    textView1.append("Device: " + clientScanResult.getDevice() + "\n");
                    textView1.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");
                    textView1.append("isReachable: " + clientScanResult.isReachable() + "\n");
                }

//                if (wifiApManager.IsApOn()) {
//                    textView2.setText(wifiApManager.GetHotspotIpAddress());
//                }
//                else {
//                    textView2.setText("AP Off");
//                }
            }
        });
    }

    public class socketTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            soc.SocketServerStart();
            return null;
        }
    }

    public  void StartSocket() {
        if (wifiApManager.IsApOn()) {
            try {
                new socketTask().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Get Clients");
        menu.add(0, 1, 0, "Open AP");
        menu.add(0, 2, 0, "Close AP");
        menu.add(0, 3, 0, "Start Socket");
        menu.add(0, 4, 0, "Send Test");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                scan();
                break;
            case 1:
                wifiApManager.showWritePermissionSettings();
                wifiApManager.OpenAp("CloudTill", "12345678");
                break;
            case 2:
                wifiApManager.CloseAp();
                break;
            case 3:
                StartSocket();
                break;
            case 4:
                new Thread(){
                    @Override
                    public void run(){
                        try {
                            soc.SocketSendMsg("socketTest", soc.Broadcast);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    }.start();
                break;
            }
            return super.onOptionsItemSelected(item);
    }
}
