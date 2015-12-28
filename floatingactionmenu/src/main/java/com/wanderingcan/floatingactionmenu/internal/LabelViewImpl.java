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

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.TextView;


/**
 * A Implementation class that helps determine how a LabelView behaves based on the which Implementation
 * is used.
 */
public abstract class LabelViewImpl {

    TextView mTextView;

    public String getText(){
        return mTextView.getText().toString();
    }

    public void setText(CharSequence text){
        mTextView.setText(text);
    }

    public void setText (char[] text, int start, int len){
        mTextView.setText(text, start, len);
    }

    public void setText (CharSequence text, TextView.BufferType type){
        mTextView.setText(text, type);
    }

    public void setText (int resId){
        mTextView.setText(resId);
    }

    public void setText (int resId, TextView.BufferType type){
        mTextView.setText(resId, type);
    }

    public void setTextSize (float size){
        mTextView.setTextSize(size);
    }

    public void setTextSize (int unit, float size){
        mTextView.setTextSize(unit, size);
    }

    @SuppressWarnings("deprecation")
    public void setTextAppearance (Context context, int resId){
        mTextView.setTextAppearance(context, resId);
    }

    public void setTextColor (ColorStateList colors){
        mTextView.setTextColor(colors);
    }

    public void setTextColor (int color){
        mTextView.setTextColor(color);
    }

    public void setEms (int ems){
        mTextView.setEms(ems);
    }

    public void setMaxEms(int maxEms){
        mTextView.setMaxEms(maxEms);
    }

    public void setMinEms(int minEms){
        mTextView.setMinEms(minEms);
    }

    public void setEllipsize (TextUtils.TruncateAt where){
        mTextView.setEllipsize(where);
    }

    public abstract void setBackgroundColor(int color);

    public abstract void setBackground(Drawable background);

    public abstract void setBackgroundResource (int resId);

    public abstract void setRadius(float radius);

    public abstract float getRadius();

    public abstract void setElevation(float elevation);

    public abstract float getElevation();

    public abstract void setMaxElevation(float maxElevation);

    public abstract float getMaxElevation();

    public abstract void setContentPadding(int left, int top, int right, int bottom);

    public abstract void setUseCompatPadding(boolean useCompatPadding);

    public abstract boolean getUseCompatPadding();

    public abstract void setPreventCornerOverlap(boolean preventCornerOverlap);

    public abstract boolean getPreventCornerOverlap();

}
