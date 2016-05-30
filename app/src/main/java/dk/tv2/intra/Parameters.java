package dk.tv2.intra;

import android.os.Build;

/**
 * Created by henrik on 12/05/2016.
 */
public class Parameters {
    //
    static String mUserid;
    static String mService;
    static String clientId;
    static String clientSecret;
    static String tenant;
    static String redirect;
    static int apiVersion;
    static String deviceVersion;
    IntentReceiver mIntentReceiver;

    static public void Parameters() {
        //
        int website;
        apiVersion= Build.VERSION.SDK_INT;
        deviceVersion= Build.VERSION.RELEASE;


        website=1;

        if (website==1) {

            // The original TV 2 Tango test website

            mService = "https://tangotest-tv2.msappproxy.net/";
            redirect = "https://tangotest-tv2.msappproxy.net/";
            mUserid="his@tv2.dk";
            tenant = "ad00f650-cdf1-41b5-8ff1-88b7c9650d3e";
            clientId="53a2d2cf-6262-4ac6-a521-9488fd0a9246";
        }

        if (website==2) {

            // A Sharepoint website at a free server developed
            // by Henrik.

            // using https gives SSL error
            mService="http://tv2.tv2.cloudappsportal.com/SitePages/p3.htm";

            mUserid="his@tv2.cloudappsportal.com";
        }

        if (website==3) {

            // The Intranet website developed by Henrik at TV2.

            mService="https://tv2.sharepoint.com/sites/mobilintra/";
            mService="https://tv2.sharepoint.com/sites/mobilintra/SitePages/p5.aspx";
            //mService="https://tv2.sharepoint.com/";
            //mService="mobilintra";
            //mService="dk.tv2.intra";
            //mService="intra.tv2.dk";

            redirect="https://tv2.sharepoint.com/sites/mobilintra/SitePages/p5.aspx";
            redirect="https://tv2.sharepoint.com/";
            //redirect="https://tv2.sharepoint.com/sites/mobilintra/";

            // This leads to bad request.
            //mService="https://tv2.sharepoint.com/sites/mobilintra";

            mUserid="his@tv2.dk";

            tenant = "C4488752-F866-40E4-8148-DCAE678AF8DC";

            //clientId="g_63edd7f4_5907_4aff_a332_d6ea0b3e104a";

            clientId="D0814CAE-DDB5-4365-8331-F149639A5B99";

            clientId = "C4488752-F866-40E4-8148-DCAE678AF8DC";

            //clientId="g_63edd7f4_5907_4aff_a332_d6ea0b3e104a";

            //tenant = "D0814CAE-DDB5-4365-8331-F149639A5B99";

            // The following items created by hiy 2016-05-13.
            clientId="444d0269-a437-4ceb-9709-9b18cee9802c";

            //clientId="c4871703-7067-4490-a251-9f29063353f7";
            //clientId="41b6389c-4841-4fcd-8a76-2a5164d7b058";

            clientSecret="KiHR2qYvLctDzW1XzqNuiRkd4Xnjd9fVu1v3oqnwnkY=";

            // this gives us an error message that client id and
            // resource must not be identical.
            //mService=clientId;

            // This gives the error "bad request: id not found in directory tenant"
            //clientId=clientSecret;


            // tenant id is the same as the original site.
            // it is also called "directory".
            tenant = "ad00f650-cdf1-41b5-8ff1-88b7c9650d3e";

            // We get "bad request" with this old client id.
            //clientId="53a2d2cf-6262-4ac6-a521-9488fd0a9246";

        }

    }

}
