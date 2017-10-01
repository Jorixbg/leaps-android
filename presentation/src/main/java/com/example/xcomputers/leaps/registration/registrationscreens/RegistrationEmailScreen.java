package com.example.xcomputers.leaps.registration.registrationscreens;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.xcomputers.leaps.R;
import com.example.xcomputers.leaps.base.BaseView;
import com.example.xcomputers.leaps.base.EmptyPresenter;
import com.example.xcomputers.leaps.base.Layout;
import com.example.xcomputers.leaps.registration.IRegistrationContainer;
import com.example.xcomputers.leaps.registration.IRegistrationInsideView;
import com.example.xcomputers.leaps.utils.CustomEditText;

import java.util.List;

import rx.Subscription;

import static com.example.xcomputers.leaps.registration.RegistrationContainerView.EMAIL_ADDRESS_KEY;


/**
 * Created by xComputers on 04/06/2017.
 */
@Layout(layoutId = R.layout.registration_email_view)
public class RegistrationEmailScreen extends BaseView<EmptyPresenter>  implements IRegistrationInsideView {

    private Button cancelBtn;
    private Button nextBtn;
    private CustomEditText emailET;
    private IRegistrationContainer container;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelBtn = (Button) view.findViewById(R.id.reg_email_cancel_btn);
        nextBtn = (Button) view.findViewById(R.id.reg_email_action_btn);
        emailET = (CustomEditText) view.findViewById(R.id.reg_email_et);
        if(savedInstanceState == null && getArguments() != null){
            emailET.setText(getArguments().getString(EMAIL_ADDRESS_KEY));
        } else if(savedInstanceState != null && savedInstanceState.containsKey(EMAIL_ADDRESS_KEY)){
            emailET.setText(savedInstanceState.getString(EMAIL_ADDRESS_KEY));
        }
        nextBtn.setOnClickListener(v -> {
            if(emailET.getText().toString().isEmpty()){
                Toast.makeText(getContext(), "Please provide a non empty email address", Toast.LENGTH_SHORT).show();
                return;
            } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString()).matches()){
                Toast.makeText(getContext(), "Please provide a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            container.gatherData(emailET.getText().toString());
        });

        cancelBtn.setOnClickListener(v -> {
            getActivity().finish();
        });
    }

    @Override
    public boolean onBack() {
        return container.onBack();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!emailET.getText().toString().isEmpty()){
            outState.putString(EMAIL_ADDRESS_KEY, emailET.getText().toString());
        }
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {}

    @Override
    public void setContainer(IRegistrationContainer container) {

        this.container = container;
    }
}