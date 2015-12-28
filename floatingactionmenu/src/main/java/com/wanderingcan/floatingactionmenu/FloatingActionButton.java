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

package com.wanderingcan.floatingactionmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.wanderingcan.floatingactionmenu.R;


/**
 * An extension to Android's Design library Floating Action Button to allow it to work with
 * {@link FloatingActionMenu}
 */
public class FloatingActionButton extends android.support.design.widget.FloatingActionButton {

    private String mLabelText;
    private Animation mShowAnimation;
    private Animation mHideAnimation;

    private static final Handler mHandler = new Handler();

    public FloatingActionButton(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attr = context.obtainStyledAttributes(attrs,
                R.styleable.FloatingActionButton, defStyleAttr, 0);
        mLabelText = attr.getString(R.styleable.FloatingActionButton_label);

        mShowAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_in);
        mShowAnimation.setInterpolator(new FastOutSlowInInterpolator());
        mShowAnimation.setDuration(FloatingActionMenu.ANIMATION_DURATION);

        mHideAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fab_out);
        mHideAnimation.setInterpolator(new FastOutSlowInInterpolator());
        mHideAnimation.setDuration(FloatingActionMenu.ANIMATION_DURATION);

        attr.recycle();
    }

    /**
     * Sets the animation that is played when {@link #show(boolean)} is called. Also sets the
     * animation for the attached LabelView if there is an attached LabelView
     */
    void setShowAnimation(Animation animation){
        setShowAnimation(animation, FloatingActionMenu.ANIMATION_DURATION);
    }

    /**
     * Sets the animation that is played when {@link #hide(boolean)} is called. Also sets the
     * animation for the attached LabelView if there is an attached LabelView
     */
    void setHideAnimation(Animation animation){
        setHideAnimation(animation, FloatingActionMenu.ANIMATION_DURATION);
    }

    /**
     * Sets the animation that is played when {@link #show(boolean)} is called. Also sets the
     * animation for the attached LabelView if there is an attached LabelView
     * @param animation the animation that will be played
     * @param duration the duration of the animation in milliseconds
     */
    void setShowAnimation(Animation animation, long duration){
        mShowAnimation = animation;
        mShowAnimation.setDuration(duration);

        LabelView label = getLabelView();
        if(label != null) {
            label.setShowAnimation(mHideAnimation, duration);
        }
    }

    /**
     * Sets the animation that is played when {@link #hide(boolean)} is called. Also sets the
     * animation for the attached LabelView if there is an attached LabelView
     * @param animation the animation that will be played
     * @param duration the duration of the animation in milliseconds
     */
    void setHideAnimation(Animation animation, long duration){
        mHideAnimation = animation;
        mHideAnimation.setDuration(duration);

        LabelView label = getLabelView();
        if(label != null) {
            label.setHideAnimation(mHideAnimation, duration);
        }
    }

    /**
     * Sets the duration of the animations that are played when {@link #show(boolean)} and
     * {@link #hide(boolean)} are called. Also sets the duration of the animation for the attached
     * LabelView if there is an attached LabelView
     * @param duration the duration of the animation in milliseconds
     */
    public void setAnimationDuration(long duration){
        mShowAnimation.setDuration(duration);
        mHideAnimation.setDuration(duration);
        LabelView label = getLabelView();
        if(label != null) {
            label.setAnimationDuration(duration);
        }
    }


    /**
     * Gets the animation that is played when the Floating Action Button is shown
     */
    public Animation getShowAnimation(){
        return mShowAnimation;
    }

    /**
     * Gets the animation that is played when the Floating Action Button is hidden
     */
    public Animation getHideAnimation(){
        return mHideAnimation;
    }

    private void playShowAnimation() {
        mHideAnimation.cancel();
        startAnimation(mShowAnimation);
    }

    private void playHideAnimation() {
        mShowAnimation.cancel();
        startAnimation(mHideAnimation);
    }

    /**
     * Checks whether FloatingActionButton is hidden
     *
     * @return true if FloatingActionButton is hidden, false otherwise
     */
    public boolean isHidden() {
        return getVisibility() == INVISIBLE;
    }

    /**
     * Makes the FloatingActionButton appear and sets its visibility to {@link #VISIBLE}
     *
     * @param animate if true plays "show animation"
     */
    void show(boolean animate) {
        if (isHidden()) {
            if (animate) {
                playShowAnimation();
            }
            setVisibility(VISIBLE);
        }
    }

    /**
     * Makes the Floating Action Button disappear and sets its visibility to {@link #INVISIBLE}
     *
     * @param animate if true plays "hide animation"
     */

    void hide(boolean animate) {
        if (!isHidden()) {
            if (animate) {
                playHideAnimation();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setVisibility(INVISIBLE);
                }
            }, animate ? mHideAnimation.getDuration() : 0);
        }
    }

    /**
     * Sets the Text to be displayed by the LabelView attached to the Floating Action Button
     */
    public void setLabelText(String title) {
        mLabelText = title;
        LabelView label = getLabelView();
        if (label != null) {
            label.setText(title);
        }
    }

    /**
     * Sets the LabelView that is attached to the Floating Action Button
     */
    public void setLabelView(LabelView label){
        setTag(R.id.fab_label, label);
    }

    /**
     * Gets the LabelView that is attached to the Floating Action Button
     */
    public LabelView getLabelView() {
        return (LabelView) getTag(R.id.fab_label);
    }

    /**
     * Gets the Text in the Label
     */
    public String getLabelText() {
        LabelView label = getLabelView();
        if (label != null) {
            return label.getText();
        }
        return mLabelText;
    }

}
