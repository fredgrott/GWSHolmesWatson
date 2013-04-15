package gws.grottworkshop.gwsholmeswatson.view;

import gws.grottworkshop.gwsholmeswatson.GWSHolmesWatson;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

// TODO: Auto-generated Javadoc
/**
 * The Class RemoteImageView.
 */
public class RemoteImageView extends ViewSwitcher {
	
	/** The Constant DEFAULT_ERROR_DRAWABLE_RES_ID. */
	public static final int DEFAULT_ERROR_DRAWABLE_RES_ID = android.R.drawable.ic_dialog_alert;

    /** The Constant ATTR_AUTO_LOAD. */
    private static final String ATTR_AUTO_LOAD = "autoLoad";
    
    /** The Constant ATTR_IMAGE_URL. */
    private static final String ATTR_IMAGE_URL = "imageUrl";
    
    /** The Constant ATTR_ERROR_DRAWABLE. */
    private static final String ATTR_ERROR_DRAWABLE = "errorDrawable";

    /** The Constant ANDROID_VIEW_ATTRS. */
    private static final int[] ANDROID_VIEW_ATTRS = { android.R.attr.indeterminateDrawable };
    
    /** The Constant ATTR_INDET_DRAWABLE. */
    private static final int ATTR_INDET_DRAWABLE = 0;

    /** The image url. */
    private  String imageUrl;

    /** The is loaded. */
    private  boolean autoLoad, isLoaded;

    /** The loading spinner. */
    private ProgressBar loadingSpinner;
    
    /** The image view. */
    private  GWSGestureCacheableImageView imageView;

    /** The error drawable. */
    private  Drawable progressDrawable, errorDrawable;

    /** The image loader. */
    private RemoteImageLoader imageLoader;
    
    /** The shared image loader. */
    private static RemoteImageLoader sharedImageLoader;

    /**
     * Use this method to inject an image loader that will be shared across all instances of this
     * class. If the shared reference is null, a new {@link RemoteImageLoader} will be instantiated
     * for every instance of this class.
     * 
     * @param imageLoader
     *            the shared image loader
     */
    public static void setSharedImageLoader(RemoteImageLoader imageLoader) {
        sharedImageLoader = imageLoader;
    }

    /**
     * Instantiates a new remote image view.
     *
     * @param context the view's current context
     * @param imageUrl the URL of the image to download and show
     * @param autoLoad Whether the download should start immediately after creating the view. If set to
     * false, use {@link #loadImage()} to manually trigger the image download.
     */
    public RemoteImageView(Context context, String imageUrl, boolean autoLoad) {
        super(context);
        initialize(context, imageUrl, null, null, autoLoad, null);
    }

    /**
     * Instantiates a new remote image view.
     *
     * @param context the view's current context
     * @param imageUrl the URL of the image to download and show
     * @param progressDrawable the drawable to be used for the {@link ProgressBar} which is displayed while the
     * image is loading
     * @param errorDrawable the drawable to be used if a download error occurs
     * @param autoLoad Whether the download should start immediately after creating the view. If set to
     * false, use {@link #loadImage()} to manually trigger the image download.
     */
    public RemoteImageView(Context context, String imageUrl, Drawable progressDrawable,
            Drawable errorDrawable, boolean autoLoad) {
        super(context);
        initialize(context, imageUrl, progressDrawable, errorDrawable, autoLoad, null);
    }

    /**
     * Instantiates a new remote image view.
     *
     * @param context the context
     * @param attributes the attributes
     */
    public RemoteImageView(Context context, AttributeSet attributes) {
        super(context, attributes);

        // Read all Android specific view attributes into a typed array first.
        // These are attributes that are specific to RemoteImageView, but which are not in the
        // ignition XML namespace.
        TypedArray imageViewAttrs = context.getTheme().obtainStyledAttributes(attributes,
                ANDROID_VIEW_ATTRS, 0, 0);
        int progressDrawableId = imageViewAttrs.getResourceId(ATTR_INDET_DRAWABLE, 0);
        imageViewAttrs.recycle();

        int errorDrawableId = attributes.getAttributeResourceValue(GWSHolmesWatson.XMLNS,
                ATTR_ERROR_DRAWABLE, DEFAULT_ERROR_DRAWABLE_RES_ID);
        Drawable errorDrawable = context.getResources().getDrawable(errorDrawableId);

        Drawable progressDrawable = null;
        if (progressDrawableId > 0) {
            progressDrawable = context.getResources().getDrawable(progressDrawableId);
        }

        String imageUrl = attributes.getAttributeValue(GWSHolmesWatson.XMLNS, ATTR_IMAGE_URL);
        boolean autoLoad = attributes
                .getAttributeBooleanValue(GWSHolmesWatson.XMLNS, ATTR_AUTO_LOAD, true);

        initialize(context, imageUrl, progressDrawable, errorDrawable, autoLoad, attributes);
    }

