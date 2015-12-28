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

package com.wanderingcan.floatingactionmenu.internal;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.widget.TextView;

/**
 * The TextView within a CardView Implementation of the LabelView
 */
public class LabelViewCard extends LabelViewImpl {

    private CardView mCardView;

    public LabelViewCard(TextView textView, CardView cardView){
        mTextView = textView;
        mCardView = cardView;
    }

    @Override
    public void setBackgroundColor(int color) {
        mCardView.setCardBackgroundColor(color);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= 16){
            mCardView.setBackground(background);
        }else {
            mCardView.setBackgroundDrawable(background);
        }
    }

    @Override
    public void setBackgroundResource(int resId) {
        mCardView.setBackgroundResource(resId);
    }

    @Override
    public void setRadius(float radius) {
        mCardView.setRadius(radius);
    }

    @Override
    public float getRadius() {
        return mCardView.getRadius();
    }

    @Override
    public void setElevation(float elevation) {
        if (Build.VERSION.SDK_INT >= 21) {
            mCardView.setElevation(elevation);
        }else {
            mCardView.setCardElevation(elevation);
        }
    }

    @Override
    public float getElevation() {
        if (Build.VERSION.SDK_INT >= 21) {
           return mCardView.getElevation();
        }else {
           return mCardView.getCardElevation();
        }
    }

    @Override
    public void setMaxElevation(float maxElevation) {
        mCardView.setMaxCardElevation(maxElevation);
    }

    @Override
    public float getMaxElevation() {
        return mCardView.getMaxCardElevation();
    }

    @Override
    public void setContentPadding(int left, int top, int right, int bottom) {
        mCardView.setContentPadding(left, top, right, bottom);
    }

    @Override
    public void setUseCompatPadding(boolean useCompatPadding) {
        mCardView.setUseCompatPadding(useCompatPadding);
    }

    @Override
    public boolean getUseCompatPadding() {
        return mCardView.getUseCompatPadding();
    }

    @Override
    public void setPreventCornerOverlap(boolean preventCornerOverlap) {
        mCardView.setPreventCornerOverlap(preventCornerOverlap);
    }

    @Override
    public boolean getPreventCornerOverlap() {
        return mCardView.getPreventCornerOverlap();
    }
}
