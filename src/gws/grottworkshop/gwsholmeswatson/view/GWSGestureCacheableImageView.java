package gws.grottworkshop.gwsholmeswatson.view;

import uk.co.senab.bitmapcache.CacheableBitmapDrawable;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;


import com.polites.android.GestureImageView;

/**
 * Modified GestureImageView as we need our bitmaps managed through 
 * our bitmap cache setup.Just added the appriopriate methods from 
 * CacheableImageView.
 * 
 * 
 * @author fredgrott
 *
 */
public class GWSGestureCacheableImageView extends GestureImageView {

	public GWSGestureCacheableImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public GWSGestureCacheableImageView(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		final Drawable previousDrawable = getDrawable();

        // Set new Drawable
        super.setImageDrawable(drawable);

        if (drawable != previousDrawable) {
            onDrawableSet(drawable);
            onDrawableUnset(previousDrawable);
        }
        
      
	}
	

	 private static void onDrawableSet(Drawable drawable) {
	        if (drawable instanceof CacheableBitmapDrawable) {
	            ((CacheableBitmapDrawable) drawable).setBeingUsed(true);
	        }
	    }
	 
	 private static void onDrawableUnset(final Drawable drawable) {
	        if (drawable instanceof CacheableBitmapDrawable) {
	            ((CacheableBitmapDrawable) drawable).setBeingUsed(false);
	        }
	    }
	 
	 @Override
	    public void setImageResource(int resId) {
	        final Drawable previousDrawable = getDrawable();
	        super.setImageResource(resId);
	        onDrawableUnset(previousDrawable);
	    }

	    @Override
	    public void setImageURI(Uri uri) {
	        final Drawable previousDrawable = getDrawable();
	        super.setImageURI(uri);
	        onDrawableUnset(previousDrawable);
	    }

	    @Override
	    protected void onDetachedFromWindow() {
	        super.onDetachedFromWindow();

	        // Will cause displayed bitmap wrapper to be 'free-able'
	        setImageDrawable(null);
	    }
}
