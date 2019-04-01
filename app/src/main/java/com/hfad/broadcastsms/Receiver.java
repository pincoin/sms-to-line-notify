package com.hfad.broadcastsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    String pattern;
                    Pattern r;
                    Matcher m;

                    if (phone != null) {
                        switch (phone) {
                            case KOOKMIN:
                                pattern = "\\[KB](.*)\\s.*\\s(.*)\\s(.*)\\s(.*)\\s잔액(.*)";
                                r = Pattern.compile(pattern);
                                m = r.matcher(smsMessage[0].getMessageBody());

                                if (m.find()) {
                                    String message = String.format("[국민]\n일시: %s\n이름: %s\n입출: %s\n금액: %s\n잔액: %s",
                                            m.group(1),
                                            m.group(2),
                                            m.group(3),
                                            m.group(4),
                                            m.group(5));
                                    asyncTask.execute(message);
                                } else {
                                    asyncTask.execute(smsMessage[0].getMessageBody().replace("[Web발신]", ""));
                                }
                                break;
                            case NONGHUP:
                                pattern = "농협 (\\D*)([,\\d]+)원\\s(\\d\\d/\\d\\d \\d\\d:\\d\\d) [\\d-*]+ (.*) 잔액(.*)원";
                                r = Pattern.compile(pattern);
                                m = r.matcher(smsMessage[0].getMessageBody());

                                if (m.find()) {
                                    String message = String.format("[농협]\n일시: %s\n이름: %s\n입출: %s\n금액: %s\n잔액: %s",
                                            m.group(3),
                                            m.group(4),
                                            m.group(1),
                                            m.group(2),
                                            m.group(5));
                                    asyncTask.execute(message);
                                } else {
                                    asyncTask.execute(smsMessage[0].getMessageBody().replace("[Web발신]", ""));
                                }
                                break;
                            case SHINHAN:
                                pattern = "신한(.*)\\s.*\\s(.*)[ ]+(.*)\\s잔액[ ]+(.*)\\s+(.*)";
                                r = Pattern.compile(pattern);
                                m = r.matcher(smsMessage[0].getMessageBody());

                                if (m.find()) {
                                    String message = String.format("[신한]\n일시: %s\n이름: %s\n입출: %s\n금액: %s\n잔액: %s",
                                            m.group(1),
                                            m.group(5),
                                            m.group(2),
                                            m.group(3),
                                            m.group(4));
                                    asyncTask.execute(message);
                                } else {
                                    asyncTask.execute(smsMessage[0].getMessageBody().replace("[Web발신]", ""));
                                }
                                break;
                            case WOORI:
                                pattern = "우리 (.*)\\s.*\\s(.*) (.*)원\\s(.*)";
                                r = Pattern.compile(pattern);
                                m = r.matcher(smsMessage[0].getMessageBody());

                                if (m.find()) {
                                    String message = String.format("[우리]\n일시: %s\n이름: %s\n입출: %s\n금액: %s",
                                            m.group(1),
                                            m.group(4),
                                            m.group(2),
                                            m.group(3));
                                    asyncTask.execute(message);
                                } else {
                                    asyncTask.execute(smsMessage[0].getMessageBody().replace("[Web발신]", ""));
                                }
                                break;
                            default:
                                asyncTask.execute(smsMessage[0].getMessageBody().replace("[Web발신]", ""));
                                break;
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
