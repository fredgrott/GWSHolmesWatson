package gws.grottworkshop.gwsholmeswatson.view;

import org.holoeverywhere.widget.ProgressBar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ViewSwitcher;

/**
 * Redinfing this or re-writing this form the Kaeppler's Ignition libs as I have 
 * things cahced at the ImageView level. 
 * 
 * @author fredgrott
 *
 */
public class GWSRemoteImageView extends ViewSwitcher {
	
	public GWSRemoteImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static final int DEFAULT_ERROR_DRAWABLE_RES_ID = android.R.drawable.ic_dialog_alert;

    private static final String ATTR_AUTO_LOAD = "autoLoad";
    private static final String ATTR_IMAGE_URL = "imageUrl";
    private static final String ATTR_ERROR_DRAWABLE = "errorDrawable";

    private static final int[] ANDROID_VIEW_ATTRS = { android.R.attr.indeterminateDrawable };
    private static final int ATTR_INDET_DRAWABLE = 0;

    private String imageUrl;

    private boolean autoLoad, isLoaded;

    private ProgressBar loadingSpinner;
    
    private GWSGestureCacheableImageView imageView;
    
    private Drawable progressDrawable, errorDrawable;

    

}
