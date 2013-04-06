package pl.verdigo.libraries.drawer;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.Window;

/**
 * Bezel Swipe helper class.
 * 
 * @author Lukasz Milewski <lukasz.milewski@gmail.com>
 */
public class BezelSwipe
{

	public enum DispatchState
	{
		CALL_SUPER, FAKE_CANCEL, RETURN_FALSE, RETURN_TRUE;
	}

	private int mIgnoredTopHeight;

	private Drawer mDrawer;

	private boolean mIsBeingDragged = false;

	private Drawer mSecondDrawer;

	private boolean mSecondDrawerHandled = false;

	private int mStartX;

	private int mStartY;

	private int mLeftDragAreaWidth;

	private int mWindowWidth;

	/**
	 * Creates BezelSwipe object.
	 * 
	 * @param drawer Drawer
	 * @param window Window
	 * @param ignoredTopHeight Ignored height
	 * @param leftDragAreaWidth Left drag area
	 */
	public BezelSwipe(Drawer drawer, Window window, int ignoredTopHeight, int leftDragAreaWidth)
	{
		this(drawer, null, window, ignoredTopHeight, leftDragAreaWidth);
	}

	/**
	 * Creates BezelSwipe object for two Drawer objects.
	 * 
	 * @param drawer Drawer
	 * @param secondDrawer Second drawer
	 * @param window Window
	 * @param ignoredTopHeight Ignored height
	 * @param leftDragAreaWidth Left drag area
	 */
	public BezelSwipe(Drawer drawer, Drawer secondDrawer, Window window, int ignoredTopHeight, int leftDragAreaWidth)
	{
		mDrawer = drawer;
		mSecondDrawer = secondDrawer;
		mIgnoredTopHeight = ignoredTopHeight;
		mLeftDragAreaWidth = leftDragAreaWidth;
		mWindowWidth = window.getDecorView().getWidth();

		if (mSecondDrawer != null && mDrawer.isRightDrawer() == mSecondDrawer.isRightDrawer())
		{
			throw new UnsupportedOperationException("Drawers needs to be on opposite sides");
		}

		updateNotificationBarHeight(window);
	}

	private void updateNotificationBarHeight(Window window)
	{
		Rect rect = new Rect();
		window.getDecorView().getWindowVisibleDisplayFrame(rect);

		int notificationHeight = rect.top;
		mIgnoredTopHeight += notificationHeight;
	}

	private void cancelSwipe()
	{
		mStartX = -1;
		mStartY = -1;

		mSecondDrawerHandled = false;
	}

	/**
	 * Wrapper for dispatching touch events.
	 * 
	 * @param ev Motion event
	 * @return Return state to original method
	 */
	public DispatchState dispatchTouchEvent(MotionEvent ev)
	{
		int x = Math.round(ev.getX());
		int y = Math.round(ev.getY());

		if (!mIsBeingDragged && y < mIgnoredTopHeight)
		{
			return DispatchState.CALL_SUPER;
		}

		if (ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			mIsBeingDragged = false;

			if ((x < mLeftDragAreaWidth && !mDrawer.isRightDrawer()) || (x > mWindowWidth - mLeftDragAreaWidth && mDrawer.isRightDrawer()))
			{
				mStartX = x;
				mStartY = y;

				mSecondDrawerHandled = false;
			}
			else if (mSecondDrawer != null && ((x < mLeftDragAreaWidth && !mSecondDrawer.isRightDrawer())
					|| (x > mWindowWidth - mLeftDragAreaWidth && mSecondDrawer.isRightDrawer())))
			{
				mStartX = x;
				mStartY = y;

				mSecondDrawerHandled = true;
			}
			else
			{
				cancelSwipe();
			}

			if ((mDrawer.isVisible()) && mSecondDrawerHandled || (mSecondDrawer != null && mSecondDrawer.isVisible() && !mSecondDrawerHandled))
			{
				cancelSwipe();
			}

			return DispatchState.CALL_SUPER;
		}

		if (ev.getAction() == MotionEvent.ACTION_MOVE && mStartX >= 0 && !mIsBeingDragged)
		{
			if (Math.abs(y - mStartY) > mLeftDragAreaWidth)
			{
				cancelSwipe();
				return DispatchState.CALL_SUPER;
			}

			if (x - mStartX >= mLeftDragAreaWidth && !getDrawer().isRightDrawer())
			{
				mIsBeingDragged = true;
			}

			if (mStartX - x >= mLeftDragAreaWidth && getDrawer().isRightDrawer())
			{
				mIsBeingDragged = true;
			}

			return DispatchState.CALL_SUPER;
		}

		if (ev.getAction() == MotionEvent.ACTION_MOVE && mIsBeingDragged)
		{
			getDrawer().isMovable();
			getDrawer().setAllowCloseOnTouch(false);
			getDrawer().showWithTouch(x);
			getDrawer().onTouch(null, ev);

			return DispatchState.FAKE_CANCEL;
		}

		if (ev.getAction() == MotionEvent.ACTION_UP && mIsBeingDragged)
		{
			getDrawer().finishShowing();
		}

		if ((ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) && mIsBeingDragged)
		{
			getDrawer().onTouch(null, ev);
			getDrawer().setAllowCloseOnTouch(true);

			cancelSwipe();
			mIsBeingDragged = false;

			return DispatchState.FAKE_CANCEL;
		}

		return DispatchState.CALL_SUPER;
	}

	private Drawer getDrawer()
	{
		if (mSecondDrawerHandled)
		{
			return mSecondDrawer;
		}

		return mDrawer;
	}

}
