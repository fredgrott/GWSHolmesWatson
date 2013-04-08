
package org.holoeverywhere.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.holoeverywhere.FontLoader;
import org.holoeverywhere.LayoutInflater;
import com.actionbarsherlock.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class CalendarView.
 */
@SuppressLint("SimpleDateFormat")
public class CalendarView extends FrameLayout {
    
    /**
     * The listener interface for receiving onDateChange events.
     * The class that is interested in processing a onDateChange
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addOnDateChangeListener<code> method. When
     * the onDateChange event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OnDateChangeEvent
     */
    public interface OnDateChangeListener {
        
        /**
         * On selected day change.
         *
         * @param view the view
         * @param year the year
         * @param month the month
         * @param dayOfMonth the day of month
         */
        public void onSelectedDayChange(CalendarView view, int year, int month,
                int dayOfMonth);
    }

    /**
     * The Class ScrollStateRunnable.
     */
    private class ScrollStateRunnable implements Runnable {
        
        /** The m new state. */
        private int mNewState;
        
        /** The m view. */
        private AbsListView mView;

        /**
         * Do scroll state change.
         *
         * @param view the view
         * @param scrollState the scroll state
         */
        public void doScrollStateChange(AbsListView view, int scrollState) {
            mView = view;
            mNewState = scrollState;
            removeCallbacks(this);
            postDelayed(this, CalendarView.SCROLL_CHANGE_DELAY);
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        @SuppressLint("NewApi")
        public void run() {
            mCurrentScrollState = mNewState;
            if (mNewState == OnScrollListener.SCROLL_STATE_IDLE
                    && mPreviousScrollState != OnScrollListener.SCROLL_STATE_IDLE) {
                View child = mView.getChildAt(0);
                if (child == null) {
                    return;
                }
                int dist = child.getBottom() - mListScrollTopOffset;
                if (dist > mListScrollTopOffset) {
                    int y = dist - (mIsScrollingUp ? child.getHeight() : 0);
                    if (VERSION.SDK_INT >= 11) {
                        mView.smoothScrollBy(y,
                                CalendarView.ADJUSTMENT_SCROLL_DURATION);
                    } else {
                        mView.scrollBy(0, y);
                    }
                }
            }
            mPreviousScrollState = mNewState;
        }
    }

    /**
     * The Class WeeksAdapter.
     */
    private class WeeksAdapter extends BaseAdapter implements OnTouchListener {
        
        /**
         * The listener interface for receiving calendarGesture events.
         * The class that is interested in processing a calendarGesture
         * event implements this interface, and the object created
         * with that class is registered with a component using the
         * component's <code>addCalendarGestureListener<code> method. When
         * the calendarGesture event occurs, that object's appropriate
         * method is invoked.
         *
         * @see CalendarGestureEvent
         */
        class CalendarGestureListener extends
                GestureDetector.SimpleOnGestureListener {
            
            /* (non-Javadoc)
             * @see android.view.GestureDetector.SimpleOnGestureListener#onSingleTapUp(android.view.MotionEvent)
             */
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        }

        /** The context. */
        private Context context;
        
        /** The m focused month. */
        private int mFocusedMonth;
        
        /** The m gesture detector. */
        private GestureDetector mGestureDetector;
        
        /** The m selected date. */
        private final Calendar mSelectedDate = Calendar.getInstance();
        
        /** The m selected week. */
        private int mSelectedWeek;
        
        /** The m total week count. */
        private int mTotalWeekCount;

        /**
         * Instantiates a new weeks adapter.
         *
         * @param context the context
         */
        public WeeksAdapter(Context context) {
            this.context = context;
            mGestureDetector = new GestureDetector(context,
                    new CalendarGestureListener());
            init();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return mTotalWeekCount;
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
            return position;
        }

        /**
         * Gets the selected day.
         *
         * @return the selected day
         */
        public Calendar getSelectedDay() {
            return mSelectedDate;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WeekView weekView = null;
            if (convertView != null) {
                weekView = (WeekView) convertView;
            } else {
                weekView = new WeekView(context);
                android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                weekView.setLayoutParams(params);
                weekView.setClickable(true);
                weekView.setOnTouchListener(this);
            }

            int selectedWeekDay = mSelectedWeek == position ? mSelectedDate
                    .get(Calendar.DAY_OF_WEEK) : -1;
            weekView.init(position, selectedWeekDay, mFocusedMonth);

            return weekView;
        }

        /**
         * Inits the.
         */
        private void init() {
            mSelectedWeek = getWeeksSinceMinDate(mSelectedDate);
            mTotalWeekCount = getWeeksSinceMinDate(mMaxDate);
            if (mMinDate.get(Calendar.DAY_OF_WEEK) != mFirstDayOfWeek
                    || mMaxDate.get(Calendar.DAY_OF_WEEK) != mFirstDayOfWeek) {
                mTotalWeekCount++;
            }
        }

        /**
         * On date tapped.
         *
         * @param day the day
         */
        private void onDateTapped(Calendar day) {
            setSelectedDay(day);
            setMonthDisplayed(day);
        }

