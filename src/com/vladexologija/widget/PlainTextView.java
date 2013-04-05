package com.vladexologija.widget;

import com.actionbarsherlock.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Plain text view
 * @author vladimir.glusac.rill
 */
public class PlainTextView extends LinearLayout{
	
	private LayoutInflater inflater;
	private TextView item;
	private View spacer;
	

	public PlainTextView(Context context) {
		super(context);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		intitializer();
	}
	
	public void setText(String text){
		item.setText(text);
	}
	
	public void showSpacer(){
		spacer.setVisibility(View.VISIBLE);
	}
	
	private void intitializer() {
		 inflater.inflate(R.layout.gtv_plain_text_view, this);
		 item = (TextView) findViewById(R.id.plain_text_view);
		 spacer = (View) findViewById(R.id.spacer);
		 spacer.setVisibility(View.GONE);
	}
		

}
