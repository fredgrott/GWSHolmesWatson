/**
 * Copyright 2012
 *
 * Nicolas Desjardins
 * https://github.com/mrKlar
 *
 * Facilite solutions
 * http://www.facilitesolutions.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ca.laplanete.mobile.pageddragdropgrid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

// TODO: Auto-generated Javadoc
/**
 * The Class DragDropGrid.
 */
@SuppressLint("UseSparseArrays")
public class DragDropGrid extends ViewGroup implements OnTouchListener, OnLongClickListener {

	/** The animation duration. */
	private static int ANIMATION_DURATION = 250;
	
	/** The egde detection margin. */
	private static int EGDE_DETECTION_MARGIN = 35;

	/** The adapter. */
	private PagedDragDropGridAdapter adapter;
	
	/** The on click listener. */
	private OnClickListener onClickListener = null;
	
	/** The container. */
	private PagedContainer container;

	/** The new positions. */
	private SparseArray<Integer> newPositions = new SparseArray<Integer>();

	/** The grid page width. */
	private int gridPageWidth = 0;
	
	/** The dragged. */
	private int dragged = -1;
	
	/** The column width size. */
	private int columnWidthSize;
	
	/** The row height size. */
	private int rowHeightSize;
	
	/** The biggest child width. */
	private int biggestChildWidth;
	
	/** The biggest child height. */
	private int biggestChildHeight;
	
	/** The computed column count. */
	private int computedColumnCount;
	
	/** The computed row count. */
	private int computedRowCount;
	
	/** The initial x. */
	private int initialX;
	
	/** The initial y. */
	private int initialY;
	
	/** The moving view. */
	private boolean movingView;
	
	/** The last target. */
	private int lastTarget = -1;
	
	/** The was on edge just now. */
	private boolean wasOnEdgeJustNow = false;
	
	/** The edge scroll timer. */
	private Timer edgeScrollTimer;

	/** The edge timer handler. */
	final private Handler edgeTimerHandler = new Handler();
	
	/** The last touch x. */
	private int lastTouchX;
	
	/** The last touch y. */
	private int lastTouchY;
	
	/** The grid page height. */
	private int gridPageHeight;
	
	/** The delete zone. */
	private DeleteDropZoneView deleteZone;

	/**
	 * Instantiates a new drag drop grid.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 * @param defStyle the def style
	 */
	public DragDropGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * Instantiates a new drag drop grid.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public DragDropGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * Instantiates a new drag drop grid.
	 *
	 * @param context the context
	 */
	public DragDropGrid(Context context) {
		super(context);
		init();
	}

	/**
	 * Instantiates a new drag drop grid.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 * @param defStyle the def style
	 * @param adapter the adapter
	 * @param container the container
	 */
	public DragDropGrid(Context context, AttributeSet attrs, int defStyle, PagedDragDropGridAdapter adapter, PagedContainer container) {
		super(context, attrs, defStyle);
		this.adapter = adapter;
		this.container = container;
		init();
	}

	/**
	 * Instantiates a new drag drop grid.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 * @param adapter the adapter
	 * @param container the container
	 */
	public DragDropGrid(Context context, AttributeSet attrs, PagedDragDropGridAdapter adapter, PagedContainer container) {
		super(context, attrs);
		this.adapter = adapter;
		this.container = container;
		init();
	}

	/**
	 * Instantiates a new drag drop grid.
	 *
	 * @param context the context
	 * @param adapter the adapter
	 * @param container the container
	 */
	public DragDropGrid(Context context, PagedDragDropGridAdapter adapter, PagedContainer container) {
		super(context);
		this.adapter = adapter;
		this.container = container;
		init();
	}

	/**
	 * Inits the.
	 */
	private void init() {
		setOnTouchListener(this);
		setOnLongClickListener(this);
		createDeleteZone();
	}

	/**
	 * Sets the adapter.
	 *
	 * @param adapter the new adapter
	 */
	public void setAdapter(PagedDragDropGridAdapter adapter) {
		this.adapter = adapter;
		addChildViews();
	}

	/* (non-Javadoc)
	 * @see android.view.View#setOnClickListener(android.view.View.OnClickListener)
	 */
	public void setOnClickListener(OnClickListener l) {
	    onClickListener = l;
	}

	/**
	 * Adds the child views.
	 */
	private void addChildViews() {
		for (int page = 0; page < adapter.pageCount(); page++) {
			for (int item = 0; item < adapter.itemCountInPage(page); item++) {
				addView(adapter.view(page, item));
			}
		}
		deleteZone.bringToFront();
	}

	/**
	 * Animate move all items.
	 */
	private void animateMoveAllItems() {
		Animation rotateAnimation = createFastRotateAnimation();

		for (int i=0; i < getItemViewCount(); i++) {
			View child = getChildAt(i);
			child.startAnimation(rotateAnimation);
		 }
	}

