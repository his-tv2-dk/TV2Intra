package dk.tv2.intra;

import android.*;
import android.app.Application;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.RemoteConnection;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationException;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.PromptBehavior;
import com.urbanairship.UAirship;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
//import org.apache.http.impl.client.DefaultHttpClient;

public class MainActivity extends AppCompatActivity {

    // Member variables

    private static final String TAG = MainActivity.class.getSimpleName();
    Context mContext;
    Activity mAct;
    FloatingActionButton mFab;
    Wifinet mWifinet;
    TextView mainText;
    WriteLogfile mWriteLogfile;
    Login mLogin;
   // String mFullMessage;
    Intent mAzureIntent;
    String mService;
    //Execute mExecute;
    //AuthenticationResult mAuth;
    //String mToken;
    int mRedirects;
    Navigation mNavigation;
    MainContext mMainContext;
    Buffer mBuffer;

    private void setupNotificationSystem() {

        // Initialize the buffer for notification messages
        // It is best to load the buffer system before
        // the intent receiver but it is difficult because
        // the main activity has not yet started.

        mBuffer = new Buffer(mContext, mainText, mAct);
//        mBuffer = new Buffer(this, mainText, mAct);

        // Initialize the receiver for notification messages

        IntentReceiver.init2(mContext, mAct, mBuffer);

    }

    public void stopReceiver() {
        //mContext.unregisterReceiver(MyApp.mIntentReceiver);
        //mContext.unregisterReceiver(MyApp.getIntentReceiver());
        //this.getApplicationContext().unregisterReceiver(MyApp.mIntentReceiver);
        IntentReceiver receiver = MyApp.getMyApp().mIntentReceiver;
        MyApp.getMyApp().unregisterReceiver(receiver);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        // Add a menu item called "Refresh" to the app menu.

        //ViewPager v;
        //v=new ViewPager(this);

        // It seems to be possible to use a bigger textsize
        // but is relatively complicated.

        menu.add(0, 1, 0, "Refresh");
        menu.add(0, 2, 0, "Intra");
        menu.add(0, 3, 0, "Message center");
        menu.add(0, 4, 0, "Test new user interface");
        menu.add(0, 5, 0, "Stop");
        return super.onCreateOptionsMenu(menu);
    }

    public void setupWifiScanner() {

        // Setup the wifi scanner by using the Wifinet class.

        mWifinet = new Wifinet();
        mWifinet.setupWifiScanner(mContext, mAct, mainText);
    }

    public boolean notEqualStrings(String s, String s2){
        return (!(s.equals(s2)));
    }

    private class MyBrowser extends WebViewClient {

        /* This belongs in httpauthhandler
        @Override
        public void proceed(String username, String password){
           //
            debug("   proceed");
        }
        */

//        @Override
//        public void onReceivedHttpAuthRequest(WebView view,
//                                              HttpAuthHandler handler,
//                                              String host,
//                                              String realm){
//           // handler.proceed(mUsrName, mPassC);
//            debug("received http");
//        }

        /*
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view,
            WebResourceRequest request) {

            debug("intercept");
            return new WebResourceResponse(contentTypeValue, encodingValue, responseInputStream);
        }
        */

        @Override
        public void onLoadResource (WebView view, String url) {
            super.onLoadResource(view, url);

            // All javascripts go thru here.

            debug("onLoadResource "+url);

            String extension = MimeTypeMap.getFileExtensionFromUrl(url);

            debug("   extension="+extension);

        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            debug("error received");
        }

        @Override
        public void	onReceivedLoginRequest(WebView view, String realm, String account, String args) {
            super.onReceivedLoginRequest(view, realm, account, args);
            debug("login request received");
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
            super.onReceivedSslError(view, handler, error);
            debug("SSL error");
        }

