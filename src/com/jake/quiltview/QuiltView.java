package com.jake.quiltview;
import java.util.ArrayList;

import com.actionbarsherlock.R;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;

import android.util.AttributeSet;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;


// TODO: Auto-generated Javadoc
/**
 * The Class QuiltView.
 */
@SuppressLint("Recycle")
public class QuiltView extends FrameLayout implements OnGlobalLayoutListener {

	/** The quilt. */
	public QuiltViewBase quilt;
	
	/** The scroll. */
	public ViewGroup scroll;
	
	/** The padding. */
	public int padding = 5;
	
	/** The is vertical. */
	public boolean isVertical = false;
	
	/** The views. */
	public ArrayList<View> views;
	
	/**
	 * Instantiates a new quilt view.
	 *
	 * @param context the context
	 * @param isVertical the is vertical
	 */
	public QuiltView(Context context,boolean isVertical) {
		super(context);
		this.isVertical = isVertical;
		setup();
	}
	
	/**
	 * Instantiates a new quilt view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public QuiltView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
			    R.styleable.QuiltView);
			 
		String orientation = a.getString(R.styleable.QuiltView_scrollOrientation);
		if(orientation != null){
			if(orientation.equals("vertical")){
				isVertical = true;
			} else {
				isVertical = false;
			}
		}
		setup();
	}
	
	/**
	 * Setup.
	 */
	public void setup(){
		views = new ArrayList<View>();
		
		if(isVertical){
			scroll = new ScrollView(this.getContext());
		} else {
			scroll = new HorizontalScrollView(this.getContext());
		}
		quilt = new QuiltViewBase(getContext(), isVertical);
		scroll.addView(quilt);
		this.addView(scroll);
		
	}
	
	/**
	 * Adds the patch images.
	 *
	 * @param images the images
	 */
	public void addPatchImages(ArrayList<ImageView> images){
		
		for(ImageView image: images){
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
			image.setLayoutParams(params);
			
			LinearLayout wrapper = new LinearLayout(this.getContext());
			wrapper.setPadding(padding, padding, padding, padding);
			wrapper.addView(image);
			quilt.addPatch(wrapper);
		}
	}
	
	/**
	 * Adds the patch views.
	 *
	 * @param views_a the views_a
	 */
	public void addPatchViews(ArrayList<View> views_a){
		for(View view: views_a){
			quilt.addPatch(view);
		}
	}
	
	/**
	 * Adds the patches on layout.
	 */
	public void addPatchesOnLayout(){
		for(View view: views){
			quilt.addPatch(view);
		}
	}
	
	/**
	 * Removes the quilt.
	 *
	 * @param view the view
	 */
	public void removeQuilt(View view){
		quilt.removeView(view);
	}
	
	/**
	 * Sets the child padding.
	 *
	 * @param padding the new child padding
	 */
	public void setChildPadding(int padding){
		this.padding = padding;
	}
	
	/**
	 * Refresh.
	 */
	public void refresh(){
		quilt.refresh();
	}
	
	/**
	 * Sets the orientation.
	 *
	 * @param isVertical the new orientation
	 */
	public void setOrientation(boolean isVertical){
		this.isVertical = isVertical;
	}

	
	/* (non-Javadoc)
	 * @see android.view.ViewTreeObserver.OnGlobalLayoutListener#onGlobalLayout()
	 */
	@Override
	public void onGlobalLayout() {
		//addPatchesOnLayout();
	}
}
