package nu.mine.tmyymmt.android.widget;

import java.util.ArrayList;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.app.Activity;
import android.content.Context;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Retractive Scroll Views for Android.
 * 
 * This is a library for Android. This is a retractable HorizontalScrollView.
 * You can also sync scrolling in views.
 * 
 * @version 0.1
 * @see https://github.com/tmyymmt/Retractive-Scroll-Views-for-Android
 * @author Tomoya Yamamoto <a
 *         href="mailto:tmyymmt+github@gmail.com">&lt;tmyymmt+
 *         github@gmail.com&gt;</a> <a
 *         href="http://about.me/tmyymmt">http://about.me/tmyymmt</a>
 */
public class RetractiveHorizontalScrollView extends android.widget.HorizontalScrollView {
    /**
     * Flag for moving when called OnScrollChanged(). If this flag is true then
     * move this view.
     */
    private boolean moveOnScrollChangedFlag_ = false;

    /**
     * Last action which is event of onTouch(View v, MotionEvent event).
     */
    private int lastAction_ = 0;

    /**
     * RetractiveHorizontalScrollViews to sync with this object.
     */
    private ArrayList<RetractiveHorizontalScrollView> syncScrollViews_ = null;

    /**
     * The width of retracting by pixel size. Default value is the absolute
     * width of the display in pixels.
     */
    private int retractiveWidth_ = 0;

    /**
     * This is double of retractiveWidth_ by pixel size.
     */
    private int retractiveWidthDouble_ = 0;

    /**
     * Scroll position which is right side border.
     */
    private int rightPosition_ = 0;

    /**
     * Commander object. This is a watchdog for eternal loop.
     */
    private RetractiveHorizontalScrollView commanderObject_ = null;