        //@Override
        public boolean oldshouldOverrideUrlLoading(WebView view, String url) {

            // Write diagnostic output.

            debug("   "+url);
            debug("   len="+url.length());

            // Load the url and return true.

            view.loadUrl(url);
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // Local variables
            HashMap<String,String> map;
            String url2;

            // Instantiate local variables

            map=new HashMap<String,String>();

            // Pass the authentication token we previously received
            // thru Azure onto the web application.  This is done
            // with a http request header called "Authentication".

            //    map.put("Authentication", mLogin.mToken);
//            map.put("Authorization", mLogin.mToken);
            map.put("Authorization", "Bearer "+mLogin.mToken);

            // Write diagnostic output.

            debug("MyBrowser add auth token");
            debug("   "+url);
            debug("   len="+url.length());

            // Enable javascript and dom

            view.getSettings().setJavaScriptEnabled(true);
            view.getSettings().setDomStorageEnabled(true);

            // If we set url to "http://dmi.dk" a loop arises whereby
            // this method is repeatedly called.

            url2="http://www.dmi.dk/";
            mRedirects++;
            //if (notEqualStrings(url, url2)) {
            if (mRedirects==1) {
                debug("   return true");
                //debug("   "+url+" not equal to "+url2);
                //url = url2;
                //view.loadUrl(url, map);

                // We cannot use postUrl because we do not
                // have any post data.

                byte[] postData;
                String s;

  //              debug("   use POST with client id");
                s = "client_id="+mLogin.clientId+"&token="+mLogin.mToken;
    //            debug("   POST: "+s);
                postData = s.getBytes();

      //           view.postUrl(url, postData);

                // It does not change anything using loadUrl(url)
                // versus loadUrl(url,map).



                view.loadUrl(url, map);

                return true;
                }

            else {
                debug("   return false");
                //view.loadUrl(url, map);
                //view.loadUrl(url);
                return false;
            }
        }

/*
        @Override
        public WebResourceResponse shouldInterceptRequest(
            WebView view,
            WebResourceRequest request) {
       //     public WebResourceResponse shouldInterceptRequest(WebView _view, String _url)
         //   {
                URL url;
                Uri uri;
                try {
                  //  url = new URL(_url);
                    uri = request.getUrl();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                }

                HttpURLConnection urlConnection;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                WebResourceResponse response = null;
                try {

                    response = new WebResourceResponse( urlConnection.getContentType(),
                            urlConnection.getContentEncoding(),
                            urlConnection.getInputStream() );
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }

                return response;
            }
*/

/*
            try {

                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("MY-CUSTOM-HEADER", "header value");
                httpGet.setHeader(HttpHeaders.USER_AGENT, "custom user-agent");
                HttpResponse httpReponse = client.execute(httpGet);

                PreferenceActivity.Header contentType = httpReponse.getEntity().getContentType();
                PreferenceActivity.Header encoding = httpReponse.getEntity().getContentEncoding();
                InputStream responseInputStream = httpReponse.getEntity().getContent();

                String contentTypeValue = null;
                String encodingValue = null;
                if (contentType != null) {
                    contentTypeValue = contentType.getValue();
                }
                if (encoding != null) {
                    encodingValue = encoding.getValue();
                }

                return new WebResourceResponse(contentTypeValue, encodingValue, responseInputStream);
            } catch (ClientProtocolException e) {
                //return null to tell WebView we failed to fetch it WebView should try again.
                return null;
            } catch (IOException e) {
                //return null to tell WebView we failed to fetch it WebView should try again.
                return null;
            }
            */

        public void onReceivedHttpError (WebView view, WebResourceRequest request,
                                 WebResourceResponse errorResponse) {
            debug("http error");
            super.onReceivedHttpError(view,request,errorResponse);
        }

        public void onPageStarted (WebView view, String url, Bitmap favicon) {
            debug("page started: "+url);
            super.onPageStarted(view,url,favicon);

        }
    }

    WebView mWebView;

