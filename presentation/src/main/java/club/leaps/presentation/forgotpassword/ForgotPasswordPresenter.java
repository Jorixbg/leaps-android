package club.leaps.presentation.forgotpassword;

import club.leaps.presentation.base.BasePresenter;
import club.leaps.networking.base.ApiResponse;
import club.leaps.networking.forgotpass.ForgotPasswordService;
import club.leaps.presentation.base.BasePresenter;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by xComputers on 03/06/2017.
 */

class ForgotPasswordPresenter extends BasePresenter {

    private ForgotPasswordService service = new ForgotPasswordService();
    private Subject<Void, Void> forgotPassSubject;
    private Subject<String, String> dataValidationSubject;
    private Subject<Void, Void> generalErrorSubject;

    ForgotPasswordPresenter() {
        forgotPassSubject = PublishSubject.create();
        generalErrorSubject = PublishSubject.create();
        dataValidationSubject = PublishSubject.create();
    }

    void sendPasswordEmail(String email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            service.sendForgotPassEmail(email).subscribe(this::onSuccess, this::onError);
        }else {
            dataValidationSubject.onNext("Please provide a valid email address!");
        }
    }

    private void onSuccess(ApiResponse response) {
        forgotPassSubject.onNext(null);
    }

    private void onError(Throwable t) {
        errorHandler().call(t);
        generalErrorSubject.onNext(null);
    }

    Observable<Void> getForgotPassObservable() {

        return forgotPassSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    Observable<String> getValidationObservable() {
        return dataValidationSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }

    Observable<Void> getGeneralErrorObservable(){
        return generalErrorSubject.asObservable().observeOn(AndroidSchedulers.mainThread());
    }
}
