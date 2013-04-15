package gws.grottworkshop.gwsholmeswatson.view;


import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import gws.grottworkshop.gwsholmeswatson.GWSApplication;
import gws.grottworkshop.gwsholmeswatson.cache.ImageCache;
import android.content.Context;
import android.graphics.drawable.Drawable;

// TODO: Auto-generated Javadoc
/**
 * The Class RemoteImageLoader.
 */
public class RemoteImageLoader {
	
	/** The image cache. */
	private ImageCache imageCache = GWSApplication.getImageCache();
	
	
	
	//private int appVersion;
	
	//private int maxSizeMemory;
	
	//private long maxSizeDisk;
	/** http request buffer size. */
	private static final int DEFAULT_BUFFER_SIZE = 65536;
	
	/** The default buffer size. */
	private int defaultBufferSize = DEFAULT_BUFFER_SIZE;
	
	/** The Constant DEFAULT_NUM_RETRIES. */
	private static final int DEFAULT_NUM_RETRIES = 3;
	
	/** The num retries. */
	private int numRetries = DEFAULT_NUM_RETRIES;
	
	//private Converter<byte[]> converter;
	
	/** The error drawable. */
	private Drawable dummyDrawable, errorDrawable;
	
	private Logger GWSLOG = LoggerFactory.getLogger(RemoteImageLoader.class);
	
	/** The executor. */
	private ThreadPoolExecutor executor;
	
	/** The Constant DEFAULT_POOL_SIZE. */
	private static final int DEFAULT_POOL_SIZE = 3;
	
	/**
	 * Sets the thread pool size.
	 *
	 * @param numThreads the new thread pool size
	 */
	public void setThreadPoolSize(int numThreads) {
        executor.setMaximumPoolSize(numThreads);
    }
	
	/**
	 * Instantiates a new remote image loader.
	 *
	 * @param context the context
	 */
	public RemoteImageLoader(Context context) {
        this(context, true);
    }

	/**
	 * Instantiates a new remote image loader.
	 *
	 * @param context the context
	 * @param isCacheCreated the is cache created
	 */
	public RemoteImageLoader(Context context, boolean isCacheCreated) {
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
		if (!isCacheCreated) {
			GWSLOG.error("it works better if you have extended the GWSApplication class");
		}
		errorDrawable = context.getResources().getDrawable(android.R.drawable.ic_dialog_alert);
	}
	
	/**
	 * Sets the download in progress drawable.
	 *
	 * @param drawable the new download in progress drawable
	 */
	public void setDownloadInProgressDrawable(Drawable drawable) {
        this.dummyDrawable = drawable;
    }

    /**
     * Sets the download failed drawable.
     *
     * @param drawable the new download failed drawable
     */
    public void setDownloadFailedDrawable(Drawable drawable) {
        this.errorDrawable = drawable;
    }

    /**
     * Sets the image cache.
     *
     * @param imageCache the new image cache
     */
    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }
    
    /**
     * Clear image cache.
     */
    public void clearImageCache() {
        if (imageCache != null) {
            //TODO
        }
    }
    
    /**
     * Gets the image cache.
     *
     * @return the image cache
     */
    public ImageCache getImageCache() {
        return imageCache;
    }

    /**
     * Load image.
     *
     * @param imageUrl the image url
     * @param imageView the image view
     * @param handler the handler
     */
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
    	
	    /**
	     * Load image.
	     *
	     * @param imageUrl the image url
	     * @param imageView the image view
	     */
	    public void loadImage(String imageUrl, GWSGestureCacheableImageView imageView) {
            loadImage(imageUrl, imageView, new RemoteImageLoaderHandler(imageView, imageUrl, errorDrawable));
        }
}