	/**
	 * Cancel animations.
	 */
	private void cancelAnimations() {
		 for (int i=0; i < getItemViewCount(); i++) {
			 if (i != dragged) {
				 View child = getChildAt(i);
				 child.clearAnimation();
			 }
		 }
	}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	public boolean onInterceptTouchEvent(MotionEvent event) {
	    return onTouch(null, event);
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			touchDown(event);
			break;
		case MotionEvent.ACTION_MOVE:
			touchMove(event);
			break;
		case MotionEvent.ACTION_UP:
			touchUp(event);
			break;
		}
		if (aViewIsDragged())
			return true;
		return false;
	}

	/**
	 * Touch up.
	 *
	 * @param event the event
	 */
	private void touchUp(MotionEvent event) {
	    if(!aViewIsDragged()) {
	        if(onClickListener != null) {
                View clickedView = getChildAt(getTargetAtCoor((int) event.getX(), (int) event.getY()));
                if(clickedView != null)
                    onClickListener.onClick(clickedView);
            }
	    } else {
    		manageChildrenReordering();
    		hideDeleteView();
    		cancelEdgeTimer();

    		movingView = false;
    		dragged = -1;
    		lastTarget = -1;
    		container.enableScroll();
    		cancelAnimations();
	    }
	}

	/**
	 * Manage children reordering.
	 */
	private void manageChildrenReordering() {
		boolean draggedDeleted = touchUpInDeleteZoneDrop(lastTouchX, lastTouchY);

		if (draggedDeleted) {
			animateDeleteDragged();
			reorderChildrenWhenDraggedIsDeleted();
		} else {
			reorderChildren();
		}
	}

	/**
	 * Animate delete dragged.
	 */
	private void animateDeleteDragged() {
		ScaleAnimation scale = new ScaleAnimation(1.4f, 0f, 1.4f, 0f, biggestChildWidth / 2 , biggestChildHeight / 2);
		scale.setDuration(200);
		scale.setFillAfter(true);
		scale.setFillEnabled(true);

		getChildAt(dragged).clearAnimation();
		getChildAt(dragged).startAnimation(scale);
	}

	/**
	 * Reorder children when dragged is deleted.
	 */
	private void reorderChildrenWhenDraggedIsDeleted() {
		Integer newDraggedPosition = newPositions.get(dragged,dragged);

		List<View> children = cleanUnorderedChildren();
		addReorderedChildrenToParent(children);

		tellAdapterDraggedIsDeleted(newDraggedPosition);
		removeViewAt(newDraggedPosition);

		requestLayout();
	}

	/**
	 * Tell adapter dragged is deleted.
	 *
	 * @param newDraggedPosition the new dragged position
	 */
	private void tellAdapterDraggedIsDeleted(Integer newDraggedPosition) {
		ItemPosition position = itemInformationAtPosition(newDraggedPosition);
		adapter.deleteItem(position.pageIndex,position.itemIndex);
	}

	/**
	 * Touch down.
	 *
	 * @param event the event
	 */
	private void touchDown(MotionEvent event) {
		initialX = (int)event.getRawX();
		initialY = (int)event.getRawY();

		lastTouchX = (int)event.getRawX() + (currentPage() * gridPageWidth);
		lastTouchY = (int)event.getRawY();
	}

	/**
	 * Touch move.
	 *
	 * @param event the event
	 */
	private void touchMove(MotionEvent event) {
		if (movingView && aViewIsDragged()) {
			lastTouchX = (int) event.getX();
			lastTouchY = (int) event.getY();

			moveDraggedView(lastTouchX, lastTouchY);
			manageSwapPosition(lastTouchX, lastTouchY);
			manageEdgeCoordinates(lastTouchX);
			manageDeleteZoneHover(lastTouchX, lastTouchY);
		}
	}

	/**
	 * Manage delete zone hover.
	 *
	 * @param x the x
	 * @param y the y
	 */
	private void manageDeleteZoneHover(int x, int y) {
		Rect zone = new Rect();
		deleteZone.getHitRect(zone);

		if (zone.intersect(x, y, x+1, y+1)) {
			deleteZone.highlight();
		} else {
			deleteZone.smother();
		}
	}

	/**
	 * Touch up in delete zone drop.
	 *
	 * @param x the x
	 * @param y the y
	 * @return true, if successful
	 */
	private boolean touchUpInDeleteZoneDrop(int x, int y) {
		Rect zone = new Rect();
		deleteZone.getHitRect(zone);

		if (zone.intersect(x, y, x+1, y+1)) {
			deleteZone.smother();
			return true;
		}
		return false;
	}

	/**
	 * Move dragged view.
	 *
	 * @param x the x
	 * @param y the y
	 */
	private void moveDraggedView(int x, int y) {
		View childAt = getChildAt(dragged);
		int width = childAt.getMeasuredWidth();
		int height = childAt.getMeasuredHeight();

		int l = x - (1 * width / 2);
		int t = y - (1 * height / 2);

		childAt.layout(l, t, l + width, t + height);
	}

	/**
	 * Manage swap position.
	 *
	 * @param x the x
	 * @param y the y
	 */
	private void manageSwapPosition(int x, int y) {
		int target = getTargetAtCoor(x, y);
		if (childHasMoved(target) && target != lastTarget) {
			animateGap(target);
			lastTarget = target;
		}
	}

	/**
	 * Manage edge coordinates.
	 *
	 * @param x the x
	 */
	private void manageEdgeCoordinates(int x) {
		final boolean onRightEdge = onRightEdgeOfScreen(x);
		final boolean onLeftEdge = onLeftEdgeOfScreen(x);

		if (canScrollToEitherSide(onRightEdge,onLeftEdge)) {
			if (!wasOnEdgeJustNow) {
				startEdgeDelayTimer(onRightEdge, onLeftEdge);
				wasOnEdgeJustNow = true;
			}
		} else {
			if (wasOnEdgeJustNow) {
				stopAnimateOnTheEdge();
			}
			wasOnEdgeJustNow = false;
			cancelEdgeTimer();
		}
	}

	/**
	 * Stop animate on the edge.
	 */
	private void stopAnimateOnTheEdge() {
			View draggedView = getChildAt(dragged);
			draggedView.clearAnimation();
			animateDragged();
	}

	/**
	 * Cancel edge timer.
	 */
	private void cancelEdgeTimer() {

		if (edgeScrollTimer != null) {
			edgeScrollTimer.cancel();
			edgeScrollTimer = null;
		}
	}

	/**
	 * Start edge delay timer.
	 *
	 * @param onRightEdge the on right edge
	 * @param onLeftEdge the on left edge
	 */
	private void startEdgeDelayTimer(final boolean onRightEdge, final boolean onLeftEdge) {
		if (canScrollToEitherSide(onRightEdge, onLeftEdge)) {
			animateOnTheEdge();
			if (edgeScrollTimer == null) {
				edgeScrollTimer = new Timer();
				scheduleScroll(onRightEdge, onLeftEdge);
			}
		}
	}

	/**
	 * Schedule scroll.
	 *
	 * @param onRightEdge the on right edge
	 * @param onLeftEdge the on left edge
	 */
	private void scheduleScroll(final boolean onRightEdge, final boolean onLeftEdge) {
		edgeScrollTimer.schedule(new TimerTask() {
		    @Override
		    public void run() {
		    	if (wasOnEdgeJustNow) {
		    		wasOnEdgeJustNow = false;
		    		edgeTimerHandler.post(new Runnable() {
						@Override
						public void run() {
							hideDeleteView();
							scroll(onRightEdge, onLeftEdge);
							cancelAnimations();
							animateMoveAllItems();
							animateDragged();
							popDeleteView();
						}
					});
		    	}
		    }
		}, 1000);
	}

	/**
	 * Can scroll to either side.
	 *
	 * @param onRightEdge the on right edge
	 * @param onLeftEdge the on left edge
	 * @return true, if successful
	 */
	private boolean canScrollToEitherSide(final boolean onRightEdge, final boolean onLeftEdge) {
		return (onLeftEdge && container.canScrollToPreviousPage()) || (onRightEdge && container.canScrollToNextPage());
	}

	/**
	 * Scroll.
	 *
	 * @param onRightEdge the on right edge
	 * @param onLeftEdge the on left edge
	 */
	private void scroll(boolean onRightEdge, boolean onLeftEdge) {
		cancelEdgeTimer();

		if (onLeftEdge && container.canScrollToPreviousPage()) {
			scrollToPreviousPage();
		} else if (onRightEdge && container.canScrollToNextPage()) {
			scrollToNextPage();
		}
		wasOnEdgeJustNow = false;
	}

	/**
	 * Scroll to next page.
	 */
	private void scrollToNextPage() {
		tellAdapterToMoveItemToNextPage(dragged);
		moveDraggedToNextPage();

		container.scrollRight();
		int currentPage = currentPage();
		int lastItem = adapter.itemCountInPage(currentPage)-1;
		dragged = positionOfItem(currentPage, lastItem);

		requestLayout();
		stopAnimateOnTheEdge();
	}

	/**
	 * Scroll to previous page.
	 */
	private void scrollToPreviousPage() {
		tellAdapterToMoveItemToPreviousPage(dragged);
		moveDraggedToPreviousPage();

		container.scrollLeft();
		int currentPage = currentPage();
		int lastItem = adapter.itemCountInPage(currentPage)-1;
		dragged = positionOfItem(currentPage, lastItem);

		requestLayout();
		stopAnimateOnTheEdge();
	}

	/**
	 * Move dragged to previous page.
	 */
	private void moveDraggedToPreviousPage() {
		List<View> children = cleanUnorderedChildren();

		List<View> reorderedViews = reeorderView(children);
		int draggedEndPosition = newPositions.get(dragged, dragged);

		View draggedView = reorderedViews.get(draggedEndPosition);
		reorderedViews.remove(draggedEndPosition);

		int currentPage = currentPage();
		int indexFirstElementInCurrentPage = 0;
		for (int i=0;i<currentPage;i++) {
			indexFirstElementInCurrentPage += adapter.itemCountInPage(i);
		}

		int indexOfDraggedOnNewPage = indexFirstElementInCurrentPage-1;
		reorderAndAddViews(reorderedViews, draggedView, indexOfDraggedOnNewPage);
	}

	/**
	 * Removes the item children.
	 *
	 * @param children the children
	 */
	private void removeItemChildren(List<View> children) {
		for (View child : children) {
			removeView(child);
		}
	}

	/**
	 * Move dragged to next page.
	 */
	private void moveDraggedToNextPage() {
		List<View> children = cleanUnorderedChildren();

		List<View> reorderedViews = reeorderView(children);
		int draggedEndPosition = newPositions.get(dragged, dragged);

		View draggedView = reorderedViews.get(draggedEndPosition);
		reorderedViews.remove(draggedEndPosition);

		int currentPage = currentPage();
		int indexLastElementInNextPage = 0;
		for (int i=0;i<=currentPage+1;i++) {
			indexLastElementInNextPage += adapter.itemCountInPage(i);
		}

		int indexOfDraggedOnNewPage = indexLastElementInNextPage-1;
		reorderAndAddViews(reorderedViews, draggedView, indexOfDraggedOnNewPage);
	}

	/**
	 * Reorder and add views.
	 *
	 * @param reorderedViews the reordered views
	 * @param draggedView the dragged view
	 * @param indexOfDraggedOnNewPage the index of dragged on new page
	 */
	private void reorderAndAddViews(List<View> reorderedViews, View draggedView, int indexOfDraggedOnNewPage) {

		reorderedViews.add(indexOfDraggedOnNewPage,draggedView);
		newPositions.clear();

		for (View view : reorderedViews) {
			if (view != null) {
				addView(view);
			}
		}

		deleteZone.bringToFront();
	}

	/**
	 * On left edge of screen.
	 *
	 * @param x the x
	 * @return true, if successful
	 */
	private boolean onLeftEdgeOfScreen(int x) {
		int currentPage = container.currentPage();

		int leftEdgeXCoor = currentPage*gridPageWidth;
		int distanceFromEdge = x - leftEdgeXCoor;
		return (x > 0 && distanceFromEdge <= EGDE_DETECTION_MARGIN);
	}

	/**
	 * On right edge of screen.
	 *
	 * @param x the x
	 * @return true, if successful
	 */
	private boolean onRightEdgeOfScreen(int x) {
		int currentPage = container.currentPage();

		int rightEdgeXCoor = (currentPage*gridPageWidth) + gridPageWidth;
		int distanceFromEdge = rightEdgeXCoor - x;
		return (x > (rightEdgeXCoor - EGDE_DETECTION_MARGIN)) && (distanceFromEdge < EGDE_DETECTION_MARGIN);
	}

	/**
	 * Animate on the edge.
	 */
	private void animateOnTheEdge() {
		View v = getChildAt(dragged);

		ScaleAnimation scale = new ScaleAnimation(.667f, 1.5f, .667f, 1.5f, v.getMeasuredWidth() * 3 / 4, v.getMeasuredHeight() * 3 / 4);
		scale.setDuration(200);
		scale.setRepeatMode(Animation.REVERSE);
		scale.setRepeatCount(Animation.INFINITE);

		v.clearAnimation();
		v.startAnimation(scale);
	}

	/**
	 * Animate gap.
	 *
	 * @param targetLocationInGrid the target location in grid
	 */
	private void animateGap(int targetLocationInGrid) {
		int viewAtPosition = currentViewAtPosition(targetLocationInGrid);

		if (viewAtPosition == dragged) {
			return;
		}

		View targetView = getChildAt(viewAtPosition);

		Point oldXY = getCoorForIndex(viewAtPosition);
		Point newXY = getCoorForIndex(newPositions.get(dragged, dragged));

		Point oldOffset = computeTranslationStartDeltaRelativeToRealViewPosition(targetLocationInGrid, viewAtPosition, oldXY);
		Point newOffset = computeTranslationEndDeltaRelativeToRealViewPosition(oldXY, newXY);

		animateMoveToNewPosition(targetView, oldOffset, newOffset);
		saveNewPositions(targetLocationInGrid, viewAtPosition);
	}

	/**
	 * Compute translation end delta relative to real view position.
	 *
	 * @param oldXY the old xy
	 * @param newXY the new xy
	 * @return the point
	 */
	private Point computeTranslationEndDeltaRelativeToRealViewPosition(Point oldXY, Point newXY) {
		return new Point(newXY.x - oldXY.x, newXY.y - oldXY.y);
	}

	/**
	 * Compute translation start delta relative to real view position.
	 *
	 * @param targetLocation the target location
	 * @param viewAtPosition the view at position
	 * @param oldXY the old xy
	 * @return the point
	 */
	private Point computeTranslationStartDeltaRelativeToRealViewPosition(int targetLocation, int viewAtPosition, Point oldXY) {
		Point oldOffset;
		if (viewWasAlreadyMoved(targetLocation, viewAtPosition)) {
			Point targetLocationPoint = getCoorForIndex(targetLocation);
			oldOffset = computeTranslationEndDeltaRelativeToRealViewPosition(oldXY, targetLocationPoint);
		} else {
			oldOffset = new Point(0,0);
		}
		return oldOffset;
	}

	/**
	 * Save new positions.
	 *
	 * @param targetLocation the target location
	 * @param viewAtPosition the view at position
	 */
	private void saveNewPositions(int targetLocation, int viewAtPosition) {
		newPositions.put(viewAtPosition, newPositions.get(dragged, dragged));
		newPositions.put(dragged, targetLocation);
		tellAdapterToSwapDraggedWithTarget(newPositions.get(dragged, dragged), newPositions.get(viewAtPosition, viewAtPosition));
	}

	/**
	 * View was already moved.
	 *
	 * @param targetLocation the target location
	 * @param viewAtPosition the view at position
	 * @return true, if successful
	 */
	private boolean viewWasAlreadyMoved(int targetLocation, int viewAtPosition) {
		return viewAtPosition != targetLocation;
	}

	/**
	 * Animate move to new position.
	 *
	 * @param targetView the target view
	 * @param oldOffset the old offset
	 * @param newOffset the new offset
	 */
	private void animateMoveToNewPosition(View targetView, Point oldOffset, Point newOffset) {
		AnimationSet set = new AnimationSet(true);

		Animation rotate = createFastRotateAnimation();
		Animation translate = createTranslateAnimation(oldOffset, newOffset);

		set.addAnimation(rotate);
		set.addAnimation(translate);

		targetView.clearAnimation();
		targetView.startAnimation(set);
	}

	/**
	 * Creates the translate animation.
	 *
	 * @param oldOffset the old offset
	 * @param newOffset the new offset
	 * @return the translate animation
	 */
	private TranslateAnimation createTranslateAnimation(Point oldOffset, Point newOffset) {
		TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, oldOffset.x,
															  Animation.ABSOLUTE, newOffset.x,
															  Animation.ABSOLUTE, oldOffset.y,
															  Animation.ABSOLUTE, newOffset.y);
		translate.setDuration(ANIMATION_DURATION);
		translate.setFillEnabled(true);
		translate.setFillAfter(true);
		translate.setInterpolator(new AccelerateDecelerateInterpolator());
		return translate;
	}

	/**
	 * Creates the fast rotate animation.
	 *
	 * @return the animation
	 */
	private Animation createFastRotateAnimation() {
		Animation rotate = new RotateAnimation(-2.0f,
										  2.0f,
										  Animation.RELATIVE_TO_SELF,
										  0.5f,
										  Animation.RELATIVE_TO_SELF,
										  0.5f);

	 	rotate.setRepeatMode(Animation.REVERSE);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setDuration(60);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());

		return rotate;
	}

	/**
	 * Current view at position.
	 *
	 * @param targetLocation the target location
	 * @return the int
	 */
	private int currentViewAtPosition(int targetLocation) {
		int viewAtPosition = targetLocation;
		for (int i = 0; i < newPositions.size(); i++) {
			int value = newPositions.valueAt(i);
			if (value == targetLocation) {
				viewAtPosition = newPositions.keyAt(i);
				break;
			}
		}
		return viewAtPosition;
	}

	/**
	 * Gets the coor for index.
	 *
	 * @param index the index
	 * @return the coor for index
	 */
	private Point getCoorForIndex(int index) {
		ItemPosition page = itemInformationAtPosition(index);

		int row = page.itemIndex / computedColumnCount;
		int col = page.itemIndex - (row * computedColumnCount);

		int x = (currentPage() * gridPageWidth) + (columnWidthSize * col);
		int y = rowHeightSize * row;

		return new Point(x, y);
	}

	/**
	 * Gets the target at coor.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the target at coor
	 */
	private int getTargetAtCoor(int x, int y) {
		int page = currentPage();

		int col = getColumnOfCoordinate(x, page);
		int row = getRowOfCoordinate(y);
		int positionInPage = col + (row * computedColumnCount);

		return positionOfItem(page, positionInPage);
	}

	/**
	 * Gets the column of coordinate.
	 *
	 * @param x the x
	 * @param page the page
	 * @return the column of coordinate
	 */
	private int getColumnOfCoordinate(int x, int page) {
		int col = 0;
		int pageLeftBorder = (page) * gridPageWidth;
		for (int i = 1; i <= computedColumnCount; i++) {
			int colRightBorder = (i * columnWidthSize) + pageLeftBorder;
			if (x < colRightBorder) {
				break;
			}
			col++;
		}
		return col;
	}

	/**
	 * Gets the row of coordinate.
	 *
	 * @param y the y
	 * @return the row of coordinate
	 */
	private int getRowOfCoordinate(int y) {
		int row = 0;
		for (int i = 1; i <= computedRowCount; i++) {
			if (y < i * rowHeightSize) {
				break;
			}
			row++;
		}
		return row;
	}

	/**
	 * Current page.
	 *
	 * @return the int
	 */
	private int currentPage() {
		return container.currentPage();
	}

	/**
	 * Reorder children.
	 */
	private void reorderChildren() {
		List<View> children = cleanUnorderedChildren();
		addReorderedChildrenToParent(children);
		requestLayout();
	}

	/**
	 * Clean unordered children.
	 *
	 * @return the list
	 */
	private List<View> cleanUnorderedChildren() {
		List<View> children = saveChildren();
		removeItemChildren(children);
		return children;
	}

	/**
	 * Adds the reordered children to parent.
	 *
	 * @param children the children
	 */
	private void addReorderedChildrenToParent(List<View> children) {
		List<View> reorderedViews = reeorderView(children);
		newPositions.clear();

		for (View view : reorderedViews) {
			if (view != null)
				addView(view);
		}

		deleteZone.bringToFront();
	}

	/**
	 * Save children.
	 *
	 * @return the list
	 */
	private List<View> saveChildren() {
		List<View> children = new ArrayList<View>();
		for (int i = 0; i < getItemViewCount(); i++) {
			View child = getChildAt(i);
			child.clearAnimation();
			children.add(child);
		}
		return children;
	}

	/**
	 * Reeorder view.
	 *
	 * @param children the children
	 * @return the list
	 */
	private List<View> reeorderView(List<View> children) {
		View[] views = new View[children.size()];

		for (int i = 0; i < children.size(); i++) {
			int position = newPositions.get(i, -1);
			if (childHasMoved(position)) {
				views[position] = children.get(i);
			} else {
				views[i] = children.get(i);
			}
		}
		return new ArrayList<View>(Arrays.asList(views));
	}

	/**
	 * Child has moved.
	 *
	 * @param position the position
	 * @return true, if successful
	 */
	private boolean childHasMoved(int position) {
		return position != -1;
	}

	/* (non-Javadoc)
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

		Display display = wm.getDefaultDisplay();

		widthSize = acknowledgeWidthSize(widthMode, widthSize, display);
		heightSize = acknowledgeHeightSize(heightMode, heightSize, display);

		adaptChildrenMeasuresToViewSize(widthSize, heightSize);
		searchBiggestChildMeasures();
		computeGridMatrixSize(widthSize, heightSize);
		computeColumnsAndRowsSizes(widthSize, heightSize);

		measureChild(deleteZone, MeasureSpec.makeMeasureSpec(gridPageWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((int)getPixelFromDip(40), MeasureSpec.EXACTLY));

		setMeasuredDimension(widthSize * adapter.pageCount(), heightSize);
	}

	/**
	 * Gets the pixel from dip.
	 *
	 * @param size the size
	 * @return the pixel from dip
	 */
	private float getPixelFromDip(int size) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, r.getDisplayMetrics());
		return px;
	}

	/**
	 * Compute columns and rows sizes.
	 *
	 * @param widthSize the width size
	 * @param heightSize the height size
	 */
	private void computeColumnsAndRowsSizes(int widthSize, int heightSize) {
		columnWidthSize = widthSize / computedColumnCount;
		rowHeightSize = heightSize / computedRowCount;
	}

	/**
	 * Compute grid matrix size.
	 *
	 * @param widthSize the width size
	 * @param heightSize the height size
	 */
	private void computeGridMatrixSize(int widthSize, int heightSize) {
		if (adapter.columnCount() != -1 && adapter.rowCount() != -1) {
			computedColumnCount = adapter.columnCount();
			computedRowCount = adapter.rowCount();
		} else {
			if (biggestChildWidth > 0 && biggestChildHeight > 0) {
				computedColumnCount = widthSize / biggestChildWidth;
				computedRowCount = heightSize / biggestChildHeight;
			}
		}

		if (computedColumnCount == 0) {
			computedColumnCount = 1;
		}

		if (computedRowCount == 0) {
			computedRowCount = 1;
		}
	}

	/**
	 * Search biggest child measures.
	 */
	private void searchBiggestChildMeasures() {
		biggestChildWidth = 0;
		biggestChildHeight = 0;
		for (int index = 0; index < getItemViewCount(); index++) {
			View child = getChildAt(index);

			if (biggestChildHeight < child.getMeasuredHeight()) {
				biggestChildHeight = child.getMeasuredHeight();
			}

			if (biggestChildWidth < child.getMeasuredWidth()) {
				biggestChildWidth = child.getMeasuredWidth();
			}
		}
	}

	/**
	 * Gets the item view count.
	 *
	 * @return the item view count
	 */
	private int getItemViewCount() {
		// -1 to remove the DeleteZone from the loop
		return getChildCount()-1;
	}

	/**
	 * Adapt children measures to view size.
	 *
	 * @param widthSize the width size
	 * @param heightSize the height size
	 */
	private void adaptChildrenMeasuresToViewSize(int widthSize, int heightSize) {
		if (adapter.columnCount() != PagedDragDropGridAdapter.AUTOMATIC && adapter.rowCount() != PagedDragDropGridAdapter.AUTOMATIC) {
			int desiredGridItemWidth = widthSize / adapter.columnCount();
			int desiredGridItemHeight = heightSize / adapter.rowCount();
			measureChildren(MeasureSpec.makeMeasureSpec(desiredGridItemWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(desiredGridItemHeight, MeasureSpec.AT_MOST));
		} else {
			measureChildren(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		}
	}

	/**
	 * Acknowledge height size.
	 *
	 * @param heightMode the height mode
	 * @param heightSize the height size
	 * @param display the display
	 * @return the int
	 */
	@SuppressWarnings("deprecation")
	private int acknowledgeHeightSize(int heightMode, int heightSize, Display display) {
		if (heightMode == MeasureSpec.UNSPECIFIED) {
			heightSize = display.getHeight();
		}
		gridPageHeight = heightSize;
		return heightSize;
	}

	/**
	 * Acknowledge width size.
	 *
	 * @param widthMode the width mode
	 * @param widthSize the width size
	 * @param display the display
	 * @return the int
	 */
	@SuppressWarnings("deprecation")
	private int acknowledgeWidthSize(int widthMode, int widthSize, Display display) {
		if (widthMode == MeasureSpec.UNSPECIFIED) {
			widthSize = display.getWidth();
		}
		gridPageWidth = widthSize;
		return widthSize;
	}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int pageWidth  = (l + r) / adapter.pageCount();

		for (int page = 0; page < adapter.pageCount(); page++) {
			layoutPage(pageWidth, page);
		}
	}

	/**
	 * Layout page.
	 *
	 * @param pageWidth the page width
	 * @param page the page
	 */
	private void layoutPage(int pageWidth, int page) {
		int col = 0;
		int row = 0;
		for (int childIndex = 0; childIndex < adapter.itemCountInPage(page); childIndex++) {
			layoutAChild(pageWidth, page, col, row, childIndex);
			col++;
			if (col == computedColumnCount) {
				col = 0;
				row++;
			}
		}
	}

	/**
	 * Layout a child.
	 *
	 * @param pageWidth the page width
	 * @param page the page
	 * @param col the col
	 * @param row the row
	 * @param childIndex the child index
	 */
	private void layoutAChild(int pageWidth, int page, int col, int row, int childIndex) {
		int position = positionOfItem(page, childIndex);

		View child = getChildAt(position);

		int left = 0;
		int top = 0;
		if (position == dragged && lastTouchOnEdge()) {
			left = computePageEdgeXCoor(child);
			top = lastTouchY - (child.getMeasuredHeight() / 2);
		} else {
			left = (page * pageWidth) + (col * columnWidthSize) + ((columnWidthSize - child.getMeasuredWidth()) / 2);
			top = (row * rowHeightSize) + ((rowHeightSize - child.getMeasuredHeight()) / 2);
		}
		child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
	}

	/**
	 * Last touch on edge.
	 *
	 * @return true, if successful
	 */
	private boolean lastTouchOnEdge() {
		return onRightEdgeOfScreen(lastTouchX) || onLeftEdgeOfScreen(lastTouchX);
	}

	/**
	 * Compute page edge x coor.
	 *
	 * @param child the child
	 * @return the int
	 */
	private int computePageEdgeXCoor(View child) {
		int left;
		left = lastTouchX - (child.getMeasuredWidth() / 2);
		if (onRightEdgeOfScreen(lastTouchX)) {
			left = left - gridPageWidth;
		} else if (onLeftEdgeOfScreen(lastTouchX)) {
			left = left + gridPageWidth;
		}
		return left;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnLongClickListener#onLongClick(android.view.View)
	 */
	@Override
	public boolean onLongClick(View v) {	    
	    if(positionForView(v) != -1) {
    		container.disableScroll();
    
    		movingView = true;
    		dragged = positionForView(v);
    
    		animateMoveAllItems();
    
    		animateDragged();
    		popDeleteView();
    
    		return true;
	    }
	    
	    return false;
	}

	/**
	 * Animate dragged.
	 */
	private void animateDragged() {

		ScaleAnimation scale = new ScaleAnimation(1f, 1.4f, 1f, 1.4f, biggestChildWidth / 2 , biggestChildHeight / 2);
		scale.setDuration(200);
		scale.setFillAfter(true);
		scale.setFillEnabled(true);

		if (aViewIsDragged()) {
			getChildAt(dragged).clearAnimation();
			getChildAt(dragged).startAnimation(scale);
		}
	}

	/**
	 * A view is dragged.
	 *
	 * @return true, if successful
	 */
	private boolean aViewIsDragged() {
		return dragged != -1;
	}

	/**
	 * Pop delete view.
	 */
	private void popDeleteView() {

		deleteZone.setVisibility(View.VISIBLE);

		int l = currentPage() * deleteZone.getMeasuredWidth();
		int t = gridPageHeight - deleteZone.getMeasuredHeight();
		deleteZone.layout(l,  t, l + gridPageWidth, t + gridPageHeight);
	}

	/**
	 * Creates the delete zone.
	 */
	private void createDeleteZone() {
		deleteZone = new DeleteDropZoneView(getContext());
		addView(deleteZone);
	}

	/**
	 * Hide delete view.
	 */
	private void hideDeleteView() {
		deleteZone.setVisibility(View.GONE);
	}

	/**
	 * Position for view.
	 *
	 * @param v the v
	 * @return the int
	 */
	private int positionForView(View v) {
		for (int index = 0; index < getItemViewCount(); index++) {
			View child = getChildAt(index);
				if (isPointInsideView(initialX, initialY, child)) {
					return index;
				}
		}
		return -1;
	}

	/**
	 * Checks if is point inside view.
	 *
	 * @param x the x
	 * @param y the y
	 * @param view the view
	 * @return true, if is point inside view
	 */
	private boolean isPointInsideView(float x, float y, View view) {
		int location[] = new int[2];
		view.getLocationOnScreen(location);
		int viewX = location[0];
		int viewY = location[1];

		if (pointIsInsideViewBounds(x, y, view, viewX, viewY)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Point is inside view bounds.
	 *
	 * @param x the x
	 * @param y the y
	 * @param view the view
	 * @param viewX the view x
	 * @param viewY the view y
	 * @return true, if successful
	 */
	private boolean pointIsInsideViewBounds(float x, float y, View view, int viewX, int viewY) {
		return (x > viewX && x < (viewX + view.getWidth())) && (y > viewY && y < (viewY + view.getHeight()));
	}

	/**
	 * Sets the container.
	 *
	 * @param container the new container
	 */
	public void setContainer(PagedDragDropGrid container) {
		this.container = container;
	}

	/**
	 * Position of item.
	 *
	 * @param pageIndex the page index
	 * @param childIndex the child index
	 * @return the int
	 */
	private int positionOfItem(int pageIndex, int childIndex) {
		int currentGlobalIndex = 0;
		for (int currentPageIndex = 0; currentPageIndex < adapter.pageCount(); currentPageIndex++) {
			int itemCount = adapter.itemCountInPage(currentPageIndex);
			for (int currentItemIndex = 0; currentItemIndex < itemCount; currentItemIndex++) {
				if (pageIndex == currentPageIndex && childIndex == currentItemIndex) {
					return currentGlobalIndex;
				}
				currentGlobalIndex++;
			}
		}
		return -1;
	}

	/**
	 * Item information at position.
	 *
	 * @param position the position
	 * @return the item position
	 */
	private ItemPosition itemInformationAtPosition(int position) {
		int currentGlobalIndex = 0;
		for (int currentPageIndex = 0; currentPageIndex < adapter.pageCount(); currentPageIndex++) {
			int itemCount = adapter.itemCountInPage(currentPageIndex);
			for (int currentItemIndex = 0; currentItemIndex < itemCount; currentItemIndex++) {
				if (currentGlobalIndex == position) {
					return new ItemPosition(currentPageIndex, currentItemIndex);
				}
				currentGlobalIndex++;
			}
		}
		return null;
	}

	/**
	 * Tell adapter to swap dragged with target.
	 *
	 * @param dragged the dragged
	 * @param target the target
	 */
	private void tellAdapterToSwapDraggedWithTarget(int dragged, int target) {
		ItemPosition draggedItemPositionInPage = itemInformationAtPosition(dragged);
		ItemPosition targetItemPositionInPage = itemInformationAtPosition(target);
		if (draggedItemPositionInPage != null && targetItemPositionInPage != null) {
			adapter.swapItems(draggedItemPositionInPage.pageIndex,draggedItemPositionInPage.itemIndex, targetItemPositionInPage.itemIndex);
		}
	}

	/**
	 * Tell adapter to move item to previous page.
	 *
	 * @param itemIndex the item index
	 */
	private void tellAdapterToMoveItemToPreviousPage(int itemIndex) {
		ItemPosition itemPosition = itemInformationAtPosition(itemIndex);
		adapter.moveItemToPreviousPage(itemPosition.pageIndex,itemPosition.itemIndex);
	}

	/**
	 * Tell adapter to move item to next page.
	 *
	 * @param itemIndex the item index
	 */
	private void tellAdapterToMoveItemToNextPage(int itemIndex) {
		ItemPosition itemPosition = itemInformationAtPosition(itemIndex);
		adapter.moveItemToNextPage(itemPosition.pageIndex,itemPosition.itemIndex);
	}

	/**
	 * The Class ItemPosition.
	 */
	private class ItemPosition {
		
		/** The page index. */
		public int pageIndex;
		
		/** The item index. */
		public int itemIndex;

		/**
		 * Instantiates a new item position.
		 *
		 * @param pageIndex the page index
		 * @param itemIndex the item index
		 */
		public ItemPosition(int pageIndex, int itemIndex) {
			super();
			this.pageIndex = pageIndex;
			this.itemIndex = itemIndex;
		}
	}
}
