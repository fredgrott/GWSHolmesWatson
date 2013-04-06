package com.cocosw.undobar;

import com.actionbarsherlock.R.drawable;

public class UndoBarStyle {

	int iconRes;
	int titleRes;
	int bgRes = drawable.ub_undobar;
	long duration = 5000;

	public UndoBarStyle(final int icon, final int title) {
		iconRes = icon;
		titleRes = title;
	}

	public UndoBarStyle(final int icon, final int title, final long duration) {
		this(icon, title);
		this.duration = duration;
	}

	public UndoBarStyle(final int icon, final int title, final int bg,
			final long duration) {
		this(icon, title, duration);
		bgRes = bg;
	}

}
