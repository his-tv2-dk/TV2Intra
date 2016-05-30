package dk.tv2.intra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import java.util.List;
import android.*;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.urbanairship.UAirship;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
/**
 * Created by henrik on 07/04/2016.
 */
public class Wifinet {
    WifiManager mMainWifi;
    WifiReceiver mReceiverWifi;
    TextView mMainText2;
    boolean mReceiverActive;

    public Wifinet() {
        mReceiverActive = false;
        Log.i("","Wifinet started");
    }

    public void setupWifiScanner(
        Context context,
        Activity act,
        TextView mainText) {

        // Local variables

        IntentFilter intentFilter;

        // Report entry

        Log.i("","Setup wifi scanner");

        // Save text view for possible later use by the receiver.

        mMainText2=mainText;

        // Check if wifi is enabled

        mMainWifi = (WifiManager) act.getSystemService(Context.WIFI_SERVICE);
        if (mMainWifi.isWifiEnabled() == false) {

            // If wifi disabled then enable it

            Toast.makeText(act.getApplicationContext(), "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();

            //     mainWifi.setWifiEnabled(true);
        }

        // Request that WifiReceiver is called when wifi connections change.

        mReceiverWifi = new WifiReceiver();
        intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        act.registerReceiver(mReceiverWifi, intentFilter);

        // Keep track of receiver status

        mReceiverActive=true;

        // Start wifi scanning

        mMainWifi.startScan();

        // Inform user WifiScan started.

        mainText.setTextSize(40);
        mainText.setText("WifiScan started");
    }

    public void unregister(Activity act) {
        Log.i("","Unregister wifi receiver");
        if (mReceiverActive) {
            act.unregisterReceiver(mReceiverWifi);
            mReceiverActive = false;
        }
    }

    class WifiReceiver extends BroadcastReceiver {

        // This method is called when wifi connections change

        public void onReceive(Context c, Intent intent) {

            // Local variables

            StringBuilder sb = new StringBuilder();
            List<ScanResult> wifiList;

            // Get the list of wifi connections

            wifiList = mMainWifi.getScanResults();

            // Report number of wifi connections

            sb = new StringBuilder();
            sb.append("\n        Number Of Wifi connections :"+wifiList.size()+"\n\n");

            // Report data for each wifi connection

            for(int i = 0; i < wifiList.size(); i++) {
                sb.append(new Integer(i+1).toString() + ". ");
                sb.append((wifiList.get(i)).toString());
                sb.append("\n\n");
            }

            mMainText2.setText(sb);

        }
    }
}
