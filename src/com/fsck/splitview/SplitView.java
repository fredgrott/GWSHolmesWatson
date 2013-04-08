package com.fsck.splitview;

import com.actionbarsherlock.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

// TODO: Auto-generated Javadoc
/**
 * The Class SplitView.
 */
public class SplitView extends LinearLayout implements OnTouchListener {

    /** The m handle id. */
    private int mHandleId;
    
    /** The m handle. */
    private View mHandle;

    /** The m primary content id. */
    private int mPrimaryContentId;
    
    /** The m primary content. */
    private View mPrimaryContent;

    /** The m secondary content id. */
    private int mSecondaryContentId;
    
    /** The m secondary content. */
    private View mSecondaryContent;

    /** The m last primary content size. */
    private int mLastPrimaryContentSize;

    /** The m dragging. */
    @SuppressWarnings("unused")
	private boolean mDragging;
    
    /** The m dragging started. */
    private long mDraggingStarted;
    
    /** The m drag start x. */
    private float mDragStartX;
    
    /** The m drag start y. */
    private float mDragStartY;

    /** The m pointer offset. */
    private float mPointerOffset;

    /** The Constant MAXIMIZED_VIEW_TOLERANCE_DIP. */
    final static private int MAXIMIZED_VIEW_TOLERANCE_DIP = 30;
    
    /** The Constant TAP_DRIFT_TOLERANCE. */
    final static private int TAP_DRIFT_TOLERANCE = 3;
    
    /** The Constant SINGLE_TAP_MAX_TIME. */
    final static private int SINGLE_TAP_MAX_TIME = 175;

    /**
     * Instantiates a new split view.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public SplitView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray viewAttrs = context.obtainStyledAttributes(attrs, R.styleable.SplitView);

        RuntimeException e = null;
        mHandleId = viewAttrs.getResourceId(R.styleable.SplitView_handle, 0);
        if (mHandleId == 0) {
            e = new IllegalArgumentException(viewAttrs.getPositionDescription() +
                                             ": The required attribute handle must refer to a valid child view.");
        }

        mPrimaryContentId = viewAttrs.getResourceId(R.styleable.SplitView_primaryContent, 0);
        if (mPrimaryContentId == 0) {
            e = new IllegalArgumentException(viewAttrs.getPositionDescription() +
                                             ": The required attribute primaryContent must refer to a valid child view.");
        }


        mSecondaryContentId = viewAttrs.getResourceId(R.styleable.SplitView_secondaryContent, 0);
        if (mSecondaryContentId == 0) {
            e = new IllegalArgumentException(viewAttrs.getPositionDescription() +
                                             ": The required attribute secondaryContent must refer to a valid child view.");
        }

        viewAttrs.recycle();

        if (e != null) {
            throw e;
        }
    }

    /**
     * On finish inflate.
     */
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mHandle = findViewById(mHandleId);
        if (mHandle == null) {
            String name = getResources().getResourceEntryName(mHandleId);
            throw new RuntimeException("Your Panel must have a child View whose id attribute is 'R.id." + name + "'");

        }
        mPrimaryContent = findViewById(mPrimaryContentId);
        if (mPrimaryContent == null) {
            String name = getResources().getResourceEntryName(mPrimaryContentId);
            throw new RuntimeException("Your Panel must have a child View whose id attribute is 'R.id." + name + "'");

        }

        mLastPrimaryContentSize = getPrimaryContentSize();

        mSecondaryContent = findViewById(mSecondaryContentId);
        if (mSecondaryContent == null) {
            String name = getResources().getResourceEntryName(mSecondaryContentId);
            throw new RuntimeException("Your Panel must have a child View whose id attribute is 'R.id." + name + "'");

        }

