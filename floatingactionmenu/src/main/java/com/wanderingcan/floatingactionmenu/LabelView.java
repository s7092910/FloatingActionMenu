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
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wanderingcan.floatingactionmenu.internal.LabelViewCard;
import com.wanderingcan.floatingactionmenu.internal.LabelViewImpl;
import com.wanderingcan.floatingactionmenu.internal.LabelViewText;
import com.wanderingcan.floatingactionmenu.R;

/**
 * A View that allows either a CardView or a TextView to act like a label. Can be used separate from
 * the Floating Action Menu. This View is used in the Floating Action Menu
 */
public class LabelView extends RelativeLayout {

    public static final int LABEL_TEXT = 0;
    public static final int LABEL_CARD = 1;

    private LabelViewImpl mImpl;
    private Type mType;

    private Animation mShowAnimation;
    private Animation mHideAnimation;

    private static final Handler mHandler = new Handler();

    public LabelView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public LabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attr = context.obtainStyledAttributes(attrs,
                R.styleable.LabelView, defStyleAttr, 0);
        int type = attr.getInt(R.styleable.LabelView_labelType, 0);

        if(type == LABEL_TEXT) {
            setLabelTypeInternal(Type.TEXT);
        }else {
            setLabelTypeInternal(Type.CARD);
        }

