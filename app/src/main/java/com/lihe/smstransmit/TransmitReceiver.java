package com.lihe.smstransmit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lihe on 2016/12/16.
 */

public class TransmitReceiver extends BroadcastReceiver {
    private final String ACTION_SMS = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("noco", "TransmitReceiver.onReceive");
        if (!ACTION_SMS.equals(intent.getAction())) {
            return;
        }

        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        String receiveTime = "";
        String number = "";
        String content = "";
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                receiveTime = format.format(date);
                number = msg.getOriginatingAddress();
                content += msg.getDisplayMessageBody();
            }

            String message = "";
            String title_remove_always = MainActivity.getSettingNote(context, "title");
            if (title_remove_always.equals("always")) {
                message = content;
            } else {
                message = number+"\n"+
                        receiveTime+"\n" +
                        content;
            }
            Log.i("noco",message);

            String transmitNunmber = MainActivity.getSettingNote(context,"number");
            if (transmitNunmber.equals("")){//第一次安装软件时，在没有设置转发号码的时候不转发

            }else {//添加了号码
                String email_always = MainActivity.getSettingNote(context, "email");
                if (email_always.equals("always")) {
                    transmitMessgeByEmail(number + " " + receiveTime, content);
                } else {
                    if (!transmitMessageTo(transmitNunmber, message)) {
                        transmitMessgeByEmail(number + " " + receiveTime, content);
                    }
                }
            }
        }
    }

    public boolean transmitMessageTo(String phoneNumber,String message){//转发短信
        Log.d("noco", "transmitMessageTo");
        SmsManager manager = SmsManager.getDefault();
        /** 切分短信，每七十个汉字切一个，短信长度限制不足七十就只有一个：返回的是字符串的List集合*/
        ArrayList<String> texts =manager.divideMessage(message);//这个必须有
        Log.d("noco", "texts count : " + texts.size());

        if (texts.size() > 1) {
            String temp_text = texts.get(0);
            String text = temp_text.substring(0, temp_text.length() - 3) + "...";
            manager.sendTextMessage(phoneNumber, null, text, null, null);
            return false;
        } else if ( texts.size() == 1){
            manager.sendTextMessage(phoneNumber, null, texts.get(0), null, null);
            return true;
        }

        return  false;
    }

    public void transmitMessgeByEmail(final String subject, final String message) {
        Log.d("noco", "transmitMessgeByEmail");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EmailUtils.sendEmail("liheemail@163.com", subject, message);
                } catch (Exception e) {
                    Log.e("noco", "Email Send Error!");
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
