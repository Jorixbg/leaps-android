package com.example.xcomputers.leaps.payment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.adyen.core.PaymentRequest;
import com.adyen.core.interfaces.HttpResponseCallback;
import com.adyen.core.interfaces.PaymentDataCallback;
import com.adyen.core.interfaces.PaymentRequestListener;
import com.adyen.core.models.Payment;
import com.adyen.core.models.PaymentRequestResult;
import com.adyen.core.utils.AsyncHttpClient;
import com.example.xcomputers.leaps.MainActivity;
import com.example.xcomputers.leaps.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentManager {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String SETUP = "setup";
    private static final String VERIFY = "verify";

    // Add the URL for your server here; or you can use the demo server of Adyen: https://checkoutshopper-test.adyen.com/checkoutshopper/demoserver/
    private String merchantServerUrl = "https://checkoutshopper-test.adyen.com/checkoutshopper/demoserver/";

    // Add the api secret key for your server here; you can retrieve this key from customer area.
    private String merchantApiSecretKey = "0101368667EE5CD5932B441CFA249C976528B4C5984B84580387074C6045D235879B675B8DE45A25E4A146EAA8B66137F44F9BF6A5FF0F8AF210C15D5B0DBEE47CDCB5588C48224C6007";

    // Add the header key for merchant server api secret key here; e.g. "x-demo-server-api-key"
    private String merchantApiHeaderKeyForApiSecretKey = "x-demo-server-api-key";

    private PaymentRequest paymentRequest;
    private Context context;



    private final PaymentRequestListener paymentRequestListener = new PaymentRequestListener() {
        @Override
        public void onPaymentDataRequested(@NonNull PaymentRequest paymentRequest, @NonNull String token,
                                           @NonNull final PaymentDataCallback paymentDataCallback) {
            final Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json; charset=UTF-8");
            headers.put(merchantApiHeaderKeyForApiSecretKey, merchantApiSecretKey);

            AsyncHttpClient.post(merchantServerUrl + SETUP, headers, getSetupDataString(token), new HttpResponseCallback() {
                @Override
                public void onSuccess(final byte[] response) {
                    paymentDataCallback.completionWithPaymentData(response);
                }
                @Override
                public void onFailure(final Throwable e) {
                    Log.e(TAG, "HTTP Response problem: ", e);
                }
            });
        }

        @Override
        public void onPaymentResult(@NonNull PaymentRequest paymentRequest,
                                    @NonNull PaymentRequestResult paymentRequestResult) {
            if (paymentRequestResult.isProcessed() && (
                    paymentRequestResult.getPayment().getPaymentStatus() == Payment.PaymentStatus.AUTHORISED
                            || paymentRequestResult.getPayment().getPaymentStatus()
                            == Payment.PaymentStatus.RECEIVED)) {
//                verifyPayment(paymentRequestResult.getPayment());
//                Intent intent  = new Intent(context, SuccessActivity.class);
//                startActivity(intent);
//                finish();
            } else {
//                Intent intent  = new Intent(context, FailureActivity.class);
//                startActivity(intent);
//                finish();
            }
        }

        private String getSetupDataString(final String token) {
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("merchantAccount", "TestMerchant"); // Not required when communicating with merchant server
                jsonObject.put("shopperLocale", "NL");
                jsonObject.put("token", token);
                jsonObject.put("returnUrl", "example-shopping-app://");
                jsonObject.put("countryCode", "NL");
                final JSONObject amount = new JSONObject();
                amount.put("value", "17408");
                amount.put("currency", "USD");
                jsonObject.put("amount", amount);
                jsonObject.put("channel", "android");
                jsonObject.put("reference", "M+M Black dress & accessories");
                jsonObject.put("shopperReference", "example-customer@exampleprovider");
            } catch (final JSONException jsonException) {
                Log.e(TAG, "Setup failed", jsonException);
            }
            return jsonObject.toString();
        }
    };


    public PaymentRequestListener getPaymentListener(){
        return paymentRequestListener;
    }

}