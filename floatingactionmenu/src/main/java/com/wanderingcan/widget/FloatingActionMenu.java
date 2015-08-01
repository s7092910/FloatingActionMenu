/*
 * Copyright 2015 Christopher Beda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wanderingcan.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AnimRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.wanderingcan.widget.internal.TouchDelegateGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A Menu that uses {@link FloatingActionButton} as buttons. This uses an extension of Android's Design
 * library Floating Action Button. There are many customizations to the menu, for example having
 * labels for the menu buttons
 */
@CoordinatorLayout.DefaultBehavior(FloatingActionMenu.Behavior.class)
public class FloatingActionMenu extends ViewGroup {
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    @IntDef({UP, DOWN, LEFT, RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MenuDirection {}

    public static final int LABELS_LEFT = 0;
    public static final int LABELS_RIGHT = 1;

    @IntDef({LABELS_LEFT, LABELS_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LabelsPosition {}

    public static final int LABELS_CARD = 1;
    public static final int LABELS_TEXT = 2;

    @IntDef({LABELS_CARD, LABELS_TEXT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LabelsType {}

    protected static final long ANIMATION_DURATION = 200;
    private static final float COLLAPSED_PLUS_ROTATION = 0f;
    private static final float EXPANDED_PLUS_ROTATION = 45f;

    private int mExpandDirection;

    private int mButtonSpacing;
    private int mButtonMargin;
    private int mLabelsMargin;

    private boolean isMenuOpened;
    private boolean isMenuAnimationRunning;
    private boolean isClosedOnTouchOutside;

    private FloatingActionButton mMenuButton;
    private int mMaxButtonWidth;
    private int mMaxButtonHeight;
    private int mLabelsStyle;
    private int mLabelsType;
    private int mLabelsPosition;
    private int mButtonsCount;

    private Handler mUiHandler = new Handler();

    private long mAnimationDelayPerItem;
    private int mBackgroundColor;
    private int mMenuShowAnimation;
    private int mMenuHideAnimation;
    private AnimatorSet mExpandAnimation = new AnimatorSet();
    private AnimatorSet mCollapseAnimation = new AnimatorSet();
    private ValueAnimator mShowBackgroundAnimator;
    private ValueAnimator mHideBackgroundAnimator;
    private long mAnimationDuration;
    private boolean mFillParent;

    private TouchDelegateGroup mTouchDelegateGroup;

    private OnFloatingActionsMenuUpdateListener mMenuListener;

    GestureDetector mGestureDetector = new GestureDetector(getContext(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return isClosedOnTouchOutside && isMenuOpened;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    close();
                    return true;
                }
            });

    public interface OnFloatingActionsMenuUpdateListener {
        void onMenuExpanded();
        void onMenuCollapsed();
    }

    public FloatingActionMenu(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        mLabelsMargin = getResources().getDimensionPixelSize(R.dimen.fab_labels_margin);

        TypedArray attr = context.obtainStyledAttributes(attrs,
                R.styleable.FloatingActionMenu, defStyleAttr, 0);
        mButtonSpacing = getResources().getDimensionPixelSize(R.dimen.fab_actions_spacing);

        mExpandDirection = attr.getInt(R.styleable.FloatingActionMenu_layout_expand,
                UP);
        mLabelsPosition = attr.getInt(R.styleable.FloatingActionMenu_layout_labels,
                LABELS_LEFT);
        mButtonMargin = attr.getDimensionPixelSize(R.styleable.FloatingActionMenu_content_padding,
                (int) getResources().getDimension(R.dimen.fab_content_padding));

        mLabelsStyle = attr.getResourceId(R.styleable.FloatingActionMenu_labelStyle, 0);
        mLabelsType = attr.getInt(R.styleable.FloatingActionMenu_labelType, 0);

        mBackgroundColor = attr.getColor(R.styleable.FloatingActionMenu_menuBackgroundColor,
                Color.TRANSPARENT);
        mFillParent = attr.getBoolean(R.styleable.FloatingActionMenu_menuBackgroundFillParent,
                false);

        //Animations
        mMenuShowAnimation = attr.getResourceId(R.styleable.FloatingActionMenu_menuOpenAnimation,
                R.anim.fab_in);
        mMenuHideAnimation = attr.getResourceId(R.styleable.FloatingActionMenu_menuCloseAnimation,
                R.anim.fab_out);
        mAnimationDuration = attr.getInt(R.styleable.FloatingActionMenu_menuItemAnimationTime,
                (int) ANIMATION_DURATION);
        mAnimationDelayPerItem = attr.getInt(R.styleable.FloatingActionMenu_menuItemAnimationDelay,
                100);

        isClosedOnTouchOutside = attr.getBoolean(R.styleable.FloatingActionMenu_closeOnOutsideTouch,
                true);

        attr.recycle();

        mTouchDelegateGroup = new TouchDelegateGroup(this);
        setTouchDelegate(mTouchDelegateGroup);

        initMenuButton(context, attrs);
        initBackgroundDimAnimation();
    }

    private void initBackgroundDimAnimation() {
        final int maxAlpha = Color.alpha(mBackgroundColor);
        final int red = Color.red(mBackgroundColor);
        final int green = Color.green(mBackgroundColor);
        final int blue = Color.blue(mBackgroundColor);

        mShowBackgroundAnimator = ValueAnimator.ofInt(0, maxAlpha);
        mShowBackgroundAnimator.setDuration(mAnimationDuration);
        mShowBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer alpha = (Integer) animation.getAnimatedValue();
                setBackgroundColor(Color.argb(alpha, red, green, blue));
            }
        });

        mHideBackgroundAnimator = ValueAnimator.ofInt(maxAlpha, 0);
        mHideBackgroundAnimator.setDuration(mAnimationDuration);
        mHideBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer alpha = (Integer) animation.getAnimatedValue();
                setBackgroundColor(Color.argb(alpha, red, green, blue));
            }
        });
    }

    private void initMenuButton(Context context, AttributeSet attributeSet) {
        mMenuButton = new FloatingActionButton(context, attributeSet);

        mMenuButton.setId(R.id.fab_expand_menu_button);
        mMenuButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
        mMenuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        addView(mMenuButton, super.generateDefaultLayoutParams());
        mButtonsCount++;
        initDefaultIconAnimation();
    }

    private void initDefaultIconAnimation() {

        mExpandAnimation.play(ObjectAnimator.ofFloat(
                mMenuButton,
                "rotation",
                COLLAPSED_PLUS_ROTATION,
                EXPANDED_PLUS_ROTATION
        ));

        mCollapseAnimation.play(ObjectAnimator.ofFloat(
                mMenuButton,
                "rotation",
                EXPANDED_PLUS_ROTATION,
                COLLAPSED_PLUS_ROTATION
        ));
        mExpandAnimation.setInterpolator(new OvershootInterpolator(5f));
        mCollapseAnimation.setInterpolator(new OvershootInterpolator(5f));
        mExpandAnimation.setDuration(mAnimationDuration);
        mCollapseAnimation.setDuration(mAnimationDuration);
    }

    private boolean isBackgroundEnabled() {
        return mBackgroundColor != Color.TRANSPARENT;
    }

    /**
     * Sets the menu update listener
     * @see OnFloatingActionsMenuUpdateListener
     */
    public void setOnFloatingActionsMenuUpdateListener(OnFloatingActionsMenuUpdateListener listener) {
        mMenuListener = listener;
    }

    private boolean expandsHorizontally() {
        return mExpandDirection == LEFT || mExpandDirection == RIGHT;
    }

    /**
     * Adds the Floating Action Button to the menu. And creates and attaches a LabelView to the Button
     * if the Style is set.
     * @param button The Floating Action Button to be added to the menu
     */
    public void addButton(FloatingActionButton button) {
        addView(button, mButtonsCount - 1);
        mButtonsCount++;

        Animation showAnimation = AnimationUtils.loadAnimation(getContext(), mMenuShowAnimation);
        if(mMenuHideAnimation == R.anim.fab_in){
            showAnimation.setInterpolator(new FastOutSlowInInterpolator());
        }

        Animation hideAnimation = AnimationUtils.loadAnimation(getContext(), mMenuHideAnimation);
        if(mMenuHideAnimation == R.anim.fab_out){
            hideAnimation.setInterpolator(new FastOutSlowInInterpolator());
        }

        button.setShowAnimation(showAnimation, mAnimationDuration);
        button.setHideAnimation(hideAnimation, mAnimationDuration);
        if (mLabelsStyle != 0 || mLabelsType != 0) {
            createLabels();
        }
    }

    /**
     * Adds the Floating Action Button to the menu. And creates and attaches a LabelView to the Button
     * if the Style is set.
     * @param button The Floating Action Button to be added to the menu
     */
    public void addButton(FloatingActionButton button, int index) {
        addView(button, index);
        mButtonsCount++;

        Animation showAnimation = AnimationUtils.loadAnimation(getContext(), mMenuShowAnimation);
        if(mMenuHideAnimation == R.anim.fab_in){
            showAnimation.setInterpolator(new FastOutSlowInInterpolator());
        }

        Animation hideAnimation = AnimationUtils.loadAnimation(getContext(), mMenuHideAnimation);
        if(mMenuHideAnimation == R.anim.fab_out){
            hideAnimation.setInterpolator(new FastOutSlowInInterpolator());
        }

        button.setShowAnimation(showAnimation, mAnimationDuration);
        button.setHideAnimation(hideAnimation, mAnimationDuration);
        if (mLabelsStyle != 0 || mLabelsType != 0) {
            createLabels();
        }
    }


    /**
     * Removes the Floating Action Button from the Floating Action Menu
     * @param button the Floating Action Button to remove from the Menu
     */
    public void removeButton(FloatingActionButton button) {
        removeView(button.getLabelView());
        removeView(button);
        button.setLabelView(null);
        mButtonsCount--;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (isClosedOnTouchOutside) {
            return mTouchDelegateGroup.onTouchEvent(event) || mGestureDetector.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        mMaxButtonWidth = 0;
        mMaxButtonHeight = 0;
        int maxLabelWidth = 0;

        for (int i = 0; i < mButtonsCount; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                switch (mExpandDirection) {
                    case UP:
                    case DOWN:
                        mMaxButtonWidth = Math.max(mMaxButtonWidth, child.getMeasuredWidth());
                        height += child.getMeasuredHeight();
                        break;
                    case LEFT:
                    case RIGHT:
                        width += child.getMeasuredWidth();
                        mMaxButtonHeight = Math.max(mMaxButtonHeight, child.getMeasuredHeight());
                        break;
                }

                if (!expandsHorizontally()) {
                    LabelView label = (LabelView) child.getTag(R.id.fab_label);
                    if (label != null) {
                        maxLabelWidth = Math.max(maxLabelWidth, label.getMeasuredWidth());
                    }
                }
            }
        }

        if (!expandsHorizontally()) {
            width = mMaxButtonWidth + (maxLabelWidth > 0 ? maxLabelWidth + mLabelsMargin : 0);
        } else {
            height = mMaxButtonHeight;
        }

        switch (mExpandDirection) {
            case UP:
            case DOWN:
                height += mButtonSpacing * (mButtonsCount - 1);
                height = adjustForOvershoot(height);
                width += mButtonMargin;
                break;
            case LEFT:
            case RIGHT:
                width += mButtonSpacing * (mButtonsCount - 1);
                width = adjustForOvershoot(width);
                height += mButtonMargin;
                break;
        }

        if (getLayoutParams().width == LayoutParams.MATCH_PARENT ||
                (isBackgroundEnabled()&& mFillParent)) {
            width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        }

        if (getLayoutParams().height == LayoutParams.MATCH_PARENT ||
                (isBackgroundEnabled()&& mFillParent)) {
            height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        switch (mExpandDirection) {
            case UP:
                //Fall through
            case DOWN:
                boolean expandUp = mExpandDirection == UP;

                if (changed) {
                    mTouchDelegateGroup.clearTouchDelegates();
                }

                int addButtonTop = expandUp ? b - t - mMenuButton.getMeasuredHeight() - getPaddingBottom()
                        : getPaddingTop();

                // Ensure mMenuButton is centered on the line where the buttons should be
                int buttonsHorizontalCenter = mLabelsPosition == LABELS_LEFT
                        ? r - l - mMaxButtonWidth / 2 - getPaddingRight()
                        : mMaxButtonWidth / 2 + getPaddingLeft();
                int addButtonLeft = buttonsHorizontalCenter - mMenuButton.getMeasuredWidth() / 2;

                int buttonMarginHeight = expandUp ? (mButtonMargin/2): (-mButtonMargin/2);
                int buttonMarginWidth = mLabelsPosition == LABELS_LEFT
                        ? (mButtonMargin/2)
                        :(-mButtonMargin/2);

                mMenuButton.layout(addButtonLeft - buttonMarginWidth, addButtonTop - buttonMarginHeight,
                        addButtonLeft + mMenuButton.getMeasuredWidth() - buttonMarginWidth,
                        addButtonTop + mMenuButton.getMeasuredHeight() - buttonMarginHeight);

                int labelsOffset = mMaxButtonWidth / 2 + mLabelsMargin;
                int labelsXNearButton = mLabelsPosition == LABELS_LEFT
                        ? buttonsHorizontalCenter - labelsOffset
                        : buttonsHorizontalCenter + labelsOffset;

                int nextY = expandUp ?
                        addButtonTop - mButtonSpacing :
                        addButtonTop + mMenuButton.getMeasuredHeight() + mButtonSpacing;

                for (int i = 0; i <= mButtonsCount - 1; i++) {
                    final View child = getChildAt(i);

                    if (child == mMenuButton || child.getVisibility() == GONE) continue;

                    int childX = buttonsHorizontalCenter - child.getMeasuredWidth() / 2;
                    int childY = expandUp ? nextY - child.getMeasuredHeight() : nextY;
                    child.layout(childX - buttonMarginWidth, childY - buttonMarginHeight,
                            childX + child.getMeasuredWidth() - buttonMarginWidth,
                            childY + child.getMeasuredHeight() - buttonMarginHeight);

                    if(!isMenuOpened) {
                        ((FloatingActionButton) child).hide(false);
                    }

                    LabelView label = (LabelView) child.getTag(R.id.fab_label);
                    if (label != null) {
                        int labelXAwayFromButton = mLabelsPosition == LABELS_LEFT
                                ? labelsXNearButton - label.getMeasuredWidth()
                                : labelsXNearButton + label.getMeasuredWidth();

                        int labelLeft = mLabelsPosition == LABELS_LEFT
                                ? labelXAwayFromButton
                                : labelsXNearButton;

                        int labelRight = mLabelsPosition == LABELS_LEFT
                                ? labelsXNearButton
                                : labelXAwayFromButton;

                        int labelTop = childY +
                                (child.getMeasuredHeight() - label.getMeasuredHeight()) / 2;

                        label.layout(labelLeft, labelTop - buttonMarginHeight,
                                labelRight,
                                labelTop + label.getMeasuredHeight() - buttonMarginHeight);

                        Rect touchArea = new Rect(
                                Math.min(childX, labelLeft),
                                childY - mButtonSpacing / 2,
                                Math.max(childX + child.getMeasuredWidth(), labelRight),
                                childY + child.getMeasuredHeight() + mButtonSpacing / 2);
                        mTouchDelegateGroup.addTouchDelegate(new TouchDelegate(touchArea, child));

                        if(!isMenuOpened) {
                            label.hide(false);
                        }
                    }

                    nextY = expandUp ?
                            childY - mButtonSpacing :
                            childY + child.getMeasuredHeight() + mButtonSpacing;
                }
                break;

            case LEFT:
                //Fall through
            case RIGHT:
                boolean expandLeft = mExpandDirection == LEFT;

                addButtonLeft = expandLeft ? r - l - mMenuButton.getMeasuredWidth() - getPaddingLeft()
                        : getPaddingRight();
                // Ensure mMenuButton is centered on the line where the buttons should be
                addButtonTop = b - t - mMaxButtonHeight +
                        (mMaxButtonHeight - mMenuButton.getMeasuredHeight()) / 2;

                buttonMarginHeight = (mButtonMargin/2);
                buttonMarginWidth = expandLeft ? (mButtonMargin/2): (-mButtonMargin/2);

                mMenuButton.layout(addButtonLeft - buttonMarginWidth, addButtonTop - buttonMarginHeight,
                        addButtonLeft + mMenuButton.getMeasuredWidth() - buttonMarginWidth,
                        addButtonTop + mMenuButton.getMeasuredHeight() - buttonMarginHeight);

                int nextX = expandLeft ?
                        addButtonLeft - mButtonSpacing :
                        addButtonLeft + mMenuButton.getMeasuredWidth() + mButtonSpacing;

                for (int i = mButtonsCount - 1; i >= 0; i--) {
                    final View child = getChildAt(i);

                    if (child == mMenuButton || child.getVisibility() == GONE) continue;

                    int childX = expandLeft ? nextX - child.getMeasuredWidth() : nextX;
                    int childY = addButtonTop + (mMenuButton.getMeasuredHeight() - child.getMeasuredHeight()) / 2;
                    child.layout(childX - buttonMarginWidth, childY - buttonMarginHeight,
                            childX + child.getMeasuredWidth() - buttonMarginWidth,
                            childY + child.getMeasuredHeight() - buttonMarginHeight);

                    if(!isMenuOpened) {
                        ((FloatingActionButton) child).hide(false);
                    }

                    nextX = expandLeft ?
                            childX - mButtonSpacing :
                            childX + child.getMeasuredWidth() + mButtonSpacing;
                }

                break;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        bringChildToFront(mMenuButton);
        mButtonsCount = getChildCount();

        for (int i = 0; i < mButtonsCount; i++) {
            final View child = getChildAt(i);
            if(child != mMenuButton) {
                Animation showAnimation = AnimationUtils.loadAnimation(getContext(), mMenuShowAnimation);
                if(mMenuShowAnimation == R.anim.fab_in){
                    showAnimation.setInterpolator(new FastOutSlowInInterpolator());
                }

                Animation hideAnimation = AnimationUtils.loadAnimation(getContext(), mMenuHideAnimation);
                if(mMenuHideAnimation == R.anim.fab_out){
                    hideAnimation.setInterpolator(new FastOutSlowInInterpolator());
                }

                ((FloatingActionButton)child).setShowAnimation(showAnimation, mAnimationDuration);
                ((FloatingActionButton)child).setHideAnimation(hideAnimation, mAnimationDuration);
                ((FloatingActionButton)child).hide(false);
            }
        }

        if (mLabelsStyle != 0 || mLabelsType != 0) {
            createLabels();
        }
    }

    private int adjustForOvershoot(int dimension) {
        return dimension * 12 / 10;
    }

    private void createLabels() {
        Context context = new ContextThemeWrapper(getContext(), mLabelsStyle);

        for (int i = 0; i < mButtonsCount; i++) {
            FloatingActionButton button = (FloatingActionButton) getChildAt(i);
            String title = button.getLabelText();

            if (button == mMenuButton || title == null ||
                    button.getTag(R.id.fab_label) != null) continue;

            LabelView label = new LabelView(context);
            if(mLabelsType == LABELS_CARD) {
                label.setLabelType(LabelView.Type.CARD);
            }else {
                label.setLabelType(LabelView.Type.TEXT);
            }

            int style;
            if(mLabelsStyle != 0){
                style = mLabelsStyle;
            }else {
                style = R.style.menu_labels_style_blank;
            }

            label.setTextAppearance(getContext(), style);

            Animation showAnimation = AnimationUtils.loadAnimation(getContext(), mMenuShowAnimation);
            if(mMenuHideAnimation == R.anim.fab_in){
                showAnimation.setInterpolator(new FastOutSlowInInterpolator());
            }

            Animation hideAnimation = AnimationUtils.loadAnimation(getContext(), mMenuHideAnimation);
            if(mMenuHideAnimation == R.anim.fab_out){
                hideAnimation.setInterpolator(new FastOutSlowInInterpolator());
            }

            label.setShowAnimation(showAnimation, mAnimationDuration);
            label.setHideAnimation(hideAnimation, mAnimationDuration);

            label.setText(button.getLabelText());
            addView(label);

            button.setLabelView(label);
        }
    }

    /**
     * Closes the Floating Action Menu
     */
    public void close() {
        if (isMenuOpened && !isMenuAnimationRunning) {
            isMenuAnimationRunning = true;
            if (isBackgroundEnabled()) {
                mHideBackgroundAnimator.start();
            }

            mTouchDelegateGroup.setEnabled(false);
            mCollapseAnimation.start();
            mExpandAnimation.cancel();

            int delay = 0;
            int counter = 0;
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                if (child instanceof FloatingActionButton
                        && child != mMenuButton && child.getVisibility() != GONE) {
                    counter++;

                    final FloatingActionButton fab = (FloatingActionButton) child;
                    mUiHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isMenuOpened()) {
                                fab.hide(true);
                                LabelView label = fab.getLabelView();
                                if(label != null){
                                    label.hide(true);
                                }
                            }
                        }
                    }, delay);
                    delay += mAnimationDelayPerItem;
                }
            }

            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isMenuOpened = false;
                    isMenuAnimationRunning = false;

                    if (mMenuListener != null) {
                        mMenuListener.onMenuCollapsed();
                    }
                }
            }, (counter * mAnimationDelayPerItem) + mAnimationDuration);
        }
    }

    /**
     * Toggles if the Floating Action Menu is open or close
     */
    public void toggle() {
        if (isMenuOpened) {
            close();
        } else {
            open();
        }
    }

    /**
     * Opens the Floating Action Menu
     */
    public void open() {
        if (!isMenuOpened && !isMenuAnimationRunning) {
            isMenuAnimationRunning = true;
            if (isBackgroundEnabled()) {
                mShowBackgroundAnimator.start();
            }

            mTouchDelegateGroup.setEnabled(true);
            mCollapseAnimation.cancel();
            mExpandAnimation.start();

            int delay = 0;
            int counter = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof FloatingActionButton
                        && child != mMenuButton && child.getVisibility() != GONE) {
                    counter++;

                    final FloatingActionButton fab = (FloatingActionButton) child;
                    mUiHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isMenuOpened()) {
                                fab.show(true);
                                LabelView label = fab.getLabelView();
                                if(label != null){
                                    label.show(true);
                                }
                            }
                        }
                    }, delay);
                    delay += mAnimationDelayPerItem;
                }
            }

            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isMenuOpened = true;
                    isMenuAnimationRunning = false;

                    if (mMenuListener != null) {
                        mMenuListener.onMenuExpanded();
                    }
                }
            }, (counter * mAnimationDelayPerItem) + mAnimationDuration);
        }
    }

    /**
     * Returns if the menu is open or not
     * @return true if the menu is open, false otherwise
     */
    public boolean isMenuOpened() {
        return isMenuOpened;
    }

    /**
     * Plays the hide animation and hides the menu. If the menu is currently open, it will play
     * the close animation for the menu and then hide the menu
     */
    public void hide(){
        if(getVisibility() == VISIBLE) {
            int counter = 0;
            if (isMenuOpened && !isMenuAnimationRunning) {
                isMenuAnimationRunning = true;
                if (isBackgroundEnabled()) {
                    mHideBackgroundAnimator.start();
                }

                mTouchDelegateGroup.setEnabled(false);
                mCollapseAnimation.start();
                mExpandAnimation.cancel();

                int delay = 0;
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child instanceof FloatingActionButton
                            && child != mMenuButton && child.getVisibility() != GONE) {
                        counter++;

                        final FloatingActionButton fab = (FloatingActionButton) child;
                        mUiHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isMenuOpened()) {
                                    fab.hide(true);
                                    LabelView label = fab.getLabelView();
                                    if (label != null) {
                                        label.hide(true);
                                    }
                                }
                            }
                        }, delay);
                        delay += mAnimationDelayPerItem;
                    }
                }
            }
            int delay = counter > 0 ? (int) ((++counter * mAnimationDelayPerItem)
                    + mAnimationDuration) : 0;
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isMenuOpened = false;
                    isMenuAnimationRunning = false;
                    mMenuButton.hide(true);
                }
            }, delay);
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setVisibility(GONE);
                }
            }, delay + mAnimationDuration);
        }
    }

    /**
     * Plays the show animation and makes the Floating Action Menu visible
     */
    public void show(){
        if(getVisibility() == GONE) {
            setVisibility(VISIBLE);
            mMenuButton.show(true);
        }
    }

    /**
     * Sets if touching an area other than any Floating Action Buttons or LabelViews will close
     * the menu
     * @param close set to true if you want the menu to close
     */
    public void setClosedOnTouchOutside(boolean close) {
        isClosedOnTouchOutside = close;
    }

    /**
     * Gets the main Floating Action Button that opens and closes the Floating Action Menu
     */
    public FloatingActionButton getMenuButton(){
        return mMenuButton;
    }

    /**
     * Sets the delay between each animation of each menu button
     */
    public void setAnimationDelayPerItem(int durationMillis){
        mAnimationDelayPerItem = durationMillis;
    }

    /**
     * Sets the animation duration of each item
     */
    public void setAnimationDuration(long durationMillis){
        mAnimationDuration = durationMillis;
        mCollapseAnimation.setDuration(durationMillis);
        mShowBackgroundAnimator.setDuration(mAnimationDuration);
        mHideBackgroundAnimator.setDuration(mAnimationDuration);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if(child instanceof FloatingActionButton){
                ((FloatingActionButton) child).setAnimationDuration(durationMillis);
            }
        }
    }

    /**
     * Sets the main button animation interpolator that is played when the menu is opened
     */
    public void setOpenInterpolator(Interpolator interpolator){
        mExpandAnimation.setInterpolator(interpolator);
    }

    /**
     * Sets the main button animation interpolator that is played when the menu is closed
     */
    public void setCloseInterpolator(Interpolator interpolator){
        mCollapseAnimation.setInterpolator(interpolator);
    }

    /**
     * Sets which side of the of the menu the labels appear on
     */
    public void setLabelsPosition(@LabelsPosition int position){
        mLabelsPosition = position;
        requestLayout();
    }

    /**
     * Sets the direction that the menu should expand too
     */
    public void setMenuDirection(@MenuDirection int direction){
        mExpandDirection = direction;
        requestLayout();
    }

    /**
     * Sets the menu Open Animation for all the Floating Action Buttons that are part of the Floating
     * Action Menu
     * @param resId the Resource Id of the Animation Resource
     */
    public void setMenuOpenAnimation(@AnimRes int resId){
        mMenuShowAnimation = resId;
        for (int i = mButtonsCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child != mMenuButton) {
                Animation showAnimation = AnimationUtils.loadAnimation(getContext(), mMenuShowAnimation);
                if (mMenuShowAnimation == R.anim.fab_in) {
                    showAnimation.setInterpolator(new FastOutSlowInInterpolator());
                }

                ((FloatingActionButton) child).setShowAnimation(showAnimation, mAnimationDuration);
            }
        }
    }

    /**
     * Sets the menu Close Animation for all the Floating Action Buttons that are part of the Floating
     * Action Menu
     * @param resId the Resource Id of the Animation Resource
     */
    public void setMenuCloseAnimation(@AnimRes int resId){
        mMenuHideAnimation = resId;
        for (int i = mButtonsCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child != mMenuButton) {
                Animation hideAnimation = AnimationUtils.loadAnimation(getContext(), mMenuHideAnimation);
                if (mMenuHideAnimation == R.anim.fab_in) {
                    hideAnimation.setInterpolator(new FastOutSlowInInterpolator());
                }

                ((FloatingActionButton) child).setHideAnimation(hideAnimation, mAnimationDuration);
            }
        }
    }

    public void setLabelStyle(@StyleRes int style){
        mLabelsStyle = style;
        removeLabels();
        createLabels();
    }

    public void setLabelType(@LabelsType int type){
        mLabelsType = type;
        removeLabels();
        createLabels();
    }

    public void removeLabels(){
        for (int i = mButtonsCount - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if(child instanceof FloatingActionButton) {
                ((FloatingActionButton) child).setLabelView(null);
            }

        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mExpanded = isMenuOpened;

        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            isMenuOpened = savedState.mExpanded;
            mTouchDelegateGroup.setEnabled(isMenuOpened);

            super.onRestoreInstanceState(savedState.getSuperState());
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public static class SavedState extends BaseSavedState {
        public boolean mExpanded;

        public SavedState(Parcelable parcel) {
            super(parcel);
        }

        private SavedState(Parcel in) {
            super(in);
            mExpanded = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mExpanded ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * Floating Action Menu Behavior to allow it to work with the Coordinator Layout. Works for Scolling
     * Views
     */
    public static class Behavior extends
            CoordinatorLayout.Behavior<FloatingActionMenu>{

        public Behavior() {
            super();
        }

        public Behavior(Context context, AttributeSet attrs){
            super(context, attrs);
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionMenu child,
                                       View dependency) {
            return dependency instanceof Snackbar.SnackbarLayout;
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionMenu child,
                                              View dependency) {
            float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
            child.setTranslationY(translationY);
            return false;
        }

        @Override
        public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,FloatingActionMenu child,
                                           View directTargetChild, View target, int nestedScrollAxes) {
            return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                    super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
                            nestedScrollAxes);
        }

        @Override
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionMenu child,
                                   View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
                                   int dyUnconsumed) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                    dyUnconsumed);

            if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
                child.hide();
            } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
                child.show();
            }
        }
    }
}
