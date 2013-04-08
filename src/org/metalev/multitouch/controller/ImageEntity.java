/*
 * Code based off the PhotoSortrView from Luke Hutchinson's MTPhotoSortr
 * example (http://code.google.com/p/android-multitouch-controller/)
 *
 * License:
 *   Dual-licensed under the Apache License v2 and the GPL v2.
 */
package org.metalev.multitouch.controller;

import android.graphics.drawable.Drawable;

import android.graphics.Canvas;


import android.content.res.Resources;
import android.content.Context;





// TODO: Auto-generated Javadoc
/**
 * The Class ImageEntity.
 */
@SuppressWarnings("serial")
public class ImageEntity extends MultiTouchEntity {

    /** The Constant INITIAL_SCALE_FACTOR. */
    private static final double INITIAL_SCALE_FACTOR = 0.50;

    /** The m drawable. */
    private transient Drawable mDrawable;

    /** The m resource id. */
    private int mResourceId;

    /**
     * Instantiates a new image entity.
     *
     * @param resourceId the resource id
     * @param res the res
     */
    public ImageEntity(int resourceId, Resources res)  {
        super(res);

        mResourceId = resourceId;
    }

    /**
     * Instantiates a new image entity.
     *
     * @param e the e
     * @param res the res
     */
    public ImageEntity(ImageEntity e, Resources res) {
        super(res);

        mDrawable = e.mDrawable;
        mResourceId = e.mResourceId;
        mScaleX = e.mScaleX;
        mScaleY = e.mScaleY;
        mCenterX = e.mCenterX;
        mCenterY = e.mCenterY;
        mAngle = e.mAngle;
    }

    /* (non-Javadoc)
     * @see org.metalev.multitouch.controller.MultiTouchEntity#draw(android.graphics.Canvas)
     */
    public void draw(Canvas canvas) {
        canvas.save();

        float dx = (mMaxX + mMinX) / 2;
        float dy = (mMaxY + mMinY) / 2;

        mDrawable.setBounds((int) mMinX, (int) mMinY, (int) mMaxX, (int) mMaxY);

        canvas.translate(dx, dy);
        canvas.rotate(mAngle * 180.0f / (float) Math.PI);
        canvas.translate(-dx, -dy);

        mDrawable.draw(canvas);

        canvas.restore();
    }

    /**
     * Called by activity's onPause() method to free memory used for loading the images.
     */
    @Override
    public void unload() {
        this.mDrawable = null;
    }

    /**
     * Called by activity's onResume() method to load the images.
     *
     * @param context the context
     * @param startMidX the start mid x
     * @param startMidY the start mid y
     */
    @SuppressWarnings("unused")
	@Override
    public void load(Context context, float startMidX, float startMidY) {
        Resources res = context.getResources();
        getMetrics(res);

        mStartMidX = startMidX;
        mStartMidY = startMidY;

        mDrawable = res.getDrawable(mResourceId);

        mWidth = mDrawable.getIntrinsicWidth();
        mHeight = mDrawable.getIntrinsicHeight();

        float centerX;
        float centerY;
        float scaleX;
        float scaleY;
        float angle;
        if (mFirstLoad) {
            centerX = startMidX;
            centerY = startMidY;

            float scaleFactor = (float) (Math.max(mDisplayWidth, mDisplayHeight) /
                    (float) Math.max(mWidth, mHeight) * INITIAL_SCALE_FACTOR);
            scaleX = scaleY = scaleFactor;
            angle = 0.0f;

            mFirstLoad = false;
        } else {
            centerX = mCenterX;
            centerY = mCenterY;
            scaleX = mScaleX;
            scaleY = mScaleY;
            angle = mAngle;
        }
        setPos(centerX, centerY, scaleX, scaleY, mAngle);
    }
}
