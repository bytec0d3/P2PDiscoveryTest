package it.cnr.iit.peerservicediscoverytest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiController {

    private static final String TAG = "WifiController";
    private Context context;
    private WifiReceiver receiver;

    public WifiController(Context context){
        this.context = context;

        receiver = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        context.registerReceiver(receiver, intentFilter);
    }

    void restartWifi(){
        Log.d(TAG, "Disabling Wifi...");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    private void enableWifi(){
        Log.d(TAG, "Enabling Wifi...");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    class WifiReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {

                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

                if(state == WifiManager.WIFI_STATE_DISABLED)
                    enableWifi();
            }
        }
    }
}
