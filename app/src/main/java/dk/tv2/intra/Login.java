package dk.tv2.intra;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.microsoft.aad.adal.ADALError;
import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationException;
import com.microsoft.aad.adal.AuthenticationResult;
import com.microsoft.aad.adal.AuthenticationSettings;
import com.microsoft.aad.adal.Logger;
import com.microsoft.aad.adal.PromptBehavior;

/**
 * Created by henrik on 15/04/2016.
 */
public class Login {
    Context mContext;
    Activity mActivity;
    WriteLogfile mWriteLogfile;
    TextView mMainText;
    Execute mExecute;
    String mService;
    String mToken;
    String clientId;

    private static final String TAG = MainActivity.class.getSimpleName();

    // For Microsoft Azure
    AuthenticationContext mAuthContext;

    /*

    The Azure system uses the Oauth2 authentication method
    described in RFC 6749.

    The RFC 6749 is 75 pages and quite readable and very informative.
    If you have a problem with this system, take a look in the
    RFC 6749.  Below we discuss briefly some aspects of the
    Oauth2 protocol.

    We differentiate between authentication token and access token.

    The authentication token is the identifier strings you obtain
    from the IT department.  The access token is a string you obtain
    when you ask the Azure system for it.

    At page 21 the RFC 6749 says "The client MUST use the HTTP
    "POST" method when making access token requests".

    There are 2 kinds of callback methods involved with the
    Azure system.

    1.  MainActivity.onActivityResult
    2.  Login.AuthenticationCallback onSuccess, onError

    It seems onActivityResult is only called in case of error.
    One might think the Intent passed to onActivityResult is
    the intent to be displayed, but that makes little sense
    as onActivityResult is only called in case of an error.


    It is quite difficult to get an overview of the Azure system
    because different papers indicate you should do different
    operations.

    For instance there is a paper "Getting started with Azure
    Mobile Engagement for android apps".  Is this paper relevant
    for our needs?
    It describes completely different operations  than other
    papers.  The papers are usually not dated, so you have no
    impression if a paper is outdated.

    There is a github "Azure samples" with examples on how
    to use Azure, but most of it is .NET, IOS, and other platforms
    not android.

    There is an android example github called "Azure-Samples
    /active-directory-android".  It says you must download
    something for Node.js and for .Net but why should we do
    that when we do not use javascript and .Net?

    In step 5 it tells you to download ADAL and add it to
    your Eclipse workspace.  But why should we do that when
    we do not use Eclipse?

    It says redirect_url is not used yet, but we have found
    the interface crashes if you do not supply a proper
    redirect_url.

    The "android app quickstart" says "Step5.  Add
    references to android adal to
    your project".  It says click for more info, and when
    you click, you get a message that Eclipse support has
    stopped.

    It says "add the project dependency for debugging in to your
    project settings".  What does that mean?  A google search
    did not help.

    It says

       4.  Create an instance of AuthenticationContext at
       your main Activity.

    We do that call in the class Login using the activity
    from the context of the caller, which is MainActivity.

    We do not know how to test that the call went well,
    but we do test for 2 exceptions.  We know those
    exceptions do not occur.

    There is a reference to "Android Native Client Sample"
    which is a github - it is not clear what that product does
    and where to find the call.

    The guide then goes to point 5.

       5.  Copy this code block to handle the end of
       AuthenticationActivity after user enters credentials
       and receives authorization code:
       onActivityResult

    We have onActivityResult in the MainActivity class.
    The guide says we must call the authentication context
    with the result - if we do not do it, we do not get the
    cancel error.

       6.  To ask for a token, you define a callback
       private AuthenticationCallback<AuthenticationResult> callback

    This callback method is also in the MainActivity class.


    We have searched for how to start the web application after we
    have received access token for 2 days or so and found very little
    useful information.  This would seem to indicate that it is not
    possible.  Presumably this is not the way it should be handled.
    It seems difficult or impossible to pass the access token along
    with an intent.  It does not seem much easier with a webview.

    */
    boolean extendedLogging;

    public Login(Context context, TextView tv,
        String service) {

        // Member variables
        mContext = context;
        mActivity = (Activity) context;
        mMainText=tv;
        mService=service;
        mToken=null;

        extendedLogging=false;

        // Microsoft has this system for debugging, but it would
        // be nice if you could simply get an understandable error message.
        //ApplicationInsights.setDeveloperMode(true);

        establishLogfileSystem();
        debug("Login class loaded");

        //Logger.Logger();

        //int n;
        // static problem n=Logger.getLogLevel();

        //mAndroidLogEnabled is private
        setupAzureLogger();



        // This call was available in an earlier version of ADAL.
        //new mAuthContext.ClientCredential(clientId, Parameters.clientSecret);

        mExecute=new Execute(mContext,mService);
        startAzure();
        debug("   Azure started");
    }