        attr.recycle();
    }

    /**
     * Sets the Label Type for the View.
     *
     * @see LabelView.Type
     */
    public void setLabelType(Type type){
        removeAllViews();
        setLabelTypeInternal(type);
    }

    /**
     * Gets the Label Type
     * @see Type
     */
    public Type getLabelType(){
        return mType;
    }

    /**
     * Sets the animation that is played when {@link #show(boolean)} is called.
     */
    public void setShowAnimation(Animation animation){
        setShowAnimation(animation, FloatingActionMenu.ANIMATION_DURATION);
    }

    /**
     * Sets the animation that is played when {@link #hide(boolean)} is called.
     */
    public void setHideAnimation(Animation animation){
        setHideAnimation(animation, FloatingActionMenu.ANIMATION_DURATION);
    }

    /**
     * Sets the animation that is played when {@link #show(boolean)} is called.
     * @param animation the animation that will be played
     * @param duration the duration of the animation in milliseconds
     */
    public void setShowAnimation(Animation animation, long duration){
        mShowAnimation = animation;
        mShowAnimation.setDuration(duration);
    }

    /**
     * Sets the animation that is played when {@link #hide(boolean)} is called.
     * @param animation the animation that will be played
     * @param duration the duration of the animation in milliseconds
     */
    public void setHideAnimation(Animation animation, long duration){
        mHideAnimation = animation;
        mHideAnimation.setDuration(duration);
    }

    /**
     * Sets the duration of the animations that are played when {@link #show(boolean)} and
     * {@link #hide(boolean)} are called.
     * @param duration the duration of the animation in milliseconds
     */
    public void setAnimationDuration(long duration){
        mShowAnimation.setDuration(duration);
        mHideAnimation.setDuration(duration);
    }

    void playShowAnimation() {
        mHideAnimation.cancel();
        startAnimation(mShowAnimation);
    }

    void playHideAnimation() {
        mShowAnimation.cancel();
        startAnimation(mHideAnimation);
    }

    /**
     * Checks whether LabelView is hidden
     *
     * @return true if LabelView is hidden, false otherwise
     */
    boolean isHidden() {
        return getVisibility() == INVISIBLE;
    }

    /**
     * Makes the LabelView appear and sets its visibility to {@link #VISIBLE}
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
     * Makes the LabelView to disappear and sets its visibility to {@link #INVISIBLE}
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

    public String getText(){
        return mImpl.getText();
    }

    /**
     * Sets the string value of the LabelView.
     */
    public void setText(CharSequence text){
        mImpl.setText(text);
    }

    /**
     * Sets the string value of the LabelView.
     */
    public void setText (char[] text, int start, int len){
        mImpl.setText(text, start, len);
    }

    /**
     * Sets the string value of the LabelView.
     */
    public void setText (CharSequence text, TextView.BufferType type){
        mImpl.setText(text, type);
    }

    /**
     * Sets the string value of the LabelView.
     *
     * @param resId Resource Id to the string
     */
    public void setText (int resId){
        mImpl.setText(resId);
    }

    /**
     * Sets the string value of the LabelView.
     *
     * @param resId Resource Id to the string
     */
    public void setText (int resId, TextView.BufferType type){
        mImpl.setText(resId, type);
    }

    /**
     * Set the default text size to the given value, interpreted as "scaled pixel" units.
     * This size is adjusted based on the current density and user font size preference.
     *
     * @param size The scaled pixel size.
     */
    public void setTextSize (float size){
        mImpl.setTextSize(size);
    }

    /**
     * Set the default text size to a given unit and value. See {@link TypedValue} for the
     * possible dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     */
    public void setTextSize (int unit, float size){
        mImpl.setTextSize(unit, size);
    }

    /**
     * Sets the text color, size, style, hint color, and highlight color from the specified
     * TextAppearance resource.
     */
    public void setTextAppearance (Context context, int resId){
        mImpl.setTextAppearance(context, resId);
    }

    /**
     * Sets the text color.
     */
    public void setTextColor (ColorStateList colors){
        mImpl.setTextColor(colors);
    }

    /**
     * Sets the text color for all the states (normal, selected, focused) to be this color.
     */
    public void setTextColor (int color){
        mImpl.setTextColor(color);
    }

    /**
     * Makes the TextView exactly this many ems wide
     */
    public void setEms (int ems){
        mImpl.setEms(ems);
    }

    /**
     * Makes the TextView at most this many ems wide
     */
    public void setMaxEms(int maxEms){
        mImpl.setMaxEms(maxEms);
    }

    /**
     * Makes the TextView at least this many ems wide
     */
    public void setMinEms(int minEms){
        mImpl.setMinEms(minEms);
    }

    /**
     * Causes words in the text that are longer than the view is wide
     * to be ellipsized instead of broken in the middle. Use <code>null</code>
     * to turn off ellipsizing.
     *
     */
    public void setEllipsize (TextUtils.TruncateAt where){
        mImpl.setEllipsize(where);
    }

    @Override
    public void setBackgroundColor(int color) {
        mImpl.setBackgroundColor(color);
    }

    @Override
    public void setBackground(Drawable background) {
        if(mImpl != null) {
            mImpl.setBackground(background);
        }
    }

    @Override
    public void setBackgroundResource(int resId) {
        mImpl.setBackgroundResource(resId);
    }

    /**
     * Updates the corner radius of the LabelView.
     * <p>
     * Setting this value with Label Type set to Text does not have any effect
     *
     * @param radius The radius in pixels of the corners of the rectangle shape
     */
    public void setRadius(float radius) {
        mImpl.setRadius(radius);
    }

    /**
     * Returns the corner radius of the CardView.
     *
     * @return Corner radius of the CardView
     */
    public float getRadius() {
        return mImpl.getRadius();
    }

    @Override
    public void setElevation(float elevation) {
        mImpl.setElevation(elevation);
    }

    @Override
    public float getElevation() {
        return mImpl.getElevation();
    }

    /**
     * Updates the backward compatible elevation of the LabelView.
     * <p>
     * Calling this method has no effect if device OS version is L or newer and
     * {@link #getUseCompatPadding()} is <code>false</code>.
     * <p>
     * Setting this value with {@link #getLabelType()} is {@link Type#TEXT} Text does not have any
     * effect
     *
     * @param maxElevation The backward compatible elevation in pixels.
     * @see #setElevation(float)
     * @see #getMaxElevation()
     */
    public void setMaxElevation(float maxElevation) {
        mImpl.setMaxElevation(maxElevation);
    }

    /**
     * Returns the backward compatible elevation of the LabelView.
     * <p>
     * Setting this value with {@link #getLabelType()} is {@link Type#TEXT} Text does not have any
     * effect
     *
     * @return Elevation of the CardView
     * @see #setMaxElevation(float)
     * @see #getElevation()
     */
    public float getMaxElevation() {
        return mImpl.getMaxElevation();
    }


    /**
     * Sets the padding between the Label's Card edges and the Text.
     * <p>
     * Setting this value with {@link #getLabelType()} is {@link Type#TEXT} Text does not have any
     * effect
     *
     * @param left   The left padding in pixels
     * @param top    The top padding in pixels
     * @param right  The right padding in pixels
     * @param bottom The bottom padding in pixels
     */
    public void setContentPadding(int left, int top, int right, int bottom) {
        mImpl.setContentPadding(left, top, right, bottom);
    }

    /**
     * LabelView adds additional padding to draw shadows on platforms before L.
     * <p>
     * This may cause Cards to have different sizes between L and before L.
     * As an alternative, you can set this flag to <code>true</code> and LabelView will add the same
     * padding values on platforms L and after.
     * <p>
     * Since setting this flag to true adds unnecessary gaps in the UI, default value is
     * <code>false</code>.
     * <p>
     * Setting this value with {@link #getLabelType()} is {@link Type#TEXT} Text does not have any
     * effect
     *
     * @param useCompatPadding True if LabelView should add padding for the shadows on platforms L
     *                         and above.
     */
    public void setUseCompatPadding(boolean useCompatPadding) {
        mImpl.setUseCompatPadding(useCompatPadding);
    }

    /**
     * Returns whether LabelView will add inner padding on platforms L and after.
     * <p>
     * Setting this value with {@link #getLabelType()} is {@link Type#TEXT} Text does not have any
     * effect
     *
     * @return True CardView adds inner padding on platforms L and after to have same dimensions
     * with platforms before L.
     */
    public boolean getUseCompatPadding(){
        return mImpl.getUseCompatPadding();
    }

    /**
     * On API 20 and before, LabelView does not clip the bounds of the Card for the rounded corners.
     * Instead, it adds padding to content so that it won't overlap with the rounded corners.
     * You can disable this behavior by setting this field to <code>false</code>.
     * <p>
     * Setting this value on API 21 and above does not have any effect unless you have enabled
     * compatibility padding.
     * <p>
     * Setting this value with {@link #getLabelType()} is {@link Type#TEXT} Text does not have any
     * effect
     *
     * @param preventCornerOverlap Whether CardView should add extra padding to content to avoid
     *                             overlaps with the CardView corners.
     * @see #setUseCompatPadding(boolean)
     */
    public void setPreventCornerOverlap(boolean preventCornerOverlap) {
        mImpl.setPreventCornerOverlap(preventCornerOverlap);
    }

    /**
     * Returns whether LabelView should add extra padding to content to avoid overlaps with rounded
     * corners on API versions 20 and below.
     * <p>
     * Setting this value with {@link #getLabelType()} is {@link Type#TEXT} Text does not have any
     * effect
     *
     * @return True if CardView prevents overlaps with rounded corners on platforms before L.
     *         Default value is <code>true</code>.
     */
    public boolean getPreventCornerOverlap(){
        return mImpl.getPreventCornerOverlap();
    }

    private void setLabelTypeInternal(Type type){
        Context context = getContext();
        TextView textView = new TextView(context);

        if(type == Type.TEXT) {
            addView(textView);
            mImpl = new LabelViewText(textView);
            mType = Type.TEXT;
        }else {
            Resources res = context.getResources();
            int cardPaddingY = res.getDimensionPixelSize(R.dimen.card_y_padding);
            int cardPaddingX = res.getDimensionPixelSize(R.dimen.card_x_padding);
            int cardElevation = res.getDimensionPixelSize(R.dimen.cardview_default_elevation);
            int cardRadius = res.getDimensionPixelSize(R.dimen.card_radius);

            CardView cardView = new CardView(context);
            cardView.addView(textView);
            cardView.setCardBackgroundColor(Color.WHITE);
            cardView.setCardElevation(cardElevation);
            cardView.setRadius(cardRadius);
            cardView.setContentPadding(cardPaddingX, cardPaddingY, cardPaddingX, cardPaddingY);
            cardView.setUseCompatPadding(true);

            addView(cardView);

            mImpl = new LabelViewCard(textView, cardView);
            mType = Type.CARD;
        }
    }

    public enum Type{
        /**
         * A CardView is used to surround a TextView is used for the LabelView
         */
        CARD,
        /**
         * A TextView is use for the LabelView
         */
        TEXT
    }
}
