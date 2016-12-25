package it.cnr.iit.peerservicediscoverytest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";

    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.tv);
        textView.setText("Status: IDLE");

        button = (Button) findViewById(R.id.btn);
        button.setText("START");

        requestPermissions();

        if (Build.VERSION.SDK_INT >= 23 &&
                this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission granted");
        }
    }

    private void requestPermissions(){

        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName))
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
        }

        startActivity(intent);
    }

    private void keepScreenOn(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /*public void buttonClicked(View view){
        if(started){
            started = false;
            wifiLock.release();
            stopBatteryReceiver();
            discoveryScheduler.shutdown();
            wifiScheduler.shutdown();
            unregisterReceiver(mReceiver);
            peerDiscovery.stop();
            serviceDiscovery.stop();
            textView.setText("Status: IDLE");
            button.setText("START");

        }else{
            started = true;
            wifiLock.acquire();
            startBatteryReceiver();
            registerReceiver(mReceiver, mIntentFilter);
            startScheduler();
            textView.setText("Status: Running");
            button.setText("STOP");
        }
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause");
    }
}
