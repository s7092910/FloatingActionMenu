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
import android.widget.TextView;

/**
 * The TextView Implementation of the LabelView
 */
public class LabelViewText extends LabelViewImpl {

    public LabelViewText(TextView textView){
        mTextView = textView;
    }

    @Override
    public void setBackgroundColor(int color) {
        mTextView.setBackgroundColor(color);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= 16){
            mTextView.setBackground(background);
        }else {
            mTextView.setBackgroundDrawable(background);
        }
    }

    @Override
    public void setBackgroundResource(int resId) {
        mTextView.setBackgroundResource(resId);
    }

    @Override
    public void setRadius(float radius) {
        //Not Implemented
    }

    @Override
    public float getRadius() {
        //Not Implemented
        return 0;
    }

    @Override
    public void setElevation(float elevation) {
        if (Build.VERSION.SDK_INT >= 21){
            mTextView.setElevation(elevation);
        }
    }

    @Override
    public float getElevation() {
        if (Build.VERSION.SDK_INT >= 21){
            return mTextView.getElevation();
        }
        return 0;
    }

    @Override
    public void setMaxElevation(float maxElevation) {
        //Not Implemented
    }

    @Override
    public float getMaxElevation() {
        //Not Implemented
        return 0;
    }

    @Override
    public void setContentPadding(int left, int top, int right, int bottom) {
        //Not Implemented
    }

    @Override
    public void setUseCompatPadding(boolean useCompatPadding) {
        //Not Implemented
    }

    @Override
    public boolean getUseCompatPadding() {
        //Not Implemented
        return false;
    }

    @Override
    public void setPreventCornerOverlap(boolean preventCornerOverlap) {
        //Not Implemented
    }

    @Override
    public boolean getPreventCornerOverlap() {
        //Not Implemented
        return false;
    }
}
