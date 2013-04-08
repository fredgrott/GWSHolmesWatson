
package org.holoeverywhere.internal;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;

import org.holoeverywhere.LayoutInflater;
import com.actionbarsherlock.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.internal.view.menu.ContextMenuDecorView;

public class WindowDecorView extends ContextMenuDecorView {
    public static WindowDecorView inflateDecorView(LayoutInflater layoutInflater, int layout) {
        WindowDecorView view = new WindowDecorView(layoutInflater.getContext());
        layoutInflater.inflate(layout, view, true);
        return view;
    }

    private TypedValue mFixedHeightMajor;
    private TypedValue mFixedHeightMinor;
    private TypedValue mFixedWidthMajor;
    private TypedValue mFixedWidthMinor;
    private TypedValue mMinHeightMajor;
    private TypedValue mMinHeightMinor;
    private TypedValue mMinWidthMajor;
    private TypedValue mMinWidthMinor;

    private WindowDecorView(Context context) {
        this(context, null, null);
    }

    public WindowDecorView(Context context, View view, ViewGroup.LayoutParams params) {
        super(context, view, params);
        TypedArray a = context.obtainStyledAttributes(R.styleable.WindowSizes);
        if (a.hasValue(R.styleable.WindowSizes_windowMinWidthMajor)) {
            a.getValue(R.styleable.WindowSizes_windowMinWidthMajor,
                    mMinWidthMajor = new TypedValue());
        }
        if (a.hasValue(R.styleable.WindowSizes_windowMinWidthMinor)) {
            a.getValue(R.styleable.WindowSizes_windowMinWidthMinor,
                    mMinWidthMinor = new TypedValue());
        }
        if (a.hasValue(R.styleable.WindowSizes_windowMinHeightMajor)) {
            a.getValue(R.styleable.WindowSizes_windowMinHeightMajor,
                    mMinHeightMajor = new TypedValue());
        }
        if (a.hasValue(R.styleable.WindowSizes_windowMinHeightMinor)) {
            a.getValue(R.styleable.WindowSizes_windowMinHeightMinor,
                    mMinHeightMinor = new TypedValue());
        }
        if (a.hasValue(R.styleable.WindowSizes_windowFixedWidthMajor)) {
            a.getValue(R.styleable.WindowSizes_windowFixedWidthMajor,
                    mFixedWidthMajor = new TypedValue());
        }
        if (a.hasValue(R.styleable.WindowSizes_windowFixedWidthMinor)) {
            a.getValue(R.styleable.WindowSizes_windowFixedWidthMinor,
                    mFixedWidthMinor = new TypedValue());
        }
        if (a.hasValue(R.styleable.WindowSizes_windowFixedHeightMajor)) {
            a.getValue(R.styleable.WindowSizes_windowFixedHeightMajor,
                    mFixedHeightMajor = new TypedValue());
        }
        if (a.hasValue(R.styleable.WindowSizes_windowFixedHeightMinor)) {
            a.getValue(R.styleable.WindowSizes_windowFixedHeightMinor,
                    mFixedHeightMinor = new TypedValue());
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        final boolean isPortrait = metrics.widthPixels < metrics.heightPixels;
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        boolean fixedWidth = false, fixedHeight = false;
        if (widthMode == AT_MOST) {
            final TypedValue tvw = isPortrait ? mFixedWidthMinor : mFixedWidthMajor;
            if (tvw != null && tvw.type != TypedValue.TYPE_NULL) {
                final int w;
                if (tvw.type == TypedValue.TYPE_DIMENSION) {
                    w = (int) tvw.getDimension(metrics);
                } else if (tvw.type == TypedValue.TYPE_FRACTION) {
                    w = (int) tvw.getFraction(metrics.widthPixels, metrics.widthPixels);
                } else {
                    w = 0;
                }
                if (w > 0) {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                            Math.min(w, MeasureSpec.getSize(widthMeasureSpec)), EXACTLY);
                    fixedWidth = true;
                }
            }
        }
        if (heightMode == AT_MOST) {
            final TypedValue tvh = isPortrait ? mFixedHeightMajor : mFixedHeightMinor;
            if (tvh != null && tvh.type != TypedValue.TYPE_NULL) {
                final int h;
                if (tvh.type == TypedValue.TYPE_DIMENSION) {
                    h = (int) tvh.getDimension(metrics);
                } else if (tvh.type == TypedValue.TYPE_FRACTION) {
                    h = (int) tvh.getFraction(metrics.heightPixels, metrics.heightPixels);
                } else {
                    h = 0;
                }
                if (h > 0) {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            Math.min(h, MeasureSpec.getSize(heightMeasureSpec)), EXACTLY);
                    fixedHeight = true;
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        boolean measure = false;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, EXACTLY);
        if (!fixedWidth && widthMode == AT_MOST) {
            final TypedValue tv = isPortrait ? mMinWidthMinor : mMinWidthMajor;
            if (tv != null && tv.type != TypedValue.TYPE_NULL) {
                final int min;
                if (tv.type == TypedValue.TYPE_DIMENSION) {
                    min = (int) tv.getDimension(metrics);
                } else if (tv.type == TypedValue.TYPE_FRACTION) {
                    min = (int) tv.getFraction(metrics.widthPixels, metrics.widthPixels);
                } else {
                    min = 0;
                }

                if (width < min) {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(min, EXACTLY);
                    measure = true;
                }
            }
        }
        if (!fixedHeight && heightMode == AT_MOST) {
            final TypedValue tv = isPortrait ? mMinHeightMinor : mMinHeightMajor;
            if (tv != null && tv.type != TypedValue.TYPE_NULL) {
                final int min;
                if (tv.type == TypedValue.TYPE_DIMENSION) {
                    min = (int) tv.getDimension(metrics);
                } else if (tv.type == TypedValue.TYPE_FRACTION) {
                    min = (int) tv.getFraction(metrics.heightPixels, metrics.heightPixels);
                } else {
                    min = 0;
                }
                if (height < min) {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(min, EXACTLY);
                    measure = true;
                }
            }
        }
        if (measure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
