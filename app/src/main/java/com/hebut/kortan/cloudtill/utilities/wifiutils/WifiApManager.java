package com.hebut.kortan.cloudtill.utilities.wifiutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

public class WifiApManager {

    private enum WIFI_AP_STATE {
        WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
    }

    private static final String TAG = "WifiApManager";

    private static final String KEY_SHARED_NAME = "DefultAP";

    private static final String KEY_SSID = "DefultAP";

    private static final String KEY_PASSWORD = "12345678";

    private final WifiManager wifiManager;
    private Context context;

    public WifiApManager(Context context) {
        this.context = context;
        wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
    }

    public void showWritePermissionSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this.context)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.context.startActivity(intent);
            }
        }
    }

    /**
     * 持久化保存设备的Ap热点的名称和密码
     *
     */
    public void SaveApCache() {
        String[] params = GetApSSIDAndPwd();
        if (params != null && !TextUtils.isEmpty(params[0])) {
            Log.e(TAG, "本地的Ap热点的名称: " + params[0]);
            Log.e(TAG, "本地的Ap热点的密码: " + params[1]);
            SaveApCache(params[0], params[1]);
        } else {
            Log.e(TAG, "无Ap热点信息");
            CleanApCache();
        }
    }

    /**
     * 还原Ap热点信息
     *
     */
    public void ResetApCache() {
        String[] params = GetApCache();
        if (TextUtils.isEmpty(params[0])) {
            return;
        }
        OpenAp(params[0], params[1]);
        CloseAp();
    }

    /**
     * 获取本地持久化保存的Ap热点的名称和密码
     *
     * @return String[0]:Ap热点名称、String[1]:Ap热点的密码（密码可能为空）
     */
    private String[] GetApCache() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_SHARED_NAME, Context.MODE_PRIVATE);
        String ssid = sharedPreferences.getString(KEY_SSID, "");
        if (TextUtils.isEmpty(ssid)) {
            return new String[2];
        }
        String[] params = new String[2];
        params[0] = ssid;
        params[1] = sharedPreferences.getString(KEY_PASSWORD, "");
        return params;
    }

    private void SaveApCache(String ssid, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_SHARED_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SSID, TextUtils.isEmpty(ssid) ? "" : ssid);
        editor.putString(KEY_PASSWORD, TextUtils.isEmpty(password) ? "" : password);
        editor.apply();
    }

    private void CleanApCache() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_SHARED_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }

    /**
     * 判断Ap热点是否开启
     *
     * @return 开关
     */
    public boolean IsApOn() {
        if (wifiManager == null) {
            return false;
        }
        try {
            @SuppressLint("PrivateApi")
            Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets the Wi-Fi enabled state.
     *
     * @return {@link WIFI_AP_STATE}
     */
    public WIFI_AP_STATE GetWifiApState() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            int tmp = ((Integer) method.invoke(wifiManager));

            // Fix for Android 4
            if (tmp >= 10) {
                tmp = tmp - 10;
            }

            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }

    /**
     * 开启Ap热点
     *
     * @param ssid     SSID
     * @param password 密码
     * @return 是否成功
     */
    public boolean OpenAp(String ssid, String password) {
        if (wifiManager == null) {
            return false;
        }
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifiManager, null, false);
            method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifiManager, CreateApConfiguration(ssid, password), true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭Ap热点
     *
     */
    public void CloseAp() {
        if (wifiManager == null) {
            return;
        }
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifiManager, null, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备曾开启过的Ap热点的名称和密码（无关设备现在是否有开启Ap）
     *
     * @return Ap热点名、Ap热点密码（密码可能为空）
     */
    public String[] GetApSSIDAndPwd() {
        if (wifiManager == null) {
            Log.e(TAG, "wifiManager == null");
            return null;
        }
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration wifiConfiguration = (WifiConfiguration) method.invoke(wifiManager);
            String[] params = new String[2];
            params[0] = wifiConfiguration.SSID;
            params[1] = wifiConfiguration.preSharedKey;
            return params;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    /**
     * 获取开启Ap热点后设备本身的IP地址
     *
     * @return IP地址
     */
    public String GetHotspotIpAddress() {
        DhcpInfo dhcpInfo = wifiManager == null ? null : wifiManager.getDhcpInfo();
        if (dhcpInfo != null) {
            int address = dhcpInfo.serverAddress;
            return ((address & 0xFF)
                    + "." + ((address >> 8) & 0xFF)
                    + "." + ((address >> 16) & 0xFF)
                    + "." + ((address >> 24) & 0xFF));
        }
        return "";
    }

    /**
     * 配置Ap热点信息
     *
     * @param ssid     Ap热点SSID
     * @param password Ap热点密码
     * @return Ap热点信息
     */
    private WifiConfiguration CreateApConfiguration(String ssid, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = ssid;
        config.preSharedKey = password;
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        return config;
    }

    /**
    * Gets a list of the clients connected to the Hotspot
    *
    * @param onlyReachables   {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
    * @param reachableTimeout Reachable Timout in miliseconds
    * @param finishListener,  Interface called when the scan method finishes
    */
    public void GetClientList(final boolean onlyReachables, final int reachableTimeout, final FinishScanListener finishListener) {

        final Context contextInner = context;
        Runnable runnable = new Runnable() {
            public void run() {

                BufferedReader br = null;
                final ArrayList<ClientScanResult> result = new ArrayList<ClientScanResult>();

                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] splitted = line.split(" +");

                        if ((splitted != null) && (splitted.length >= 4)) {
                            // Basic sanity check
                            String mac = splitted[3];

                            if (mac.matches("..:..:..:..:..:..")) {
                                boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(reachableTimeout);

                                if (!onlyReachables || isReachable) {
                                    result.add(new ClientScanResult(splitted[0], splitted[3], splitted[5], isReachable));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().toString(), e.toString());
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e(this.getClass().toString(), e.getMessage());
                    }
                }

                // Get a handler that can be used to post to the main thread
                Handler mainHandler = new Handler(contextInner.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        finishListener.onFinishScan(result);
                    }
                };
                mainHandler.post(myRunnable);
            }
        };

        Thread mythread = new Thread(runnable);
        mythread.start();
    }

}