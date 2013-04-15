package gws.grottworkshop.gwsholmeswatson.view;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.actionbarsherlock.R;
import com.viewpagerindicator.CirclePageIndicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

// TODO: Auto-generated Javadoc
/**
 * The Class PaginatedGallery.
 */
public class PaginatedGallery extends ViewGroup {
	
	/** The gwslog. */
	private Logger GWSLOG = LoggerFactory.getLogger(PaginatedGallery.class);

	/** The m pager. */
	ViewPager mPager;
	
	/** The m pager indicator. */
	CirclePageIndicator mPagerIndicator;
	
	/** The m item click listener. */
	OnItemClickListener mItemClickListener;
	
	/**
	 * Instantiates a new paginated gallery.
	 *
	 * @param context the context
	 */
	public PaginatedGallery(Context context) {
		super(context);
		init(context);
	}
	
	/**
	 * Instantiates a new paginated gallery.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public PaginatedGallery(Context context, AttributeSet attrs ) {
		super(context, attrs);
		init(context, attrs);
	}
	
	/**
	 * Inits the.
	 *
	 * @param context the context
	 */
	private void init(Context context) {
		mPager = new ViewPager(context);
		mPagerIndicator = new CirclePageIndicator(context);
		addView(mPager);
		addView(mPagerIndicator);
	}
	
	/**
	 * Inits the.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	private void init(Context context, AttributeSet attrs ) {
		init(context);
		TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.PaginatedGallery);
        //Don't forget this
        a.recycle();
	}

	/**
	 * Sets the adapter.
	 *
	 * @param adapter the new adapter
	 */
	public void setAdapter(PaginatedGalleryAdapter adapter) {
		mPager.setAdapter(adapter);
		mPagerIndicator.setViewPager(mPager);
	}

	/**
	 * On layout.
	 *
	 * @param changed the changed
	 * @param l the l
	 * @param t the t
	 * @param r the r
	 * @param b the b
	 * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		GWSLOG.info( "Changed: " + changed + " Layout top : "+ t + " bottom : " + b + " Left : "+ l + " Right : "+r);
		
		b= b-t;
		t = getPaddingTop();
		View child = getChildAt(0);

	
		GWSLOG.info( "Layout top : "+ t + " bottom : " + b + " Left : "+ l + " Right : "+r);
		GWSLOG.info( "Paddingtop: "+getPaddingTop()+", PaddingBottom: "+getPaddingBottom()+", PaddingLeft: "+getPaddingLeft()+", PaddingRight: "+getPaddingRight());
		
		int pagerBottom = t + ((PaginatedGalleryAdapter) mPager.getAdapter()).getLayoutHeight();
		
		GWSLOG.info( "Pager layout, l: "+l+", t: "+t+", r: "+r+", b: "+ pagerBottom);
		child.layout(l, t, r, pagerBottom);
		
		child = getChildAt(1);
		GWSLOG.info( " Pager bottom: "+pagerBottom + ", indicator bottom : "+ (pagerBottom+child.getMeasuredHeight()));
		child.layout(l, pagerBottom, r, pagerBottom+child.getMeasuredHeight());
	}
	
	/**
	 * On measure.
	 *
	 * @param widthMeasureSpec the width measure spec
	 * @param heightMeasureSpec the height measure spec
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		View child = getChildAt(0);
		measureChild(child, widthMeasureSpec, MeasureSpec.makeMeasureSpec(getPaddingTop()+((PaginatedGalleryAdapter) mPager.getAdapter()).getLayoutHeight(), MeasureSpec.EXACTLY));
		
		child = getChildAt(1);
		measureChild(child, widthMeasureSpec, heightMeasureSpec);

		GWSLOG.info( "Setting measured dimensions: " + MeasureSpec.getSize(widthMeasureSpec) + ", " + (getChildAt(0).getMeasuredHeight() + child.getMeasuredHeight()));

		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), getPaddingTop() + getPaddingBottom() + ((PaginatedGalleryAdapter) mPager.getAdapter()).getLayoutHeight() + child.getMeasuredHeight());
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
