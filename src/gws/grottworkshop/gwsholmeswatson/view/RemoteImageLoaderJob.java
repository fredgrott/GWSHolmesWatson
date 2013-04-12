package gws.grottworkshop.gwsholmeswatson.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import uk.co.senab.bitmapcache.BitmapLruCache;

/**
 * Rewrite of same class from Paginatedgallery as we will use a cacheable image view 
 * and some things from GWSApplication class.
 * 
 * @author fredgrott
 *
 */
public class RemoteImageLoaderJob implements Runnable {
	
	private static final int DEFAULT_RETRY_HANDLER_SLEEP_TIME = 1000;

    private String imageUrl;
    private RemoteImageLoaderHandler handler;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
    
}