        /* (non-Javadoc)
         * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mListView.isEnabled() && mGestureDetector.onTouchEvent(event)) {
                WeekView weekView = (WeekView) v;
                if (!weekView.getDayFromLocation(event.getX(), mTempDate)) {
                    return true;
                }
                if (mTempDate.before(mMinDate) || mTempDate.after(mMaxDate)) {
                    return true;
                }
                onDateTapped(mTempDate);
                return true;
            }
            return false;
        }

        /**
         * Sets the focus month.
         *
         * @param month the new focus month
         */
        public void setFocusMonth(int month) {
            if (mFocusedMonth == month) {
                return;
            }
            mFocusedMonth = month;
            notifyDataSetChanged();
        }

        /**
         * Sets the selected day.
         *
         * @param selectedDay the new selected day
         */
        public void setSelectedDay(Calendar selectedDay) {
            if (selectedDay.get(Calendar.DAY_OF_YEAR) == mSelectedDate
                    .get(Calendar.DAY_OF_YEAR)
                    && selectedDay.get(Calendar.YEAR) == mSelectedDate
                            .get(Calendar.YEAR)) {
                return;
            }
            mSelectedDate.setTimeInMillis(selectedDay.getTimeInMillis());
            mSelectedWeek = getWeeksSinceMinDate(mSelectedDate);
            mFocusedMonth = mSelectedDate.get(Calendar.MONTH);
            notifyDataSetChanged();
        }
    }

    /**
     * The Class WeekView.
     */
    private class WeekView extends View {
        
        /** The m day numbers. */
        private String[] mDayNumbers;
        
        /** The m draw paint. */
        private final Paint mDrawPaint = new Paint();
        
        /** The m first day. */
        private Calendar mFirstDay;
        
        /** The m focus day. */
        private boolean[] mFocusDay;
        
        /** The m has selected day. */
        private boolean mHasSelectedDay = false;
        
        /** The m height. */
        private int mHeight;
        
        /** The m last week day month. */
        private int mLastWeekDayMonth = -1;
        
        /** The m month num draw paint. */
        private final Paint mMonthNumDrawPaint = new Paint();
        
        /** The m month of first week day. */
        private int mMonthOfFirstWeekDay = -1;
        
        /** The m num cells. */
        private int mNumCells;
        
        /** The m selected day. */
        private int mSelectedDay = -1;
        
        /** The m selected left. */
        private int mSelectedLeft = -1;
        
        /** The m selected right. */
        private int mSelectedRight = -1;
        
        /** The m temp rect. */
        private final Rect mTempRect = new Rect();
        
        /** The m week. */
        private int mWeek = -1;
        
        /** The m width. */
        private int mWidth;

        /**
         * Instantiates a new week view.
         *
         * @param context the context
         */
        public WeekView(Context context) {
            super(context);

            mHeight = (mListView.getHeight() - mListView.getPaddingTop() - mListView
                    .getPaddingBottom()) / mShownWeekCount;
            setPaintProperties();
        }

        /**
         * Draw background.
         *
         * @param canvas the canvas
         */
        private void drawBackground(Canvas canvas) {
            if (!mHasSelectedDay) {
                return;
            }
            mDrawPaint.setColor(mSelectedWeekBackgroundColor);
            mTempRect.top = mWeekSeperatorLineWidth;
            mTempRect.bottom = mHeight;
            mTempRect.left = mShowWeekNumber ? mWidth / mNumCells : 0;
            mTempRect.right = mSelectedLeft - 2;
            canvas.drawRect(mTempRect, mDrawPaint);
            mTempRect.left = mSelectedRight + 3;
            mTempRect.right = mWidth;
            canvas.drawRect(mTempRect, mDrawPaint);
        }

        /**
         * Draw selected date vertical bars.
         *
         * @param canvas the canvas
         */
        private void drawSelectedDateVerticalBars(Canvas canvas) {
            if (!mHasSelectedDay) {
                return;
            }
            mSelectedDateVerticalBar.setBounds(mSelectedLeft
                    - mSelectedDateVerticalBarWidth / 2,
                    mWeekSeperatorLineWidth, mSelectedLeft
                            + mSelectedDateVerticalBarWidth / 2, mHeight);
            mSelectedDateVerticalBar.draw(canvas);
            mSelectedDateVerticalBar.setBounds(mSelectedRight
                    - mSelectedDateVerticalBarWidth / 2,
                    mWeekSeperatorLineWidth, mSelectedRight
                            + mSelectedDateVerticalBarWidth / 2, mHeight);
            mSelectedDateVerticalBar.draw(canvas);
        }

        /**
         * Draw week numbers.
         *
         * @param canvas the canvas
         */
        private void drawWeekNumbers(Canvas canvas) {
            float textHeight = mDrawPaint.getTextSize();
            int y = (int) ((mHeight + textHeight) / 2)
                    - mWeekSeperatorLineWidth;
            int nDays = mNumCells;

            mDrawPaint.setTextAlign(Align.CENTER);
            int i = 0;
            int divisor = 2 * nDays;
            if (mShowWeekNumber) {
                mDrawPaint.setColor(mWeekNumberColor);
                int x = mWidth / divisor;
                canvas.drawText(mDayNumbers[0], x, y, mDrawPaint);
                i++;
            }
            for (; i < nDays; i++) {
                mMonthNumDrawPaint
                        .setColor(mFocusDay[i] ? mFocusedMonthDateColor
                                : mUnfocusedMonthDateColor);
                int x = (2 * i + 1) * mWidth / divisor;
                canvas.drawText(mDayNumbers[i], x, y, mMonthNumDrawPaint);
            }
        }

