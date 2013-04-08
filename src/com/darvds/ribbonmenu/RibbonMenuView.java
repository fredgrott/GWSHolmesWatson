package com.darvds.ribbonmenu;


import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.R;

// TODO: Auto-generated Javadoc
/**
 * The Class RibbonMenuView.
 */
public class RibbonMenuView extends LinearLayout {

	/** The rbm list view. */
	private ListView rbmListView;
	
	/** The rbm outside view. */
	private View rbmOutsideView;
	
	/** The callback. */
	private iRibbonMenuCallback callback;
	
	/** The menu items. */
	private static ArrayList<RibbonMenuItem> menuItems;
	
	
	/**
	 * Instantiates a new ribbon menu view.
	 *
	 * @param context the context
	 */
	public RibbonMenuView(Context context) {
		super(context);
		
		
		load();
	}
	
	/**
	 * Instantiates a new ribbon menu view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public RibbonMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);

		load();
	}


	
	
	/**
	 * Load.
	 */
	private void load(){
		
		if(isInEditMode()) return;
		
		
		inflateLayout();		
		
		initUi();
		
		
	}
	
	
	/**
	 * Inflate layout.
	 */
	private void inflateLayout(){
		
		
		
		
		try{
			LayoutInflater.from(getContext()).inflate(R.layout.rbm_menu, this, true);
			} catch(Exception e){
				
			}	
		
		
	}
	