    public void startWebsite4() {

        // Here we use Webview and the ability to add
        // http headers with the loadUrl method.

        // One of the stakeholders in the project, Lars
        // Hammer, would like the app to NOT display an URL
        // address field.  This can be accomplished using
        // a WebViewClient.  This however creates a new
        // problem.  It causes the authentication to
        // malfunction.  The problem seems to be that the
        // application makes at least one HTML redirect statement.

        // In principle presumably it should be sufficient with
        // having the authentication token on the initial
        // load of the web application, but perhaps it is
        // required on the following ones as well.

        // It is possible that authentication fails if we
        // add the authentication token to the reload sites.

        // Perhaps we can add an authentication header to
        // the loaded websites.  We tried that with

        //    shouldOverrideUrlLoading

        // This method is indeed called.  We then attach the
        // authorization header, but it has no effect.

        // The urls seems like they may be cut off.  The manual
         // testprocedure does not give the same urls, so we cannot
        // learn from that.

        // If in the shouldOverrideUrlLoading we simply return
        // false and do not set any url, authentication still fails.

        // Local variables

        HashMap<String,String> map;

        // Instantiate local variables

        mRedirects=0;
        mWebView=new WebView(this);
        map=new HashMap<String,String>();

        debug("start website 4");

        // Allow cookies

        CookieManager.getInstance().setAcceptCookie(true);

        // Pass the authentication token we previously received
        // thru Azure onto the web application.  This is done
        // with a http request header called "Authentication".
        // According to wikipedia there is no http header called
        // "Authentication" but one called "Authorization" used
        // for the so-called "basic authorization".

        // According to RFC 7235 section 4.2 the http header
        // name is "Authorization".
        // This is also the method described by Microsoft
        // in the paper with the title

        //    “Authorization code grant flow”

        // with last update 2015-09-16.

        //map.put("Authentication", mLogin.mToken);

        if (mLogin.mToken==null) {
            //
            debug("mToken is null");
            return;
        }

        map.put("Authorization", "Bearer "+mLogin.mToken);
        decodeClaims(mLogin.mToken);

        // Enable javascript and dom

        // Javascript is used by TV 2 intranet Tango.
        mWebView.getSettings().setJavaScriptEnabled(true);

        //w.getSettings().setDomStorageEnabled(true);

        // Do not display an URL address field in the webview.
        // Normally a webview should not contain an URL address field.
        // Apparently a URL address field is displayed if the
        // application makes an HTML redirect command.

        // If the following statement is commented out, authentication
        // works, but you get a window with an URL field.

        // We see no change regardless of where we place this call.

        // There is a difference between using a browser and
        // webviewclient with respect to javascript errors.
        // The browser will silently accept javascript errors
        // and javascript mime errors.  The webviewclient will
        // refuse to execute javascripts classified as text/html.

        mWebView.setWebViewClient(new MyBrowser());

        // webchromeclient will also
        // refuse to execute javascripts classified as text/html.
        //mWebView.setWebChromeClient(new WebChromeClient());

        // Display the web application as a WebView.

        mWebView.loadUrl(mService, map);

        // Display the webview

        setContentView(mWebView);
    }

    private void loadJavaScript() {
        // Local variables

        HashMap<String,String> map;

        // Instantiate local variables

        map=new HashMap<String,String>();

        debug("loadJavaScript");

        map.put("Authorization", "Bearer "+mLogin.mToken);

        String s;

        // This script has "app is not defined" error.
        s="https://tangotest-tv2.msappproxy.net/scripts/app/filters/dateNowFilter.min.js";

        // According to android, this script has mime type text/html and should not
        // be executed.
        s="https://tangotest-tv2.msappproxy.net/scripts/app/directives/diaryList.min.js";

        mWebView.loadUrl(s, map);

    }

