package dk.tv2.intra;

import android.app.Application;

import android.app.Activity;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

import android.content.IntentFilter;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.content.Context;
import android.Manifest;
import android.util.Log;
import io.fabric.sdk.android.Fabric;


/**
 * Created by henrik on 05/04/2016.
 */
public class MyApp extends Application {

    // This class is executed before MainActivity.

    // Therefore with everything we do here, we must
    // take into account that nothing is yet done
    // in MainActivity.

    // This Application class does not stop with the
    // finish statement, so do not setup more systems
    // than absolutely necessary here.

    private static MyApp myApp;

    // Local variables
    public IntentFilter intentFilter;

    public IntentReceiver mIntentReceiver;


    final String TAG = MyApp.class.getSimpleName();

    WriteLogfile mWriteLogfile;

    public MyApp() {
         }

    public void enableUrbanAirshipLocation() {

        // Enable location updates

        Log.i(TAG, "Enable Urban Airship");
        UAirship.shared().getLocationManager().setLocationUpdatesEnabled(true);

        // Allow location updates to continue in the background

        UAirship.shared().getLocationManager().setBackgroundLocationAllowed(true);
    }

    //Variable mVariable;

    public void startIntentReceiver() {

        // The default recommendation by Urban Airship is to
        // declare the receiver in the manifest file.  However,
        // that means we cannot stop the receiver.
        // Therefore we start it programmatically.

        //mVariable=new Variable();

        //mVariable.mIntentReceiver = new IntentReceiver();

        mIntentReceiver = new IntentReceiver();

        // These filter names come from Urban Airship documentation.

        intentFilter = new IntentFilter(
                "com.urbanairship.push.CHANNEL_UPDATED");

        intentFilter.addAction(
                "com.urbanairship.push.OPENED");

        intentFilter.addAction(
                "com.urbanairship.push.RECEIVED");

        intentFilter.addAction(
                "com.urbanairship.push.DISMISSED");

        intentFilter.addCategory("dk.tv2.intra");

        // Start the Urban Airship notification receiver

        registerReceiver(mIntentReceiver, intentFilter);

        debug("Urban Airship receiver registered");
    }

    public void unreg() {
        //unregisterReceiver(mVariable.mIntentReceiver);
    }

    static public MyApp getMyApp() {
        return myApp;
    }

    private void setupNotificationSystem() {

        // Initialize the buffer for notification messages
        // It is best to load the buffer system before
        // the intent receiver but it is difficult because
        // the main activity has not yet started.
        // The intent receiver needs the buffer, but
        // only if a notification comes in.

        //mBuffer = new Buffer(mContext, mainText, mAct);
//        mBuffer = new Buffer(this, mainText, mAct);

        // Initialize the receiver for notification messages
        // startIntentReceiver();

//        IntentReceiver.intentReceiver(mContext, mAct, mBuffer);
        IntentReceiver.intentReceiver(this);

    }

    public void setupUrbanAirship() {

        Log.i(TAG, "Setup Urban Airship");

        debug("setupUrbanAirship");

        startIntentReceiver();
        setupNotificationSystem();

        UAirship.takeOff(this, new UAirship.OnReadyCallback( ) {
            //
            //IntentReceiver mIntentReceiver;
            @Override

            public void onAirshipReady(UAirship airship) {

                debug("onAirshipReady");

                // It seems previously it was possible to register
                // the intent receiver programmatically, but this
                // possibility has since been lost.
                // PushManager.shared().setIntentReceiver(IntentReceiver.class);
                // PushManager.shared().setIntentReceiver(intentRcvr); // TAKE NOTE!
                //registerReceiver()   (x mIntentReceiver);

                // Enable user notifications

                airship.getPushManager().setUserNotificationsEnabled(true);

                // Enable location notifications
                enableUrbanAirshipLocation();
            }
        });


        /*
        google cloud messaging api key, sender id
        AIzaSyAxr9BzqeBUrGXrZi_XOr-pg4koG1ly2GQ
        Sender ID
        842320847217
        */

        Log.d("TV2", "Urban Airship setup done");
    }

    private void establishLogfileSystem(){

        // Name of logfile should be logfile.txt.
        // A better name might be logfile.dat, but the
        // Mac computer cannot figure out how to run an
        // editor on a file that ends with ".dat".

        mWriteLogfile=new WriteLogfile(MainContext.context,"logfile.txt");
    }

    private void debug(String s) {
        //
        mWriteLogfile.writeToFile(s);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;

        establishLogfileSystem();
        debug("now in MyApp");

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();

        Fabric.with(fabric);

//        Fabric.with(this, new Crashlytics());

        setupUrbanAirship();

        /*
        google cloud messaging api key, sender id
        AIzaSyAxr9BzqeBUrGXrZi_XOr-pg4koG1ly2GQ
        Sender ID
        842320847217
        */

        // Start Yahoo Flurry

        // This method seems to be outdated.

        //new FlurryAgent.Builder()
        //    .withLogEnabled(false)
        //    .build(this, "YKWRJYD8NVTGJ3V5V9R4");

        FlurryAgent.init(this, "YKWRJYD8NVTGJ3V5V9R4");

        Log.d("zzz","MyApp onCreate done");

    }

}
