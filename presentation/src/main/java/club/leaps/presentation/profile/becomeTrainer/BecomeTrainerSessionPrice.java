package club.leaps.presentation.profile.becomeTrainer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import club.leaps.presentation.R;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.EmptyPresenter;
import club.leaps.presentation.base.Layout;

import java.util.List;

import rx.Subscription;

/**
 * Created by xComputers on 23/07/2017.
 */
@Layout(layoutId = R.layout.become_trainer_price_view)
public class BecomeTrainerSessionPrice extends BaseView<EmptyPresenter> {

    private ImageView nextBtn;
    private EditText editText;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nextBtn = (ImageView) view.findViewById(R.id.next);
        editText = (EditText) view.findViewById(R.id.edit_text);
        nextBtn.setOnClickListener(v -> {
            if(editText.getText().toString().isEmpty()){
                Toast.makeText(getContext(), R.string.session_price_error, Toast.LENGTH_SHORT).show();
            }else{
                int price;
                try {
                    price = Integer.valueOf(editText.getText().toString());
                }catch (Exception e){
                    Toast.makeText(getContext(), R.string.error_whole_number, Toast.LENGTH_SHORT).show();
                    return;
                }
                ((BecomeTrainerActivity)getActivity()).setSessionPrice(price);
            }
        });
    }
    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {

    }
}
