package com.example.xcomputers.leaps.utils;

import android.content.Context;
import android.support.annotation.Dimension;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.example.xcomputers.leaps.R;

/**
 * Created by xComputers on 29/06/2017.
 */

public class TagView extends android.support.v7.widget.AppCompatTextView {

    private boolean isSelected;

    public TagView(Context context) {
        super(context);
        init();
    }

    public TagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round_transparent_tag_shape));
        setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        setPadding(24, 8, 28, 8);
        setTextSize(Dimension.SP, 16);
        isSelected = false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    public void toggle() {
        this.isSelected = !isSelected;
        if (isSelected) {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round_white_tag_shape));
            setTextColor(ContextCompat.getColor(getContext(), R.color.primaryBlue));
        } else {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round_transparent_tag_shape));
            setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        }
    }

    public void toggle(@DrawableRes int unselectedTagShape, @DrawableRes int selectedTagShape) {
        this.isSelected = !isSelected;
        if (isSelected) {
            setBackground(ContextCompat.getDrawable(getContext(), selectedTagShape));
            setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        } else {
            setBackground(ContextCompat.getDrawable(getContext(), unselectedTagShape));
            setTextColor(ContextCompat.getColor(getContext(), R.color.primaryBlue));
        }
    }

    public boolean isSelected() {
        return isSelected;
    }
}
