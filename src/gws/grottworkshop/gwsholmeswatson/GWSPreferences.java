package gws.grottworkshop.gwsholmeswatson;



import org.holoeverywhere.preference.PreferenceManager;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.preference.SharedPreferences.Editor;

import android.content.Context;

/**
 * The Class GWSPreferences.
 * You will extend this to add your stuff andif extending its 
 * classes in org.holoeverwyhere not the defaul android ones
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
	
	public static boolean getEULAAcceptState(Context context){
		SharedPreferences prefReader =
	            PreferenceManager.getDefaultSharedPreferences(context);
		boolean pref = prefReader.getBoolean("hasEULAAccept", false);
		return pref;
	}
	
	public static void setEULAAcceptState(Context context, boolean hazEULAAcceptState){
		Editor prefEditor =
	            PreferenceManager.getDefaultSharedPreferences(context).edit();
		prefEditor.putBoolean(
                "true",
                hazEULAAcceptState);
        prefEditor.commit();
	}
	
	
	public static void setEULAState(Context context, boolean hazEULAState) {
		Editor prefEditor =
	            PreferenceManager.getDefaultSharedPreferences(context).edit();
		prefEditor.putBoolean(
                "true",
                hazEULAState);
        prefEditor.commit();
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
