package it.cnr.iit.peerservicediscoverytest;


import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

class PeerDiscovery implements WifiP2pManager.PeerListListener{

    private static final String TAG = "PeerDiscovery";
    private WifiP2pManager p2p;
    private WifiP2pManager.Channel channel;
    private Set<String> currentPeers;

    PeerDiscovery(WifiP2pManager p2p, WifiP2pManager.Channel channel){

        this.p2p = p2p;
        this.channel = channel;
    }

    void stop(){
        p2p.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Log.d(TAG, "Stopped peer discovery");
            }

            public void onFailure(int reason) {
                Log.e(TAG, "Stopping peer discovery failed, error code " + reason);
            }
        });
    }

    private void startDiscovery(){
        p2p.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                currentPeers = new HashSet<>();
                Log.d(TAG, "Started peer discovery");
            }
            public void onFailure(int reason) {
                Log.e(TAG,"Starting peer discovery failed, error code " + reason);
            }
        });
    }

    void restartDiscovery() {

        p2p.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                Log.d(TAG, "Stopped peer discovery");
                startDiscovery();
            }

            public void onFailure(int reason) {
                Log.e(TAG, "Stopping peer discovery failed, error code " + reason);
            }
        });
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {

        for(WifiP2pDevice device : peers.getDeviceList()) {

            if(!currentPeers.contains(device.deviceAddress)) {
                currentPeers.add(device.deviceAddress);
                Log.d(TAG, "Peer: " + device.deviceAddress);
                LogManager.getInstance().logData("Peer found: "+device.deviceAddress,
                        LogManager.LOG_TYPE.TYPE_NETWORK);
            }
        }

    }
}
