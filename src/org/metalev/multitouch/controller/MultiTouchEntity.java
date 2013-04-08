/*
 * Code based off the PhotoSortrView from Luke Hutchinson's MTPhotoSortr
 * example (http://code.google.com/p/android-multitouch-controller/)
 *
 * License:
 *   Dual-licensed under the Apache License v2 and the GPL v2.
 */
package org.metalev.multitouch.controller;

import android.graphics.Canvas;
import android.graphics.Paint;


import android.content.res.Resources;
import android.content.Context;
import android.content.res.Configuration;

import android.util.DisplayMetrics;


import java.io.Serializable;

import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;

// TODO: Auto-generated Javadoc
/**
 * The Class MultiTouchEntity.
 */
@SuppressWarnings("serial")
public abstract class MultiTouchEntity implements Serializable {

    /** The m first load. */
    protected boolean mFirstLoad = true;

    /** The m paint. */
    protected transient Paint mPaint = new Paint();

    /** The m width. */
    protected int mWidth;
    
    /** The m height. */
    protected int mHeight;

    // width/height of screen
    /** The m display width. */
    protected int mDisplayWidth;
    
    /** The m display height. */
    protected int mDisplayHeight;

    /** The m center x. */
    protected float mCenterX;
    
    /** The m center y. */
    protected float mCenterY;
    
    /** The m scale x. */
    protected float mScaleX;
    
    /** The m scale y. */
    protected float mScaleY;
    
    /** The m angle. */
    protected float mAngle;

    /** The m min x. */
    protected float mMinX;
    
    /** The m max x. */
    protected float mMaxX;
    
    /** The m min y. */
    protected float mMinY;
    
    /** The m max y. */
    protected float mMaxY;

    // area of the entity that can be scaled/rotated
    // using single touch (grows from bottom right)
    /** The Constant GRAB_AREA_SIZE. */
    protected final static int GRAB_AREA_SIZE = 40;
    
    /** The m is grab area selected. */
    protected boolean mIsGrabAreaSelected = false;
    
    /** The m is latest selected. */
    protected boolean mIsLatestSelected = false;

    /** The m grab area x1. */
    protected float mGrabAreaX1;
    
    /** The m grab area y1. */
    protected float mGrabAreaY1;
    
    /** The m grab area x2. */
    protected float mGrabAreaX2;
    
    /** The m grab area y2. */
    protected float mGrabAreaY2;

    /** The m start mid x. */
    protected float mStartMidX;
    
    /** The m start mid y. */
    protected float mStartMidY;

	/** The Constant UI_MODE_ROTATE. */
	private static final int UI_MODE_ROTATE = 1;
    
    /** The Constant UI_MODE_ANISOTROPIC_SCALE. */
    private static final int UI_MODE_ANISOTROPIC_SCALE = 2;
    
    /** The m ui mode. */
    protected int mUIMode = UI_MODE_ROTATE;

    /**
     * Instantiates a new multi touch entity.
     */
    public MultiTouchEntity() {
    }

    /**
     * Instantiates a new multi touch entity.
     *
     * @param res the res
     */
    public MultiTouchEntity(Resources res) {
        getMetrics(res);
    }

    /**
     * Gets the metrics.
     *
     * @param res the res
     * @return the metrics
     */
    protected void getMetrics(Resources res) {
        DisplayMetrics metrics = res.getDisplayMetrics();
        mDisplayWidth =
            (res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                ? Math.max(metrics.widthPixels, metrics.heightPixels)
                : Math.min(metrics.widthPixels, metrics.heightPixels);
        mDisplayHeight =
            (res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                ? Math.min(metrics.widthPixels, metrics.heightPixels)
                : Math.max(metrics.widthPixels, metrics.heightPixels);
    }

    /**
     * Set the position and scale of an image in screen coordinates.
     *
     * @param newImgPosAndScale the new img pos and scale
     * @return true, if successful
     */
    public boolean setPos(PositionAndScale newImgPosAndScale) {
        float newScaleX;
        float newScaleY;

        if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0) {
            newScaleX = newImgPosAndScale.getScaleX();
        } else {
            newScaleX = newImgPosAndScale.getScale();
        }

        if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0) {
            newScaleY = newImgPosAndScale.getScaleY();
        } else {
            newScaleY = newImgPosAndScale.getScale();
        }

        return setPos(newImgPosAndScale.getXOff(),
                      newImgPosAndScale.getYOff(),
                      newScaleX,
                      newScaleY,
                      newImgPosAndScale.getAngle());
    }

