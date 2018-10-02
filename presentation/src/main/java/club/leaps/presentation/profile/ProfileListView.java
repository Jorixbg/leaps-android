package club.leaps.presentation.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import club.leaps.presentation.MainActivity;
import club.leaps.presentation.R;
import club.leaps.presentation.User;
import club.leaps.presentation.base.BaseView;
import club.leaps.presentation.base.Layout;
import club.leaps.presentation.profile.becomeTrainer.BecomeTrainerActivity;
import club.leaps.presentation.profile.trainerProfile.TrainerProfileEditView;
import club.leaps.presentation.profile.trainerProfile.TrainerProfilePreview;
import club.leaps.presentation.profile.tutorial.TutorialActivity;
import club.leaps.presentation.profile.userProfile.UserEditProfileView;
import club.leaps.presentation.profile.userProfile.UserProfilePreview;
import club.leaps.presentation.utils.EntityHolder;
import club.leaps.presentation.utils.GlideInstance;

import java.util.List;

import club.leaps.presentation.profile.becomeTrainer.BecomeTrainerActivity;
import club.leaps.presentation.profile.trainerProfile.TrainerProfileEditView;
import club.leaps.presentation.profile.trainerProfile.TrainerProfilePreview;
import club.leaps.presentation.profile.tutorial.TutorialActivity;
import club.leaps.presentation.profile.userProfile.UserEditProfileView;
import club.leaps.presentation.profile.userProfile.UserProfilePreview;
import rx.Subscription;

/**
 * Created by xComputers on 21/07/2017.
 */
@Layout(layoutId = R.layout.profile_list_view)
public class ProfileListView extends BaseView<ProfileListPresenter> {

    private static final int BECOME_TRAINER_REQUEST = 4567;

    private ImageView profilePic;
    private TextView name;
    private TextView userName;
    private TextView settings;
    private ImageView editProfileBtn;
    private TextView viewTutorial;
    private TextView logOut;
    private Button becomeTrainerbtn;
    private RelativeLayout becomeTrainerRl;
    private IProfileTabContainer container;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        showLoading();
        presenter.getUser(User.getInstance().getUserId(), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));

    }

    private void init(){
        if(User.getInstance().isTrainer()){
            becomeTrainerRl.setVisibility(View.GONE);
        }

        GlideInstance.loadImageCircle(getContext(), EntityHolder.getInstance().getEntity().profileImageUrl(), profilePic, R.drawable.profile_placeholder);

        logOut.setOnClickListener(v -> {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().remove("Authorization").apply();
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().remove("UserId").apply();
            User.clear();
            EntityHolder.clear();
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        });
        name.setText(User.getInstance().getFirstName() + " " + User.getInstance().getLastName());
        userName.setText(User.getInstance().getUsername());

        settings.setOnClickListener(v -> openFragment(SettingsView.class, new Bundle(), true));

        viewTutorial.setOnClickListener(v -> startActivity(new Intent(getContext(), TutorialActivity.class)));
        becomeTrainerbtn.setOnClickListener(v -> getActivity().startActivityForResult(new Intent(getContext(), BecomeTrainerActivity.class), BECOME_TRAINER_REQUEST));
        Class clazz = User.getInstance().isTrainer() ? TrainerProfilePreview.class : UserProfilePreview.class;
        profilePic.setOnClickListener(v -> openFragment(clazz, new Bundle(), true));
        Class editClass = User.getInstance().isTrainer() ? TrainerProfileEditView.class : UserEditProfileView.class;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getParentFragment() instanceof IProfileTabContainer){
            container = ((IProfileTabContainer)getParentFragment());
        }
    }

    @Override
    protected ProfileListPresenter createPresenter() {
        return new ProfileListPresenter();
    }

    private void initViews(View view){
        profilePic = (ImageView) view.findViewById(R.id.profile_list_image);
        name = (TextView) view.findViewById(R.id.profile_list_name);
        userName = (TextView) view.findViewById(R.id.profile_list_username);
        settings = (TextView) view.findViewById(R.id.profile_list_settings);
        editProfileBtn = (ImageView) view.findViewById(R.id.profile_list_profile_btn);
        viewTutorial = (TextView) view.findViewById(R.id.profile_list_tutorial);
        logOut = (TextView) view.findViewById(R.id.profile_list_log_out);
        becomeTrainerbtn = (Button) view.findViewById(R.id.profile_listing_become_trainer_btn);
        becomeTrainerbtn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Typoforge Studio - Cervo-Medium.otf"));
        becomeTrainerRl = (RelativeLayout) view.findViewById(R.id.profile_list_become_trainer_rl);
    }



    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getUserObservable().subscribe(aVoid -> {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("UserId", String.valueOf(User.getInstance().getUserId())).apply();
            hideLoading();
            init();
        }));
        subscriptions.add(presenter.getErrorObservable().subscribe(aVoid -> {
            hideLoading();
        }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BECOME_TRAINER_REQUEST){
            showLoading();
            presenter.getUser(User.getInstance().getUserId(), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""));
        }
    }
}
