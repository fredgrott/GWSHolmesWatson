package net.frakbot.imageviewex.operation;

import java.io.IOException;

import net.frakbot.cache.CacheHelper;
import net.frakbot.imageviewex.ImageViewNext;
import net.frakbot.imageviewex.requestmanager.ImageViewExRequestFactory;
import net.frakbot.remote.RemoteHelper;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.LruCache;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Editor;

/**
 * Operation to download an image from the network.
 * Requested input:
 * - ImageMemCacheOperation.PARAM_IMAGE_URL, the URL of the image
 * Given output:
 * - ImageViewExRequestFactory.BUNDLE_EXTRA_OBJECT, the byte array of the image
 * 
 * @author Francesco Pontillo
 *
 */
public class ImageDownloadOperation implements Operation {
	
	public static final String PARAM_IMAGE_URL = 
            "net.frakbot.imageviewex.extra.url";

	@Override
	public Bundle execute(Context context, Request request)
			throws ConnectionException, DataException, CustomRequestException {
		
		// Initializes the caches, if they're not initialized already
		ImageViewNext.initCaches(context);
		
		// Get the URL from the input Bundle
		String url = request.getString(PARAM_IMAGE_URL);
		if (url == null || url.equals("")) throw new DataException("No value for URL " + url);
		
		byte[] image = null;
		try {
			image = RemoteHelper.download(url);
		} catch (IOException e) {
			throw new DataException("NETWORK: Error while getting value for URL " + url);
		}
		
		// If the object is not null
		if (image != null) {
			// Save into the disk cache
			DiskLruCache diskCache = ImageViewNext.getDiskCache();
			try {
				Editor editor = diskCache.edit(CacheHelper.UriToDiskLruCacheString(url));
	            if (editor != null) {
		            if(CacheHelper.writeByteArrayToEditor(image, editor)) {               
		            	diskCache.flush();
		                editor.commit();
		            } else {
		                editor.abort();
		            }
	            }
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Save into the memory cache
			LruCache<String, byte[]> memCache = ImageViewNext.getMemCache();
			memCache.put(url, image);
		}
		
		Bundle b = new Bundle();
		b.putByteArray(ImageViewExRequestFactory.BUNDLE_EXTRA_OBJECT, image);
		return b;
	}

}
