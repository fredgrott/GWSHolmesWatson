package gws.grottworkshop.gwsholmeswatson;



import java.util.List;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 * The Class GWSActivity extends holoeverywhere activity
 * so that we get full use of both actiionbarsherlock 
 * and holoeverywhere.
 * 
 */
public class GWSActivity extends Activity {
	
	private boolean eulaAccpeted;
	
	private Logger GWSLOG = LoggerFactory.getLogger(GWSActivity.class);
    
	/** 
	 * onCreateConfig()
	 * 
	 * @see android.support.v4.app._HoloActivity#onCreateConfig(android.os.Bundle)
	 */
	@Override
	protected Holo onCreateConfig(Bundle savedInstanceState) {
		
		GWSLOG.info("GWSActivity created");
		
		// for performance set customviews through a factory
		setCustomViews();
		EULAHelper.showEula(eulaAccpeted, GWSActivity.this);
		
		Application application = (Application) getApplication();
		if (application instanceof GWSApplication) {
            ((GWSApplication) application).setActiveContext(getClass().getCanonicalName(), this);
        }
		// TODO Auto-generated method stub
		return super.onCreateConfig(savedInstanceState);
	}
	
	/**
	 * Sets the custom views via a factory singleton, 
	 * method is setup to
	 * be overridden and default is a no-op.
	 * 
	 * Typically you would implement it as:
	 * <code>
	 * public final class MyViewsFactory implements LayoutInflater.Factory {
     *   @Override
     *   public View onCreateView(String name, Context context, AttributeSet attrs) {
     *      if (TextUtils.equals(viewName, "com.mycompany.myproject.CustomView"))
     *       return new CustomView(context, attrs);
     *   else
     *       return null;
     *    }
     *  }
	 * </code>
	 * 
	 * Then in onCreate:
	 * <code>
	 * LayoutInflater.from(this).setFactory(MyViewsFactory.getInstance());
	 * </code>
	 */
	public void setCustomViews(){
		
	}
	
	/**
     * Checks if the application is in the background (i.e behind another application's Activity).
     * Borrowed from Kaeppler(DroidFu/Ignition)
     * @param context
     * @return true if another application is above this one.
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }
    
    public boolean isApplicationBroughtToBackground() {
        return isApplicationBroughtToBackground(this);
    }

    /**
     * Handle application closing.
     *
     * @param context the context
     * @param keyCode the key code
     */
    static void handleApplicationClosing(final Context context, int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> tasks = am.getRunningTasks(2);

            RunningTaskInfo currentTask = tasks.get(0);
            RunningTaskInfo nextTask = tasks.get(1);

            // if we're looking at this application's base/launcher Activity,
            // and the next task is the Android home screen, then we know we're
            // about to close the app
            if (currentTask.topActivity.equals(currentTask.baseActivity)
                    && nextTask.baseActivity.getPackageName().startsWith("com.android.launcher")) {
                GWSApplication application = (GWSApplication) context
                        .getApplicationContext();
                application.onClose();
            }
        }
    }
    
    /**
     * On key down.
     *
     * @param keyCode the key code
     * @param event the event
     * @return true, if successful
     * @see android.support.v4.app.FragmentActivity#onKeyDown(int, android.view.KeyEvent)
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        handleApplicationClosing(this, keyCode);
        return super.onKeyDown(keyCode, event);
    }
	
}
