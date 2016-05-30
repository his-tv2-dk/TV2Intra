package dk.tv2.intra;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.widget.TextView;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by henrik on 20/04/2016.
 */
public class Execute {

    Context mContext;
    Activity mActivity;
    WriteLogfile mWriteLogfile;
    String mService;


    public Execute(Context context, String service) {
        mContext = context;
        mService=service;
        mActivity = (Activity) context;

        // Microsoft has this system for debugging, but it would
        // be nice if you could simply get an understandable error message.
        //ApplicationInsights.setDeveloperMode(true);

        establishLogfileSystem();
        debug("Execute class loaded");

    }

    private void establishLogfileSystem(){

        // Name of logfile should be logfile.txt.
        // A better name might be logfile.dat, but the
        // Mac computer cannot figure out how to run an
        // editor on a file that ends with ".dat".

        mWriteLogfile=new WriteLogfile(mContext,"logfile.txt");
    }

    private void debug(String s) {
        //
        mWriteLogfile.writeToFile(s);
    }

    public void execLoggedIn() {
        // Suggestion from a microsoft paper
        Intent loggedInIntent;

        loggedInIntent = new Intent(mActivity.getApplicationContext(),
                //mActivity.LoggedInActivity.class);
                MainActivity.class);
        mActivity.startActivity(loggedInIntent);
    }

    private class exec2 extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            ArrayList<String> list;

            list = new ArrayList<String>();
            HttpURLConnection conn = null;
            BufferedReader br = null;


            try {
                List<String> items = new ArrayList<>();
                URL url = new URL(mService);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                //conn.setRequestProperty("Content-Type", "application/json");

                //debug("params 0="+params[0]);
                conn.setRequestProperty("Authorization", "Bearer " + params[0]);
                debug("   http return code: "+conn.getResponseCode());

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " +
                            conn.getResponseCode());


                }

                return items;
            } catch (Exception e) {
                return new ArrayList<>();
            } finally {
                //AppHelper.close(br);
                if (conn != null) {
                    conn.disconnect();
                }
            }
            //return list;
        }
    }

    public void code(String items){

        //exec2 task;
        try{
            debug("now attempt exec2");
            new exec2().execute(items).get();
            debug("exec2 done");
        }
        catch (Exception e){
            debug("async failed");

        }
    }
}

