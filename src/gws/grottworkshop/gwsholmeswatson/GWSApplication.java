package gws.grottworkshop.gwsholmeswatson;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;

/**
 * The Class GWSApplication,ie on main thread, 
 * to set up the singletons we need for
 * high  performance based applications.
 * 
 * To use you extend this class in your own project 
 * and remember you can override most of the methods.
 * 
 */
public class GWSApplication extends Application {
	
	private HashMap<String, WeakReference<Context>> contextObjects = new HashMap<String, WeakReference<Context>>();

    /**
     * Gets the active context.
     *
     * @param className the class name
     * @return the active context
     */
    public synchronized Context getActiveContext(String className) {
        WeakReference<Context> ref = contextObjects.get(className);
        if (ref == null) {
            return null;
        }

        final Context c = ref.get();
        if (c == null) // If the WeakReference is no longer valid, ensure it is removed.
            contextObjects.remove(className);

        return c;
    }

    public synchronized void setActiveContext(String className, Context context) {
        WeakReference<Context> ref = new WeakReference<Context>(context);
        this.contextObjects.put(className, ref);
    }

    public synchronized void resetActiveContext(String className) {
        contextObjects.remove(className);
    }
	
	@Override
	public ApplicationInfo getApplicationInfo() {
		// TODO Auto-generated method stub
		return super.getApplicationInfo();
	}
	
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}
	/**
	 * This no-op by default as it is not guaranteed 
	 * to always work. Is triggered by a back button event 
	 * that exits the application.
	 */
	public void onClose() {
        // NO-OP by default
    }
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

}
