package club.leaps.presentation.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import club.leaps.presentation.R;


/**
 * Created by xComputers on 18/06/2017.
 */

public class CustomWhiteButton extends android.support.v7.widget.AppCompatButton {


    public CustomWhiteButton(Context context) {
        super(context);
        init();
    }

    public CustomWhiteButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomWhiteButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init(){

        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round_white_button_shape));
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Typoforge Studio - Cervo-Medium.otf"));
        setTextColor(ContextCompat.getColor(getContext(), R.color.primaryBlue));
    }
}
