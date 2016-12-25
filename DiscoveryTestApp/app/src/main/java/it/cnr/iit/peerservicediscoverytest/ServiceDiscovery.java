package it.cnr.iit.peerservicediscoverytest;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ServiceDiscovery implements WifiP2pManager.DnsSdServiceResponseListener {

    private static final String TAG = "ServiceDiscovery";

    private WifiP2pManager p2p;
    private WifiP2pManager.Channel channel;

    private Set<String> currentServices;

    ServiceDiscovery(WifiP2pManager p2p, WifiP2pManager.Channel channel){
        this.p2p = p2p;
        this.channel = channel;
    }

    void stop(){

        p2p.clearLocalServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Local services cleared");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "Error clearing local services: "+reason);
            }
        });

        p2p.setDnsSdResponseListeners(channel, null, null);
        p2p.clearServiceRequests(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Service requests cleared");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "Error clearing service requests: "+reason);
            }
        });
    }


    void discoverServices() {
        currentServices = new HashSet<>();
        p2p.setDnsSdResponseListeners(channel, this, null);
        removeService();
    }

    private void clearRequests(){
        p2p.clearServiceRequests(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                createRequest();
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "Error clearing service requests: "+reason);
            }
        });
    }

    private void createRequest(){
        p2p.addServiceRequest(channel,
                WifiP2pDnsSdServiceRequest.newInstance(),
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Service request");
                        startDiscovery();
                    }

                    @Override
                    public void onFailure(int code) {
                        Log.e(TAG, "Error in Service request: "+code);
                    }
                });
    }

    private void startDiscovery(){
        p2p.discoverServices(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onFailure(int code) {
                Log.e(TAG, "Error starting service discovery: " + code);
            }
        });
    }

    @Override
    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
        if(!currentServices.contains(srcDevice.deviceAddress)) {
            currentServices.add(srcDevice.deviceAddress);
            Log.d(TAG, "Service from " + srcDevice.deviceAddress);
            LogManager.getInstance().logData("Service found: "+srcDevice.deviceAddress,
                    LogManager.LOG_TYPE.TYPE_NETWORK);
        }
    }

    private void createService() {
        //  Create a string map containing information about your service.
        Map<String, String> record = new HashMap<>();
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        p2p.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Service created");
                clearRequests();
            }

            @Override
            public void onFailure(int arg0) {
                Log.e(TAG, "Error creating the local service: "+arg0);
            }
        });
    }

    private void removeService(){
        p2p.clearLocalServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Local services cleared");
                createService();
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "Error clearing local services: "+reason);
            }
        });
    }
}
