package dk.tv2.intra;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;

/**
 * Created by henrik on 02/05/2016.
 */
public class MessageCenterFragment extends Fragment {

    // We could not find a guide on how View Pager and fragments
    // work, so we wrote the following guide.

    // This is the overall process.

    // The Navigation class is started.

    // The Message Center Fragment constructor without arguments
    // is called.  This happens because the Navigation class
    // loads the Message Center Fragment class.

    // The Navigation class calls the init method in the Message
    // Center Fragment class.

    // IntraPageAdapter in the Navigation class is called.

    // getItem(0) in the adapter is called.


    Context mContext;
    WriteLogfile mWriteLogfile;
    static WriteLogfile msWriteLogfile;

    public void init(Context c) {

        mContext = c;

        establishLogfileSystem();

        debug("   Message Center Fragment started by init");


    }

    public void init2(int c) {

        debug("Message Center Fragment started 2");


    }

    private void establishLogfileSystem(){

        // Name of logfile should be logfile.txt.
        // A better name might be logfile.dat, but the
        // Mac computer cannot figure out how to run an
        // editor on a file that ends with ".dat".

        mWriteLogfile=new WriteLogfile(mContext,"logfile.txt");
        msWriteLogfile=new WriteLogfile(mContext,"logfile.txt");
    }

    private void debug(String s) {
        //
        mWriteLogfile.writeToFile(s);
    }

    static private void debugs(String s) {
        //
        msWriteLogfile.writeToFile(s);
    }


    public MessageCenterFragment() {

        // This constructor is required by android.
        // In an example in the developer manual, this
        // constructor is empty.

        mContext = MainContext.context;
        establishLogfileSystem();

        debug("   MessageCenterFragment android constructor");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
        ViewGroup container, Bundle savedInstanceState) {

        TextView tv;
        View v;

        /*
        v = inflater.inflate(R.layout.first_frag, container, false);

        tv = (TextView) v.findViewById(R.id.tvFragFirst);
*/

        debug("onCreateView: enter");
        v=new View(mContext);

        tv = new TextView(mContext);

        //tv.setText(getArguments().getString("msg"));
        tv.setText("x26");
        debug("onCreateView: return x26");

        return v;
        }
/*
    public static MessageCenterFragment newInstance(String text) {

        MessageCenterFragment f = new MessageCenterFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
*/

    public static Fragment newInstance(String text) {

        debugs("new instance");

        Fragment f = new MessageCenterFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }


}
