package dk.tv2.intra;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by henrik on 02/05/2016.
 */
public class Navigation extends FragmentActivity {

    // Local variables

    ViewPager mViewPager;
    static Context mContext;
    Context mContext2;
    Activity mAct;
    //DemoCollectionPagerAdapter d;
    FragmentManager mFragmentManager;
    static WriteLogfile mWriteLogfile;
    MessageCenterFragment mMessageCenterFragment;
   //Fragment mMessageCenterFragment;

    public Navigation(Context c,
        Activity act) {

        // Initialize local variables.
        mContext=c;
        mContext2=c;
        mAct=act;

        establishLogfileSystem();

        debug("navigation starting");

        // Start the ViewPager system

        // Start fragment 1, which is the message center fragment.
        mMessageCenterFragment = new MessageCenterFragment();

        debug("   Navigation: call init");
        mMessageCenterFragment.init(mContext);

        //mMessageCenterFragment.init2(0);

        // Create our ViewPager instance.

        mViewPager=new ViewPager(mContext);

        // It is apparently not necessary to set an ID on
        // the ViewPager.

        //mViewPager.setID(1);

        // Create Fragment Manager

        mFragmentManager=getSupportFragmentManager();

        // Set the Pager adapter

        //mViewPager.setAdapter(new DemoCollectionPagerAdapter(mFragmentManager));
        mViewPager.setAdapter(new IntraPageAdapter2(mFragmentManager));

        // Display the View Pager

        mAct.setContentView(mViewPager);

        // mAct.addView(mViewPager);

    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.

    public class
        //DemoCollectionPagerAdapter
        IntraPageAdapter
        extends FragmentStatePagerAdapter {

        public
            //DemoCollectionPagerAdapter
            IntraPageAdapter
            (FragmentManager fm) {
            super(fm);

            debug("   IntraPageAdapter called with Fragment Manager fm");
        }

        @Override
        public Fragment getItem(int i) {
            debug("IntraPageAdapter getItem called with "+i);

            mMessageCenterFragment = null; // prevent compiler warning
            if (i == 0) {

                // Start fragment 1, which is the message center fragment.

                debug("   getitem alloc messagecenterfragment");

                mMessageCenterFragment = new MessageCenterFragment();

                debug("   getItem call init");

                mMessageCenterFragment.init(mContext);
            }

/*
            Fragment fragment = new DemoObjectFragment();

            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
            fragment.setArguments(args);
            */

//            return fragment;
            return mMessageCenterFragment;
        }

        @Override
        public int getCount() {
            //

            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //
            return "OBJECT " + (position + 1);
        }
    }

    public class
        IntraPageAdapter2
        extends FragmentPagerAdapter {
        public IntraPageAdapter2 (FragmentManager fm) {
            super(fm);

            debug("   IntraPageAdapter2 called with Fragment Manager fm");
        }

        @Override
        public Fragment getItem(int i) {
            debug("IntraPageAdapter getItem called with "+i);

            mMessageCenterFragment = null; // prevent compiler warning
            if (i == 0) {

                // Start fragment 1, which is the message center fragment.

                debug("   getitem alloc messagecenterfragment");

                mMessageCenterFragment = new MessageCenterFragment();

                debug("   getItem call init");

                mMessageCenterFragment.init(mContext);
            }

/*
            Fragment fragment = new DemoObjectFragment();

            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
            fragment.setArguments(args);
            */

//            return fragment;
            return mMessageCenterFragment;
        }

        @Override
        public int getCount() {
            //

            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //
            return "OBJECT " + (position + 1);
        }
    }


    private void establishLogfileSystem(){

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

    // Instances of this class are fragments representing a single
    // object in our collection.

    public static class DemoObjectFragment extends Fragment {
        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            TextView v;
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            /*
            View rootView = inflater.inflate(

                    R.layout.fragment_collection_object, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    Integer.toString(args.getInt(ARG_OBJECT)));
            */
            debug("fragment");

            v = new TextView(mContext);
            return (View)v;
        }

    }

}
