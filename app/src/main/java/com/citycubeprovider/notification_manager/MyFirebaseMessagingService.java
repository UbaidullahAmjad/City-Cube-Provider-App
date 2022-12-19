package com.citycubeprovider.notification_manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.citycubeprovider.R;
import com.citycubeprovider.activities.HomeActivity;
import com.citycubeprovider.retrofit.Constant;
import com.citycubeprovider.utility.SessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private NotificationChannel mChannel;
    private NotificationManager notifManager;
    JSONObject object;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "Notification_Data:" + remoteMessage.getData());


        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            try {
                String title = "", key = "", status = "";

                object = new JSONObject(data.get("message"));
                status = object.getString("status");
                if (status.equals("Pending")) {
                    // title =   object.getString("title");
                    title = getString(R.string.new_booking_request);
                    key = object.getString("key");
                    Intent intent1 = new Intent("Job_Status_Action1");
                    Log.e("SendData=====", object.toString());
                    intent1.putExtra("object", object.toString());
                    sendBroadcast(intent1);

                }

                else if (status.equals("chat")) {
                    key = object.getString("message");
                    title = getString(R.string.new_chat_msg_user);
                    Intent intent1 = new Intent("Job_Status_Action");
                    intent1.putExtra("request_id", object.getString("request_id"));
                    intent1.putExtra("status", status);
                    sendBroadcast(intent1);
                }

             /*   else if (status.equals("Cancel_by_user")) {
                    key = object.getString("key");
                    title = getString(R.string.booking_cancel_by_user);
                    Intent intent1 = new Intent("Job_Status_Action1");
                    intent1.putExtra("request_id", object.getString("request_id"));
                    intent1.putExtra("status", status);
                    intent1.putExtra("object", object.toString());
                    sendBroadcast(intent1);
                }*/

                if (!SessionManager.readString(getApplicationContext(), Constant.USER_INFO, "").equals("")){
                    wakeUpScreen();
                    displayCustomNotificationForOrders(status, title, key);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void displayCustomNotificationForOrders(String status, String title, String msg) {
        // SessionManager.writeString(getApplicationContext(),"provider_id",provider_id);
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder;
            Intent intent = null;
            intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent;
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                mChannel = new NotificationChannel
                        ("0", title, importance);
                mChannel.setDescription((String) msg);
                mChannel.enableVibration(true);
                mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),attributes);
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, "0");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentTitle(title)
                    .setSmallIcon(R.drawable.logo) // required
                    .setContentText(msg)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_RINGTONE));

            Notification notification = builder.build();
            notifManager.notify(0, notification);
        } else {
            Intent intent = null;
            intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.drawable.logo)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(msg));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(1251, notificationBuilder.build());
        }
    }


    private void wakeUpScreen() {
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();

        Log.e("screen on......", "" + isScreenOn);
        if (isScreenOn == false) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }


}