    public String safeSubstring(String s, int i, int end) {
        //
        int end2;

        if (end>s.length())
            end2=s.length();
        else
            end2=end;
        //debug("safeSubstring: "+end2);

        return s.substring(i,end2);
    }

    public void debugSplit(String s) {
        //
        String s2;
        String s3;
        int i;
        int lin;
        boolean moreToWrite;

        // We know the length of s is greater than 60,
        // so we need to write at least one line of text.

        moreToWrite=true;
        i=0;
        s3="Log from MS: "+s;
        lin=0;

        while (moreToWrite) {
            //

            lin++;
            s2=safeSubstring(s3, i, i+60);
            if (lin==1)
                debug(s2);
            else
                debug("   "+s2);
            i=i+60;
            moreToWrite=(i<s3.length());
        }

    }

    public void writeToOurLogfile(String message) {
        if (extendedLogging) {
            //
            if (message.length() > 60)
                debugSplit(message);
            else
                debug("Log from MS: " + message);
        }

    }

    public void setupAzureLogger() {
        //
        Logger.getInstance().setLogLevel(Logger.LogLevel.Verbose);

        // Setup a logger for the Microsoft Azure system.
        Logger.getInstance().setExternalLogger(new Logger.ILogger() {

            // We get called by the Microsoft Azure system
            // when it has something to log.

            @Override
            public void Log(String tag, String message, String additionalMessage,
                            Logger.LogLevel level, ADALError errorCode) {

                // Simply write the message into our logfile.
                writeToOurLogfile(message);
            }
        });
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

    // Microsoft Azure call back system

    public AuthenticationCallback<AuthenticationResult> callbackAzure =
        new AuthenticationCallback<AuthenticationResult>() {

            // Microsoft Azure error handler for authentication

            @Override
            public void onError(Exception exc) {
                debug("Authentication callback onError");
                if (exc instanceof AuthenticationException) {

                    // This error is NOT, repeat, NOT necessarily
                    // due to the user having cancelled the request.

                    // Look in the logfile.  It may say something like

                    // The+application+named+ad00f650-cdf1-41b5-8ff1-88b7c9650d3e+
                    // was+not+found+in+the+tenant+named+
                    // ad00f650-cdf1-41b5-8ff1-88b7c9650d3e.++This+can+
                    // happen+if+the+application+has+not+been+installed+by+
                    // the+administrator+of+the+tenant+or+consented+to+by+
                    // any+user+in+the+tenant.++You+might+have+sent+your+
                    // authentication+request+to+the+wrong+tenant.
                    //
                    // Note the message that you may have sent your
                    // authentication request to the wrong tenant.
                    // Our tenant is "ad00f650-cdf1-41b5-8ff1-88b7c9650d3e"
                    // We see the error message indicates that the tenant
                    // and the application have the same id.
                    // The tenant id is right, so something is wrong with
                    // the application id.

                    debug("   auth cancelled - message from Azure:");
                    debug("      "+exc.getMessage());
                    debug("      "+exc.getLocalizedMessage());
                    debug("      "+exc.getStackTrace());


                    debug("   The message from Azure may say \"User cancelled\" but");
                    debug("   this is not necessarily true.  Look in the logfile.");
                    mMainText.setText("Cancelled");
                    mMainText.setTextSize(40);
                    Log.d(TAG, "Cancelled");
                } else {
                    debug("   auth error");
                    mMainText.setText("Authentication error:" + exc.getMessage());
                    Log.d(TAG, "Authentication error:" + exc.getMessage());
                    }
                }

                // Microsoft Azure success handler for authentication

                @Override
                public void onSuccess(AuthenticationResult result) {
                    //mResult = result;
                    debug("Authentication callback onSuccess");

                    if (result == null || result.getAccessToken() == null
                            || result.getAccessToken().isEmpty()) {
                        debug("   empty token");
                        mMainText.setText("Token is empty");
                        Log.d(TAG, "Token is empty");
                    } else {
                        // request is successful
                        debug("   Success: passed");
                        debug("   auth successful");
                        debug("      "+result.getStatus());
                        Log.d(TAG, "Status:" + result.getStatus() + " Expired:"
                                + result.getExpiresOn().toString());
                        mMainText.setText("PASSED");
                        mMainText.setTextSize(40);
                        if (result.getIdToken()==null)
                            debug("      no ID token");
                        else
                            debug("      Userid: "+result.getUserInfo().getDisplayableId());
                        //items = new TodoListHttpService().getAllItems(result.getAccessToken());
                        String items = result.getAccessToken();
                        debug("items="+items);
                        mToken=items;
                        //mExecute.code(items);
                        }
                    }
            };

    private void configWebview() {
        // The error message
        //    "never saw a connection for the pid"
        // according to Stackoverflow means you need these configs.

        // The message
        //    Sending intent to cancel authentication activity
        // is delivered when
        //    hasCancelError(url)
        // and after that message Microsoft calls
        //    view.stopLoading();
        //    cancelWebViewRequest();
        // You can see this in
        //    shouldOverrideUrlLoading
        //
    //    webview.getSettings().setJavaScriptEnabled(true);
    //    webview.getSettings().setDomStorageEnabled(true);

    }

    public void startAzure() {
        String authority;
        String resource;
        String redirect;
        String userLoginhint;
        String tenant;
        String extraQueryParameters;

        debug("startAzure");

      //  getUserId();

        // Microsoft Azure
        // Authority is in the form of https://login.windows.net/yourtenant.onmicrosoft.com
        // This will use SharedPreferences as default cache
        //tenant="https://tangotest-tv2.msappproxy.net/";
        tenant = "ad00f650-cdf1-41b5-8ff1-88b7c9650d3e";
        tenant = Parameters.tenant;

        authority = "https://login.windows.net/"+tenant+".onmicrosoft.com";
        authority = "https://login.microsoftonline.com/"+tenant;
        try {
            mAuthContext = new AuthenticationContext(
                    mActivity,
                    //MainActivity.this,
                    authority, true);
        } catch (java.security.NoSuchAlgorithmException e) {
            debug("   azure failed");
            Log.e("", "Azure authentication failed");
        } catch (javax.crypto.NoSuchPaddingException e) {
            Log.e("", "Azure authentication failed with padding");
            debug("   azure failed 2");
        }
        debug("   Azure context requested for");
        debug("      "+authority);

        Logger.getInstance().setLogLevel(Logger.LogLevel.Verbose);

        // According to Microsoft this call is deprecated.
        //AuthenticationSettings.Instance.setSkipBroker(true);

        /*
        Explanation of the parameters:

        Resource is required and is the resource you are trying to access.

        Clientid is required and comes from the AzureAD Portal.

        You can setup redirectUri as your packagename.
        It is not required to be provided for the acquireToken call.

        PromptBehavior helps to ask for credentials to skip cache and cookie.

        Callback will be called after authorization code is exchanged for a token.
        */

        userLoginhint="";

        // The Microsoft documentation says you do not need to use
        // the redirect argument, but the login attempt fails and
        // says a redirect address is required.

        // You get an error from the login if using dk.tv2.intra.
        redirect="dk.tv2.intra";

        // This allows the login activity to get started.
        redirect="http://intra.tv2.dk";
        redirect="https://tangotest-tv2.msappproxy.net/";
        redirect=Parameters.redirect;

        resource="ad00f650-cdf1-41b5-8ff1-88b7c9650d3e";
        resource="dk.tv2.intra";
        resource="TV 2 Intra";
        resource="https://tangotest-tv2.msappproxy.net/";
        resource = Parameters.mService;
       // resource="mobilintra";

        clientId="53a2d2cf-6262-4ac6-a521-9488fd0a9246";
        clientId=Parameters.clientId;

        //debug("   redirect="+redirect);
        extraQueryParameters="";
        userLoginhint=Parameters.mUserid;


        // acquireToken starts interactive flow if required.
        // If prompt behaviour is auto it will remove
        // the refresh token from cache and fallback on
        // the user interface if mActivity is not
        // null.  Default is AUTO.

        // During this call you may be prompted for userid and
        // password on the device.

        /*
        This call is apparently not available anymore.

        Uri redirectURI;

        try {
            mAuthContext.AcquireToken(
                    resource,
                    clientId,
                    redirectURI
                    );
         */
        try {
            mAuthContext.acquireToken(
                //MainActivity.this,
                mActivity,
                resource,
                clientId,
                redirect,
                userLoginhint,
                PromptBehavior.Auto,  // Only require login if necessary
                // PromptBehavior.REFRESH_SESSION,
                //PromptBehavior.Always, // Force user to login
                extraQueryParameters,
                callbackAzure);
        }
        catch (RuntimeException e) {
            debug("   Azure acquire token failed with runtime error");
        }
        debug("   Azure acquire token issued with");
        debug("      dest="+resource);
        debug("      client="+clientId);
        debug("      redirect="+redirect);
        debug("   If you get the error message");
        debug("      No service namespace named "+tenant);
        debug("      it means the tenant code is wrong.");
        debug("   If the url is wrong you get an error message on");
        debug("      the device: Bad request.  The application named x was");
        debug("      not found on the tenant y.");
        debug("   If the password is wrong you get an error message on");
        debug("      the device.");
        debug("   If the client id is wrong you get the error message");
        debug("      \"We have received a bad request\"");
    }

}
