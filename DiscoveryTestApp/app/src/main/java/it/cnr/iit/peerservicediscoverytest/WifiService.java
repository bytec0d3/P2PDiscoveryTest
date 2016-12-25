package it.cnr.iit.peerservicediscoverytest;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WifiService extends Service implements WifiP2pManager.ChannelListener{

    private static final String TAG = "WifiService";

    ScheduledExecutorService discoveryScheduler = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService wifiScheduler = Executors.newSingleThreadScheduledExecutor();
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    BatteryReceiver batteryReceiver;

    WifiController wifiController;
    PeerDiscovery peerDiscovery;
    ServiceDiscovery serviceDiscovery;

    WifiP2pManager p2p;
    WifiP2pManager.Channel channel;

    WifiManager.WifiLock wifiLock;
    PowerManager.WakeLock powerLock;

    private static final int START_DELAY = 120;             // 2 minutes
    private static final int DISCOVERY_TIME = 60;           // 1 minute
    private static final int WIFI_RESET_TIME = 3600;        // 1 hour


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        powerLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PowerLock");
        powerLock.acquire();

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "LockTag");
        wifiLock.acquire();

        p2p = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        batteryReceiver = new BatteryReceiver();

        if (p2p == null) {
            Log.e(TAG, "This device does not support Wi-Fi Direct");
        }else {

            channel = p2p.initialize(this, getMainLooper(), this);

            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

            mReceiver = new WiFiDirectBroadcastReceiver();

            wifiController = new WifiController(this);
            peerDiscovery = new PeerDiscovery(p2p, channel);
            serviceDiscovery = new ServiceDiscovery(p2p, channel);

            startBatteryReceiver();
            registerReceiver(mReceiver, mIntentFilter);
            startDiscoveryScheduler();
            startWifiResetScheduler();
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onChannelDisconnected() {
        Log.e(TAG, "Channel disconnected");
    }

    private void startBatteryReceiver(){
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void startDiscoveryScheduler(){
        discoveryScheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                LogManager.getInstance().logData("Start discovery", LogManager.LOG_TYPE.TYPE_NETWORK);
                peerDiscovery.restartDiscovery();
                serviceDiscovery.discoverServices();
            }
        }, START_DELAY, DISCOVERY_TIME, TimeUnit.SECONDS);
    }

    private void startWifiResetScheduler(){

        wifiScheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                LogManager.getInstance().logData("Restarting Wifi", LogManager.LOG_TYPE.TYPE_NETWORK);
                wifiController.restartWifi();
            }
        }, 0, WIFI_RESET_TIME, TimeUnit.SECONDS);

    }

    public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
                if (p2p != null) p2p.requestPeers(channel, peerDiscovery);
        }
    }
}
