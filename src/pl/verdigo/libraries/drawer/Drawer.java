package pl.verdigo.libraries.drawer;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import pl.verdigo.libraries.drawer.internal.IDrawerProxy;
import pl.verdigo.libraries.drawer.internal.LeftDrawer;
import pl.verdigo.libraries.drawer.internal.RightDrawer;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.FloatMath;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actionbarsherlock.R;
import com.actionbarsherlock.internal.nineoldandroids.animation.Animator;
import com.actionbarsherlock.internal.nineoldandroids.animation.Animator.AnimatorListener;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;

/**
 * Drawer implementation. TODO create documentation in JavaDoc here.
 * 
 * @author Lukasz Milewski <lukasz.milewski@gmail.com>
 */
public abstract class Drawer implements OnClickListener, OnTouchListener
{

	public static final int ORIENTATION_BOTH = 0;

	public static final int ORIENTATION_POTRAIT = 1;

	public static final int ORIENTATION_LANDSCAPE = 2;

	protected static final int DRAWER_CONTENT_MOVE_PROPORTION = 5;

	private static final long DEFAULT_DURATION = 250;

	private static final int DRAWER_SHADOW_WIDTH = 8;

	protected int mActivityWidth;

	private boolean mAllowCloseOnTouch = true;

	private long mAnimationDuration = DEFAULT_DURATION;

	private boolean mAnimationEnabled = true;

	private Context mContext;

	private FrameLayout mDecorView;

	protected int mDeviation = 0;

	protected View mDrawer;

	protected View mDrawerActivity;

	private ImageView mDrawerClickable;

	protected LinearLayout mDrawerContent;

	private DrawerListener mDrawerListener;

	protected View mDrawerShadow;

	protected boolean mFadeDrawer = false;

	private float mDrawerWidthPortrait = -48;

	private float mDrawerWidthLand = -40;

	private int mLayout;

	protected boolean mMovable = true;

	protected boolean mMoved = false;

	protected boolean mMoveDrawer = false;

	protected boolean mMovedBeyondMargin = false;

	protected int mMovedPosition = 0;

	private boolean mNeedToReinitialize = false;

	private Window mParentWindow;

	private boolean mReuse = false;

	protected boolean mScaleDrawer = false;

	protected int mShadowWidth = DRAWER_SHADOW_WIDTH;

	protected boolean mTransform3dDrawer = false;

	private boolean mVisible = false;

	public static Drawer createLeftDrawer(Context context, int layout)
	{
		return new LeftDrawer(context, layout);
	}

	public static Drawer createRightDrawer(Context context, int layout)
	{
		return new RightDrawer(context, layout);
	}

	/**
	 * Creates {@link Drawer} object.
	 * 
	 * @param context Context
	 * @param layout Layout to inflate into {@link Drawer}
	 */
	protected Drawer(Context context, int layout)
	{
		mContext = context;
		mLayout = layout;

		if (mContext instanceof Activity)
		{
			mParentWindow = ((Activity) mContext).getWindow();
		}
	}

	/**
	 * Calculates duration of animation. When {@link Drawer} is in state of
	 * moving, duration of animation will be calculated based on the position.
	 * 
	 * @param show Animation for showing/hiding
	 * @return time in milliseconds
	 */
	private long calculateDuration(boolean show)
	{
		if (mMoved)
		{
			float ratio = (float) mMovedPosition / getDrawerWidth();
			long duration = Math.round(mAnimationDuration * (show ? 1F - ratio : ratio));

			return duration >= 0 ? duration : -1 * duration;
		}

		return mAnimationDuration;
	}

	/**
	 * Cancel (dismiss) {@link Drawer}. If animation is enabled it will be
	 * played.
	 */
	public final void cancel()
	{
		if (!mVisible)
		{
			return;
		}

		if (mDrawerListener != null)
		{
			mDrawerListener.onDrawerBeforeCancel();
		}

		mVisible = false;

		mDrawerClickable.setOnClickListener(null);
		mDrawerClickable.setOnTouchListener(null);

		if (mAnimationEnabled)
		{
			cancelWithAnimation();
		}
		else
		{
			removeDrawer();
		}
	}

