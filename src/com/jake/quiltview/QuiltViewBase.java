package com.jake.quiltview;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;

import android.util.DisplayMetrics;

import android.view.View;

import android.widget.FrameLayout;
import com.gridlayout.*;


// TODO: Auto-generated Javadoc
/**
 * The Class QuiltViewBase.
 */
@SuppressLint("ViewConstructor")
public class QuiltViewBase extends GridLayout {
	
	/** The size. */
	public int[] size;
	
	/** The columns. */
	public int columns;
	
	/** The rows. */
	public int rows;
	
	/** The view_width. */
	public int view_width = -1;
	
	/** The view_height. */
	public int view_height = -1;
	
	/** The is vertical. */
	public boolean isVertical = true;
	
	/** The views. */
	public ArrayList<View> views;
	
	/**
	 * Instantiates a new quilt view base.
	 *
	 * @param context the context
	 * @param isVertical the is vertical
	 */
	public QuiltViewBase(Context context, boolean isVertical) {
		super(context);
		this.isVertical = isVertical;
		if(view_width == -1){
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();
			int width = metrics.widthPixels;
			int height = metrics.heightPixels - 120;
			view_width = width - this.getPaddingLeft() - this.getPaddingRight();
			view_height = height - this.getPaddingTop() - this.getPaddingBottom();
		}
		views = new ArrayList<View>();
		setup();
	}
	
	/**
	 * Setup.
	 */
	public void setup(){
		if(isVertical){
			setupVertical();
		} else {
			setupHorizontal();
		}
	}
	
	/**
	 * Setup vertical.
	 */
	@SuppressWarnings("static-access")
	public void setupVertical(){
		size = getBaseSizeVertical();
		this.setColumnCount(columns);
		this.setRowCount(-1);
		this.setOrientation(this.HORIZONTAL);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		this.setLayoutParams(params);
	}
	
	/**
	 * Setup horizontal.
	 */
	@SuppressWarnings("static-access")
	public void setupHorizontal(){
		size = getBaseSizeHorizontal();
		this.setRowCount(rows);
		this.setColumnCount(-1);
		this.setOrientation(this.VERTICAL);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		this.setLayoutParams(params);
	}
	
	/**
	 * Adds the patch.
	 *
	 * @param view the view
	 */
	public void addPatch(View view){
		
		int count = this.getChildCount();
		
		QuiltViewPatch child = QuiltViewPatch.init(count);
		
		GridLayout.LayoutParams params = new GridLayout.LayoutParams();
		params.width = size[0]*child.width_ratio;
		params.height = size[1]*child.height_ratio;
		params.rowSpec = GridLayout.spec(Integer.MIN_VALUE, child.height_ratio);
		params.columnSpec = GridLayout.spec(Integer.MIN_VALUE, child.width_ratio);
		view.setLayoutParams(params);
		addView(view);
		views.add(view);
	}
	
	/**
	 * Refresh.
	 */
	public void refresh(){
		this.removeAllViewsInLayout();
		setup();
		for(View view : views){
			addPatch(view);
		}
	}
	
	/**
	 * Gets the base size.
	 *
	 * @return the base size
	 */
	public int[] getBaseSize(){
		int[] size = new int[2];
		
		float width_height_ratio = (3.0f/4.0f);
		
		int base_width = getBaseWidth();
		int base_height = (int) (base_width*width_height_ratio);
		
		size[0] = base_width; // width
		size[1] = base_height; // height
		return size;
	}
	
	/**
	 * Gets the base size vertical.
	 *
	 * @return the base size vertical
	 */
	public int[] getBaseSizeVertical(){
		int[] size = new int[2];
		
		float width_height_ratio = (3.0f/4.0f);
		
		int base_width = getBaseWidth();
		int base_height = (int) (base_width*width_height_ratio);
		
		size[0] = base_width; // width
		size[1] = base_height; // height
		return size;
	}
	
	/**
	 * Gets the base size horizontal.
	 *
	 * @return the base size horizontal
	 */
	public int[] getBaseSizeHorizontal(){
		int[] size = new int[2];
		
		float width_height_ratio = (4.0f/3.0f);
		
		int base_height = getBaseHeight();
		int base_width = (int) (base_height*width_height_ratio);
		
		size[0] = base_width; // width
		size[1] = base_height; // height
		return size;
	}
	
	/**
	 * Gets the base width.
	 *
	 * @return the base width
	 */
	public int getBaseWidth(){
		if(view_width < 500){
			columns = 2;
		} else if(view_width < 801){
			columns = 3;
		} else if(view_width < 1201){
			columns = 4;
		} else if(view_width < 1601){
			columns = 5;
		} else {
			columns = 6;
		}
		return (view_width / columns);
	}
	
	/**
	 * Gets the base height.
	 *
	 * @return the base height
	 */
	public int getBaseHeight(){
		if(view_height < 350){
			rows = 2;
		} else if(view_height < 650){
			rows = 3;
		} else if(view_height < 1050){
			rows = 4;
		} else if(view_height < 1250){
			rows = 5;
		} else {
			rows = 6;
		}
		return (view_height / rows);
	}
	 
	 /*@Override 
	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    
	    view_width = parentWidth;
        view_height = parentHeight;
        
        setup(isVertical);
	 }*/
	
	/* (non-Javadoc)
 	 * @see android.view.View#onSizeChanged(int, int, int, int)
 	 */
 	@Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
            super.onSizeChanged(xNew, yNew, xOld, yOld);
            view_width = xNew;
            view_height = yNew;
    }
	

}
