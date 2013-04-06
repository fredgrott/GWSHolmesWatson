package pl.verdigo.libraries.drawer.internal;

import pl.verdigo.libraries.drawer.Drawer;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.actionbarsherlock.internal.nineoldandroids.view.animation.AnimatorProxy;

public class RightDrawer extends Drawer
{

	private DrawerProxy mDrawerProxy;

	public RightDrawer(Context context, int layout)
	{
		super(context, layout);
	}

	protected int getTargetPosition()
	{
		return 0 - getDrawerWidth();
	}

	protected boolean isRightDrawer()
	{
		return true;
	}

	/**
	 * Creates DrawerProxy object.
	 * 
	 * @return DrawerProxy object
	 */
	protected IDrawerProxy createDrawerProxy()
	{
		if (mDrawerProxy == null)
		{
			mDrawerProxy = new DrawerProxy(mDrawerActivity, mDrawer, mDrawerShadow, mDrawerContent);
		}

		return mDrawerProxy;
	}

	/**
	 * Handles touch events. If {@link Drawer} is not movable all touch events
	 * are ignored.
	 * 
	 * @param view Touched view
	 * @param event Event
	 */
	public boolean onTouch(View view, MotionEvent event)
	{
		if (!mMovable)
		{
			return false;
		}

		int drawerWidth = getDrawerWidth();

		if (event.getAction() == MotionEvent.ACTION_UP && isAllowCloseOnTouch())
		{
			int border = drawerWidth - (drawerWidth / 3);

			if (mMovedPosition > 0 - border)
			{
				cancel();
				return true;
			}
			else if (mMovedPosition >= drawerWidth && !mMovedBeyondMargin)
			{
				cancel();
				return true;
			}

			mMovedBeyondMargin = false;
			if (mMovedPosition < drawerWidth && isAnimationEnabled())
			{
				showWithAnimation();
			}
			else if (!isAnimationEnabled())
			{
				IDrawerProxy proxy = createDrawerProxy();
				proxy.setPosition(drawerWidth);
			}

			mDeviation = 0;

			mMoved = false;
			mMovedPosition = 0;

			return true;
		}
		if (event.getAction() == MotionEvent.ACTION_UP && !isAllowCloseOnTouch())
		{
			mMovedBeyondMargin = false;
			if (isAnimationEnabled())
			{
				showWithAnimation();
			}
			else
			{
				IDrawerProxy proxy = createDrawerProxy();
				proxy.setPosition(drawerWidth);
			}

			mDeviation = 0;
			mMoved = false;

			return true;
		}
		else if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			mDeviation = Math.round(event.getRawX()) + getDrawerWidth();
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE)
		{
			mMoved = true;
			mMovedPosition = Math.round(event.getRawX() - mDeviation);

			if (mMovedPosition < 0 - drawerWidth)
			{
				mMovedPosition = 0 - drawerWidth;
			}

			if (mMovedPosition >= 0)
			{
				mMovedPosition = 0;
			}
			else
			{
				mMovedBeyondMargin = true;
			}

			IDrawerProxy proxy = createDrawerProxy();
			proxy.setPosition(mMovedPosition);

			return true;
		}

		return false;
	}

	/**
	 * Internal DrawerProxy class to handle animation of {@link Drawer}
	 * 
	 * @author Lukasz Milewski <lukasz.milewski@gmail.com>
	 */
	public class DrawerProxy implements IDrawerProxy
	{

		private int mOriginalWidth;

		private View mView;
		
		private View mViewAlpha;

		private AnimatorProxy mViewAlphaProxy;

		private View mViewShadow;

		private View mViewWidth;

		private int mDrawerWidth;

		public DrawerProxy(View view, View viewWidth, View viewShadow, View alphaView)
		{
			mView = view;
			mViewWidth = viewWidth;
			mViewShadow = viewShadow;
			mViewAlpha = alphaView;
			mViewAlphaProxy = AnimatorProxy.wrap(alphaView);

			mOriginalWidth = mActivityWidth;
			mDrawerWidth = getDrawerWidth();
		}

		public int getLeft()
		{
			return mView.getPaddingLeft();
		}

		public void setAlpha(int position)
		{
			float value = (Float.valueOf(0 - position) / Float.valueOf(mDrawerWidth)) * 0.7f + 0.3f;
			mViewAlphaProxy.setAlpha(value);
		}

		public void setPosition(int position)
		{
			setLeftPadding(mView, position);
			setLeftPadding(mViewShadow, position + mOriginalWidth);

			setWidth(mView, mOriginalWidth + position);
			setWidth(mViewShadow, position);

			setLeftMargin(mViewWidth, mOriginalWidth + position);
			setWidth(mViewWidth, -1 * position);

			if ((mMoveDrawer || mScaleDrawer) && !mTransform3dDrawer)
			{
				setLeftPadding(mViewAlpha, 0 - (mDrawerWidth + position));
			}
			else
			{
				int padding = 0 - (mDrawerWidth + position);

				setLeftPadding(mViewAlpha, padding);
				setWidth(mViewAlpha, mDrawerWidth + padding);
			}

			if (mFadeDrawer)
			{
				setAlpha(position);
			}

			// if (mScaleDrawer && !mTransform3dDrawer)
			// {
			// 	setScale(position);
			// }

			// if (mTransform3dDrawer)
			// {
			// 	setTransform3d(position);
			// }
		}

		private void setLeftPadding(View view, int left)
		{
			view.setPadding(left, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
		}

		private void setLeftMargin(View view, int left)
		{
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
			params.setMargins(left, params.topMargin, params.rightMargin, params.bottomMargin);

			view.setLayoutParams(params);
		}

		// private void setScale(int position)
		// {
		// 	float scale = (Float.valueOf(position) / Float.valueOf(mDrawerWidth)) * 0.2f + 0.8f;
		// 	mViewAlphaProxy.setScaleX(scale);
		// 	mViewAlphaProxy.setScaleY(scale);
		// }

		// @TargetApi(11)
		// private void setTransform3d(int position)
		// {
		// 	int maxLeft = Math.round(mDrawerWidth * 0.9f);
		// 	int negativePaddingLeft = -1 * (int) (maxLeft - (Float.valueOf(position) * 0.9f));
		// 	setLeftPadding(mViewWidth, negativePaddingLeft);
		// 
		// 	float scale = (Float.valueOf(position) / Float.valueOf(mDrawerWidth)) * 0.3f + 0.7f;
		// 	mViewAlphaProxy.setScaleX(scale);
		// 	mViewAlphaProxy.setScaleY(scale);
		// 
		// 	float rotate = (Float.valueOf(position) / Float.valueOf(mDrawerWidth)) * 0.9f + 0.1f;
		// 	mViewAlpha.setRotationY(-45 + (rotate * 45));
		// }

		private void setWidth(View view, int width)
		{
			ViewGroup.LayoutParams params = view.getLayoutParams();
			params.width = width;
			view.setLayoutParams(params);
		}

	}

}
