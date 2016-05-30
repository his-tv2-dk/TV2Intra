package dk.tv2.intra;
import com.urbanairship.push.BaseIntentReceiver;
import android.*;
import android.app.ActivityManager;
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
import com.urbanairship.push.PushMessage;

import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.Manifest;

/**
 * Created by henrik on 11/04/2016.
 */
public class IntentReceiver extends BaseIntentReceiver
    {
    static WriteLogfile mWriteLogfile;
    static Context mContext;
    static int bufferIndex;
    static String[] buffer;
    static int bufferIndexMax;
    static int numberOfMessagesReceived;
    static Activity mAct;
    static Buffer mBuffer;
    static boolean ready;

    static public void intentReceiver(Context context
        ) {

        // Local variables
        boolean test_data_desired;

        ready=false;

        // Unpack arguments
        mContext = context;

        establishLogfileSystem();
        debug("intent receiver now started");
        bufferIndexMax = 5;

        // Because we use a circular buffer, we can start anywhere
        // we like with bufferIndex.
        bufferIndex = 0;

        buffer = new String[bufferIndexMax + 1];
        numberOfMessagesReceived = 0;

        // Set test data desired to true if you want
        // 2 test messages in the buffer to begin with.
        test_data_desired = false;

        if (test_data_desired) {

            storeInBuffer("11:02   Forlad  bygningen.");
            storeInBuffer("12:26   Brand i receptionen.");
        }
    }

    static public void init2(Context context,
        Activity act,
        Buffer buf) {

        // Local variables
        boolean test_data_desired;

        // Unpack arguments
        mContext = context;
        mAct = act;
        mBuffer = buf;

        debug("intent receiver now started");
        ready=true;
        }

    static private void establishLogfileSystem(){

        // Name of logfile should be logfile.txt.
        // A better name might be logfile.dat, but the
        // Mac computer cannot figure out how to run an
        // editor on a file that ends with ".dat".

        mWriteLogfile=new WriteLogfile(mContext,"logfile.txt");
    }

    static private void debug(String s) {
        //
        mWriteLogfile.writeToFile(s);

    }

    // All these methods are from Urban Airship

    private static final String TAG = "IntentReceiver";

    @Override
    protected void onChannelRegistrationSucceeded(Context context, String channelId) {
        Log.i(TAG, "Channel registration updated. Channel Id:" + channelId + ".");
    }

    @Override
    protected void onChannelRegistrationFailed(Context context) {
        Log.i(TAG, "Channel registration failed.");

    }

    static private void storeInBuffer(String s) {

        bufferIndex=bufferIndex+1;

        if (bufferIndex>bufferIndexMax) {
            bufferIndex=1;

        }

        // Save the message in the buffer.
        debug("store in "+bufferIndex);
        buffer[bufferIndex]=s;

        // As long as the buffer is not filled, count the
        // number of messages.  Once we have reached the
        // maximum space in the buffer, the number of
        // messages remains constant.

        if (numberOfMessagesReceived<bufferIndexMax)
            numberOfMessagesReceived++;

    }

    protected void moveTasksToFront(ActivityManager am) {

        // Local variables
        ActivityManager.AppTask apptask;

        if (Parameters.apiVersion>=21) {

            // Get our own tasks
            // getAppTasks requires API level 21
            List<ActivityManager.AppTask> tasklist=am.getAppTasks();

            // Go thru our tasks, even if there is only one task

            for (int i = 0; i < tasklist.size(); i++) {

                // Get the task from the list of tasks
                apptask=tasklist.get(i);

                // Move the task to front, that is, make it visible
                // on the screen of the device.

                // moveToFront requires API level 21
                apptask.moveToFront();

                debug("i="+i);
            }
        }
    }

    protected void moveToFront() {

        // Move our app to the front, that is, make it visible on
        // the screen.  If you need to change this code, see Remains
        // class file for other kinds of code that may be useful.

        // Local variables
        ActivityManager.AppTask apptask;
        //ActivityManager.RecentTaskInfo taskinfo;
        final ActivityManager am;

        // Get activity manager

        am = (ActivityManager) mAct.getSystemService(
                Context.ACTIVITY_SERVICE);

        // Move all our tasks to front
        moveTasksToFront(am);
        }

    @Override
    protected void onPushReceived(Context context, PushMessage message, int notificationId) {
        Log.i(TAG, "Received push notification. Alert: " +
                message.getAlert() + ". Notification ID: " + notificationId);
        debug("push received");

        // This would be too easy.
        //mAct.moveTaskToBack(false);

        moveToFront();
        /*
        Intent i=new Intent(ApplicationStatus,
                NotifyActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//optional
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);//will cause it to come to foreground
        i.putExtra("ID_TimeLeft",String.valueOf(TimeLeft));
        startActivity(i);*/

        storeInBuffer(message.getAlert());
        mBuffer.showMessages();

    }

    @Override
    protected void onBackgroundPushReceived(Context context, PushMessage message) {
        Log.i(TAG, "Received background push message: " + message);
        debug("on background push received");
    }

    @Override
    protected boolean onNotificationOpened(Context context,
        PushMessage message, int notificationId) {
        Log.i(TAG, "User clicked notification. Alert: " + message.getAlert());

        debug("onNotificationOpened");

        // Return false to let UA handle launching the launch activity
        return false;
    }

    @Override
    protected boolean onNotificationActionOpened(Context context,
        PushMessage message, int notificationId,
        String buttonId, boolean isForeground) {

        Log.i(TAG, "User clicked notification button. Button ID: " +
                buttonId + " Alert: " + message.getAlert());

        // Return false to let UA handle launching the launch activity
        return false;
    }

    @Override
    protected void onNotificationDismissed(Context context, PushMessage message,
        int notificationId) {
        Log.i(TAG, "Notification dismissed. Alert: " +
                message.getAlert() + ". Notification ID: " + notificationId);
    }
}