        /**
         * Draw week separators.
         *
         * @param canvas the canvas
         */
        private void drawWeekSeparators(Canvas canvas) {
            int firstFullyVisiblePosition = mListView.getFirstVisiblePosition();
            if (mListView.getChildAt(0).getTop() < 0) {
                firstFullyVisiblePosition++;
            }
            if (firstFullyVisiblePosition == mWeek) {
                return;
            }
            mDrawPaint.setColor(mWeekSeparatorLineColor);
            mDrawPaint.setStrokeWidth(mWeekSeperatorLineWidth);
            float x = mShowWeekNumber ? mWidth / mNumCells : 0;
            canvas.drawLine(x, 0, mWidth, 0, mDrawPaint);
        }

        /**
         * Gets the day from location.
         *
         * @param x the x
         * @param outCalendar the out calendar
         * @return the day from location
         */
        public boolean getDayFromLocation(float x, Calendar outCalendar) {
            int dayStart = mShowWeekNumber ? mWidth / mNumCells : 0;
            if (x < dayStart || x > mWidth) {
                outCalendar.clear();
                return false;
            }
            int dayPosition = (int) ((x - dayStart) * mDaysPerWeek / (mWidth - dayStart));
            outCalendar.setTimeInMillis(mFirstDay.getTimeInMillis());
            outCalendar.add(Calendar.DAY_OF_MONTH, dayPosition);
            return true;
        }

        /**
         * Gets the first day.
         *
         * @return the first day
         */
        public Calendar getFirstDay() {
            return mFirstDay;
        }

        /**
         * Gets the month of first week day.
         *
         * @return the month of first week day
         */
        public int getMonthOfFirstWeekDay() {
            return mMonthOfFirstWeekDay;
        }

        /**
         * Gets the month of last week day.
         *
         * @return the month of last week day
         */
        public int getMonthOfLastWeekDay() {
            return mLastWeekDayMonth;
        }

        /**
         * Inits the.
         *
         * @param weekNumber the week number
         * @param selectedWeekDay the selected week day
         * @param focusedMonth the focused month
         */
        public void init(int weekNumber, int selectedWeekDay, int focusedMonth) {
            mSelectedDay = selectedWeekDay;
            mHasSelectedDay = mSelectedDay != -1;
            mNumCells = mShowWeekNumber ? mDaysPerWeek + 1 : mDaysPerWeek;
            mWeek = weekNumber;
            mTempDate.setTimeInMillis(mMinDate.getTimeInMillis());
            mTempDate.add(Calendar.WEEK_OF_YEAR, mWeek);
            mTempDate.setFirstDayOfWeek(mFirstDayOfWeek);
            mDayNumbers = new String[mNumCells];
            mFocusDay = new boolean[mNumCells];
            int i = 0;
            if (mShowWeekNumber) {
                mDayNumbers[0] = Integer.toString(mTempDate
                        .get(Calendar.WEEK_OF_YEAR));
                i++;
            }
            int diff = mFirstDayOfWeek - mTempDate.get(Calendar.DAY_OF_WEEK);
            mTempDate.add(Calendar.DAY_OF_MONTH, diff);
            mFirstDay = (Calendar) mTempDate.clone();
            mMonthOfFirstWeekDay = mTempDate.get(Calendar.MONTH);
            for (; i < mNumCells; i++) {
                mFocusDay[i] = mTempDate.get(Calendar.MONTH) == focusedMonth;
                if (mTempDate.before(mMinDate) || mTempDate.after(mMaxDate)) {
                    mDayNumbers[i] = "";
                } else {
                    mDayNumbers[i] = Integer.toString(mTempDate
                            .get(Calendar.DAY_OF_MONTH));
                }
                mTempDate.add(Calendar.DAY_OF_MONTH, 1);
            }
            if (mTempDate.get(Calendar.DAY_OF_MONTH) == 1) {
                mTempDate.add(Calendar.DAY_OF_MONTH, -1);
            }
            mLastWeekDayMonth = mTempDate.get(Calendar.MONTH);
            updateSelectionPositions();
        }

        /* (non-Javadoc)
         * @see android.view.View#onDraw(android.graphics.Canvas)
         */
        @Override
        protected void onDraw(Canvas canvas) {
            drawBackground(canvas);
            drawWeekNumbers(canvas);
            drawWeekSeparators(canvas);
            drawSelectedDateVerticalBars(canvas);
        }

        /* (non-Javadoc)
         * @see android.view.View#onMeasure(int, int)
         */
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mHeight);
        }

