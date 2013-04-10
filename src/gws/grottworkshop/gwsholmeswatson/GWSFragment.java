package gws.grottworkshop.gwsholmeswatson;

import org.holoeverywhere.app.Fragment;

import org.holoeverywhere.app.ListFragment;

import com.actionbarsherlock.R;
import com.actionbarsherlock.view.MenuItem;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

/**
 * The Class GWSFragment, borrowed from hnilsen.
 * 
 * @author fredgrott
 */
public class GWSFragment extends Fragment {

	private static Context context;
	
	
	/**
	 * Checks if is online.
	 *
	 * @param context the context
	 * @return true, if is online
	 */
	static public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
	
	/**
     * Shorthand-function for doing something ASyncTask-y while online only
     *
     * @param context Applikasjonskonteksten
     */
    public static void updateData(Context context) {
        if(isOnline(context)) {
            new updateDataTask(context).execute();
        }
    }

	
	public static void updateDataWithABSRefresh(Context context,
            MenuItem item,
            GWSSecondListFragment.OnFragmentUpdateListener callback,
            int position) {
         if(isOnline(context)) {
           new updateDataWithABSRefreshTask(context, item, callback, position).execute();
         }
     }
	
	 private static class updateDataWithABSRefreshTask extends AsyncTask<Void, Void, Void> {
	        GWSSecondListFragment.OnFragmentUpdateListener callback;
	        Context context;
	        MenuItem item;
	        int position;

	        private updateDataWithABSRefreshTask(Context context, MenuItem item, GWSSecondListFragment.OnFragmentUpdateListener callback, int position) {
	            super();
	            this.context = context;
	            this.item = item;
	            this.callback = callback;
	            this.position = position;
	        }

	        @Override
	        protected void onPreExecute() {
	            item.setActionView(R.layout.fbs_menuitem_action_refresh);
	        }

	        @Override
	        protected Void doInBackground(Void... voids) {
	            if(isOnline(context)) {
	                // do fancy download stuff here
	            }

	            return null;
	        }

	        @Override
	        protected void onPostExecute(Void v) {
	            callback.onFragmentUpdate(position, true);
	            item.setActionView(null);
	        }
	    }

	 /**
	     * Do something and don't update the UI
	     */
	    private static class updateDataTask extends AsyncTask<Void, Void, Void> {
	        Context context;

	        private updateDataTask(Context context) {
	            super();
	            this.context = context;
	        }

	        @Override
	        protected Void doInBackground(Void... voids) {
	            if(isOnline(context)) {
	                // do fancy online stuff here
	            }

	            return null;
	        }
	    }

}
