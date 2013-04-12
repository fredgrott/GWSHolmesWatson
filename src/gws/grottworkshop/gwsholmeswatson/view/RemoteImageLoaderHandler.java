package gws.grottworkshop.gwsholmeswatson.view;

import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * rewrite of same class from PaginatedGalery to use
 * GWSGesturecacheableImageView as we have integrated cacheof bitmaps at the view level.
 * @author fredgrott
 *
 */
public class RemoteImageLoaderHandler  extends Handler {

	public static final int HANDLER_MESSAGE_ID = 0;
    public static final String BITMAP_EXTRA = "gws:extra_bitmap";
    public static final String IMAGE_URL_EXTRA = "gws:extra_image_url";

    private GWSGestureCacheableImageView imageView;
    private String imageUrl;
    private Drawable errorDrawable;
    
    public RemoteImageLoaderHandler(GWSGestureCacheableImageView imageView, String imageUrl, Drawable errorDrawable) {
        this.imageView = imageView;
        this.imageUrl = imageUrl;
        this.errorDrawable = errorDrawable;
    }

    @Override
    public final void handleMessage(Message msg) {
        if (msg.what == HANDLER_MESSAGE_ID) {
            handleImageLoadedMessage(msg);
        }
    }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public GWSGestureCacheableImageView  getImageView() {
        return imageView;
    }

    public void setImageView(GWSGestureCacheableImageView imageView) {
        this.imageView = imageView;
    }
}
