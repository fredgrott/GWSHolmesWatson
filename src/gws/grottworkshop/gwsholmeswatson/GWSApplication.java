package gws.grottworkshop.gwsholmeswatson;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.holoeverywhere.app.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.senab.bitmapcache.BitmapLruCache;





import android.content.Context;
import android.content.Intent;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Environment;


// TODO: Auto-generated Javadoc
/**
 * The Class GWSApplication,ie on main thread, 
 * to set up the singletons we need for
 * high  performance based applications.
 * 
 * To use you extend this class in your own project 
 * and remember you can override most of the methods.
 * 
 * Borrowing concepts/code from Kaeppler and Mottier and AOSP,
 * modifications and additions are also under Apache License 2.0
 * and by:
 * @author fredgrott
 * 
 */
public class GWSApplication extends Application {
	
	/** The context objects. */
	private HashMap<String, WeakReference<Context>> contextObjects = new HashMap<String, WeakReference<Context>>();

	/** The context. */
	private static Context context;
	
	private ExecutorService mExecutorService;
	
	/** The m low memory listeners. */
	private ArrayList<WeakReference<OnLowMemoryListener>> mLowMemoryListeners;
	
	private Logger GWSLOG = LoggerFactory.getLogger(GWSApplication.class);
	
	public static boolean isLandscape;
	
	private BitmapLruCache mCache;
	
	/**
     * Return the class of the home Activity. The home Activity is the main
     * entrance point of your application. This is usually where the
     * dashboard/general menu is displayed. You will supply that 
     * in the extension of this class by overriding this method with:
     * <code>
     * Public lass<?> getHomeActivityClass() {
     *     return YourHomeActivity.class;
     * }
     * </code>
     * 
     * @return The Class of the home Activity
     */
	public Class<?> getHomeActivityClass() {
        return null;
    }
	
	/**
	 * Gets the main application intent. Will be null if you 
	 * do not use and non-null if you override it in your 
	 * application class extending this application class 
	 * with whatever you fill in for method.
	 *
	 * @return the main application intent
	 */
	public Intent getMainApplicationIntent() {
        return null;
    }