    /**
     * Set the position and scale of an image in screen coordinates.
     *
     * @param centerX the center x
     * @param centerY the center y
     * @param scaleX the scale x
     * @param scaleY the scale y
     * @param angle the angle
     * @return true, if successful
     */
    protected boolean setPos(float centerX, float centerY,
                             float scaleX, float scaleY, float angle) {
        float ws = (mWidth / 2) * scaleX;
        float hs = (mHeight / 2) * scaleY;

        mMinX = centerX - ws;
        mMinY = centerY - hs;
        mMaxX = centerX + ws;
        mMaxY = centerY + hs;

        mGrabAreaX1 = mMaxX - GRAB_AREA_SIZE;
        mGrabAreaY1 = mMaxY - GRAB_AREA_SIZE;
        mGrabAreaX2 = mMaxX;
        mGrabAreaY2 = mMaxY;

        mCenterX = centerX;
        mCenterY = centerY;
        mScaleX = scaleX;
        mScaleY = scaleY;
        mAngle = angle;

        return true;
    }

    /**
     * Return whether or not the given screen coords are inside this image.
     *
     * @param touchX the touch x
     * @param touchY the touch y
     * @return true, if successful
     */
    public boolean containsPoint(float touchX, float touchY) {
        return (touchX >= mMinX && touchX <= mMaxX && touchY >= mMinY && touchY <= mMaxY);
    }

    /**
     * Grab area contains point.
     *
     * @param touchX the touch x
     * @param touchY the touch y
     * @return true, if successful
     */
    public boolean grabAreaContainsPoint(float touchX, float touchY) {
        return (touchX >= mGrabAreaX1 && touchX <= mGrabAreaX2 &&
                touchY >= mGrabAreaY1 && touchY <= mGrabAreaY2);
    }

    /**
     * Reload.
     *
     * @param context the context
     */
    public void reload(Context context) {
        mFirstLoad = false; // Let the load know properties have changed so reload those,
                            // don't go back and start with defaults
        load(context, mCenterX, mCenterY);
    }

    /**
     * Draw.
     *
     * @param canvas the canvas
     */
    public abstract void draw(Canvas canvas);
    
    /**
     * Load.
     *
     * @param context the context
     * @param startMidX the start mid x
     * @param startMidY the start mid y
     */
    public abstract void load(Context context, float startMidX, float startMidY);
    
    /**
     * Unload.
     */
    public abstract void unload();

    /**
     * Gets the width.
     *
     * @return the width
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * Gets the height.
     *
     * @return the height
     */
    public int getHeight() {
        return mHeight;
    }

    /**
     * Gets the center x.
     *
     * @return the center x
     */
    public float getCenterX() {
        return mCenterX;
    }

    /**
     * Gets the center y.
     *
     * @return the center y
     */
    public float getCenterY() {
        return mCenterY;
    }

    /**
     * Gets the scale x.
     *
     * @return the scale x
     */
    public float getScaleX() {
        return mScaleX;
    }

    /**
     * Gets the scale y.
     *
     * @return the scale y
     */
    public float getScaleY() {
        return mScaleY;
    }

    /**
     * Gets the angle.
     *
     * @return the angle
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Gets the min x.
     *
     * @return the min x
     */
    public float getMinX() {
        return mMinX;
    }

    /**
     * Gets the max x.
     *
     * @return the max x
     */
    public float getMaxX() {
        return mMaxX;
    }

    /**
     * Gets the min y.
     *
     * @return the min y
     */
    public float getMinY() {
        return mMinY;
    }

    /**
     * Gets the max y.
     *
     * @return the max y
     */
    public float getMaxY() {
        return mMaxY;
    }

    /**
     * Sets the checks if is grab area selected.
     *
     * @param selected the new checks if is grab area selected
     */
    public void setIsGrabAreaSelected(boolean selected) {
        mIsGrabAreaSelected = selected;
    }

    /**
     * Checks if is grab area selected.
     *
     * @return true, if is grab area selected
     */
    public boolean isGrabAreaSelected() {
        return mIsGrabAreaSelected;
    }
}
