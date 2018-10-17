package club.leaps.presentation.payments;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.adyen.checkout.core.CheckoutException;
import com.adyen.checkout.ui.CheckoutController;
import com.adyen.checkout.ui.CheckoutSetupParameters;
import com.adyen.checkout.ui.CheckoutSetupParametersHandler;

public class PaymentsEngine {

    public static void startPayment(Activity activity){
        CheckoutController.startPayment(activity, new CheckoutSetupParametersHandler() {
            @Override
            public void onRequestPaymentSession(@NonNull CheckoutSetupParameters checkoutSetupParameters) {
                // TODO: Forward to your own server and request the payment session from Adyen with the given CheckoutSetupParameters.
                String encodedPaymentSession = createPaymentSession(checkoutSetupParameters);
            }

            @Override
            public void onError(@NonNull CheckoutException checkoutException) {
                // TODO: Handle error.
            }
        });
    }

    private static String createPaymentSession(CheckoutSetupParameters checkoutSetupParameters) {
        return null; // TODO Call server endpoint
    }
}
