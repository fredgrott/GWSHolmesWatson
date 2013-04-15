package gws.grottworkshop.gwsholmeswatson.view;


import gws.grottworkshop.gwsholmeswatson.GWSApplication;
import gws.grottworkshop.gwsholmeswatson.cache.ImageCache;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;

// TODO: Auto-generated Javadoc
/**
 * Modified from paginatedgallery touse logback as logger 
 * and a modified imagecache.
 * 
 * @author fredgrott
 *
 */
public class RemoteImageLoaderJob implements Runnable {

	/** The gwslog. */
	private Logger GWSLOG = LoggerFactory.getLogger(RemoteImageLoaderJob.class);

    /** The Constant DEFAULT_RETRY_HANDLER_SLEEP_TIME. */
    private static final int DEFAULT_RETRY_HANDLER_SLEEP_TIME = 1000;

    /** The image url. */
    private String imageUrl;
    
    /** The handler. */
    private RemoteImageLoaderHandler handler;
    
    /** The image cache. */
    private ImageCache imageCache = GWSApplication.getImageCache();
    
    /** The default buffer size. */
    private int numRetries, defaultBufferSize;

    /**
     * Instantiates a new remote image loader job.
     *
     * @param imageUrl the image url
     * @param handler the handler
     * @param imageCache the image cache
     * @param numRetries the num retries
     * @param defaultBufferSize the default buffer size
     */
    public RemoteImageLoaderJob(String imageUrl, RemoteImageLoaderHandler handler, ImageCache imageCache,
            int numRetries, int defaultBufferSize) {
        this.imageUrl = imageUrl;
        this.handler = handler;
        this.imageCache = imageCache;
        this.numRetries = numRetries;
    }

    /**
     * The job method run on a worker thread. It will first query the image cache, and on a miss,
     * download the image from the Web.
     */
    @Override
    public void run() {
        Bitmap bitmap = null;

        if (imageCache != null) {
            // at this point we know the image is not in memory, but it could be cached to SD card
            bitmap = imageCache.getBitmap(imageUrl);
        }

        if (bitmap == null) {
            bitmap = downloadImage();
        }

        notifyImageLoaded(imageUrl, bitmap);
    }

    // TODO: we could probably improve performance by re-using connections instead of closing them
    // after each and every download
    /**
     * Download image.
     *
     * @return the bitmap
     */
    protected Bitmap downloadImage() {
        int timesTried = 1;

        while (timesTried <= numRetries) {
            try {
                byte[] imageData = retrieveImageData();

                if (imageData == null) {
                    break;
                }

                if (imageCache != null) {
                    imageCache.put(imageUrl, imageData);
                }

                return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

            } catch (Throwable e) {
                GWSLOG.warn( "download for " + imageUrl + " failed (attempt " + timesTried + ")");
                e.printStackTrace();
                SystemClock.sleep(DEFAULT_RETRY_HANDLER_SLEEP_TIME);
                timesTried++;
            }
        }

        return null;
    }

    /**
     * Retrieve image data.
     *
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected byte[] retrieveImageData() throws java.io.IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

//        connection.setRequestProperty("Authorization", "Basic "+ "bm9xc3RvcmU6dHJpYWw=");
        
        // determine the image size and allocate a buffer
        int fileSize = connection.getContentLength();
        if (fileSize <= 0) {
            fileSize = defaultBufferSize;
            GWSLOG.warn("Server did not set a Content-Length header, will default to buffer size of "
                            + defaultBufferSize + " bytes");
        }
        byte[] imageData = new byte[fileSize];

        // download the file
        GWSLOG.warn( "fetching image " + imageUrl + " (" + fileSize + ")");
        BufferedInputStream istream = new BufferedInputStream(connection.getInputStream());
        int bytesRead = 0;
        int offset = 0;
        while (bytesRead != -1 && offset < fileSize) {
            bytesRead = istream.read(imageData, offset, fileSize - offset);
            offset += bytesRead;
        }

        // clean up
        istream.close();
        connection.disconnect();

        return imageData;
    }

    /**
     * Notify image loaded.
     *
     * @param url the url
     * @param bitmap the bitmap
     */
    protected void notifyImageLoaded(String url, Bitmap bitmap) {
        Message message = new Message();
        message.what = RemoteImageLoaderHandler.HANDLER_MESSAGE_ID;
        Bundle data = new Bundle();
        data.putString(RemoteImageLoaderHandler.IMAGE_URL_EXTRA, url);
        Bitmap image = bitmap;
        data.putParcelable(RemoteImageLoaderHandler.BITMAP_EXTRA, image);
        message.setData(data);

        handler.sendMessage(message);
    }
}
