package com.example.xcomputers.leaps.profile.becomeTrainer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;

import java.util.List;

import rx.Subscription;

/**
 * Created by xComputers on 23/07/2017.
 */
@Layout(layoutId = R.layout.become_trainer_phone_view)
public class BecomeTrainerPhoneNumber extends BaseView<EmptyPresenter> {


    private ImageView nextBtn;
    private EditText editText;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nextBtn = (ImageView) view.findViewById(R.id.next);
        editText = (EditText) view.findViewById(R.id.edit_text);
        nextBtn.setOnClickListener(v -> {
            if(editText.getText().toString().isEmpty()){
                Toast.makeText(getContext(), R.string.phone_number_error, Toast.LENGTH_SHORT).show();
            }else{
                ((BecomeTrainerActivity)getActivity()).setPhoneNumber(editText.getText().toString());
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