	private static final int CORE_POOL_SIZE = 5;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "GWS App Thread #" + mCount.getAndIncrement());
        }
    };
    
    /**
     * Gets the executor,touse if we have a long running task.
     *
     * @return the executor
     */
    public ExecutorService getExecutor() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newFixedThreadPool(CORE_POOL_SIZE, sThreadFactory);
        }
        return mExecutorService;
        
        
    }
	
    /**
     * The listener interface for receiving onLowMemory events.
     * The class that is interested in processing a onLowMemory
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addOnLowMemoryListener<code> method. When
     * the onLowMemory event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OnLowMemoryEvent
     */
    public static interface OnLowMemoryListener {
        
        /**
         * Callback to be invoked when the system needs memory.
         */
        public void onLowMemoryReceived();
    }

	/**
	 * Instantiates a new gWS application.
	 */
	public GWSApplication() {
		mLowMemoryListeners = new ArrayList<WeakReference<OnLowMemoryListener>>();
	}
    
	/**
	 * Register on low memory listener.
	 *
	 * @param listener the listener
	 */
	public void registerOnLowMemoryListener(OnLowMemoryListener listener) {
        if (listener != null) {
            mLowMemoryListeners.add(new WeakReference<OnLowMemoryListener>(listener));
        }
    }
	
	/**
	 * Unregister on low memory listener.
	 *
	 * @param listener the listener
	 */
	public void unregisterOnLowMemoryListener(OnLowMemoryListener listener) {
        if (listener != null) {
            int i = 0;
            while (i < mLowMemoryListeners.size()) {
                final OnLowMemoryListener l = mLowMemoryListeners.get(i).get();
                if (l == null || l == listener) {
                    mLowMemoryListeners.remove(i);
                } else {
                    i++;
                }
            }
        }
    }
	
	/**
	 * onLowMemory().
	 *
	 * @see android.app.Application#onLowMemory()
	 */
	public void onLowMemory() {
        super.onLowMemory();
        clearCaches();
        int i = 0;
        while (i < mLowMemoryListeners.size()) {
            final OnLowMemoryListener listener = mLowMemoryListeners.get(i).get();
            if (listener == null) {
                mLowMemoryListeners.remove(i);
            } else {
                listener.onLowMemoryReceived();
                i++;
            }
        }
    }
    
    /**
     * Gets the active context.
     *
     * @param className the class name
     * @return the active context
     */
    public synchronized  Context getActiveContext(String className) {
        WeakReference<Context> ref = contextObjects.get(className);
        if (ref == null) {
            return null;
        }

        final Context c = ref.get();
        if (c == null) // If the WeakReference is no longer valid, ensure it is removed.
            contextObjects.remove(className);

        return c;
    }

    /**
     * Sets the active context.
     *
     * @param className the class name
     * @param context the context
     */
    public synchronized void setActiveContext(String className, Context context) {
        WeakReference<Context> ref = new WeakReference<Context>(context);
        this.contextObjects.put(className, ref);
    }

    /**
     * Reset active context.
     *
     * @param className the class name
     */
    public synchronized void resetActiveContext(String className) {
        contextObjects.remove(className);
    }
	
	/**
	 * Gets the application info.
	 *
	 * @return the application info
	 * @see android.content.ContextWrapper#getApplicationInfo()
	 */
	public ApplicationInfo getApplicationInfo(String packageName) {
		// TODO Auto-generated method stub
		ApplicationInfo app = null;
		try {
			app = context.getPackageManager().getApplicationInfo(packageName, 0);
		
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			GWSLOG.error("packageName not found");
		}
		
		
		return app;
	}
	
	/**
	 * Gets the app context.
	 *
	 * @return the app context
	 */
	public static Context getAppContext() {
		
		 return GWSApplication.context;
	}
	
	
	
	
	/**
	 * Is triggered by a back button event 
	 * that exits the application from the handleApplicationClosing
	 * method in GWSActivity.
	 */
	public void onClose() {
        clearCaches();
    }
	
	
	/**
	 * On configuration changed.
	 * Remember in manifest set:
	 * <code>
	 * <activity android:name=".MyActivity"
     *    android:configChanges="orientation|keyboardHidden"
     *    android:label="@string/app_name">
	 * </code>
	 * 
	 * The default without the manifest setting is 
	 * to be ignored by app and restart activity upon 
	 * configuration change.If special addition handling is required,
	 * override the method.
	 * 
	 * @param newConfig the new config
	 * @see android.app.Application#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isLandscape = true;
		}else{
			isLandscape =false;
		}
		
	}
	
	/**
	 * On create.
	 *
	 * @see org.holoeverywhere.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		GWSApplication.context = getApplicationContext();
		
		GWSLOG.info("GWSApplicaiton class created");
		setCaches();
		setID();
		
		
		super.onCreate();
	}
	
	/**
	 * On terminate.
	 *
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		clearCaches();
		super.onTerminate();
	}
	
	/**
	 * Sets the caches, normally we overrride this 
	 * method.
	 */
	public void setCaches(){
		File cacheLocation;

        // If we have external storage use it for the disk cache. Otherwise we use
        // the cache dir
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            cacheLocation = new File(
                    Environment.getExternalStorageDirectory() + "/Android-BitmapCache");
        } else {
            cacheLocation = new File(getFilesDir() + "/Android-BitmapCache");
        }
        cacheLocation.mkdirs();

        BitmapLruCache.Builder builder = new BitmapLruCache.Builder(this);
        builder.setMemoryCacheEnabled(true).setMemoryCacheMaxSizeUsingHeapSize();
        builder.setDiskCacheEnabled(true).setDiskCacheLocation(cacheLocation);

        mCache = builder.build();
	}
	
	/**
	 * clearCaches, normally we overrride this.
	 */
	public void clearCaches() {
		//clears mCache of all bitmaps that are not displayed.
		mCache.trimMemory();
	}
	
	public BitmapLruCache getBitmapCache() {
		return mCache;
	}
	
	/**
	 * Set theID if its not stored yet in shared prefs or if
	 * in shared prefs than set to that value.
	 */
	public void setID() {
		
		
		
		if(!GWSPreferences.getIDState(getApplicationContext())){
			GWSPreferences.setIDState(getApplicationContext(), true);
			GWSPreferences.setIDString(getApplicationContext(), PseudoID.ePseudoID);
			
		} else{          
			GWSPreferences.setIDState(getApplicationContext(), false);
		}
		
		
	}
	
	


}
