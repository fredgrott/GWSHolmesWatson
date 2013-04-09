package gws.grottworkshop.gwsholmeswatson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import android.annotation.SuppressLint;
import android.os.Build;

import android.util.Log;

/**
 * The Class PsdeudoID, UUID for tracking has 
 * problems with being null and resetting upon factory reset.
 * Tablets without the data mobile operator connection do not have 
 * IMEI but than we have same problems with UUID as above.
 * 
 * I just compute a unique 32 bit string and MD5 has that convert
 * to a hex uppercase string.  WARNING, it has a range limit of 
 * 4 billion and thus will only hve 4 billion unique IDs.
 * 
 * The key thing is that I have a unique ID that has been 
 * slightly encrypted that is not the UUID or the IMEI and is 
 * still there as the same unique ID upon factory reset and handles 
 * the fact that we have many different devices other than just phones.
 * 
 * @author fredgrott
 */
@SuppressLint("DefaultLocale")
public class PseudoID {
	
	
	public static String ePseudoID = setPsdeuoIMEIString();

	private static String psdeuoIMEIString ;
	
	public static String encryptedPseudoIMEIString= new String();
	
	private static MessageDigest mDigest;
	

	private String computeUnencryptedPseudoIMEI(){
		
		psdeuoIMEIString =
		Build.BOARD+ Build.BRAND+ Build.FINGERPRINT+
		Build.DEVICE+ Build.DISPLAY+ Build.ID+ Build.MANUFACTURER+
		Build.TAGS+ Build.MODEL+ Build.PRODUCT+
		Build.TIME;
		
		
		return psdeuoIMEIString;
		
	}
	
	@SuppressLint("DefaultLocale")
	public static String setPsdeuoIMEIString() {
		
		try {
			mDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Log.e("gws", "nomd5");
		}
		mDigest.update(psdeuoIMEIString.getBytes(),0,psdeuoIMEIString.length());
		
		byte p_md5Data[] = mDigest.digest();
		
		for (int i=0;i<p_md5Data.length;i++) {
			
			int b =  (0xFF & p_md5Data[i]);
			
			if (b <= 0xF) encryptedPseudoIMEIString+="0";
			
			encryptedPseudoIMEIString+=Integer.toHexString(b); 
			
			encryptedPseudoIMEIString = encryptedPseudoIMEIString.toUpperCase();
		}
		
		return encryptedPseudoIMEIString;
	}

	
	
}
