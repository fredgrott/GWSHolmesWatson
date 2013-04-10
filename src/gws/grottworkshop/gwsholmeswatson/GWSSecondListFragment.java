package gws.grottworkshop.gwsholmeswatson;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ListView;

import com.actionbarsherlock.R;
import com.viewpagerindicator.TabPageIndicator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * Borrowed from fragment booststrap, hnilsen
 * @author fredgrott
 *
 */
public class GWSSecondListFragment extends Fragment {

	OnFragmentUpdateListener mCallback;
    private Context context;
    private int mExampleId = 0;

    /**
     * fragment update interface
     */
    public interface OnFragmentUpdateListener {
        public void onFragmentUpdate(int position, boolean forceupdate);
        public int getmExampleId();
    }

    /**
     * make sure the activity has implemented the fragmentupdatelistener
     * @param activity our activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnFragmentUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
            + " must implement OnFragmentUpdateListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container != null) {
            container.removeAllViews();
        }

        mExampleId = getArguments().getInt("mExampleId");
        context = getActivity().getApplicationContext();

        View view = inflater.inflate(R.layout.fbs_viewpager_second, container, false);
        createPagerView(view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mExampleId", mExampleId);
    }

    public void createPagerView(View view) {
        ViewPager awesomePager = (ViewPager) view.findViewById(R.id.secondpager);
        awesomePager.setAdapter(new ExamplePagerAdapter());

        TabPageIndicator titleIndicator = (TabPageIndicator)view.findViewById(R.id.titles);
        titleIndicator.setViewPager(awesomePager);
    }

    public int getmExampleId() {
        return this.mExampleId;
    }

    public static GWSSecondListFragment newInstance(int exampleId) {
        GWSSecondListFragment f = new GWSSecondListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("mExampleId", exampleId);
        f.setArguments(args);

        return f;
    }

    /**
     * ExamplePagerAdapter initializes the ListView's that we want to
     * scroll between. It inflates the listviews through a
     * separate inflater
     */
    private class ExamplePagerAdapter extends PagerAdapter {
        String[] pages = {"First page", "Second page", "Third page"};
        int[] pages_strings = { R.array.first_list, R.array.second_list, R.array.third_list };
        String pageContent;
        private ExamplePagerAdapter() {
        }

        @Override
        public int getCount() {
            return pages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == (ListView)o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            ((ViewPager) container).removeView((View) view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // create a new listview
            ListView listView = new ListView(context);
            ListAdapter adapter = new GWSSecondListAdapter(pages_strings[position], context);

            // bind the adapter to the lists view
            listView.setAdapter(adapter);

            ((ViewPager) container).addView(listView,0);

            return listView;
        }

        public String getPageTitle(int position) {
            return pages[position];
        }
    }
}
