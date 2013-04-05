/**
 * Layout Pager Adapter
 *
 * Copyright 2012 Daniel Lupia–ez Casares <lupidan@gmail.com>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/

package com.daniel.lupianez.casares;

import java.util.ArrayList;
import java.util.List;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Pager adapter for easy lazy loading of layouts
 * @author Daniel Lupia–ez Casares
 *
 */
public class LayoutPagerAdapter extends PagerAdapter {

	
	
	
	//****************************************************
	// On View Loaded Listener
	/**
	 * Interface defining a On View Loaded listener. Called when a view is loaded in the adapter
	 * @author Daniel Lupia–ez Casares
	 *
	 */
	public static interface OnViewLoadedListener{
		/**
		 * This method is called when the adapter loads a view to set it into a position of the pager adapter 
		 * @param pagerAdapter The pager adapter
		 * @param loadedView The loaded view
		 * @param position The position of the loaded view in the pager adapter
		 */
		void onViewLoadedListener(LayoutPagerAdapter pagerAdapter, View loadedView, int position);
	}
	
	
	
	
	
	//****************************************************
	// Members
	/**
	 * The array of layout ids to be shown in the pager adapter
	 */
	private List<Integer> layoutIds = new ArrayList<Integer>();
	/**
	 * The on view loaded listener
	 */
	private OnViewLoadedListener onViewLoadedListener = null;
	
	
	
	
	
	
	
	
	//****************************************************
	// Constructors
	/**
	 * Constructor
	 */
	public LayoutPagerAdapter() {
		super();
	}
	
	/**
	 * Constructor
	 * @param layoutIds The layout ids list
	 */
	public LayoutPagerAdapter(List<Integer> layoutIds) {
		super();
		this.layoutIds = layoutIds;
	}

	
	
	
	

	//****************************************************
	// Setters/Getters
	/**
	 * @param layoutIds the layoutIds to set
	 */
	public void setLayoutIds(List<Integer> layoutIds) {
		this.layoutIds = layoutIds;
		//Notify data set changed
		notifyDataSetChanged();
	}
	
	/**
	 * @return The current listener for OnViewLoaded
	 */
	public OnViewLoadedListener getOnViewLoadedListener() {
		return onViewLoadedListener;
	}

	/**
	 * @param onViewLoadedListener The new on view loaded listener
	 */
	public void setOnViewLoadedListener(OnViewLoadedListener onViewLoadedListener) {
		this.onViewLoadedListener = onViewLoadedListener;
	}
	
	
	
	
	
	
	
	
	

	//****************************************************
	// Pager Adapter Overrides
	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#destroyItem(android.view.ViewGroup, int, java.lang.Object)
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		//When destroying the item, remove it from the view group
		View view = (View)object;
		container.removeView(view);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view.ViewGroup, int)
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		//Get the resource id for that position
		int resourceId = layoutIds.get(position);
		//Inflate the view and add it to the container
		View view = View.inflate(container.getContext(), resourceId, null);
		
		//Tell the listener
		if ((view != null) && (onViewLoadedListener != null))
			onViewLoadedListener.onViewLoadedListener(this, view, position);
		
		//Add the view
		if (view != null)
			container.addView(view);
		
		//Finally, return the object
		return view;
		
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		//Return the size of the layout Ids
		return layoutIds.size();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#isViewFromObject(android.view.View, java.lang.Object)
	 */
	@Override
	public boolean isViewFromObject(View view, Object obj) {
		//Check if view and object are the same
		return (view == obj);
	}

	

}
