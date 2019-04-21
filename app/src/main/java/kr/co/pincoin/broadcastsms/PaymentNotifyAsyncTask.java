package kr.co.pincoin.broadcastsms;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class PaymentNotifyAsyncTask extends AsyncTask<String, Void, Void> {
    private static final String TAG = "PaymentNotifyAsyncTask";

    @Override
    protected Void doInBackground(String... params) {
        String header = "Token " + BuildConfig.PAYMENT_NOTIFY_TOKEN;

        try {
            String account = URLEncoder.encode(params[0], "UTF-8");
            String received = URLEncoder.encode(params[1], "UTF-8");
            String name = URLEncoder.encode(params[2], "UTF-8");
            String method = URLEncoder.encode(params[3], "UTF-8");
            String amount = URLEncoder.encode(params[4], "UTF-8");
            String balance = URLEncoder.encode(params[5], "UTF-8");

            URL url = new URL(BuildConfig.PAYMENT_NOTIFY_URL);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Authorization", header);

            String postParams = String.format("account=%s&received=%s&name=%s&method=%s&amount=%s&balance=%s",
                    account, received, name, method, amount, balance);

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            if (con.getResponseCode() != 200) {
                Log.e(TAG, "failed to send PAYMENT notify");
            }

            con.disconnect();
        } catch (Exception ex) {
            Log.e(TAG, "failed to connect PAYMENT notify");
            Log.e(TAG, ex.toString());
        }

        return null;
    }
}
