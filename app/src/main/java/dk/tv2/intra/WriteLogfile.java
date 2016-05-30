package dk.tv2.intra;

import android.*;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import android.net.Uri;
import java.io.OutputStreamWriter;
import java.io.IOException;


/**
 * Created by henrik on 13/04/2016.
 */
public class WriteLogfile {
    Context mContext;
    String mFilename;
    File mLogFile;
    static Object logLock;

    public WriteLogfile(Context context,
        String filename){
        File dir;

        mContext=context;
        mFilename=filename;

        logLock=new Object();

        // Logfile must be in the Download directory

        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Not necessary to create directory on Samsung test device but may be required
        // in general.

        // dir.mkdir();

        mLogFile = new File(dir, mFilename);

        // Create the logfile

        try{
            mLogFile.createNewFile();
        }

        // Report if the logfile could not be created.

        catch (IOException e) {
            Log.e("Exception", "Create file failed: " + e.toString());
        }

        // Report logging started.

        //Log.i("WriteLogfile", "----------------------------- Starting on "+filename);
        //writeToFile("Starting ------------------------------");

    }

    public void writeToFile(String s) {
        FileOutputStream f;
        OutputStreamWriter outputStreamWriter;
        boolean append;

        synchronized (logLock) {
            //
            writeToFile2(s);
        }
    }

    public void writeToFile2(String s) {
        FileOutputStream f;
        OutputStreamWriter outputStreamWriter;
        boolean append;

        try {

            // Write s to end of logfile

            append = true;
            f = new FileOutputStream(mLogFile,append);
            outputStreamWriter = new OutputStreamWriter(f);
            outputStreamWriter.write(s);

            // Write line feed

            outputStreamWriter.write("\n");
            outputStreamWriter.close();
        }

        // Report if file does not exist.

        catch (FileNotFoundException e){
            Log.e("Exception", "File not found: " + e.toString());
            Log.e("Exception", "The file must be created first");
        }

        // Report if write operation failed.

        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