        /* (non-Javadoc)
         * @see android.view.View#onSizeChanged(int, int, int, int)
         */
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mWidth = w;
            updateSelectionPositions();
        }

        /**
         * Sets the paint properties.
         */
        private void setPaintProperties() {
            mDrawPaint.setFakeBoldText(false);
            mDrawPaint.setAntiAlias(true);
            mDrawPaint.setTextSize(mDateTextSize);
            mDrawPaint.setStyle(Style.FILL);

            mMonthNumDrawPaint.setFakeBoldText(true);
            mMonthNumDrawPaint.setAntiAlias(true);
            mMonthNumDrawPaint.setTextSize(mDateTextSize);
            mMonthNumDrawPaint.setColor(mFocusedMonthDateColor);
            mMonthNumDrawPaint.setStyle(Style.FILL);
            mMonthNumDrawPaint.setTextAlign(Align.CENTER);
        }

        /**
         * Update selection positions.
         */
        private void updateSelectionPositions() {
            if (mHasSelectedDay) {
                int selectedPosition = mSelectedDay - mFirstDayOfWeek;
                if (selectedPosition < 0) {
                    selectedPosition += 7;
                }
                if (mShowWeekNumber) {
                    selectedPosition++;
                }
                mSelectedLeft = selectedPosition * mWidth / mNumCells;
                mSelectedRight = (selectedPosition + 1) * mWidth / mNumCells;
            }
        }
    }

    /** The Constant ADJUSTMENT_SCROLL_DURATION. */
    private static final int ADJUSTMENT_SCROLL_DURATION = 500;
    
    /** The Constant DATE_FORMAT. */
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    
    /** The Constant DAYS_PER_WEEK. */
    private static final int DAYS_PER_WEEK = 7;
    
    /** The Constant DEFAULT_MAX_DATE. */
    private static final String DEFAULT_MAX_DATE = "01/01/2100";
    
    /** The Constant DEFAULT_MIN_DATE. */
    private static final String DEFAULT_MIN_DATE = "01/01/1900";
    
    /** The Constant DEFAULT_SHOW_WEEK_NUMBER. */
    private static final boolean DEFAULT_SHOW_WEEK_NUMBER = true;
    
    /** The Constant DEFAULT_SHOWN_WEEK_COUNT. */
    private static final int DEFAULT_SHOWN_WEEK_COUNT = 6;
    
    /** The Constant DEFAULT_WEEK_DAY_TEXT_APPEARANCE_RES_ID. */
    private static final int DEFAULT_WEEK_DAY_TEXT_APPEARANCE_RES_ID = -1;
    
    /** The Constant GOTO_SCROLL_DURATION. */
    private static final int GOTO_SCROLL_DURATION = 1000;
    
    /** The Constant LOG_TAG. */
    private static final String LOG_TAG = CalendarView.class.getSimpleName();
    
    /** The Constant MILLIS_IN_DAY. */
    private static final long MILLIS_IN_DAY = 86400000L;
    
    /** The Constant MILLIS_IN_WEEK. */
    private static final long MILLIS_IN_WEEK = CalendarView.DAYS_PER_WEEK
            * CalendarView.MILLIS_IN_DAY;
    
    /** The Constant SCROLL_CHANGE_DELAY. */
    private static final int SCROLL_CHANGE_DELAY = 40;
    
    /** The Constant SCROLL_HYST_WEEKS. */
    private static final int SCROLL_HYST_WEEKS = 2;
    
    /** The Constant UNSCALED_BOTTOM_BUFFER. */
    private static final int UNSCALED_BOTTOM_BUFFER = 20;
    
    /** The Constant UNSCALED_LIST_SCROLL_TOP_OFFSET. */
    private static final int UNSCALED_LIST_SCROLL_TOP_OFFSET = 2;
    
    /** The Constant UNSCALED_SELECTED_DATE_VERTICAL_BAR_WIDTH. */
    private static final int UNSCALED_SELECTED_DATE_VERTICAL_BAR_WIDTH = 6;
    
    /** The Constant UNSCALED_WEEK_MIN_VISIBLE_HEIGHT. */
    private static final int UNSCALED_WEEK_MIN_VISIBLE_HEIGHT = 12;
    
    /** The Constant UNSCALED_WEEK_SEPARATOR_LINE_WIDTH. */
    private static final int UNSCALED_WEEK_SEPARATOR_LINE_WIDTH = 1;
    
    /** The m adapter. */
    private WeeksAdapter mAdapter;
    
    /** The m bottom buffer. */
    private int mBottomBuffer = 20;
    
    /** The m current locale. */
    private Locale mCurrentLocale;
    
    /** The m current month displayed. */
    private int mCurrentMonthDisplayed;
    
    /** The m current scroll state. */
    private int mCurrentScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    
    /** The m current year displayed. */
    private int mCurrentYearDisplayed;
    
    /** The m date format. */
    private final java.text.DateFormat mDateFormat = new SimpleDateFormat(
            CalendarView.DATE_FORMAT);
    
    /** The m date text size. */
    private final int mDateTextSize;
    
    /** The m day labels. */
    private String[] mDayLabels;
    
    /** The m day names header. */
    private ViewGroup mDayNamesHeader;
    
    /** The m days per week. */
    private int mDaysPerWeek = 7;
    
    /** The m first day of month. */
    private Calendar mFirstDayOfMonth;
    
    /** The m first day of week. */
    private int mFirstDayOfWeek;
    
    /** The m focused month date color. */
    private final int mFocusedMonthDateColor;
    
    /** The m friction. */
    private float mFriction = .05f;
    
    /** The m is scrolling up. */
    private boolean mIsScrollingUp = false;
    
    /** The m list scroll top offset. */
    private int mListScrollTopOffset = 2;
    
    /** The m list view. */
    private ListView mListView;
    
    /** The m max date. */
    private Calendar mMaxDate;
    
    /** The m min date. */
    private Calendar mMinDate;
    
    /** The m month name. */
    private TextView mMonthName;
    
    /** The m on date change listener. */
    private OnDateChangeListener mOnDateChangeListener;
    
    /** The m previous scroll position. */
    private long mPreviousScrollPosition;
    
    /** The m previous scroll state. */
    private int mPreviousScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    
    /** The m scroll state changed runnable. */
    private ScrollStateRunnable mScrollStateChangedRunnable = new ScrollStateRunnable();
    
    /** The m selected date vertical bar. */
    private final Drawable mSelectedDateVerticalBar;
    
    /** The m selected date vertical bar width. */
    private final int mSelectedDateVerticalBarWidth;
    
    /** The m selected week background color. */
    private final int mSelectedWeekBackgroundColor;
    
    /** The m shown week count. */
    private int mShownWeekCount;
    
    /** The m show week number. */
    private boolean mShowWeekNumber;
    
    /** The m temp date. */
    private Calendar mTempDate;
    
    /** The m unfocused month date color. */
    private final int mUnfocusedMonthDateColor;
    
    /** The m velocity scale. */
    private float mVelocityScale = 0.333f;
    
    /** The m week min visible height. */
    private int mWeekMinVisibleHeight = 12;
    
    /** The m week number color. */
    private final int mWeekNumberColor;
    
    /** The m week separator line color. */
    private final int mWeekSeparatorLineColor;
    
    /** The m week seperator line width. */
    private final int mWeekSeperatorLineWidth;

    /**
     * Instantiates a new calendar view.
     *
     * @param context the context
     */
    public CalendarView(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new calendar view.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.calendarViewStyle);
    }

    /**
     * Instantiates a new calendar view.
     *
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCurrentLocale(Locale.getDefault());
        TypedArray attributesArray = context.obtainStyledAttributes(attrs,
                R.styleable.CalendarView, defStyle, R.style.Holo_CalendarView);
        mShowWeekNumber = attributesArray.getBoolean(
                R.styleable.CalendarView_showWeekNumber,
                CalendarView.DEFAULT_SHOW_WEEK_NUMBER);
        mFirstDayOfWeek = attributesArray.getInt(
                R.styleable.CalendarView_firstDayOfWeek, 1);
        String minDate = attributesArray
                .getString(R.styleable.CalendarView_minDate);
        if (TextUtils.isEmpty(minDate) || !parseDate(minDate, mMinDate)) {
            parseDate(CalendarView.DEFAULT_MIN_DATE, mMinDate);
        }
        String maxDate = attributesArray
                .getString(R.styleable.CalendarView_maxDate);
        if (TextUtils.isEmpty(maxDate) || !parseDate(maxDate, mMaxDate)) {
            parseDate(CalendarView.DEFAULT_MAX_DATE, mMaxDate);
        }
        if (mMaxDate.before(mMinDate)) {
            throw new IllegalArgumentException(
                    "Max date cannot be before min date.");
        }
        mShownWeekCount = attributesArray.getInt(
                R.styleable.CalendarView_shownWeekCount,
                CalendarView.DEFAULT_SHOWN_WEEK_COUNT);
        mSelectedWeekBackgroundColor = attributesArray.getColor(
                R.styleable.CalendarView_selectedWeekBackgroundColor, 0);
        mFocusedMonthDateColor = attributesArray.getColor(
                R.styleable.CalendarView_focusedMonthDateColor, 0);
        mUnfocusedMonthDateColor = attributesArray.getColor(
                R.styleable.CalendarView_unfocusedMonthDateColor, 0);
        mWeekSeparatorLineColor = attributesArray.getColor(
                R.styleable.CalendarView_weekSeparatorLineColor, 0);
        mWeekNumberColor = attributesArray.getColor(
                R.styleable.CalendarView_weekNumberColor, 0);
        mSelectedDateVerticalBar = attributesArray
                .getDrawable(R.styleable.CalendarView_selectedDateVerticalBar);
        attributesArray.getResourceId(
                R.styleable.CalendarView_dateTextAppearance,
                android.R.style.TextAppearance_Small);
        mDateTextSize = (int) (12 * getContext().getResources()
                .getDisplayMetrics().density);
        int weekDayTextAppearanceResId = attributesArray.getResourceId(
                R.styleable.CalendarView_weekDayTextAppearance,
                CalendarView.DEFAULT_WEEK_DAY_TEXT_APPEARANCE_RES_ID);
        attributesArray.recycle();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mWeekMinVisibleHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                CalendarView.UNSCALED_WEEK_MIN_VISIBLE_HEIGHT, displayMetrics);
        mListScrollTopOffset = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                CalendarView.UNSCALED_LIST_SCROLL_TOP_OFFSET, displayMetrics);
        mBottomBuffer = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                CalendarView.UNSCALED_BOTTOM_BUFFER, displayMetrics);
        mSelectedDateVerticalBarWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                CalendarView.UNSCALED_SELECTED_DATE_VERTICAL_BAR_WIDTH,
                displayMetrics);
        mWeekSeperatorLineWidth = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        CalendarView.UNSCALED_WEEK_SEPARATOR_LINE_WIDTH,
                        displayMetrics);
        LayoutInflater.inflate(context, R.layout.he_calendar_view, this, true);
        FontLoader.apply(this);
        mListView = (ListView) findViewById(R.id.list);
        mDayNamesHeader = (ViewGroup) findViewById(R.id.day_names);
        mMonthName = (TextView) findViewById(R.id.month_name);
        setUpHeader(weekDayTextAppearanceResId);
        setUpListView();
        setUpAdapter();
        mTempDate.setTimeInMillis(System.currentTimeMillis());
        if (mTempDate.before(mMinDate)) {
            goTo(mMinDate, false, true, true);
        } else if (mMaxDate.before(mTempDate)) {
            goTo(mMaxDate, false, true, true);
        } else {
            goTo(mTempDate, false, true, true);
        }
        invalidate();
    }

    /**
     * Gets the calendar for locale.
     *
     * @param oldCalendar the old calendar
     * @param locale the locale
     * @return the calendar for locale
     */
    private Calendar getCalendarForLocale(Calendar oldCalendar, Locale locale) {
        if (oldCalendar == null) {
            return Calendar.getInstance(locale);
        } else {
            final long currentTimeMillis = oldCalendar.getTimeInMillis();
            Calendar newCalendar = Calendar.getInstance(locale);
            newCalendar.setTimeInMillis(currentTimeMillis);
            return newCalendar;
        }
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public long getDate() {
        return mAdapter.mSelectedDate.getTimeInMillis();
    }

    /**
     * Gets the first day of week.
     *
     * @return the first day of week
     */
    public int getFirstDayOfWeek() {
        return mFirstDayOfWeek;
    }

    /**
     * Gets the max date.
     *
     * @return the max date
     */
    public long getMaxDate() {
        return mMaxDate.getTimeInMillis();
    }

    /**
     * Gets the min date.
     *
     * @return the min date
     */
    public long getMinDate() {
        return mMinDate.getTimeInMillis();
    }

    /**
     * Gets the show week number.
     *
     * @return the show week number
     */
    public boolean getShowWeekNumber() {
        return mShowWeekNumber;
    }

    /**
     * Gets the weeks since min date.
     *
     * @param date the date
     * @return the weeks since min date
     */
    private int getWeeksSinceMinDate(Calendar date) {
        if (date.before(mMinDate)) {
            throw new IllegalArgumentException("fromDate: "
                    + mMinDate.getTime() + " does not precede toDate: "
                    + date.getTime());
        }
        long endTimeMillis = date.getTimeInMillis()
                + date.getTimeZone().getOffset(date.getTimeInMillis());
        long startTimeMillis = mMinDate.getTimeInMillis()
                + mMinDate.getTimeZone().getOffset(mMinDate.getTimeInMillis());
        long dayOffsetMillis = (mMinDate.get(Calendar.DAY_OF_WEEK) - mFirstDayOfWeek)
                * CalendarView.MILLIS_IN_DAY;
        return (int) ((endTimeMillis - startTimeMillis + dayOffsetMillis) / CalendarView.MILLIS_IN_WEEK);
    }

    /**
     * Go to.
     *
     * @param date the date
     * @param animate the animate
     * @param setSelected the set selected
     * @param forceScroll the force scroll
     */
    @SuppressLint("NewApi")
    private void goTo(Calendar date, boolean animate, boolean setSelected,
            boolean forceScroll) {
        if (date.before(mMinDate) || date.after(mMaxDate)) {
            throw new IllegalArgumentException("Time not between "
                    + mMinDate.getTime() + " and " + mMaxDate.getTime());
        }
        int firstFullyVisiblePosition = mListView.getFirstVisiblePosition();
        View firstChild = mListView.getChildAt(0);
        if (firstChild != null && firstChild.getTop() < 0) {
            firstFullyVisiblePosition++;
        }
        int lastFullyVisiblePosition = firstFullyVisiblePosition
                + mShownWeekCount - 1;
        if (firstChild != null && firstChild.getTop() > mBottomBuffer) {
            lastFullyVisiblePosition--;
        }
        if (setSelected) {
            mAdapter.setSelectedDay(date);
        }
        int position = getWeeksSinceMinDate(date);
        if (position < firstFullyVisiblePosition
                || position > lastFullyVisiblePosition || forceScroll) {
            mFirstDayOfMonth.setTimeInMillis(date.getTimeInMillis());
            mFirstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
            setMonthDisplayed(mFirstDayOfMonth);
            if (mFirstDayOfMonth.before(mMinDate)) {
                position = 0;
            } else {
                position = getWeeksSinceMinDate(mFirstDayOfMonth);
            }
            mPreviousScrollState = OnScrollListener.SCROLL_STATE_FLING;
            if (animate && VERSION.SDK_INT >= 11) {
                mListView
                        .smoothScrollToPositionFromTop(position,
                                mListScrollTopOffset,
                                CalendarView.GOTO_SCROLL_DURATION);
            } else {
                mListView.setSelectionFromTop(position, mListScrollTopOffset);
                onScrollStateChanged(mListView,
                        OnScrollListener.SCROLL_STATE_IDLE);
            }
        } else if (setSelected) {
            setMonthDisplayed(date);
        }
    }

    /* (non-Javadoc)
     * @see android.view.View#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return mListView.isEnabled();
    }

    /**
     * Checks if is same date.
     *
     * @param firstDate the first date
     * @param secondDate the second date
     * @return true, if is same date
     */
    private boolean isSameDate(Calendar firstDate, Calendar secondDate) {
        return firstDate.get(Calendar.DAY_OF_YEAR) == secondDate
                .get(Calendar.DAY_OF_YEAR)
                && firstDate.get(Calendar.YEAR) == secondDate
                        .get(Calendar.YEAR);
    }

    /* (non-Javadoc)
     * @see android.view.View#onConfigurationChanged(android.content.res.Configuration)
     */
    @SuppressLint("NewApi")
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setCurrentLocale(newConfig.locale);
    }

    /**
     * On scroll.
     *
     * @param view the view
     * @param firstVisibleItem the first visible item
     * @param visibleItemCount the visible item count
     * @param totalItemCount the total item count
     */
    private void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        WeekView child = (WeekView) view.getChildAt(0);
        if (child == null) {
            return;
        }
        long currScroll = view.getFirstVisiblePosition() * child.getHeight()
                - child.getBottom();
        if (currScroll < mPreviousScrollPosition) {
            mIsScrollingUp = true;
        } else if (currScroll > mPreviousScrollPosition) {
            mIsScrollingUp = false;
        } else {
            return;
        }
        int offset = child.getBottom() < mWeekMinVisibleHeight ? 1 : 0;
        if (mIsScrollingUp) {
            child = (WeekView) view.getChildAt(CalendarView.SCROLL_HYST_WEEKS
                    + offset);
        } else if (offset != 0) {
            child = (WeekView) view.getChildAt(offset);
        }
        int month;
        if (mIsScrollingUp) {
            month = child.getMonthOfFirstWeekDay();
        } else {
            month = child.getMonthOfLastWeekDay();
        }
        int monthDiff;
        if (mCurrentMonthDisplayed == 11 && month == 0) {
            monthDiff = 1;
        } else if (mCurrentMonthDisplayed == 0 && month == 11) {
            monthDiff = -1;
        } else {
            monthDiff = month - mCurrentMonthDisplayed;
        }
        if (!mIsScrollingUp && monthDiff > 0 || mIsScrollingUp && monthDiff < 0) {
            Calendar firstDay = child.getFirstDay();
            if (mIsScrollingUp) {
                firstDay.add(Calendar.DAY_OF_MONTH, -CalendarView.DAYS_PER_WEEK);
            } else {
                firstDay.add(Calendar.DAY_OF_MONTH, CalendarView.DAYS_PER_WEEK);
            }
            setMonthDisplayed(firstDay);
        }
        mPreviousScrollPosition = currScroll;
        mPreviousScrollState = mCurrentScrollState;
    }

    /**
     * On scroll state changed.
     *
     * @param view the view
     * @param scrollState the scroll state
     */
    private void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollStateChangedRunnable.doScrollStateChange(view, scrollState);
    }

    /**
     * Parses the date.
     *
     * @param date the date
     * @param outDate the out date
     * @return true, if successful
     */
    private boolean parseDate(String date, Calendar outDate) {
        try {
            outDate.setTime(mDateFormat.parse(date));
            return true;
        } catch (ParseException e) {
            Log.w(CalendarView.LOG_TAG, "Date: " + date + " not in format: "
                    + CalendarView.DATE_FORMAT);
            return false;
        }
    }

    /**
     * Sets the current locale.
     *
     * @param locale the new current locale
     */
    private void setCurrentLocale(Locale locale) {
        if (locale.equals(mCurrentLocale)) {
            return;
        }

        mCurrentLocale = locale;
        mTempDate = getCalendarForLocale(mTempDate, locale);
        mFirstDayOfMonth = getCalendarForLocale(mFirstDayOfMonth, locale);
        mMinDate = getCalendarForLocale(mMinDate, locale);
        mMaxDate = getCalendarForLocale(mMaxDate, locale);
    }

    /**
     * Sets the date.
     *
     * @param date the new date
     */
    public void setDate(long date) {
        setDate(date, false, false);
    }

    /**
     * Sets the date.
     *
     * @param date the date
     * @param animate the animate
     * @param center the center
     */
    public void setDate(long date, boolean animate, boolean center) {
        mTempDate.setTimeInMillis(date);
        if (isSameDate(mTempDate, mAdapter.mSelectedDate)) {
            return;
        }
        goTo(mTempDate, animate, true, center);
    }

    /* (non-Javadoc)
     * @see android.view.View#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        mListView.setEnabled(enabled);
    }

    /**
     * Sets the first day of week.
     *
     * @param firstDayOfWeek the new first day of week
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if (mFirstDayOfWeek == firstDayOfWeek) {
            return;
        }
        mFirstDayOfWeek = firstDayOfWeek;
        mAdapter.init();
        mAdapter.notifyDataSetChanged();
        setUpHeader(CalendarView.DEFAULT_WEEK_DAY_TEXT_APPEARANCE_RES_ID);
    }

    /**
     * Sets the max date.
     *
     * @param maxDate the new max date
     */
    public void setMaxDate(long maxDate) {
        mTempDate.setTimeInMillis(maxDate);
        if (isSameDate(mTempDate, mMaxDate)) {
            return;
        }
        mMaxDate.setTimeInMillis(maxDate);
        mAdapter.init();
        Calendar date = mAdapter.mSelectedDate;
        if (date.after(mMaxDate)) {
            setDate(mMaxDate.getTimeInMillis());
        } else {
            goTo(date, false, true, false);
        }
    }

    /**
     * Sets the min date.
     *
     * @param minDate the new min date
     */
    public void setMinDate(long minDate) {
        mTempDate.setTimeInMillis(minDate);
        if (isSameDate(mTempDate, mMinDate)) {
            return;
        }
        mMinDate.setTimeInMillis(minDate);
        Calendar date = mAdapter.mSelectedDate;
        if (date.before(mMinDate)) {
            mAdapter.setSelectedDay(mMinDate);
        }
        mAdapter.init();
        if (date.before(mMinDate)) {
            setDate(mTempDate.getTimeInMillis());
        } else {
            goTo(date, false, true, false);
        }
    }

    /**
     * Sets the month displayed.
     *
     * @param calendar the new month displayed
     */
    private void setMonthDisplayed(Calendar calendar) {
        final int newMonthDisplayed = calendar.get(Calendar.MONTH);
        final int newYearDisplayed = calendar.get(Calendar.YEAR);
        if (mCurrentMonthDisplayed != newMonthDisplayed
                || mCurrentYearDisplayed != newYearDisplayed) {
            mCurrentMonthDisplayed = newMonthDisplayed;
            mCurrentYearDisplayed = newYearDisplayed;
            mAdapter.setFocusMonth(mCurrentMonthDisplayed);
            final int flags = DateUtils.FORMAT_SHOW_DATE
                    | DateUtils.FORMAT_NO_MONTH_DAY
                    | DateUtils.FORMAT_SHOW_YEAR;
            final long millis = calendar.getTimeInMillis();
            String newMonthName = DateUtils.formatDateRange(getContext(),
                    millis, millis, flags);
            mMonthName.setText(newMonthName);
            mMonthName.invalidate();
        }
    }

    /**
     * Sets the on date change listener.
     *
     * @param listener the new on date change listener
     */
    public void setOnDateChangeListener(OnDateChangeListener listener) {
        mOnDateChangeListener = listener;
    }

    /**
     * Sets the show week number.
     *
     * @param showWeekNumber the new show week number
     */
    public void setShowWeekNumber(boolean showWeekNumber) {
        if (mShowWeekNumber == showWeekNumber) {
            return;
        }
        mShowWeekNumber = showWeekNumber;
        mAdapter.notifyDataSetChanged();
        setUpHeader(CalendarView.DEFAULT_WEEK_DAY_TEXT_APPEARANCE_RES_ID);
    }

    /**
     * Sets the up adapter.
     */
    private void setUpAdapter() {
        if (mAdapter == null) {
            mAdapter = new WeeksAdapter(getContext());
            mAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    if (mOnDateChangeListener != null) {
                        Calendar selectedDay = mAdapter.getSelectedDay();
                        mOnDateChangeListener.onSelectedDayChange(
                                CalendarView.this,
                                selectedDay.get(Calendar.YEAR),
                                selectedDay.get(Calendar.MONTH),
                                selectedDay.get(Calendar.DAY_OF_MONTH));
                    }
                }
            });
            mListView.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Sets the up header.
     *
     * @param weekDayTextAppearanceResId the new up header
     */
    @SuppressWarnings("deprecation")
	private void setUpHeader(int weekDayTextAppearanceResId) {
        mDayLabels = new String[mDaysPerWeek];
        for (int i = mFirstDayOfWeek, count = mFirstDayOfWeek + mDaysPerWeek; i < count; i++) {
            int calendarDay = i > Calendar.SATURDAY ? i - Calendar.SATURDAY : i;
            mDayLabels[i - mFirstDayOfWeek] = DateUtils.getDayOfWeekString(
                    calendarDay, DateUtils.LENGTH_SHORTEST);
        }
        TextView label = (TextView) mDayNamesHeader.getChildAt(0);
        if (mShowWeekNumber) {
            label.setVisibility(View.VISIBLE);
        } else {
            label.setVisibility(View.GONE);
        }
        for (int i = 1, count = mDayNamesHeader.getChildCount(); i < count; i++) {
            label = (TextView) mDayNamesHeader.getChildAt(i);
            if (weekDayTextAppearanceResId > -1) {
                label.setTextAppearance(getContext(),
                        weekDayTextAppearanceResId);
            }
            if (i < mDaysPerWeek + 1) {
                label.setText(mDayLabels[i - 1]);
                label.setVisibility(View.VISIBLE);
            } else {
                label.setVisibility(View.GONE);
            }
        }
        mDayNamesHeader.invalidate();
    }

    /**
     * Sets the up list view.
     */
    @SuppressLint("NewApi")
    private void setUpListView() {
        mListView.setDivider(null);
        mListView.setItemsCanFocus(true);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                CalendarView.this.onScroll(view, firstVisibleItem,
                        visibleItemCount, totalItemCount);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                CalendarView.this.onScrollStateChanged(view, scrollState);
            }
        });
        if (VERSION.SDK_INT >= 11) {
            mListView.setFriction(mFriction);
            mListView.setVelocityScale(mVelocityScale);
        }
    }
}