    private void decodeClaims(String mAccessToken) {
        //Get claims out of access token (content between 2nd and 3rd periods)
        int firstIndex = mAccessToken.indexOf(".");
        int secondIndex = mAccessToken.indexOf(".", firstIndex+2);
        String claims = mAccessToken.substring(firstIndex + 1, secondIndex);
        //Decode base64 URL ended claims
        byte[] data = Base64.decode(claims, Base64.URL_SAFE);
        try {
            String text = new String(data, "ASCII");
            //Display claims on screen
            debug(text);
            JSONObject jObject = new JSONObject(text);
            //Get and display the logged in user name
            debug("Logged in as " + jObject.getString("unique_name"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "Error decoding claims: " + e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error converting to json: " + e.getMessage());
        }

    }

    public void showIntra(){
        String url;

        debug("showIntra");

        //url = "http://intra.tv2.dk";
        url = "http://intra.tv2local.dk";
        url = "https://tangotest-tv2.msappproxy.net/";
        url = Parameters.mService;

        //url = "http://tv2.dk";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        //startActivity(i);
        //startActivity(mAzureIntent);
        //execLoggedIn();
        startWebsite4();

        // This call is a test to see what the javascript file looks like.
        // The javascript will be visible on the device.
        // loadJavaScript();

    }

    // onMenuItemSelected(featureid,menuitem) cannot be overridden
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int x;

        // We get here when the user clicks the "Refresh" menu option.

        x = item.getItemId();
        Log.i(TAG, "ready to select option " + x);

        if (x==1) {

            // Inform the user we start scanning wifi networks.

            mainText.setTextSize(40);
            mainText.setText("Starting Scan");

            // Setup the wifi scanner.

            setupWifiScanner();
        }

        if (x==2) {

            // Display TV2 Intra net browser

            mainText.setTextSize(40);
            mainText.setText("Starting Intra");
            showIntra();
        }

        if (x==3) {

            // Display received Urban Airship messages

            mainText.setTextSize(40);
            mainText.setText("Starting Message center");
            mBuffer.showMessages();
        }

        if (x==4) {

            // Test fragment view pager
            setupNavigation();

        }
        if (x==5) {

            // Stop the app completely

            debug("stop completely");
            stopCompletely();

        }
        return super.onOptionsItemSelected(item);
    }

    public void stopCompletely() {

        // Stop all background threads.  We do not have any.

        // Stop the Urban Airship receiver

        stopReceiver();

        // Stop the user interface thread.
        // finishAndRemoveTask requires api level 21

        //finishAndRemoveTask();
        debug("super.finish");
        super.finish();
    }

    public void setupButton() {

        // Create a button and an "on click" listener method for the button.

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        if (mFab != null) {
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
        else {
            Log.e(TAG, "Could not find fab button");
        }
        Log.i(TAG, "Button setup done");
    }

    public void setupUserInterface() {

        // Setup a text view, a button, and a toolbar.
        // Set content view.

        Log.i(TAG, "Setup user interface");
        debug("Setup user interface");
        debug("   loc 2");
        mainText = new TextView(mContext);
        setContentView(mainText);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        setupButton();
    }

    private void attemptToGetPermissions(Activity act) {

        // Get GPS permissions and the other permissions we need.
        // Use the class Permissions.

        Permissions permissions;

        permissions = new Permissions();
        permissions.getPermissions(act);
    }

    private void basicOnCreateSetup() {

        // Set some user interface member variables like context, activity,
        // and so on.

        Log.i(TAG, "Starting up");
        mContext = this;
        mAct = (Activity) mContext;

        // Setup parameters using the class Parameters.
        //new Parameters();
        Parameters.Parameters();

        mService=Parameters.mService;

        // We must instantiate Wifinet early because onPause may be
        // called early.
        mWifinet = new Wifinet();
        //setContentView(R.layout.activity_main);
    }

    private void debug(String s){
        //
        mWriteLogfile.writeToFile(s);
    }

    private void establishLogfileSystem(){

        // Name of logfile should be logfile.txt.
        // A better name might be logfile.dat, but the
        // Mac computer cannot figure out how to run an
        // editor on a file that ends with ".dat".

        mWriteLogfile=new WriteLogfile(mContext,"logfile.txt");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        debug("Result received");

        // Handle Microsoft Azure login

        /*
        These codes are directly from the Microsoft source code
        from github.

        public static final int BROWSER_CODE_CANCEL = 2001;
        public static final int BROWSER_CODE_ERROR = 2002;

        * Flow complete.
        public static final int BROWSER_CODE_COMPLETE = 2003;

        * Broker returns full response.
        public static final int TOKEN_BROKER_RESPONSE = 2004;

        * Webview throws Authentication exception. It needs to be send to
        * callback.
        public static final int
                BROWSER_CODE_AUTHENTICATION_EXCEPTION = 2005;

        * CA flow, device doesn't have company portal or azure authenticator installed.
        * Waiting for broker package to be installed, and resume request in broker.
        public static final int BROKER_REQUEST_RESUME = 2006;

        public static final int BROWSER_FLOW = 1001;
        public static final int TOKEN_FLOW = 1002;
        public static final int BROKER_FLOW = 1003;
        */

        if (mLogin.mAuthContext != null) {
            debug("   Pass auth onto Azure");
            debug("      requestCode=" + requestCode);
            debug("      resultCode=" + resultCode);
            if (requestCode == 1001) {
                debug("      requestCode 1001 is good");

            }
            if (resultCode == 2003) {
                debug("      resultCode 2003 is good");

            } else
                debug("      Bad resultCode");

            mAzureIntent = data;
            if (data == null)
                debug("      Intent data is null");
            else {
                debug("      Intent data is not null");
                debug("      " + data.toString());
            }

            mLogin.mAuthContext.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startAzure() {
        //
        //mExecute = new Execute(mContext);

        mLogin = new Login(mContext, mainText, mService);
    }

    public void setupNavigation() {
        //
        debug("setupNavigation");
        mNavigation = new Navigation(mContext, mAct);
        debug("   setupNavigation done");
        //mNavigation = new Navigation();
        //Navigation.Navigation(mContext, mAct);
       // mNavigation.test();

    }

    public void establishMainContext() {
        //
        mMainContext = new MainContext(mContext);
    }

    @Override
    protected void onPause() {

        // We must unregister the wifi listener before the app stops.
        // Do it here.

        //mWifinet.unregister(mAct);
        super.onPause();
        Log.i(TAG, "onPause");
        debug("onPause");
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        Log.i(TAG, "onDestroy");
        debug("onDestroy");

        // This call is necessary to make sure
        // the android system does not keep old data
        // in the message center buffer.
        System.exit(0);
    }

    @Override
    public void onBackPressed() {

        // The default behaviour is to destroy the
        // activity.  We want to continue, so do NOT
        // call super.onBackPressed();

        // However if we continue, we block the screen
        // for other apps.  Use moveTaskToBack to put
        // the app in the background.  It will come back
        // to the foreground when the user touches the
        // app icon.

        //super.onBackPressed();

        // The moveTaskToBack apparently stops
        // the activity, so it is safe to restart it later,
        // but it is a mess to do because we probably
        // loose all our data.  It probably means the
        // app is started from scratch.

        // moveTaskToFront requires REORDER permission.

        moveTaskToBack(true);

        Log.i(TAG, "onBackPressed");
        debug("onBackPressed");
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        // registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();

        debug("onResume");

    }

    public void giveEarlyDebugInfo() {
        debug("--------------------------------------------------------------------");
        debug("After parameters loaded mService="+mService);
        debug("apiLevel="+Parameters.apiVersion);
        debug("deviceVersion="+Parameters.deviceVersion);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Urban Airship must be initialized in an application class, not
        // here in the main activity class.

        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting up");
        basicOnCreateSetup();
        establishMainContext();
        establishLogfileSystem();
        giveEarlyDebugInfo();
        //setupNavigation();
        setupUserInterface();
        attemptToGetPermissions(mAct);
        setupNotificationSystem();
        startAzure();
   }

}
