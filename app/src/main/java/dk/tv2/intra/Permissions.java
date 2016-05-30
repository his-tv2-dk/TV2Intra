package dk.tv2.intra;

import android.*;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by henrik on 08/04/2016.
 */
public class Permissions {

    private void explainIfRequired() {
        /*

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(act,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        */
    }

    private void requestOurPermissions(Activity act) {
        int MY_PERMISSIONS_REQUEST_LOC=1;

        // MY_PERMISSIONS_REQUEST_LOC is an
        // app-defined int constant. The callback method gets the
        // result of the request.

        // It is not permitted to request one permission at a time.
        // We have to group them.

        // Microsoft Azure requires MANAGE_ACCOUNTS and USE_CREDENTIALS,
        // but they are not used in android 6.

        ActivityCompat.requestPermissions(act,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.GET_ACCOUNTS,
//                        android.Manifest.permission.MANAGE_ACCOUNTS,
//                        android.Manifest.permission.USE_CREDENTIALS,
                },
                MY_PERMISSIONS_REQUEST_LOC);
    }

    public void getPermissions(Activity act) {

        // Local variables

        boolean fineLocationGranted;
        boolean writeExternalStorageGranted;
        boolean permissionsGranted;
        boolean getAccountsGranted;

        // The following procedure may seem somewhat complex.  It seems it
        // might be simpler to request the required permissions
        // one by one.  However, android does not permit that method.
        // We have to handle all permissions at once.

        // Check if fine location permission granted

        fineLocationGranted = ContextCompat.checkSelfPermission(act,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        // Check if write to external storage granted

        writeExternalStorageGranted = ContextCompat.checkSelfPermission(act,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        // Check if get_accounts granted

        getAccountsGranted = ContextCompat.checkSelfPermission(act,
                android.Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED;

        // Check if all permissions are granted

        permissionsGranted = fineLocationGranted&&
                getAccountsGranted&&writeExternalStorageGranted;

        if (!permissionsGranted)
            explainIfRequired();
        else
            requestOurPermissions(act);
    }
}