	/**
	 * Plays cancel animation. It slides {@link Drawer} from right to left. If
	 * drawer is currently moved by touch event, animation will start from
	 * current position and will be appropriately shortened.
	 */
	private void cancelWithAnimation()
	{
		final int start = mMoved ? mMovedPosition : getTargetPosition();

		ObjectAnimator anim = ObjectAnimator.ofInt(createDrawerProxy(), "position", start, 0);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setDuration(calculateDuration(false));
		anim.addListener(new AnimatorListener()
		{
			public void onAnimationStart(Animator animation)
			{
			}

			public void onAnimationEnd(Animator animation)
			{
				removeDrawer();
			}

			public void onAnimationCancel(Animator animation)
			{
			}

			public void onAnimationRepeat(Animator animation)
			{
			}

		});

		anim.start();
	}

	/**
	 * Cancel (dismiss) {@link Drawer} without animation. This is equivalent to
	 * <pre>
	 * boolean previous = drawer.isAnimationEnabled();
	 * drawer.setAnimationEnabled(false);
	 * drawer.cancel();
	 * drawer.setAnimationEnabled(previous);
	 * </pre>
	 */
	public final void cancelWithoutAnimation()
	{
		boolean animationEnabled = mAnimationEnabled;
		mAnimationEnabled = false;

		cancel();

		mAnimationEnabled = animationEnabled;
	}

	/**
	 * Creates DrawerProxy object.
	 * 
	 * @return DrawerProxy object
	 */
	protected abstract IDrawerProxy createDrawerProxy();

	protected abstract int getTargetPosition();

	/**
	 * Returns {@link Drawer} width. Value provided by developer is in DPI,
	 * therefore it has to be calculated into pixels.
	 * 
	 * @return Drawer width in pixels
	 */
	protected int getDrawerWidth()
	{
		float density = mContext.getResources().getDisplayMetrics().density;
		float width = mDrawerWidthPortrait;

		if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			width = mDrawerWidthLand;
		}

		if (width < 0)
		{
			width = (mActivityWidth / density) - Math.abs(width);
		}

