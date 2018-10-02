package club.leaps.presentation.test;

import android.support.v4.app.Fragment;

import club.leaps.presentation.R;
import club.leaps.presentation.base.Layout;

/**
 * Created by Ivan on 9/2/2017.
 */
@Layout(layoutId = R.layout.test_following)
public class TestView extends Fragment {

   /*
   extends BaseView<TestPresenter>

   private TextView eventName;
    private TextView ownerName;
    private TextView eventDescription;
    private Button button;
    private Button userBtn;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventName =(TextView) view.findViewById(R.id.test_event_name);
        ownerName = (TextView) view.findViewById(R.id.test_event_owner);
        eventDescription = (TextView) view.findViewById(R.id.test_event_description);
        button = (Button) view.findViewById(R.id.button_test);
        userBtn = (Button) view.findViewById(R.id.user_button_test);
        button.setOnClickListener(v -> {
            showLoading();
            presenter.getFollowingEvent(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""), 1);
        });

        userBtn.setOnClickListener(v -> {
            showLoading();
            presenter.getFollowingUser(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Authorization", ""), 122);
        });

    }


    @Override
    protected TestPresenter createPresenter() {
        return new TestPresenter();
    }

    @Override
    protected void addSubscriptions(List<Subscription> subscriptions) {
        subscriptions.add(presenter.getFollowingObservable().subscribe(this::followingSuccess));
        subscriptions.add(presenter.getErrorFollowingObservable().subscribe(aVoid -> hideLoading()));
        subscriptions.add(presenter.getErrorFollowingObservable().subscribe(this::onError));
        subscriptions.add(presenter.getFollowingUserSubject().subscribe(this::userFollowingSuccess));
        subscriptions.add(presenter.getErrorFollowingUserSubject().subscribe(this::onError));
    }

    private void followingSuccess(RealEvent realEvent){
        hideLoading();
        eventName.setText(realEvent.attendees().length+"");
        ownerName.setText(realEvent.ownerName());
        eventDescription.setText(realEvent.description());
    }

    private void userFollowingSuccess(UserResponse userResponse){
        hideLoading();
        eventName.setText(userResponse.firstName());
        ownerName.setText(userResponse.lastName());
        eventDescription.setText(userResponse.isTrainer()+"");
    }


    private void onError(Throwable t){

        hideLoading();
        Toast.makeText(getContext(), getString(R.string.lbl_general_error), Toast.LENGTH_SHORT).show();
    }*/

}
