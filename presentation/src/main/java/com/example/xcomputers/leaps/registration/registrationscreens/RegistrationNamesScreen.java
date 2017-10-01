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

import static com.example.xcomputers.leaps.registration.RegistrationContainerView.FIRST_NAME_KEY;
import static com.example.xcomputers.leaps.registration.RegistrationContainerView.LAST_NAME_KEY;

/**
 * Created by xComputers on 04/06/2017.
 */
@Layout(layoutId = R.layout.registration_names_view)
public class RegistrationNamesScreen extends BaseView<EmptyPresenter> implements IRegistrationInsideView {

    private Button cancelBtn;
    private Button nextBtn;
    private CustomEditText firstNameET;
    private CustomEditText lastNameET;
    private IRegistrationContainer container;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cancelBtn = (Button) view.findViewById(R.id.reg_names_cancel_btn);
        nextBtn = (Button) view.findViewById(R.id.reg_names_action_btn);
        firstNameET = (CustomEditText) view.findViewById(R.id.reg_names_first_name_et);
        lastNameET = (CustomEditText) view.findViewById(R.id.reg_names_last_name_et);
        checkArguments(savedInstanceState);
        nextBtn.setOnClickListener(v -> {
            if (firstNameET.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please provide a non empty first name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (lastNameET.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please provide a non empty last name", Toast.LENGTH_SHORT).show();
                return;
            }
            container.gatherData(firstNameET.getText().toString(), lastNameET.getText().toString());
        });

        cancelBtn.setOnClickListener(v -> {
            getActivity().finish();
        });
    }

    @Override
    public boolean onBack() {
       return container.onBack();

    }

    private void checkArguments(Bundle savedInstanceState){
        if (savedInstanceState == null && getArguments() != null) {
            if (getArguments().containsKey(FIRST_NAME_KEY)) {
                firstNameET.setText(getArguments().getString(FIRST_NAME_KEY));
            }
            if (getArguments().containsKey(LAST_NAME_KEY)) {
                lastNameET.setText(getArguments().getString(LAST_NAME_KEY));
            }
        } else if(savedInstanceState != null){
            if (savedInstanceState.containsKey(FIRST_NAME_KEY)) {
                firstNameET.setText(getArguments().getString(FIRST_NAME_KEY));
            }
            if (savedInstanceState.containsKey(LAST_NAME_KEY)) {
                lastNameET.setText(getArguments().getString(LAST_NAME_KEY));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!firstNameET.getText().toString().isEmpty()) {
            outState.putString(FIRST_NAME_KEY, firstNameET.getText().toString());
        }
        if (!lastNameET.getText().toString().isEmpty()) {
            outState.putString(LAST_NAME_KEY, lastNameET.getText().toString());
        }
    }

    @Override
    protected EmptyPresenter createPresenter() {
        return new EmptyPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
    }

    @Override
    public void setContainer(IRegistrationContainer container) {
        this.container = container;
    }
}