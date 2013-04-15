package gws.grottworkshop.gwsholmeswatson;

import gws.grottworkshop.gwsholmeswatson.cache.ImageCache;
import gws.grottworkshop.gwsholmeswatson.cache.TwoLevelLruCache.Converter;

import java.io.File;
import java.io.IOException;
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

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;



import uk.co.senab.bitmapcache.BitmapLruCache;





import android.content.Context;
import android.content.Intent;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
	
	/** The m executor service. */
	private ExecutorService mExecutorService;
	
	/** The m low memory listeners. */
	private ArrayList<WeakReference<OnLowMemoryListener>> mLowMemoryListeners;
	
	/** The gwslog. */
	private Logger GWSLOG = LoggerFactory.getLogger(GWSApplication.class);
	
	/** The is landscape. */
	public static boolean isLandscape;
	
	/** The m cache. */
	private BitmapLruCache mCache;
	
	/** The i cache. */
	public static ImageCache iCache;
	
	/** The m converter. */
	private Converter<byte[]> mConverter;
	
	/** The max mem size. */
	public static int maxMemSize;
	
	/** The max disk size. */
	public static long maxDiskSize;
	
	
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

	/** The Constant CORE_POOL_SIZE. */
	private static final int CORE_POOL_SIZE = 5;

    /** The Constant sThreadFactory. */
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
	 * @param packageName the package name
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
		configureLogbackDirectly();
		GWSLOG.info("GWSApplicaiton class created");
		setCachePrep();
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
	 * Gets the app name.
	 *
	 * @return the app name
	 */
	public String getAppName() {
		final PackageManager pm = getApplicationContext().getPackageManager();
		ApplicationInfo ai;
		try {
		    ai = pm.getApplicationInfo( this.getPackageName(), 0);
		} catch (final NameNotFoundException e) {
		    ai = null;
		}
		final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
		return applicationName;
	}
	
	/**
	 * Gets the app version.
	 *
	 * @return the app version
	 */
	public String getAppVersion() {
		PackageInfo pInfo;
		String version;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version= pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			version = "namenotfound";
		}
		
		return version;
	}
	
	/**
	 * Sets the cache prep.
	 */
	public void setCachePrep() {
		maxMemSize = 3072000;
		maxDiskSize= 10240000L;
	}
	
	/**
	 * Sets the caches, normally we overrride this 
	 * method.
	 */
	public void setCaches(){
		File cacheLocation;
		File cacheMeLocation;

        // If we have external storage use it for the disk cache. Otherwise we use
        // the cache dir
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            cacheLocation = new File(
                    Environment.getExternalStorageDirectory() + "/" + getAppName() + "-Android-BitmapCache");
            cacheMeLocation= new File(Environment.getExternalStorageDirectory() + "/" + getAppName() + "-GWSCache");
        } else {
            cacheLocation = new File(getFilesDir() + "/" + getAppName() + "-Android-BitmapCache");
            cacheMeLocation = new File(getFilesDir() + "/" + getAppName() + "-GWSCache");
        }
        cacheLocation.mkdirs();

        BitmapLruCache.Builder builder = new BitmapLruCache.Builder(this);
        builder.setMemoryCacheEnabled(true).setMemoryCacheMaxSizeUsingHeapSize();
        builder.setDiskCacheEnabled(true).setDiskCacheLocation(cacheLocation);

        mCache = builder.build();
        
        try {
			iCache = new ImageCache(cacheMeLocation, Integer.parseInt(getAppVersion()), maxMemSize, maxDiskSize, mConverter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * clearCaches, normally we overrride this.
	 */
	public void clearCaches() {
		//clears mCache of all bitmaps that are not displayed.
		mCache.trimMemory();
		
	}
	
	/**
	 * Gets the bitmap cache.
	 *
	 * @return the bitmap cache
	 */
	public BitmapLruCache getBitmapCache() {
		return mCache;
	}
	
	/**
	 * Gets the image cache.
	 *
	 * @return the image cache
	 */
	public static ImageCache getImageCache(){
		return iCache;
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
	
	/**
	 * Configure logback directly.
	 */
	private void configureLogbackDirectly() {
	    // reset the default context (which may already have been initialized)
	    // since we want to reconfigure it
	    LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
	    lc.reset();

	    // setup FileAppender
	    PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
	    encoder1.setContext(lc);
	    encoder1.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
	    encoder1.start();

	    FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
	    fileAppender.setContext(lc);
	    fileAppender.setFile(this.getFileStreamPath("app.log").getAbsolutePath());
	    fileAppender.setEncoder(encoder1);
	    fileAppender.start();

	    // setup LogcatAppender
	    PatternLayoutEncoder encoder2 = new PatternLayoutEncoder();
	    encoder2.setContext(lc);
	    encoder2.setPattern("[%thread] %msg%n");
	    encoder2.start();

	    LogcatAppender logcatAppender = new LogcatAppender();
	    logcatAppender.setContext(lc);
	    logcatAppender.setEncoder(encoder2);
	    logcatAppender.start();

	    // add the newly created appenders to the root logger;
	    // qualify Logger to disambiguate from org.slf4j.Logger
	    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
	    root.addAppender(fileAppender);
	    root.addAppender(logcatAppender);
	  }


}