        mHandle.setOnTouchListener(this);

    }
    
    /**
     * On touch.
     *
     * @param view the view
     * @param me the me
     * @return true, if successful
     */
    @SuppressWarnings("unused")
	@Override
    public boolean onTouch(View view, MotionEvent me) {
        ViewGroup.LayoutParams thisParams = getLayoutParams();
        // Only capture drag events if we start
        if (view != mHandle) {
            return false;
        }
        //Log.v("foo", "at "+SystemClock.elapsedRealtime()+" got touch event " + me);
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            mDragging = true;
            mDraggingStarted = SystemClock.elapsedRealtime();
            mDragStartX = me.getX();
            mDragStartY = me.getY();
            if (getOrientation() == VERTICAL) {
                mPointerOffset = me.getRawY() - mPrimaryContent.getMeasuredHeight();
            } else {
                mPointerOffset = me.getRawX() - mPrimaryContent.getMeasuredWidth();
            }
            return true;
        }
        else if (me.getAction() == MotionEvent.ACTION_UP) {
            mDragging = false;
            if (
                    mDragStartX <(me.getX()+TAP_DRIFT_TOLERANCE) && 
                    mDragStartX > (me.getX() -TAP_DRIFT_TOLERANCE) && 
                    mDragStartY <  (me.getY() + TAP_DRIFT_TOLERANCE) &&
                    mDragStartY > (me.getY() - TAP_DRIFT_TOLERANCE) &&        
             ((SystemClock.elapsedRealtime() - mDraggingStarted) < SINGLE_TAP_MAX_TIME)) {
                if (isPrimaryContentMaximized() || isSecondaryContentMaximized()) {
                    setPrimaryContentSize(mLastPrimaryContentSize);
                } else {
                    maximizeSecondaryContent();
                }
            }
            return true;
        } else if (me.getAction() == MotionEvent.ACTION_MOVE) {
            if (getOrientation() == VERTICAL) {
                setPrimaryContentHeight( (int)(me.getRawY() - mPointerOffset));
            } else {
                setPrimaryContentWidth( (int)(me.getRawX() - mPointerOffset));
            }
        }
            return true;
    }

    
    /**
     * Gets the handle.
     *
     * @return the handle
     */
    public View getHandle() {
        return mHandle;
    }

    /**
     * Gets the primary content size.
     *
     * @return the primary content size
     */
    public int getPrimaryContentSize() {
            if (getOrientation() == VERTICAL) {
                return mPrimaryContent.getMeasuredHeight();
            } else {
             return mPrimaryContent.getMeasuredWidth();
            }

    }

    /**
     * Sets the primary content size.
     *
     * @param newSize the new size
     * @return true, if successful
     */
    public boolean setPrimaryContentSize(int newSize) {
        if (getOrientation() == VERTICAL) {
            return setPrimaryContentHeight(newSize);
        } else {
            return setPrimaryContentWidth(newSize);
        }
    }


    /**
     * Sets the primary content height.
     *
     * @param newHeight the new height
     * @return true, if successful
     */
    private boolean setPrimaryContentHeight(int newHeight) {
        ViewGroup.LayoutParams params = mPrimaryContent.getLayoutParams();
        if (mSecondaryContent.getMeasuredHeight() < 1 && newHeight > params.height) {
            return false;
        }
        if (newHeight >= 0) {
            params.height = newHeight;
        }
        unMinimizeSecondaryContent();
        mPrimaryContent.setLayoutParams(params);
        return true;

    }

    /**
     * Sets the primary content width.
     *
     * @param newWidth the new width
     * @return true, if successful
     */
    private boolean setPrimaryContentWidth(int newWidth) {
        ViewGroup.LayoutParams params = mPrimaryContent.getLayoutParams();


        if (mSecondaryContent.getMeasuredWidth() < 1 && newWidth > params.width) {
            return false;
        }
        if (newWidth >= 0) {
            params.width = newWidth;
        }
        unMinimizeSecondaryContent();
        mPrimaryContent.setLayoutParams(params);
        return true;
    }
    
    /**
     * Checks if is primary content maximized.
     *
     * @return true, if is primary content maximized
     */
    public boolean isPrimaryContentMaximized() {
        if ( (getOrientation() == VERTICAL && (mSecondaryContent.getMeasuredHeight() < MAXIMIZED_VIEW_TOLERANCE_DIP) ) ||
                (getOrientation() == HORIZONTAL && (mSecondaryContent.getMeasuredWidth() < MAXIMIZED_VIEW_TOLERANCE_DIP) )) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * Checks if is secondary content maximized.
     *
     * @return true, if is secondary content maximized
     */
    public boolean isSecondaryContentMaximized() {
        if ( (getOrientation() == VERTICAL && (mPrimaryContent.getMeasuredHeight() < MAXIMIZED_VIEW_TOLERANCE_DIP) ) ||
                (getOrientation() == HORIZONTAL && (mPrimaryContent.getMeasuredWidth() < MAXIMIZED_VIEW_TOLERANCE_DIP) )) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Maximize primary content.
     */
    public void maximizePrimaryContent() {
        maximizeContentPane(mPrimaryContent, mSecondaryContent);
    }

    /**
     * Maximize secondary content.
     */
    public void maximizeSecondaryContent() {
        maximizeContentPane(mSecondaryContent, mPrimaryContent);
    }



    /**
     * Maximize content pane.
     *
     * @param toMaximize the to maximize
     * @param toUnMaximize the to un maximize
     */
    @SuppressWarnings({ "deprecation" })
	private void maximizeContentPane(View toMaximize, View toUnMaximize) {
        mLastPrimaryContentSize = getPrimaryContentSize();

        ViewGroup.LayoutParams params = toUnMaximize.getLayoutParams();
        ViewGroup.LayoutParams secondaryParams = toMaximize.getLayoutParams();
        if (getOrientation() == VERTICAL) {
            params.height = 1;
           secondaryParams.height = LayoutParams.FILL_PARENT; //getLayoutParams().height - mHandle.getLayoutParams().height;
        } else {
            params.width = 1;
            secondaryParams.width = LayoutParams.FILL_PARENT; //getLayoutParams().width - mHandle.getLayoutParams().width;
        }
        toUnMaximize.setLayoutParams(params);
        toMaximize.setLayoutParams(secondaryParams);



    }

    /**
     * Un minimize secondary content.
     */
    @SuppressWarnings("deprecation")
	private void unMinimizeSecondaryContent() {
        ViewGroup.LayoutParams secondaryParams = mSecondaryContent.getLayoutParams();
        if (getOrientation() == VERTICAL) {
            secondaryParams.height = LayoutParams.FILL_PARENT;
        } else {
            secondaryParams.width = LayoutParams.FILL_PARENT;

        }
        mSecondaryContent.setLayoutParams(secondaryParams);

    }

};
