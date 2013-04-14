package gws.grottworkshop.gwsholmeswatson.view;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


// TODO: Auto-generated Javadoc
/**
 * Modified from paginatedgallery to use a gesture based imageview.
 * 
 * @author fredgrott
 *
 */
public class RemoteImageLoaderHandler extends Handler {

    /** The Constant HANDLER_MESSAGE_ID. */
    public static final int HANDLER_MESSAGE_ID = 0;
    
    /** The Constant BITMAP_EXTRA. */
    public static final String BITMAP_EXTRA = "ign:extra_bitmap";
    
    /** The Constant IMAGE_URL_EXTRA. */
    public static final String IMAGE_URL_EXTRA = "ign:extra_image_url";

    /** The image view. */
    private GWSGestureCacheableImageView imageView;
    
    /** The image url. */
    private String imageUrl;
    
    /** The error drawable. */
    private Drawable errorDrawable;

    /**
     * Instantiates a new remote image loader handler.
     *
     * @param imageView the image view
     * @param imageUrl the image url
     * @param errorDrawable the error drawable
     */
    public RemoteImageLoaderHandler(GWSGestureCacheableImageView imageView, String imageUrl, Drawable errorDrawable) {
        this.imageView = imageView;
        this.imageUrl = imageUrl;
        this.errorDrawable = errorDrawable;
    }

    /* (non-Javadoc)
     * @see android.os.Handler#handleMessage(android.os.Message)
     */
    @Override
    public final void handleMessage(Message msg) {
        if (msg.what == HANDLER_MESSAGE_ID) {
            handleImageLoadedMessage(msg);
        }
    }

    /**
     * Handle image loaded message.
     *
     * @param msg the msg
     */
    protected final void handleImageLoadedMessage(Message msg) {
        Bundle data = msg.getData();
        Bitmap bitmap = data.getParcelable(BITMAP_EXTRA);
        handleImageLoaded(bitmap, msg);
    }

    /**
     * Override this method if you need custom handler logic. Note that this method can actually be
     * called directly for performance reasons, in which case the message will be null
     * 
     * @param bitmap
     *            the bitmap returned from the image loader
     * @param msg
     *            the handler message; can be null
     * @return true if the view was updated with the new image, false if it was discarded
     */
    protected boolean handleImageLoaded(Bitmap bitmap, Message msg) {
        // If this handler is used for loading images in a ListAdapter,
        // the thread will set the image only if it's the right position,
        // otherwise it won't do anything.
        String forUrl = (String) imageView.getTag();
        if (imageUrl.equals(forUrl)) {
            Bitmap image = (bitmap == null ? ((BitmapDrawable) errorDrawable).getBitmap() : bitmap);
            imageView.setImageBitmap(image);

            // remove the image URL from the view's tag
            imageView.setTag(null);

            return true;
        }

        return false;
    }

    /**
     * Gets the image url.
     *
     * @return the image url
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the image url.
     *
     * @param imageUrl the new image url
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the image view.
     *
     * @return the image view
     */
    public GWSGestureCacheableImageView getImageView() {
        return imageView;
    }

    /**
     * Sets the image view.
     *
     * @param imageView the new image view
     */
    public void setImageView(GWSGestureCacheableImageView imageView) {
        this.imageView = imageView;
    }
}