	/**
	 * Inits the ui.
	 */
	private void initUi(){
		
		rbmListView = (ListView) findViewById(R.id.rbm_listview);
		rbmOutsideView = (View) findViewById(R.id.rbm_outside_view);
				
		rbmOutsideView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideMenu();
				
			}
		});
		
		
		rbmListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(callback != null)					
					callback.RibbonMenuItemClick(menuItems.get(position).id);
				
				hideMenu();
			}
			
		});
			
		
	}
	
	
	/**
	 * Sets the menu click callback.
	 *
	 * @param callback the new menu click callback
	 */
	public void setMenuClickCallback(iRibbonMenuCallback callback){
		this.callback = callback;
	}
	
	/**
	 * Sets the menu items.
	 *
	 * @param menu the new menu items
	 */
	public void setMenuItems(int menu){
		
		parseXml(menu);
		
		if(menuItems != null && menuItems.size() > 0)
		{
			rbmListView.setAdapter(new Adapter());
			
		}
		
		
		
	
	}
	
	
	/**
	 * Sets the background resource.
	 *
	 * @param resource the new background resource
	 */
	public void setBackgroundResource(int resource){
		rbmListView.setBackgroundResource(resource);
		
	}
	
	
	
	
	/**
	 * Show menu.
	 */
	public void showMenu(){
		rbmOutsideView.setVisibility(View.VISIBLE);	
				
		rbmListView.setVisibility(View.VISIBLE);	
		rbmListView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rbm_in_from_left));
		
	}
	
	
	/**
	 * Hide menu.
	 */
	public void hideMenu(){
		
		rbmOutsideView.setVisibility(View.GONE);
		rbmListView.setVisibility(View.GONE);	
		
		rbmListView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rbm_out_to_left));
		
	}
	
	
	/**
	 * Toggle menu.
	 */
	public void toggleMenu(){
		
		if(rbmOutsideView.getVisibility() == View.GONE){
			showMenu();
		} else {
			hideMenu();
		}
	}
	
	
	/**
	 * Parses the xml.
	 *
	 * @param menu the menu
	 */
	private void parseXml(int menu){
		
		menuItems = new ArrayList<RibbonMenuView.RibbonMenuItem>();
		
		
		try{
			XmlResourceParser xpp = getResources().getXml(menu);
			
			xpp.next();
			int eventType = xpp.getEventType();
			
			
			while(eventType != XmlPullParser.END_DOCUMENT){
				
				if(eventType == XmlPullParser.START_TAG){
					
					String elemName = xpp.getName();
						
					
					
					if(elemName.equals("item")){
											
						
						String textId = xpp.getAttributeValue("http://schemas.android.com/apk/res/android", "title");
						String iconId = xpp.getAttributeValue("http://schemas.android.com/apk/res/android", "icon");
						String resId = xpp.getAttributeValue("http://schemas.android.com/apk/res/android", "id");
						
						
						RibbonMenuItem item = new RibbonMenuItem();
						item.id = Integer.valueOf(resId.replace("@", ""));
						item.text = resourceIdToString(textId);
						item.icon = Integer.valueOf(iconId.replace("@", ""));
						
						menuItems.add(item);
						
					}
					
					
					
				}
				
				eventType = xpp.next();
				
				
			}
			
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	/**
	 * Resource id to string.
	 *
	 * @param text the text
	 * @return the string
	 */
	private String resourceIdToString(String text){
		
		if(!text.contains("@")){
			return text;
		} else {
									
			String id = text.replace("@", "");
									
			return getResources().getString(Integer.valueOf(id));
			
		}
		
	}
	
	
	/**
	 * Checks if is menu visible.
	 *
	 * @return true, if is menu visible
	 */
	public boolean isMenuVisible(){		
		return rbmOutsideView.getVisibility() == View.VISIBLE;		
	}
	
		
	
	
	/**
	 * On restore instance state.
	 *
	 * @param state the state
	 */
	@Override 
	protected void onRestoreInstanceState(Parcelable state)	{
	    SavedState ss = (SavedState)state;
	    super.onRestoreInstanceState(ss.getSuperState());

	    if (ss.bShowMenu)
	        showMenu();
	    else
	        hideMenu();
	}
	
	

	/**
	 * On save instance state.
	 *
	 * @return the parcelable
	 */
	@Override 
	protected Parcelable onSaveInstanceState()	{
	    Parcelable superState = super.onSaveInstanceState();
	    SavedState ss = new SavedState(superState);

	    ss.bShowMenu = isMenuVisible();

	    return ss;
	}

	/**
	 * The Class SavedState.
	 */
	static class SavedState extends BaseSavedState {
	    
    	/** The b show menu. */
    	boolean bShowMenu;

	    /**
    	 * Instantiates a new saved state.
    	 *
    	 * @param superState the super state
    	 */
    	SavedState(Parcelable superState) {
	        super(superState);
	    }

	    /**
    	 * Instantiates a new saved state.
    	 *
    	 * @param in the in
    	 */
    	private SavedState(Parcel in) {
	        super(in);
	        bShowMenu = (in.readInt() == 1);
	    }

	    /* (non-Javadoc)
    	 * @see android.view.AbsSavedState#writeToParcel(android.os.Parcel, int)
    	 */
    	@Override
	    public void writeToParcel(Parcel out, int flags) {
	        super.writeToParcel(out, flags);
	        out.writeInt(bShowMenu ? 1 : 0);
	    }

	    /** The Constant CREATOR. */
    	public static final Parcelable.Creator<SavedState> CREATOR
	            = new Parcelable.Creator<SavedState>() {
	        public SavedState createFromParcel(Parcel in) {
	            return new SavedState(in);
	        }

	        public SavedState[] newArray(int size) {
	            return new SavedState[size];
	        }
	    };
	}
	
	
	
	/**
	 * The Class RibbonMenuItem.
	 */
	class RibbonMenuItem{
		
		/** The id. */
		int id;
		
		/** The text. */
		String text;
		
		/** The icon. */
		int icon;
		
	}
	
	
	
	/**
	 * The Class Adapter.
	 */
	private class Adapter extends BaseAdapter {

		/** The inflater. */
		private LayoutInflater inflater;
		
		/**
		 * Instantiates a new adapter.
		 */
		public Adapter(){
			inflater = LayoutInflater.from(getContext());
		}
		
		
		
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			
			return menuItems.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			
			return null;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			
			return 0;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			final ViewHolder holder;
			
			if(convertView == null || convertView instanceof TextView){
				convertView = inflater.inflate(R.layout.rbm_item, null);
				
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.rbm_item_icon);
				holder.text = (TextView) convertView.findViewById(R.id.rbm_item_text);
						
				convertView.setTag(holder);
			
			} else {
			
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.image.setImageResource(menuItems.get(position).icon);
			holder.text.setText(menuItems.get(position).text);
			
			
			return convertView;
		}
		
		
		/**
		 * The Class ViewHolder.
		 */
		class ViewHolder {
			
			/** The text. */
			TextView text;
			
			/** The image. */
			ImageView image;
		
		}
			
		
		
		
	}
	


}
