package com.example.xcomputers.leaps.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.example.xcomputers.leaps.R;

/**
 * Created by xComputers on 03/06/2017.
 */

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {


    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round_blue_edit_text));
        setHintTextColor(ContextCompat.getColor(getContext(),R.color.primaryBlue));
    }
}
