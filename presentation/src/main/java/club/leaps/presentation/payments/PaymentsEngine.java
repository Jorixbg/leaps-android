package club.leaps.presentation.payments;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.braintreepayments.api.dropin.DropInRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;


public class PaymentsEngine {

    public static String clientToken;

    public static void initToken(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://your-server/client_token", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                clientToken = clientToken;
            }
        });
    }

}
