package gws.grottworkshop.gwsholmeswatson;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * The Class GWSPreferences.
 * You will extend this to add your stuff.
 * 
 * @author fredgrott
 */
public class GWSPreferences {
	
	
	
	/**
	 * ID gets set to a string of false if no 
	 * ID has been stored yet.
	 * @param context
	 * @return
	 */
	public static String getID(Context context) {
		SharedPreferences prefReader =
	            PreferenceManager.getDefaultSharedPreferences(context);
		String pref = prefReader.getString("ID", "false");
		return pref;
	}
	
	public static boolean getIDState(Context context){
		SharedPreferences prefReader =
	            PreferenceManager.getDefaultSharedPreferences(context);
		boolean pref = prefReader.getBoolean("iHazID", false);
		return pref;
	}
	
	public static boolean getEULAState(Context context){
		SharedPreferences prefReader =
	            PreferenceManager.getDefaultSharedPreferences(context);
		boolean pref = prefReader.getBoolean("hasEULAShown", false);
		return pref;
	}
	
	public static void setIDState(Context context, boolean hazIDState) {
		Editor prefEditor =
	            PreferenceManager.getDefaultSharedPreferences(context).edit();
		prefEditor.putBoolean(
                "true",
                hazIDState);
        prefEditor.commit();
	}
	
	public static void setIDString(Context context, String stringID) {
		Editor prefEditor =
	            PreferenceManager.getDefaultSharedPreferences(context).edit();
		prefEditor.putString("stringID", stringID);
		prefEditor.commit();
	}

}
