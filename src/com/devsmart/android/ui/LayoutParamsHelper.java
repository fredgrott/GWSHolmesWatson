package com.devsmart.android.ui;

import android.view.ViewGroup.LayoutParams;

public class LayoutParamsHelper {

	public static LayoutParams createWrapWrap() {
		LayoutParams retval = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		return retval;
	}
	
	@SuppressWarnings("deprecation")
	public static LayoutParams createFillWrap() {
		LayoutParams retval = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		return retval;
	}
	
}
