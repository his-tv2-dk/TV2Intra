package dk.tv2.intra;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;
import com.microsoft.aad.adal.AuthenticationResult;

/**

 Created by henrik on 21/04/2016.
 
 Comment here

 */
public class Buffer {

    // Member variables

    Context mContext;
    TextView mMainText;
    WriteLogfile mWriteLogfile;
    String mFullMessage;
    Activity mAct;

    public Buffer(Context context,
        TextView tv,
        Activity act) {

        // Member variables

        mContext = context;
        mAct = act;
        mMainText=tv;

        establishLogfileSystem();
        debug("Buffer class loaded");
        debug("   Buffer started");
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

    public void showMessage(int n) {
        String s;

        // The latest, most new, message is in the buffer
        // at the index bufferIndex.
        s = IntentReceiver.buffer[n];

        debug("   " + n + "  " + s);

        mMainText.setTextSize(30);

        // Put 2 blanks in front of the message because the
        // Samsung device is curved.
        s="  "+s;

        mFullMessage=mFullMessage+"\n"+s;
        mMainText.setText(mFullMessage);
    }

    public void showMessages(){
        int i;
        int k;

        k=IntentReceiver.bufferIndex;

        debug("Buffer.showMessages "+k);

        // We want to display the newest, most recent, message
        // first.

        mFullMessage="";

        for (i=1;i<=IntentReceiver.numberOfMessagesReceived;i++) {
            showMessage(k);

            // Count down to get to the message that came just before
            // the last one.
            k=k-1;

            if (k==0)
                k=IntentReceiver.bufferIndexMax;
        }
        mAct.setContentView(mMainText);
    }

}