    /**
     * Constructor.
     * 
     * @param context
     *            The Context the view is running in, through which it can
     *            access the current theme, resources, etc.
     */
    public RetractiveHorizontalScrollView(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor.
     * 
     * @param context
     *            The Context the view is running in, through which it can
     *            access the current theme, resources, etc.
     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     */
    public RetractiveHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor.
     * 
     * @param context
     *            The Context the view is running in, through which it can
     *            access the current theme, resources, etc.
     * @param attrs
     *            The attributes of the XML tag that is inflating the view.
     * @param defStyle
     *            The default style to apply to this view. If 0, no style will
     *            be applied (beyond what is included in the theme). This may
     *            either be an attribute resource, whose value will be retrieved
     *            from the current theme, or an explicit style resource.
     */
    public RetractiveHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Set retractive width by pixel size. Default value is the absolute width
     * of the display in pixels.
     * 
     * @param retractiveWidth
     *            retractive width by pixel size.
     */
    public void setRetractiveWidth(int retractiveWidth) {
        retractiveWidth_ = retractiveWidth;
        retractiveWidthDouble_ = retractiveWidth_ * 2;
        updateRetractiveWidth();
    }

    /**
     * Return retractive width by pixel size.
     * 
     * @return retractive width by pixel size
     */
    public int getRetractiveWidth() {
        return retractiveWidth_;
    }

    /**
     * Return sync views.
     * 
     * @return sync views
     */
    public ArrayList<RetractiveHorizontalScrollView> getSyncScrollViews() {
        return syncScrollViews_;
    }

    /**
     * Add sync view.
     * 
     * @param syncScrollView
     *            sync views.
     */
    public void addSyncScrollView(RetractiveHorizontalScrollView syncScrollView) {
        if (syncScrollView != null && syncScrollView != this) {
            syncScrollViews_.add(syncScrollView);
        }
    }

    /**
     * Remove sync view.
     * 
     * @param syncScrollView
     *            sync views.
     */
    public void removeSyncScrollView(RetractiveHorizontalScrollView syncScrollView) {
        syncScrollViews_.remove(syncScrollView);
    }

    /**
     * Clear sync view.
     */
    public void clearSyncScrollView() {
        syncScrollViews_.clear();
    }

    /**
     * Return the flag.
     * 
     * @return true: move when this is on scroll changed. false: don't move when
     *         this is on scroll changed.
     */
    public boolean isMoveOnScrollChanged() {
        return moveOnScrollChangedFlag_;
    }

    /**
     * Set the flag.
     * 
     * @param move
     *            true: move when this is on scroll changed. false: don't move
     *            when this is on scroll changed.
     */
    public void setMoveOnScrollChanged(boolean move) {
        this.moveOnScrollChangedFlag_ = move;
    }

    /**
     * @see android.widget.HorizontalScrollView#onLayout(boolean, int, int, int,
     *      int)
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        init2();
    }

    /**
     * @see android.view.View#onScrollChanged(int, int, int, int)
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (commanderObject_ == this) {
            commanderObject_ = null;
        } else {
            for (RetractiveHorizontalScrollView syncView : syncScrollViews_) {
                commanderObject_ = this;
                syncView.commanderObject_ = this;
                syncView.scrollTo(l, t);
            }
        }
        if (moveOnScrollChangedFlag_) {
            if (rightPosition_ < getScrollX() && retractiveWidth_ < rightPosition_) {
                scrollTo(rightPosition_, getScrollY());
            } else if (getScrollX() < retractiveWidth_) {
                scrollTo(retractiveWidth_, getScrollY());
            }
        }
    }

    /**
     * @see android.widget.HorizontalScrollView#computeHorizontalScrollRange()
     */
    @Override
    protected int computeHorizontalScrollRange() {
        int count = getChildCount();
        if (count == 0)
            return getWidth();

        return getChildAt(0).getRight() - retractiveWidthDouble_;
    }

    /**
     * @see android.widget.HorizontalScrollView#computeHorizontalScrollOffset()
     */
    @Override
    protected int computeHorizontalScrollOffset() {
        if (getScrollX() < retractiveWidth_)
            return 0;

        return getScrollX() - retractiveWidth_;
    }

    /**
     * @see android.view.View#computeHorizontalScrollExtent()
     */
    @Override
    protected int computeHorizontalScrollExtent() {
        if (rightPosition_ < getScrollX()) {
            return Math.max(1, getWidth() - (getScrollX() - rightPosition_));
        } else if (getScrollX() < retractiveWidth_) {
            return Math.max(1, getWidth() - (retractiveWidth_ - getScrollX()));
        }
        return getWidth();
    }

    /**
     * @see android.widget.HorizontalScrollView#fullScroll(int)
     */
    @Override
    public boolean fullScroll(int direction) {
        if (((LinearLayout) getChildAt(0)).getChildAt(1).getWidth() < getWidth()
                && direction == HorizontalScrollView.FOCUS_RIGHT)
            return false;

        return super.fullScroll(direction);
    }

    /**
     * @see android.widget.HorizontalScrollView#pageScroll(int)
     */
    @Override
    public boolean pageScroll(int direction) {
        if (((LinearLayout) getChildAt(0)).getChildAt(1).getWidth() < getWidth()
                && direction == HorizontalScrollView.FOCUS_RIGHT)
            return false;

        return super.pageScroll(direction);
    }

    /**
     * Initialization.
     */
    private void init() {
        syncScrollViews_ = new ArrayList<RetractiveHorizontalScrollView>();
        if (getContext() instanceof Activity && retractiveWidth_ == 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            setRetractiveWidth(metrics.widthPixels);
        }
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    moveOnScrollChangedFlag_ = true;
                    for (RetractiveHorizontalScrollView syncView : syncScrollViews_) {
                        syncView.moveOnScrollChangedFlag_ = true;
                    }
                    if (rightPosition_ < getScrollX()) {
                        scrollTo(rightPosition_, 0);
                    } else if (getScrollX() < retractiveWidth_) {
                        scrollTo(retractiveWidth_, 0);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    moveOnScrollChangedFlag_ = false;
                    for (RetractiveHorizontalScrollView syncView : syncScrollViews_) {
                        syncView.moveOnScrollChangedFlag_ = false;
                    }
                }
                if (getLastAction() == MotionEvent.ACTION_UP && event.getAction() == MotionEvent.ACTION_MOVE) {
                    event.setAction(MotionEvent.ACTION_DOWN);
                }
                setLastAction(event.getAction());
                return false;
            }
        });
    }

    /**
     * Initialization 2. This is after GUI was inflate.
     */
    private void init2() {
        updateRetractiveWidth();

        rightPosition_ = getChildAt(0).getRight() - retractiveWidth_ - getWidth();
        if (((LinearLayout) getChildAt(0)).getChildAt(1).getWidth() < getWidth()) {
            scrollTo(retractiveWidth_, getScrollY());
            for (RetractiveHorizontalScrollView syncView : syncScrollViews_) {
                syncView.scrollTo(retractiveWidth_, syncView.getScrollY());
            }
        } else {
            moveOnScrollChangedFlag_ = true;
            for (RetractiveHorizontalScrollView syncView : syncScrollViews_) {
                syncView.moveOnScrollChangedFlag_ = true;
            }
            scrollTo(0, getScrollY());
            fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            for (RetractiveHorizontalScrollView syncView : syncScrollViews_) {
                syncView.scrollTo(0, syncView.getScrollY());
                syncView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }
    }

    /**
     * Update retractive width.
     */
    private void updateRetractiveWidth() {
        if (getChildAt(0) != null) {
            ((LinearLayout) getChildAt(0)).getChildAt(0).setMinimumWidth(retractiveWidth_);
            ((LinearLayout) getChildAt(0)).getChildAt(2).setMinimumWidth(retractiveWidth_);
        }
    }

    /**
     * Return last action. Last action which is event of onTouch(View v,
     * MotionEvent event).
     * 
     * @return last action
     */
    private int getLastAction() {
        return lastAction_;
    }

    /**
     * Set last action. Last action which is event of onTouch(View v,
     * MotionEvent event).
     * 
     * @param lastAction
     *            last action.
     */
    private void setLastAction(int lastAction) {
        this.lastAction_ = lastAction;
    }
}
