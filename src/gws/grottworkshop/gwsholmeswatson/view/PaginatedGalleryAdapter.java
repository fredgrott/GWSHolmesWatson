package gws.grottworkshop.gwsholmeswatson.view;




import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.view.WindowManager;

// TODO: Auto-generated Javadoc
/**
 * The Class PaginatedGalleryAdapter.
 */
public class PaginatedGalleryAdapter extends PagerAdapter {
	
	/** The gwslog. */
	private Logger GWSLOG = LoggerFactory.getLogger(PaginatedGalleryAdapter.class);
	
	/** The view pages. */
	private int viewPages;
	
	/** The views per page. */
	private int viewsPerPage;
	
	/** The images. */
	private static List<?> images;
	
	/** The is image url. */
	private boolean isImageUrl = false;
	
	/** The context. */
	private Context context;
	
	/** The screen width. */
	private int screenWidth;
	
	/** The error drawable. */
	private Drawable errorDrawable;
	
	/** The m item click listener. */
	private OnItemClickListener mItemClickListener;
    
	/**
	 * Instantiates a new paginated gallery adapter.
	 *
	 * @param context the Context of the gallery
	 * @param list the Drawables to display in the gallery
	 * @param viewsPerPage number of views per page in the gallery
	 */
    @SuppressWarnings("deprecation")
	public PaginatedGalleryAdapter ( final Context context, final List<Drawable> list, final int viewsPerPage ) {
    	this.context = context;
    	this.viewsPerPage = viewsPerPage;
    	viewPages = (int) Math.ceil((double) list.size()/viewsPerPage );
    	images = list;
    	screenWidth = ( (WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    	if (String.class.isInstance(list.get(0))) {
    		isImageUrl = true;
    	}
    }
    
    /**
     * Instantiates a new paginated gallery adapter.
     *
     * @param context the Context of the gallery
     * @param list List of URLs to load images from
     * @param viewsPerPage number of views per page in the gallery
     * @param errorDrawable Drawable to display while the images are loading and if there is an error
     */
    @SuppressWarnings("deprecation")
	public PaginatedGalleryAdapter ( final Context context, final List<String> list, final int viewsPerPage, Drawable errorDrawable  ) {
    	this.context = context;
    	this.viewsPerPage = viewsPerPage;
    	this.errorDrawable = errorDrawable;
    	viewPages = (int) Math.ceil((double) list.size()/viewsPerPage );
    	images = list;
    	screenWidth = ( (WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		isImageUrl = true;
    }
    
    /**
     * Gets the count.
     *
     * @return the count
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return viewPages;
    }

	/**
	 * Instantiate item.
	 *
	 * @param collection the collection
	 * @param position the position
	 * @return the object
	 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view.View, int)
	 */
	@Override
	public Object instantiateItem(View collection, int position) {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenWidth/viewsPerPage));
		
		int size = images.size();
		

		for (int i = 0 ; i < viewsPerPage; i++ ) {
			final int index = position + (position * (viewsPerPage - 1))+ i;
			GWSLOG.info( "Index: "+index + " , Size : "+size);
			if (index < size ) {
				View imageView;
				if (isImageUrl) {
					imageView = new RemoteImageView(context, (String) images.get(index), errorDrawable, errorDrawable, true);
				} else {
					imageView = new GWSGestureCacheableImageView(context);
					( (GWSGestureCacheableImageView) imageView).setImageDrawable((Drawable) images.get(index));
				}
				

				imageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (mItemClickListener != null ) {
							mItemClickListener.onItemClick(v, index);
						}
					}
				});
				imageView.setLayoutParams(new LayoutParams(screenWidth/viewsPerPage, screenWidth/viewsPerPage));
				imageView.setPadding(10, 10, 10, 10);
				layout.addView(imageView);
			}
		}
		
		((ViewPager) collection).addView(layout);
		
		return layout;
	}
	
	/**
	 * Destroy item.
	 *
	 * @param collection the collection
	 * @param position the position
	 * @param view the view
	 * @see android.support.v4.view.PagerAdapter#destroyItem(android.view.View, int, java.lang.Object)
	 */
	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((LinearLayout) view);
	}
	
	/**
	 * Checks if is view from object.
	 *
	 * @param view the view
	 * @param object the object
	 * @return true, if is view from object
	 * @see android.support.v4.view.PagerAdapter#isViewFromObject(android.view.View, java.lang.Object)
	 */
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (LinearLayout) object;
	}

	/**
	 * Restore state.
	 *
	 * @param arg0 the arg0
	 * @param arg1 the arg1
	 * @see android.support.v4.view.PagerAdapter#restoreState(android.os.Parcelable, java.lang.ClassLoader)
	 */
	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {}

	/**
	 * Save state.
	 *
	 * @return the parcelable
	 * @see android.support.v4.view.PagerAdapter#saveState()
	 */
	@Override
	public Parcelable saveState() { 
		return null;
	}

	/**
	 * Start update.
	 *
	 * @param arg0 the arg0
	 * @see android.support.v4.view.PagerAdapter#startUpdate(android.view.View)
	 */
	@Override
	public void startUpdate(View arg0) { }
	
	/**
	 * Finish update.
	 *
	 * @param arg0 the arg0
	 * @see android.support.v4.view.PagerAdapter#finishUpdate(android.view.View)
	 */
	@Override
	public void finishUpdate(View arg0) {}

	/**
	 * Gets the layout height.
	 *
	 * @return the layout height
	 */
	public int getLayoutHeight() {
		GWSLOG.info( "Pager height : " + screenWidth/viewsPerPage);
		return screenWidth/viewsPerPage;
	}
	
	/**
	 * The listener interface for receiving onItemClick events.
	 * The class that is interested in processing a onItemClick
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addOnItemClickListener<code> method. When
	 * the onItemClick event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OnItemClickEvent
	 */
	public interface OnItemClickListener {
		
		/**
		 * On item click.
		 *
		 * @param view the view
		 * @param position the position
		 */
		void onItemClick(View view, int position);
	}
		
	/**
	 * Sets the on item click listener.
	 *
	 * @param listener the new on item click listener
	 */
	public void setOnItemClickListener(OnItemClickListener listener) {
		mItemClickListener = listener;
	}
}
