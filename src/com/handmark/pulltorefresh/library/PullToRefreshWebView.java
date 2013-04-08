/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library;

import com.actionbarsherlock.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

// TODO: Auto-generated Javadoc
/**
 * The Class PullToRefreshWebView.
 */
@SuppressLint("FloatMath")
public class PullToRefreshWebView extends PullToRefreshBase<WebView> {

	/** The Constant defaultOnRefreshListener. */
	private static final OnRefreshListener<WebView> defaultOnRefreshListener = new OnRefreshListener<WebView>() {

		@Override
		public void onRefresh(PullToRefreshBase<WebView> refreshView) {
			refreshView.getRefreshableView().reload();
		}

	};

	/** The default web chrome client. */
	private final WebChromeClient defaultWebChromeClient = new WebChromeClient() {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				onRefreshComplete();
			}
		}

	};

	/**
	 * Instantiates a new pull to refresh web view.
	 *
	 * @param context the context
	 */
	public PullToRefreshWebView(Context context) {
		super(context);

		/**
		 * Added so that by default, Pull-to-Refresh refreshes the page
		 */
		setOnRefreshListener(defaultOnRefreshListener);
		mRefreshableView.setWebChromeClient(defaultWebChromeClient);
	}

	/**
	 * Instantiates a new pull to refresh web view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public PullToRefreshWebView(Context context, AttributeSet attrs) {
		super(context, attrs);

		/**
		 * Added so that by default, Pull-to-Refresh refreshes the page
		 */
		setOnRefreshListener(defaultOnRefreshListener);
		mRefreshableView.setWebChromeClient(defaultWebChromeClient);
	}

	/**
	 * Instantiates a new pull to refresh web view.
	 *
	 * @param context the context
	 * @param mode the mode
	 */
	public PullToRefreshWebView(Context context, Mode mode) {
		super(context, mode);

		/**
		 * Added so that by default, Pull-to-Refresh refreshes the page
		 */
		setOnRefreshListener(defaultOnRefreshListener);
		mRefreshableView.setWebChromeClient(defaultWebChromeClient);
	}

	/**
	 * Instantiates a new pull to refresh web view.
	 *
	 * @param context the context
	 * @param mode the mode
	 * @param style the style
	 */
	public PullToRefreshWebView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);

		/**
		 * Added so that by default, Pull-to-Refresh refreshes the page
		 */
		setOnRefreshListener(defaultOnRefreshListener);
		mRefreshableView.setWebChromeClient(defaultWebChromeClient);
	}

	/* (non-Javadoc)
	 * @see com.handmark.pulltorefresh.library.PullToRefreshBase#getPullToRefreshScrollDirection()
	 */
	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	/* (non-Javadoc)
	 * @see com.handmark.pulltorefresh.library.PullToRefreshBase#createRefreshableView(android.content.Context, android.util.AttributeSet)
	 */
	@Override
	protected WebView createRefreshableView(Context context, AttributeSet attrs) {
		WebView webView;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			webView = new InternalWebViewSDK9(context, attrs);
		} else {
			webView = new WebView(context, attrs);
		}

		webView.setId(R.id.webview);
		return webView;
	}

	/* (non-Javadoc)
	 * @see com.handmark.pulltorefresh.library.PullToRefreshBase#isReadyForPullStart()
	 */
	@Override
	protected boolean isReadyForPullStart() {
		return mRefreshableView.getScrollY() == 0;
	}

	/* (non-Javadoc)
	 * @see com.handmark.pulltorefresh.library.PullToRefreshBase#isReadyForPullEnd()
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected boolean isReadyForPullEnd() {
		float exactContentHeight = FloatMath.floor(mRefreshableView.getContentHeight() * mRefreshableView.getScale());
		return mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView.getHeight());
	}

	/* (non-Javadoc)
	 * @see com.handmark.pulltorefresh.library.PullToRefreshBase#onPtrRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
		super.onPtrRestoreInstanceState(savedInstanceState);
		mRefreshableView.restoreState(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see com.handmark.pulltorefresh.library.PullToRefreshBase#onPtrSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onPtrSaveInstanceState(Bundle saveState) {
		super.onPtrSaveInstanceState(saveState);
		mRefreshableView.saveState(saveState);
	}

	/**
	 * The Class InternalWebViewSDK9.
	 */
	@TargetApi(9)
	final class InternalWebViewSDK9 extends WebView {

		// WebView doesn't always scroll back to it's edge so we add some
		// fuzziness
		/** The Constant OVERSCROLL_FUZZY_THRESHOLD. */
		static final int OVERSCROLL_FUZZY_THRESHOLD = 2;

		// WebView seems quite reluctant to overscroll so we use the scale
		// factor to scale it's value
		/** The Constant OVERSCROLL_SCALE_FACTOR. */
		static final float OVERSCROLL_SCALE_FACTOR = 1.5f;

		/**
		 * Instantiates a new internal web view sd k9.
		 *
		 * @param context the context
		 * @param attrs the attrs
		 */
		public InternalWebViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		/* (non-Javadoc)
		 * @see android.view.View#overScrollBy(int, int, int, int, int, int, int, int, boolean)
		 */
		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshWebView.this, deltaX, scrollX, deltaY, scrollY,
					getScrollRange(), OVERSCROLL_FUZZY_THRESHOLD, OVERSCROLL_SCALE_FACTOR, isTouchEvent);

			return returnValue;
		}

		/**
		 * Gets the scroll range.
		 *
		 * @return the scroll range
		 */
		@SuppressWarnings("deprecation")
		private int getScrollRange() {
			return (int) Math.max(0, FloatMath.floor(mRefreshableView.getContentHeight() * mRefreshableView.getScale())
					- (getHeight() - getPaddingBottom() - getPaddingTop()));
		}
	}
}
