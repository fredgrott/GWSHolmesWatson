package gws.grottworkshop.gwsholmeswatson;



import org.holoeverywhere.app.Activity;


import android.os.Bundle;

/**
 * The Class GWSActivity extends holoeverywhere activity
 * so that we get full use of both actiionbarsherlock 
 * and holoeverywhere.
 * 
 */
public class GWSActivity extends Activity {
	
	
    
	/** 
	 * 
	 * 
	 * @see android.support.v4.app._HoloActivity#onCreateConfig(android.os.Bundle)
	 */
	@Override
	protected Holo onCreateConfig(Bundle savedInstanceState) {
		// for performance set customviews through a factory
		setCustomViews();
		// set up caches
		setCaches();
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
	 * Sets the caches, normally we overrride this 
	 * method.
	 */
	public void setCaches(){
		
	}
}
