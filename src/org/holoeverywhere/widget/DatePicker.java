
package org.holoeverywhere.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.holoeverywhere.LayoutInflater;
import com.actionbarsherlock.R;
import org.holoeverywhere.internal.NumberPickerEditText;
import org.holoeverywhere.util.Arrays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

// TODO: Auto-generated Javadoc
/**
 * The Class DatePicker.
 */
@SuppressLint("SimpleDateFormat")
public class DatePicker extends FrameLayout {
    
    /**
     * The Class Callback.
     */
    private final class Callback implements NumberPicker.OnValueChangeListener,
            CalendarView.OnDateChangeListener {
        
        /* (non-Javadoc)
         * @see org.holoeverywhere.widget.CalendarView.OnDateChangeListener#onSelectedDayChange(org.holoeverywhere.widget.CalendarView, int, int, int)
         */
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month,
                int monthDay) {
            setDate(year, month, monthDay);
            updateSpinners();
            notifyDateChanged();
        }

        /* (non-Javadoc)
         * @see org.holoeverywhere.widget.NumberPicker.OnValueChangeListener#onValueChange(org.holoeverywhere.widget.NumberPicker, int, int)
         */
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            updateInputState();
            tempDate.setTimeInMillis(currentDate.getTimeInMillis());
            if (picker == daySpinner) {
                int maxDayOfMonth = tempDate
                        .getActualMaximum(Calendar.DAY_OF_MONTH);
                if (oldVal == maxDayOfMonth && newVal == 1) {
                    tempDate.add(Calendar.DAY_OF_MONTH, 1);
                } else if (oldVal == 1 && newVal == maxDayOfMonth) {
                    tempDate.add(Calendar.DAY_OF_MONTH, -1);
                } else {
                    tempDate.add(Calendar.DAY_OF_MONTH, newVal - oldVal);
                }
            } else if (picker == monthSpinner) {
                if (oldVal == 11 && newVal == 0) {
                    tempDate.add(Calendar.MONTH, 1);
                } else if (oldVal == 0 && newVal == 11) {
                    tempDate.add(Calendar.MONTH, -1);
                } else {
                    tempDate.add(Calendar.MONTH, newVal - oldVal);
                }
            } else if (picker == yearSpinner) {
                tempDate.set(Calendar.YEAR, newVal);
            } else {
                return;
            }
            setDate(tempDate.get(Calendar.YEAR), tempDate.get(Calendar.MONTH),
                    tempDate.get(Calendar.DAY_OF_MONTH));
            updateSpinners();
            updateCalendarView();
            notifyDateChanged();
        }

    }

    /**
     * The listener interface for receiving onDateChanged events.
     * The class that is interested in processing a onDateChanged
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addOnDateChangedListener<code> method. When
     * the onDateChanged event occurs, that object's appropriate
     * method is invoked.
     *
     * @see OnDateChangedEvent
     */
    public interface OnDateChangedListener {
        
        /**
         * On date changed.
         *
         * @param view the view
         * @param year the year
         * @param monthOfYear the month of year
         * @param dayOfMonth the day of month
         */
        void onDateChanged(DatePicker view, int year, int monthOfYear,
                int dayOfMonth);
    }

    /**
     * The Class SavedState.
     */
    private static class SavedState extends BaseSavedState {
        
        /** The Constant CREATOR. */
        @SuppressWarnings("all")
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        /** The day. */
        private final int year, month, day;

        /**
         * Instantiates a new saved state.
         *
         * @param in the in
         */
        private SavedState(Parcel in) {
            super(in);
            year = in.readInt();
            month = in.readInt();
            day = in.readInt();
        }

        /**
         * Instantiates a new saved state.
         *
         * @param superState the super state
         * @param year the year
         * @param month the month
         * @param day the day
         */
        private SavedState(Parcelable superState, int year, int month, int day) {
            super(superState);
            this.year = year;
            this.month = month;
            this.day = day;
        }

        /* (non-Javadoc)
         * @see android.view.AbsSavedState#writeToParcel(android.os.Parcel, int)
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(year);
            dest.writeInt(month);
            dest.writeInt(day);
        }
    }

    /** The Constant DATE_FORMAT. */
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    
    /** The Constant LOG_TAG. */
    private static final String LOG_TAG = DatePicker.class.getSimpleName();

    /**
     * Gets the calendar for locale.
     *
     * @param oldCalendar the old calendar
     * @param locale the locale
     * @return the calendar for locale
     */
    private static Calendar getCalendarForLocale(Calendar oldCalendar,
            Locale locale) {
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
     * Sets the content description.
     *
     * @param parent the parent
     * @param childId the child id
     * @param textId the text id
     */
    private static void setContentDescription(View parent, int childId,
            int textId) {
        if (parent == null) {
            return;
        }
        View child = parent.findViewById(childId);
        if (child != null) {
            child.setContentDescription(parent.getContext().getText(textId));
        }
    }

    /** The callback. */
    private final Callback callback = new Callback();
    
    /** The date format. */
    private final java.text.DateFormat dateFormat = new SimpleDateFormat(
            DatePicker.DATE_FORMAT);
    
    /** The year spinner. */
    private final NumberPicker daySpinner, monthSpinner, yearSpinner;
    
    /** The input method manager. */
    private final InputMethodManager inputMethodManager;
    
    /** The locale. */
    private Locale locale;
    
    /** The m calendar view. */
    private final CalendarView mCalendarView;
    
    /** The number of months. */
    private int numberOfMonths;
    
    /** The on date changed listener. */
    private OnDateChangedListener onDateChangedListener;
    
    /** The short months. */
    private String[] shortMonths;
    
    /** The spinners. */
    private final LinearLayout spinners;
    
    /** The current date. */
    private Calendar tempDate, minDate, maxDate, currentDate;

    /**
     * Instantiates a new date picker.
     *
     * @param context the context
     */
    public DatePicker(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new date picker.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public DatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.datePickerStyle);
    }

    /**
     * Instantiates a new date picker.
     *
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public DatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.DatePicker, defStyle, R.style.Holo_DatePicker);
        boolean spinnersShown = a.getBoolean(
                R.styleable.DatePicker_spinnersShown, true);
        boolean calendarViewShown = a.getBoolean(
                R.styleable.DatePicker_calendarViewShown, true);
        boolean forceShownState = a.getBoolean(
                R.styleable.DatePicker_forceShownState, false);
        int startYear = a.getInt(R.styleable.DatePicker_startYear, 1900);
        int endYear = a.getInt(R.styleable.DatePicker_endYear, 2100);
        String minDate = a.getString(R.styleable.DatePicker_minDate);
        String maxDate = a.getString(R.styleable.DatePicker_maxDate);
        int layoutResourceId = a.getResourceId(R.styleable.DatePicker_layout,
                R.layout.he_date_picker_holo);
        a.recycle();
        inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        setLocale(Locale.getDefault());
        LayoutInflater.inflate(context, layoutResourceId, this, true);
        spinners = (LinearLayout) findViewById(R.id.pickers);
        mCalendarView = (CalendarView) findViewById(R.id.calendar_view);
        daySpinner = (NumberPicker) findViewById(R.id.day);
        monthSpinner = (NumberPicker) findViewById(R.id.month);
        yearSpinner = (NumberPicker) findViewById(R.id.year);
        if (((AccessibilityManager) getContext().getSystemService(
                Context.ACCESSIBILITY_SERVICE)).isEnabled()) {
            setContentDescriptions();
        }
        mCalendarView.setOnDateChangeListener(callback);
        daySpinner.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        daySpinner.setOnLongPressUpdateInterval(100);
        daySpinner.setOnValueChangedListener(callback);
        monthSpinner.setMinValue(0);
        monthSpinner.setMaxValue(numberOfMonths - 1);
        monthSpinner.setDisplayedValues(shortMonths);
        monthSpinner.setOnLongPressUpdateInterval(200);
        monthSpinner.setOnValueChangedListener(callback);
        yearSpinner.setOnLongPressUpdateInterval(100);
        yearSpinner.setOnValueChangedListener(callback);
        if (spinnersShown || calendarViewShown || forceShownState) {
            setSpinnersShown(spinnersShown);
            setCalendarViewShown(calendarViewShown);
        } else {
            setSpinnersShown(true);
            setCalendarViewShown(false);
        }
        tempDate.clear();
        if (TextUtils.isEmpty(minDate) || !parseDate(minDate, tempDate)) {
            tempDate.set(startYear, 0, 1);
        }
        setMinDate(tempDate.getTimeInMillis());
        tempDate.clear();
        if (TextUtils.isEmpty(maxDate) || !parseDate(maxDate, tempDate)) {
            tempDate.set(endYear, 11, 31);
        }
        setMaxDate(tempDate.getTimeInMillis());
        currentDate.setTimeInMillis(System.currentTimeMillis());
        init(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH), null);
        reorderSpinners();
    }

    /**
     * Check input state.
     *
     * @param spinners the spinners
     */
    private void checkInputState(NumberPicker... spinners) {
        for (NumberPicker spinner : spinners) {
            NumberPickerEditText input = spinner.getInputField();
            if (inputMethodManager.isActive(input)) {
                input.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
    }

    /* (non-Javadoc)
     * @see android.view.View#dispatchPopulateAccessibilityEvent(android.view.accessibility.AccessibilityEvent)
     */
    @SuppressLint("NewApi")
    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    /* (non-Javadoc)
     * @see android.view.ViewGroup#dispatchRestoreInstanceState(android.util.SparseArray)
     */
    @Override
    protected void dispatchRestoreInstanceState(
            SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    /**
     * Gets the calendar view.
     *
     * @return the calendar view
     */
    public CalendarView getCalendarView() {
        return mCalendarView;
    }

    /**
     * Gets the calendar view shown.
     *
     * @return the calendar view shown
     */
    public boolean getCalendarViewShown() {
        return mCalendarView.isShown();
    }

    /**
     * Gets the day of month.
     *
     * @return the day of month
     */
    public int getDayOfMonth() {
        return currentDate.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Gets the max date.
     *
     * @return the max date
     */
    public long getMaxDate() {
        return mCalendarView.getMaxDate();
    }

    /**
     * Gets the min date.
     *
     * @return the min date
     */
    public long getMinDate() {
        return mCalendarView.getMinDate();
    }

    /**
     * Gets the month.
     *
     * @return the month
     */
    public int getMonth() {
        return currentDate.get(Calendar.MONTH);
    }

    /**
     * Gets the on date changed listener.
     *
     * @return the on date changed listener
     */
    public OnDateChangedListener getOnDateChangedListener() {
        return onDateChangedListener;
    }

    /**
     * Gets the spinners shown.
     *
     * @return the spinners shown
     */
    public boolean getSpinnersShown() {
        return spinners.isShown();
    }

    /**
     * Gets the year.
     *
     * @return the year
     */
    public int getYear() {
        return currentDate.get(Calendar.YEAR);
    }

    /**
     * Inits the.
     *
     * @param year the year
     * @param monthOfYear the month of year
     * @param dayOfMonth the day of month
     * @param onDateChangedListener the on date changed listener
     */
    public void init(int year, int monthOfYear, int dayOfMonth,
            OnDateChangedListener onDateChangedListener) {
        setOnDateChangedListener(onDateChangedListener);
        setDate(year, monthOfYear, dayOfMonth);
        updateSpinners();
        updateCalendarView();
    }

    /**
     * Checks if is new date.
     *
     * @param year the year
     * @param month the month
     * @param dayOfMonth the day of month
     * @return true, if is new date
     */
    private boolean isNewDate(int year, int month, int dayOfMonth) {
        return currentDate.get(Calendar.YEAR) != year
                || currentDate.get(Calendar.MONTH) != dayOfMonth
                || currentDate.get(Calendar.DAY_OF_MONTH) != month;
    }

    /**
     * Notify date changed.
     */
    private void notifyDateChanged() {
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        if (onDateChangedListener != null) {
            onDateChangedListener.onDateChanged(this, getYear(), getMonth(),
                    getDayOfMonth());
        }
    }

    /* (non-Javadoc)
     * @see android.view.View#onConfigurationChanged(android.content.res.Configuration)
     */
    @Override
    @SuppressLint("NewApi")
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setLocale(newConfig.locale);
    }

    /* (non-Javadoc)
     * @see android.view.View#onPopulateAccessibilityEvent(android.view.accessibility.AccessibilityEvent)
     */
    @SuppressLint("NewApi")
    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (VERSION.SDK_INT >= 14) {
            super.onPopulateAccessibilityEvent(event);
        }
        final int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_SHOW_YEAR;
        String selectedDateUtterance = DateUtils.formatDateTime(getContext(),
                currentDate.getTimeInMillis(), flags);
        event.getText().add(selectedDateUtterance);
    }

    /* (non-Javadoc)
     * @see android.view.View#onRestoreInstanceState(android.os.Parcelable)
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setDate(ss.year, ss.month, ss.day);
        updateSpinners();
        updateCalendarView();
    }

    /* (non-Javadoc)
     * @see android.view.View#onSaveInstanceState()
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), getYear(),
                getMonth(), getDayOfMonth());
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
            outDate.setTime(dateFormat.parse(date));
            return true;
        } catch (ParseException e) {
            Log.w(DatePicker.LOG_TAG, "Date: " + date + " not in format: "
                    + DatePicker.DATE_FORMAT);
            return false;
        }
    }

    /**
     * Push spinner.
     *
     * @param spinner the spinner
     * @param spinnerCount the spinner count
     * @param i the i
     */
    private void pushSpinner(NumberPicker spinner, int spinnerCount, int i) {
        if (spinner.getParent() != null
                && spinner.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) spinner.getParent();
            if (parent.getChildAt(i) != spinner) {
                parent.removeView(spinner);
                parent.addView(spinner);
                setImeOptions(spinner, spinnerCount, i);
            }
        }
    }

    /**
     * Reorder spinners.
     */
    private void reorderSpinners() {
        char[] order = DateFormat.getDateFormatOrder(getContext());
        final int spinnerCount = order.length;
        for (int i = 0; i < spinnerCount; i++) {
            switch (order[i]) {
                case DateFormat.DATE:
                    pushSpinner(daySpinner, spinnerCount, i);
                    break;
                case DateFormat.MONTH:
                    pushSpinner(monthSpinner, spinnerCount, i);
                    break;
                case DateFormat.YEAR:
                    pushSpinner(yearSpinner, spinnerCount, i);
                    break;
            }
        }
    }

    /**
     * Sets the calendar view shown.
     *
     * @param shown the new calendar view shown
     */
    public void setCalendarViewShown(boolean shown) {
        mCalendarView.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    /**
     * Sets the content descriptions.
     */
    private void setContentDescriptions() {
        DatePicker.setContentDescription(daySpinner, R.id.increment,
                R.string.date_picker_increment_day_button);
        DatePicker.setContentDescription(daySpinner, R.id.decrement,
                R.string.date_picker_decrement_day_button);
        DatePicker.setContentDescription(monthSpinner, R.id.increment,
                R.string.date_picker_increment_month_button);
        DatePicker.setContentDescription(monthSpinner, R.id.decrement,
                R.string.date_picker_decrement_month_button);
        DatePicker.setContentDescription(yearSpinner, R.id.increment,
                R.string.date_picker_increment_year_button);
        DatePicker.setContentDescription(yearSpinner, R.id.decrement,
                R.string.date_picker_decrement_year_button);
    }

    /**
     * Sets the date.
     *
     * @param year the year
     * @param month the month
     * @param dayOfMonth the day of month
     */
    private void setDate(int year, int month, int dayOfMonth) {
        currentDate.set(year, month, dayOfMonth);
        if (currentDate.before(minDate)) {
            currentDate.setTimeInMillis(minDate.getTimeInMillis());
        } else if (currentDate.after(maxDate)) {
            currentDate.setTimeInMillis(maxDate.getTimeInMillis());
        }
    }

    /* (non-Javadoc)
     * @see android.view.View#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (isEnabled() == enabled) {
            return;
        }
        super.setEnabled(enabled);
        daySpinner.setEnabled(enabled);
        monthSpinner.setEnabled(enabled);
        yearSpinner.setEnabled(enabled);
        mCalendarView.setEnabled(enabled);
    }

    /**
     * Sets the ime options.
     *
     * @param spinner the spinner
     * @param spinnerCount the spinner count
     * @param spinnerIndex the spinner index
     */
    private void setImeOptions(NumberPicker spinner, int spinnerCount,
            int spinnerIndex) {
        final int imeOptions;
        if (spinnerIndex < spinnerCount - 1) {
            imeOptions = EditorInfo.IME_ACTION_NEXT;
        } else {
            imeOptions = EditorInfo.IME_ACTION_DONE;
        }
        spinner.getInputField().setImeOptions(imeOptions);
    }

    /**
     * Sets the locale.
     *
     * @param locale the new locale
     */
    @SuppressWarnings("deprecation")
	public void setLocale(Locale locale) {
        if (locale == null || locale.equals(this.locale)) {
            return;
        }
        this.locale = locale;
        tempDate = DatePicker.getCalendarForLocale(tempDate, locale);
        minDate = DatePicker.getCalendarForLocale(minDate, locale);
        maxDate = DatePicker.getCalendarForLocale(maxDate, locale);
        currentDate = DatePicker.getCalendarForLocale(currentDate, locale);
        numberOfMonths = tempDate.getActualMaximum(Calendar.MONTH) + 1;
        shortMonths = new String[numberOfMonths];
        for (int i = 0; i < numberOfMonths; i++) {
            shortMonths[i] = DateUtils.getMonthString(Calendar.JANUARY + i,
                    DateUtils.LENGTH_MEDIUM);
        }
    }

    /**
     * Sets the max date.
     *
     * @param maxDateL the new max date
     */
    public void setMaxDate(long maxDateL) {
        tempDate.setTimeInMillis(maxDateL);
        if (tempDate.get(Calendar.YEAR) == maxDate.get(Calendar.YEAR)
                && tempDate.get(Calendar.DAY_OF_YEAR) == maxDate
                        .get(Calendar.DAY_OF_YEAR)) {
            return;
        }
        maxDate.setTimeInMillis(maxDateL);
        mCalendarView.setMaxDate(maxDateL);
        if (currentDate.after(maxDate)) {
            currentDate.setTimeInMillis(maxDate.getTimeInMillis());
            updateCalendarView();
        }
        updateSpinners();
    }

    /**
     * Sets the min date.
     *
     * @param minDateL the new min date
     */
    public void setMinDate(long minDateL) {
        tempDate.setTimeInMillis(minDateL);
        if (tempDate.get(Calendar.YEAR) == minDate.get(Calendar.YEAR)
                && tempDate.get(Calendar.DAY_OF_YEAR) == minDate
                        .get(Calendar.DAY_OF_YEAR)) {
            return;
        }
        minDate.setTimeInMillis(minDateL);
        mCalendarView.setMinDate(minDateL);
        if (currentDate.before(minDate)) {
            currentDate.setTimeInMillis(minDate.getTimeInMillis());
            updateCalendarView();
        }
        updateSpinners();
    }

    /**
     * Sets the on date changed listener.
     *
     * @param onDateChangedListener the new on date changed listener
     */
    public void setOnDateChangedListener(
            OnDateChangedListener onDateChangedListener) {
        this.onDateChangedListener = onDateChangedListener;
    }

    /**
     * Sets the spinners shown.
     *
     * @param shown the new spinners shown
     */
    public void setSpinnersShown(boolean shown) {
        spinners.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    /**
     * Update calendar view.
     */
    private void updateCalendarView() {
        mCalendarView.setDate(currentDate.getTimeInMillis(), false, false);
    }

    /**
     * Update date.
     *
     * @param year the year
     * @param month the month
     * @param dayOfMonth the day of month
     */
    public void updateDate(int year, int month, int dayOfMonth) {
        if (!isNewDate(year, month, dayOfMonth)) {
            return;
        }
        setDate(year, month, dayOfMonth);
        updateSpinners();
        updateCalendarView();
        notifyDateChanged();
    }

    /**
     * Update input state.
     */
    private void updateInputState() {
        if (inputMethodManager != null) {
            checkInputState(yearSpinner, monthSpinner, daySpinner);
        }
    }

    /**
     * Update spinners.
     */
    private void updateSpinners() {
        monthSpinner.setDisplayedValues(null);
        if (currentDate.equals(minDate)) {
            daySpinner.setMinValue(currentDate.get(Calendar.DAY_OF_MONTH));
            daySpinner.setMaxValue(currentDate
                    .getActualMaximum(Calendar.DAY_OF_MONTH));
            daySpinner.setWrapSelectorWheel(false);
            monthSpinner.setMinValue(currentDate.get(Calendar.MONTH));
            monthSpinner.setMaxValue(currentDate
                    .getActualMaximum(Calendar.MONTH));
            monthSpinner.setWrapSelectorWheel(false);
        } else if (currentDate.equals(maxDate)) {
            daySpinner.setMinValue(currentDate
                    .getActualMinimum(Calendar.DAY_OF_MONTH));
            daySpinner.setMaxValue(currentDate.get(Calendar.DAY_OF_MONTH));
            daySpinner.setWrapSelectorWheel(false);
            monthSpinner.setMinValue(currentDate
                    .getActualMinimum(Calendar.MONTH));
            monthSpinner.setMaxValue(currentDate.get(Calendar.MONTH));
            monthSpinner.setWrapSelectorWheel(false);
        } else {
            daySpinner.setMinValue(1);
            daySpinner.setMaxValue(currentDate
                    .getActualMaximum(Calendar.DAY_OF_MONTH));
            daySpinner.setWrapSelectorWheel(true);
            monthSpinner.setMinValue(0);
            monthSpinner.setMaxValue(11);
            monthSpinner.setWrapSelectorWheel(true);
        }
        String[] displayedValues = Arrays.copyOfRange(shortMonths,
                monthSpinner.getMinValue(), monthSpinner.getMaxValue() + 1);
        monthSpinner.setDisplayedValues(displayedValues);
        yearSpinner.setMinValue(minDate.get(Calendar.YEAR));
        yearSpinner.setMaxValue(maxDate.get(Calendar.YEAR));
        yearSpinner.setWrapSelectorWheel(false);
        yearSpinner.setValue(currentDate.get(Calendar.YEAR));
        monthSpinner.setValue(currentDate.get(Calendar.MONTH));
        daySpinner.setValue(currentDate.get(Calendar.DAY_OF_MONTH));
    }
}