		return (int) FloatMath.ceil(width * density);
	}

	protected abstract boolean isRightDrawer();

	/**
	 * Initialize {@link Drawer}. Drawer's layout is injected into
	 * most-top-level {@link FrameLayout) possible, this gives us an ability to
	 * move {@link ActionBar}. Clickable {@link ImageView} is also created to
	 * handle click and touch events.
	 */
	public void init()
	{
		mDecorView = (FrameLayout) mParentWindow.getDecorView();
		mDrawerActivity = (ViewGroup) mDecorView.getChildAt(0);

		mActivityWidth = mDrawerActivity.getWidth();

		mDrawer = View.inflate(mContext, R.layout.drawer_placeholder, null);
		mDrawer.setPadding(0, mDrawerActivity.getPaddingTop(), 0, mDrawerActivity.getPaddingBottom());
		mDecorView.addView(mDrawer);

		mDrawerShadow = new LinearLayout(mContext);
		mDrawerShadow.setVisibility(View.GONE);
		mDecorView.addView(mDrawerShadow);

		ImageView shadow = new ImageView(mContext);
		shadow.setLayoutParams(new LinearLayout.LayoutParams(mShadowWidth, FILL_PARENT));
		shadow.setBackgroundResource(isRightDrawer() ? R.drawable.drawer_shadow_right : R.drawable.drawer_shadow_left);
		((LinearLayout) mDrawerShadow).addView(shadow);

		mDrawerClickable = new ImageView(mContext);
		mDrawerClickable.setVisibility(View.GONE);
		mDecorView.addView(mDrawerClickable);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FILL_PARENT, FILL_PARENT);

		mDrawerContent = (LinearLayout) findViewById(R.id.drawer_content);
		mDrawerContent.addView(View.inflate(mContext, mLayout, null), lp);

		updateDrawerWidth();
	}

	/**
	 * Is closing {link Drawer} on touch events allowed. Used primarily with Bezel Swipe.
	 * 
	 * @return Boolean
	 */
	public boolean isAllowCloseOnTouch()
	{
		return mAllowCloseOnTouch;
	}

	/**
	 * Is animation currently enabled.
	 * 
	 * @return Boolean
	 */
	public boolean isAnimationEnabled()
	{
		return mAnimationEnabled;
	}

	/**
	 * Is fading {@link Drawer} enabled.
	 * 
	 * @return Boolean
	 */
	public boolean isFadeDrawer()
	{
		return mFadeDrawer;
	}

	/**
	 * Is {@link Drawer} movable with touch events.
	 * 
	 * @return Boolean
	 */
	public boolean isMovable()
	{
		return mMovable;
	}

	/**
	 * Is moving content of {@link Drawer} enabled.
	 * 
	 * @return Boolean
	 */
	public boolean isMoveDrawer()
	{
		return mMoveDrawer;
	}

	/**
	 * Is scaling of {@link Drawer} enabled.
	 * 
	 * @return Boolean
	 */
	public boolean isScaleDrawer()
	{
		return mScaleDrawer;
	}

	/**
	 * Is 3d transformation of {@link Drawer} enabled.
	 * 
	 * @return Boolean
	 */
	public boolean isTransform3dDrawer()
	{
		return mTransform3dDrawer;
	}

	/**
	 * Is drawer currently visible. If it is not visible, internal objects are
	 * destroyed and {@link Drawer} should not be used.
	 * 
	 * @return Boolean
	 */
	public boolean isVisible()
	{
		return mVisible;
	}

	/**
	 * Handles click event.
	 * 
	 * @param view Clicked view
	 */
	public void onClick(View view)
	{
		if (view == mDrawerClickable)
		{
			cancel();
		}
	}

	/**
	 * Removed {@link Drawer} from parent {@link Activity}.
	 */
	public void removeDrawer()
	{
		mMovedBeyondMargin = false;
		mMovedPosition = 0;
		mDeviation = 0;
		mMoved = false;
		
		ViewGroup.LayoutParams lp = ((ViewGroup) mDrawerActivity).getLayoutParams();
		lp.width = -1;
		mDrawerActivity.setLayoutParams(lp);
		
		mDrawerActivity.setPadding(0, mDrawerActivity.getPaddingTop(), mDrawerActivity.getPaddingRight(), mDrawerActivity.getPaddingBottom());
		mDrawerActivity.requestLayout();
		
		mDrawerClickable.setVisibility(View.GONE);
		mDrawerShadow.setVisibility(View.GONE);

		if (mDrawerListener != null)
		{
			mDrawerListener.onDrawerAfterCancel();
		}

		if (mReuse)
		{
			ViewGroup.LayoutParams params = mDrawer.getLayoutParams();
			params.width = 0;
			mDrawer.setLayoutParams(params);

			return;
		}

		mDecorView.removeView(mDrawer);
		mDecorView.removeView(mDrawerClickable);
		mDecorView.removeView(mDrawerShadow);

		mNeedToReinitialize = true;
	}

	/**
	 * Sets whether closing {@link Drawer} is available on touch events.
	 * 
	 * @param allowCloseOnTouch true/false
	 */
	public void setAllowCloseOnTouch(boolean allowCloseOnTouch)
	{
		mAllowCloseOnTouch = allowCloseOnTouch;
	}

	/**
	 * Sets duration of {@link Drawer} open/close animation.
	 * 
	 * @param animationDuration Duration in milliseconds
	 */
	public void setAnimationDuration(long animationDuration)
	{
		mAnimationDuration = animationDuration;
	}

	/**
	 * Sets whether animation should be enabled or disabled.
	 * 
	 * @param animationEnabled true/false
	 */
	public void setAnimationEnabled(boolean animationEnabled)
	{
		mAnimationEnabled = animationEnabled;
	}

	/**
	 * Sets background {@link Drawable} on {@link Drawer}. This method should be
	 * used instead of background on provided layout.
	 * 
	 * @param drawable Drawable
	 */
	public void setBackgroundDrawable(Drawable drawable)
	{
		mDrawerContent.setBackgroundDrawable(drawable);
		mDrawerContent.setPadding(0, 0, 0, 0);
	}

	/**
	 * Sets background {@link Resource} on {@link Drawer}. This method should be
	 * used instead of background on provided layout.
	 * 
	 * @param drawable Resource
	 */
	public void setBackgroundResource(int drawable)
	{
		mDrawerContent.setBackgroundResource(drawable);
		mDrawerContent.setPadding(0, 0, 0, 0);
	}

	/**
	 * Sets {@link DrawerListener} listener.
	 * 
	 * @param listener New listener
	 */
	public void setDrawerListener(DrawerListener listener)
	{
		mDrawerListener = listener;
	}

	/**
	 * Sets (@link Drawer) width for portrait and landscape. Negative value will
	 * result in subtracting width from entire activity width
	 * 
	 * @param drawerWidth Drawer width in DIPs
	 */
	public void setDrawerWidth(float drawerWidth)
	{
		setDrawerWidth(ORIENTATION_BOTH, drawerWidth);
	}

	/**
	 * Sets (@link Drawer) width for portrait and/or landscape. Negative value will
	 * result in subtracting width from entire activity width
	 * 
	 * @param type Type
	 * @param drawerWidth Drawer width in DIPs
	 */
	public void setDrawerWidth(int type, float drawerWidth)
	{
		if (type == ORIENTATION_BOTH || type == ORIENTATION_POTRAIT)
		{
			mDrawerWidthPortrait = drawerWidth;
		}
		if (type == ORIENTATION_BOTH || type == ORIENTATION_LANDSCAPE)
		{
			mDrawerWidthLand = drawerWidth;
		}
	}

	/**
	 * Sets whether {@link Drawer} will fade to black on animation.
	 * 
	 * @param fadeDrawer true/false
	 */
	public void setFadeDrawer(boolean fadeDrawer)
	{
		mFadeDrawer = fadeDrawer;
	}

	/**
	 * Sets whether {@link Drawer} is movable by touch events.
	 * 
	 * @param movable true/false
	 */
	public void setMovable(boolean movable)
	{
		mMovable = movable;
	}

	/**
	 * Sets whether content of {@link Drawer} is move during animation.
	 * 
	 * @param moveDrawer true/false
	 */
	public void setMoveDrawer(boolean moveDrawer)
	{
		mMoveDrawer = moveDrawer;
	}

	/**
	 * Sets whether content of {@link Drawer} will be reused or not.
	 * 
	 * @param reuse true/false
	 */
	public void setReuse(boolean reuse)
	{
		mReuse = reuse;
	}

	/**
	 * Sets whether content of {@link Drawer} is scaled during animation.
	 * 
	 * @param scaleDrawer true/false
	 */
	public void setScaleDrawer(boolean scaleDrawer)
	{
		this.mScaleDrawer = scaleDrawer;
	}

	/**
	 * Sets shadow width
	 * 
	 * @param shadowWidth width
	 */
	public void setShadowWidth(int shadowWidth)
	{
		this.mShadowWidth = shadowWidth;
	}

	/**
	 * Sets whether content of {@link Drawer} is 3d transformed during animation.
	 * This method is available from Android 3.0 (API level 11). On lower version
	 * nothing will happen.
	 * 
	 * @param transform3dDrawer true/false
	 */
	public void setTransform3dDrawer(boolean transform3dDrawer)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			this.mTransform3dDrawer = transform3dDrawer;
		}
	}

	/**
	 * Shows {@link Drawer}. If animation is enabled it will be played.
	 */
	public void show()
	{
		if (isVisible())
		{
			return;
		}

		if (mNeedToReinitialize)
		{
			init();
		}

		if (mDrawerListener != null)
		{
			mDrawerListener.onDrawerBeforeShow();
		}

		mMoved = false;
		mMovedPosition = 0;
		mVisible = true;

		if (isAnimationEnabled())
		{
			showWithAnimation();
		}
		else
		{
			IDrawerProxy proxy = createDrawerProxy();
			proxy.setPosition(getDrawerWidth());

			updateDrawerClickable();
			updateDrawerShadow();

			finishShowing();
		}
	}

	void showWithTouch(int deviation)
	{
		if (isVisible())
		{
			return;
		}

		if (mNeedToReinitialize)
		{
			init();
		}

		mMoved = true;
		mMovedPosition = 0;
		mVisible = true;
		mDeviation = deviation;

		IDrawerProxy proxy = createDrawerProxy();
		proxy.setPosition(0);

		updateDrawerClickable();
		updateDrawerShadow();
	}

	/**
	 * Plays show animation. It slides {@link Drawer} from left to right. If
	 * drawer is currently moved by touch event, animation will start from
	 * current position and will be appropriately shortened. If this is first
	 * time, clickable {@link ImageView} will be correctly positioned and
	 * visible.
	 */
	protected void showWithAnimation()
	{
		final int start = mMoved ? mMovedPosition : 0;

		boolean decelerate = mMoved && !mAllowCloseOnTouch;

		ObjectAnimator anim = ObjectAnimator.ofInt(createDrawerProxy(), "position", start, getTargetPosition());
		anim.setInterpolator(decelerate ? new DecelerateInterpolator() : new AccelerateInterpolator());
		anim.setDuration(calculateDuration(true));
		anim.addListener(new AnimatorListener()
		{
			public void onAnimationStart(Animator animation)
			{
			}
			
			public void onAnimationRepeat(Animator animation)
			{
			}
			
			public void onAnimationEnd(Animator animation)
			{
				finishShowing();
			}
			
			public void onAnimationCancel(Animator animation)
			{
				finishShowing();
			}
		});

		anim.start();

		if (mMoved)
		{
			return;
		}

		updateDrawerClickable();
		updateDrawerShadow();
	}

	public View findViewById(int viewId)
	{
		return mDrawer.findViewById(viewId);
	}

	void finishShowing()
	{
		if (mDrawerListener != null)
		{
			mDrawerListener.onDrawerAfterShow();
		}
	}

	/**
	 * Updates clickable {@link ImageView} - position and visibility.
	 */
	private void updateDrawerClickable()
	{
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mDrawerClickable.getLayoutParams();
		lp.gravity = (isRightDrawer() ? Gravity.LEFT : Gravity.RIGHT) | Gravity.FILL_VERTICAL;
		lp.width = mActivityWidth - getDrawerWidth();

		mDrawerClickable.setLayoutParams(lp);
		mDrawerClickable.setVisibility(View.VISIBLE);
		mDrawerClickable.setClickable(true);
		mDrawerClickable.setOnClickListener(this);
		mDrawerClickable.setOnTouchListener(this);
	}

	/**
	 * Updates shadow - position and visibility.
	 */
	private void updateDrawerShadow()
	{
		View shadow = ((LinearLayout) mDrawerShadow).getChildAt(0);
		shadow.setLayoutParams(new LinearLayout.LayoutParams(mShadowWidth, FILL_PARENT));

		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mDrawerShadow.getLayoutParams();
		lp.gravity = Gravity.FILL_VERTICAL;
		lp.width = 0;

		mDrawerShadow.setLayoutParams(lp);
		mDrawerShadow.setVisibility(View.VISIBLE);
	}

	/**
	 * Updates {@link Drawer} width. It is based on {@link Activity} minus
	 * margin provided in constructor.
	 */
	private void updateDrawerWidth()
	{
		mDrawer.getLayoutParams().width = 0;
		findViewById(R.id.drawer_content).getLayoutParams().width = getDrawerWidth();
	}

}