    /**
     * Initialize.
     *
     * @param context the context
     * @param imageUrl the image url
     * @param progressDrawable the progress drawable
     * @param errorDrawable the error drawable
     * @param autoLoad the auto load
     * @param attributes the attributes
     */
    private void initialize(Context context, String imageUrl, Drawable progressDrawable,
            Drawable errorDrawable, boolean autoLoad, AttributeSet attributes) {
        this.imageUrl = imageUrl;
        this.autoLoad = autoLoad;
        this.progressDrawable = progressDrawable;
        this.errorDrawable = errorDrawable;
        if (sharedImageLoader == null) {
            this.imageLoader = new RemoteImageLoader(context);
        } else {
            this.imageLoader = sharedImageLoader;
        }

        // ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
        // 125.0f, preferredItemHeight / 2.0f);
        // anim.setDuration(500L);

        // AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        // anim.setDuration(500L);
        // setInAnimation(anim);

        addLoadingSpinnerView(context);
        addImageView(context, attributes);

        if (autoLoad && imageUrl != null) {
            loadImage();
        } else {
            // if we don't have anything to load yet, don't show the progress element
            setDisplayedChild(1);
        }
    }

    /**
     * Adds the loading spinner view.
     *
     * @param context the context
     */
    private void addLoadingSpinnerView(Context context) {
        loadingSpinner = new ProgressBar(context);
        loadingSpinner.setIndeterminate(true);
        if (this.progressDrawable == null) {
            this.progressDrawable = loadingSpinner.getIndeterminateDrawable();
        } else {
            loadingSpinner.setIndeterminateDrawable(progressDrawable);
            if (progressDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) progressDrawable).start();
            }
        }

        LayoutParams lp = new LayoutParams(progressDrawable.getIntrinsicWidth(),
                progressDrawable.getIntrinsicHeight());
        lp.gravity = Gravity.CENTER;

        addView(loadingSpinner, 0, lp);
    }

    /**
     * Adds the image view.
     *
     * @param context the context
     * @param attributes the attributes
     */
    private void addImageView(Context context, AttributeSet attributes) {
        if (attributes != null) {
            // pass along any view attribtues inflated from XML to the image view
            imageView = new GWSGestureCacheableImageView(context, attributes);
        } else {
            imageView = new GWSGestureCacheableImageView(context);
        }
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(imageView, 1, lp);
    }

    /**
     * Use this method to trigger the image download if you had previously set autoLoad to false.
     */
    public void loadImage() {
        if (imageUrl == null) {
            throw new IllegalStateException(
                    "image URL is null; did you forget to set it for this view?");
        }
        setDisplayedChild(0);
        imageLoader.loadImage(imageUrl, imageView, new DefaultImageLoaderHandler());
    }

    /**
     * Checks if is loaded.
     *
     * @return true, if is loaded
     */
    public boolean isLoaded() {
        return isLoaded;
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
     * Often you have resources which usually have an image, but some don't. For these cases, use
     * this method to supply a placeholder drawable which will be loaded instead of a web image.
     * 
     * @param imageResourceId
     *            the resource of the placeholder image drawable
     */
    public void setNoImageDrawable(int imageResourceId) {
        imageView.setImageDrawable(getContext().getResources().getDrawable(imageResourceId));
        setDisplayedChild(1);
    }

    /**
     * Reset.
     *
     * @see android.widget.ViewSwitcher#reset()
     */
    @Override
    public void reset() {
        super.reset();

        this.setDisplayedChild(0);
    }

    /**
     * The Class DefaultImageLoaderHandler.
     */
    @SuppressLint("HandlerLeak")
	private  class DefaultImageLoaderHandler extends RemoteImageLoaderHandler {

        /**
         * Instantiates a new default image loader handler.
         */
        public  DefaultImageLoaderHandler() {
            super(imageView, imageUrl, errorDrawable);
        }

        /**
         * Handle image loaded.
         *
         * @param bitmap the bitmap
         * @param msg the msg
         * @return true, if successful
         * @see gws.grottworkshop.gwsholmeswatson.view.RemoteImageLoaderHandler#handleImageLoaded(android.graphics.Bitmap, android.os.Message)
         */
        @Override
        protected boolean handleImageLoaded(Bitmap bitmap, Message msg) {
            boolean wasUpdated = super.handleImageLoaded(bitmap, msg);
            if (wasUpdated) {
                isLoaded = true;
                setDisplayedChild(1);
            }
            return wasUpdated;
        }
    }

    /**
     * Returns the URL of the image to show. Corresponds to the view attribute ignition:imageUrl.
     * 
     * @return the image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Whether or not the image should be downloaded immediately after view inflation. Corresponds
     * to the view attribute ignition:autoLoad (default: true).
     * 
     * @return true if auto downloading of the image is enabled
     */
    public boolean isAutoLoad() {
        return autoLoad;
    }

    /**
     * The drawable that should be used to indicate progress while downloading the image.
     * Corresponds to the view attribute ignition:progressDrawable. If left blank, the platform's
     * standard indeterminate progress drawable will be used.
     * 
     * @return the progress drawable
     */
    public Drawable getProgressDrawable() {
        return progressDrawable;
    }

    /**
     * The drawable that will be shown when the image download fails. Corresponds to the view
     * attribute ignition:errorDrawable. If left blank, a stock alert icon from the Android platform
     * will be used.
     * 
     * @return the error drawable
     */
    public Drawable getErrorDrawable() {
        return errorDrawable;
    }

    /**
     * The image view that will render the downloaded image.
     * 
     * @return the {@link ImageView}
     */
    public GWSGestureCacheableImageView getImageView() {
        return imageView;
    }

    /**
     * The progress bar that is shown while the image is loaded.
     * 
     * @return the {@link ProgressBar}
     */
    public ProgressBar getProgressBar() {
        return loadingSpinner;
    }
}
