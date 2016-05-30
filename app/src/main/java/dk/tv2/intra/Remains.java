package dk.tv2.intra;

import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.util.Base64;
import android.webkit.WebView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by henrik on 21/04/2016.
 */
public class Remains {
    //List<ActivityManager.RunningAppProcessInfo> recentTasks =
    //    am.getRunningAppProcesses();

    //taskinfo=new ActivityManager.RecentTaskInfo();
    //debug("n=" + taskinfo.id);

/*
            for (int i = 0; i < recentTasks.size(); i++)
                {

                    Log.d("Executed app", "Application executed : "
                            +recentTasks.get(i).baseActivity.toShortString()
                            + "\t\t ID: "+recentTasks.get(i).id+"");
                    // bring to front
                    if (recentTasks.get(i).baseActivity.toShortString().indexOf("yourproject") > -1) {
                        activityManager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
                    }
  */

    // Microsoft has this system for debugging, but it would
    // be nice if you could simply get an understandable error message.
    //ApplicationInsights.setDeveloperMode(true);


    /*
    private static IConnectionHeaderService getService(Context ctx,
        Intent intent, boolean forceNew) {

        RemoteConnection connection = new RemoteConnection();
        ctx.getApplicationContext().startService(intent);
        ctx.getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);

        try
        {
            synchronized (connection)
            {
                while ( connection.service == null ) {
                    connection.wait();
                }
            }
        }
        catch (InterruptedException e)
        {
            Log.e(FeedUtils.class.getName(),
                    "Caught InterruptedException while waiting to obtain remote service");
        }

        IConnectionHeaderService service = connection.service;

        return service;
    }
    */

    /*

    public void startWebsite() {

        //
        //activity.StartActivity(auth.GetUI(activity));
        //startActivity(mAuth.GetUI());

    }


    public void execLoggedIn() {
        // Suggestion from a microsoft paper

        // This code simply starts the app.
        // Use a different activity and it probably starts
        // that activity.

        Intent loggedInIntent;

        debug("   execLoggedIn");
        loggedInIntent = new Intent(getApplicationContext(),
                //mActivity.LoggedInActivity.class);
                MainActivity.class);
        startActivity(loggedInIntent);
    }


    public void startWebsite3() {
        //
        String url;
        String bas;
        WebView w;

        debug("startWebsite3");

        //url = "http://intra.tv2.dk";
        url = "http://intra.tv2local.dk";
        url = "https://tangotest-tv2.msappproxy.net/";

        //url = "http://tv2.dk";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));

        bas = Base64.encodeToString(mLogin.mToken.getBytes(), Base64.NO_WRAP);
        i.putExtra("Authorization",bas);
        i.addCategory(Intent.CATEGORY_BROWSABLE);

        HttpURLConnection c;
        ServiceConnection c2;
        URL u;
        try {
            u = new URL(mService);
        } catch (Exception e) {
            debug("malformed url");
            return;
        }

        // Note that there is both an HttpURLConnection
        // and an HttpsURLConnection.

        debug("attempt to connect to "+mService);
        try {
            //         c2 =  u.openConnection();
        }
        catch (Exception e) {
            debug("http open failed");
            return;
        }
        // You can bind an intent to a ServiceConnection, but it is
        // apparently not possible to connect a httpconnection and a serviceconnection.

        //       getApplicationContext().bindService(i, c2, Context.BIND_AUTO_CREATE);

//        i.putExtra("Authorization","Bearer "+bas);
        //i.putExtra("Authorization","Bearer "+mLogin.mToken);

        startActivity(i);

    }

    public void startWebsite2() {
        HttpURLConnection c;
        URL u;

        // The error message about Block Guard Policy in the logfile
        // means you are trying to do a network operation in the
        // user interface thread.

        mService="http://dmi.dk";


        try {
            u = new URL(mService);
        } catch (Exception e) {
            debug("malformed url");
            return;
        }

        // Note that there is both an HttpURLConnection
        // and an HttpsURLConnection.

        debug("attempt to connect to "+mService);
        try {
            c = (HttpsURLConnection) u.openConnection();
        }
        catch (Exception e) {
            debug("http open failed");
            return;
        }

        c.setUseCaches(false);

        if (mLogin.mToken==null) {
            debug("mToken is null");
            return;
        }
        String bas;

        bas = Base64.encodeToString(mLogin.mToken.getBytes(), Base64.NO_WRAP);

//        c.setRequestProperty("Authorization", "basic " +
        //              Base64.encode(mToken.getBytes()));

        //c.setRequestProperty("Authorization", "basic " +
        //        mLogin.mToken);

        debug("   bas="+bas);

        // So-called basic authorization must supply a string
        // of the format "userid:password".
        // Another format is "Bearer".

        // It is fine to use setRequestProperty with Authorization but
        // that only works for the case where you connect directly
        // using the http protocol - not in interactive mode with
        // a user.

        //c.setRequestProperty("Authorization", "Basic "+bas);

        //c.setRequestProperty("Authorization", bas);

        try {
            c.connect();
        } catch (IOException e) {
            debug("http connect failed");
            debug("   "+e.getMessage());
        }

    }
*/

    //DefaultHttpClient() is a Microsoft class we cannot find.
      /*
        HttpClient httpclient = new DefaultHttpClient();

        HttpResponse httpResponse;
        String responseString = "";

        try {
            HttpGet getRequest = new HttpGet(mUrl);
            getRequest.addHeader(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER_BEARER + mToken);

            //activity.StartActivity(auth.GetUI(activity));
        //startActivity(mAuth.GetUI());

    }
        final HttpClient client = new DefaultHttpClient();
*/

}
