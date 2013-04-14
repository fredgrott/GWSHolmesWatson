package gws.grottworkshop.gwsholmeswatson.view;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import ch.qos.logback.core.pattern.Converter;

import gws.grottworkshop.gwsholmeswatson.cache.ImageCache;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class RemoteImageLoader {
	
	private ImageCache imageCache;
	
	private File fileDir;
	
	private int appVersion;
	
	private int maxSizeMemory;
	
	private long maxSizeDisk;
	/**
	 * http request buffer size
	 */
	private static final int DEFAULT_BUFFER_SIZE = 65536;
	private int defaultBufferSize = DEFAULT_BUFFER_SIZE;
	private static final int DEFAULT_NUM_RETRIES = 3;
	private int numRetries = DEFAULT_NUM_RETRIES;
	
	private Converter<byte[]> converter;
	
	private Drawable dummyDrawable, errorDrawable;
	
	
	private ThreadPoolExecutor executor;
	
	private static final int DEFAULT_POOL_SIZE = 3;
	
	public void setThreadPoolSize(int numThreads) {
        executor.setMaximumPoolSize(numThreads);
    }
	
	public RemoteImageLoader(Context context) {
        this(context, true);
    }

	public RemoteImageLoader(Context context, boolean isCacheCreated) {
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
		if (isCacheCreated) {
			try {
				imageCache = new ImageCache(null, 0, 0, 0, null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		errorDrawable = context.getResources().getDrawable(android.R.drawable.ic_dialog_alert);
	}
	
	public void setDownloadInProgressDrawable(Drawable drawable) {
        this.dummyDrawable = drawable;
    }

    public void setDownloadFailedDrawable(Drawable drawable) {
        this.errorDrawable = drawable;
    }

    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }
    
    public void clearImageCache() {
        if (imageCache != null) {
            //TODO
        }
    }
    
    public ImageCache getImageCache() {
        return imageCache;
    }

    public void loadImage(String imageUrl, GWSGestureCacheableImageView imageView, RemoteImageLoaderHandler handler) {
    	if (imageView != null) {
    		if (imageUrl == null) {
    			//in ListViews tag is set as iews are reused
    			imageView.setTag(null);
    			if (dummyDrawable != null) {
                    imageView.setImageDrawable(dummyDrawable);
                }
                return;
    			
    		}
    		String oldImageUrl = (String) imageView.getTag();
            if (imageUrl.equals(oldImageUrl)) {
                // nothing to do
                return;
            } else {
                if (dummyDrawable != null) {
                    // Set the dummy image while waiting for the actual image to be downloaded.
                    imageView.setImageDrawable(dummyDrawable);
                }
                imageView.setTag(imageUrl);
            }
    	}
    	if (imageCache != null && imageCache.containsKey(imageUrl)) {
            // do not go through message passing, handle directly instead
            handler.handleImageLoaded(imageCache.getBitmap(imageUrl), null);
        } else {
            executor.execute(new RemoteImageLoaderJob(imageUrl, handler, imageCache, numRetries,
                    defaultBufferSize));
        }
    }
    	public void loadImage(String imageUrl, GWSGestureCacheableImageView imageView) {
            loadImage(imageUrl, imageView, new RemoteImageLoaderHandler(imageView, imageUrl, errorDrawable));
        }
}
