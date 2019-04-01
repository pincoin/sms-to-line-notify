package com.hfad.broadcastsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class Receiver extends BroadcastReceiver {
    private static final String TAG = "Receiver";

    private static final String KOOKMIN = "16449999";
    private static final String NONGHUP = "15882100";
    private static final String SHINHAN = "15778000";
    private static final String WOORI = "15885000";

    @Override
    public void onReceive(Context context, Intent intent) {
        LineNotifyAsyncTask asyncTask = new LineNotifyAsyncTask();

        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                Object[] messages = (Object[]) bundle.get("pdus");

                if (messages != null) {
                    SmsMessage[] smsMessage = new SmsMessage[messages.length];

                    for (int i = 0; i < messages.length; i++) {
                        smsMessage[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
                    }

                    String phone = smsMessage[0].getOriginatingAddress();

                    if (phone != null) {
                        switch (phone) {
                            case KOOKMIN:
                            case NONGHUP:
                            case SHINHAN:
                            case WOORI:
                                phone = "은행";
                                asyncTask.execute(phone + " " + smsMessage[0].getMessageBody());
                        }
                    }
                }
            }
        } else if ("android.intent.action.BATTERY_LOW".equals(intent.getAction())) {
            asyncTask.execute("배터리 부족");
        } else if ("android.intent.action.BATTERY_OKAY".equals(intent.getAction())) {
            asyncTask.execute("배터리 정상화");
        } else if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent intentMainActivity = new Intent(context, MainActivity.class);
            intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentMainActivity);

            asyncTask.execute("부팅 완료");
        }
    }
}
