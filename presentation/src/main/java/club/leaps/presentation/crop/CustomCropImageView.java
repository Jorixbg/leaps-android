package club.leaps.presentation.crop;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Created by Ivan on 10/20/2017.
 */

public class CustomCropImageView extends CropImageView {

    public CustomCropImageView(Context context) {
        super(context);
    }

    public CustomCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        setImageBitmap(null);
        return super.onSaveInstanceState();
    }
}
