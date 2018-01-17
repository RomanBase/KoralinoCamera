package com.ankhrom.base.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.util.List;

/**
 * requires WIFI permissions
 */
@SuppressWarnings("ResourceType")
public class WifiHelper {

    private final WifiManager wifi;
    private final Context context;
    private boolean isRegistered = false;
    private boolean isUnderScan = false;

    public WifiHelper(Context context) {
        this.context = context;

        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        startNewScan();
    }

    private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {

            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                onWifiScanComplete(wifi.getScanResults());
                isUnderScan = false;
                onDestroy();
            }
        }
    };

    protected void onWifiScanComplete(List<ScanResult> wifiResults) {

    }

    public void startNewScan() {

        isUnderScan = true;
        isRegistered = true;
        context.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();
    }

    public WifiManager getWifiManager() {

        return wifi;
    }

    public boolean connectWifi(String ssid, String key) {

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);

        if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);
        } else {
            wifi.disconnect();
        }

        int netId = wifi.addNetwork(wifiConfig);
        wifi.enableNetwork(netId, true);

        return wifi.reconnect();
    }

    public String getActiveNetworkName() {

        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
            return info.getSSID();
        }

        return null;
    }

    public boolean isUnderScan() {
        return isUnderScan;
    }

    public void onDestroy() {
        try {
            if (isRegistered) {
                isRegistered = false;
                context.unregisterReceiver(wifiScanReceiver);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void openSettings(Context context) {

        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }
}
