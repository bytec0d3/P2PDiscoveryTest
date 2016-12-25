package it.cnr.iit.peerservicediscoverytest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Boot intent received.");

        context.startService(new Intent(context,WifiService.class));

    }
}
