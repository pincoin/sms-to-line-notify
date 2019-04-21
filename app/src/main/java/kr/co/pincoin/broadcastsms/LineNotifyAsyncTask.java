package kr.co.pincoin.broadcastsms;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LineNotifyAsyncTask extends AsyncTask<String, Void, Void> {
    private static final String TAG = "LineNotifyAsyncTask";

    @Override
    protected Void doInBackground(String... params) {
        String header = "Bearer " + BuildConfig.LINE_NOTIFY_TOKEN;

        try {
            String text = URLEncoder.encode(params[0], "UTF-8");
            URL url = new URL("https://notify-api.line.me/api/notify");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Authorization", header);

            String postParams = "message=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            if (con.getResponseCode() != 200) {
                Log.e(TAG, "failed to send LINE notify");
            }

            con.disconnect();
        } catch (Exception ex) {
            Log.e(TAG, "failed to connect LINE notify");
            Log.e(TAG, ex.toString());
        }

        return null;
    }
}
