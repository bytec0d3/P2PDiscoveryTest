package it.cnr.iit.peerservicediscoverytest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryReceiver extends BroadcastReceiver {

    private static final String TAG = "BatteryReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().compareToIgnoreCase(Intent.ACTION_BATTERY_CHANGED) == 0){

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if(level != -1 && scale != -1){

                float battPct = (float)level/(float)scale;
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);

                LogManager.getInstance().logData(battPct+","+plugged, LogManager.LOG_TYPE.TYPE_BATTERY);

            }else{
                Log.e(TAG, "Something goes wrong with the battery level.");
            }
        }
    }
}
