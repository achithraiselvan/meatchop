package com.meatchop.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.meatchop.R;
import com.meatchop.activities.HomeScreenActivity;
import com.meatchop.activities.MainActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class TMCMessagingService extends FirebaseMessagingService {

    String TAG = "TMCMessService";

    //this method is Called when a notification is received.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //this is the general configuration for recieving the notification
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            Uri imageuri = remoteMessage.getNotification().getImageUrl();
            String channelId = "Notification"; NotificationCompat.Builder notificationBuilder = null;
            Map<String, String> map = remoteMessage.getData();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("shownotificationbar", true);
            String tmcSubctgykey; String tmcSubctgyname;
            String tmcctgyname;
            if ((map != null) && (map.size() >= 0)) {
                intent.putExtra("notificationclicked", true);
                tmcSubctgykey = map.get("tmcsubctgykey");
                tmcSubctgyname = map.get("tmcSubctgyname");
                tmcctgyname = map.get("tmcctgyname");
                if ((tmcSubctgykey != null) && !(TextUtils.isEmpty(tmcSubctgykey))) {
                    intent.putExtra("tmcSubctgykey", tmcSubctgykey);
                }
                if ((tmcSubctgyname != null) && !(TextUtils.isEmpty(tmcSubctgyname))) {
                    intent.putExtra("tmcSubctgyname", tmcSubctgyname);
                }
                if ((tmcctgyname != null) && !(TextUtils.isEmpty(tmcctgyname))) {
                    intent.putExtra("tmcctgyname", tmcctgyname);
                }

            }

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            if (imageuri == null) {
                String title = remoteMessage.getNotification().getTitle();
                String contenttext = remoteMessage.getNotification().getBody();
                notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.mipmap.ic_tmc_notification)
                                .setContentTitle(remoteMessage.getNotification().getTitle())
                                .setContentText(remoteMessage.getNotification().getBody())
                                .setSound(defaultSoundUri)
                                .setColor(ContextCompat.getColor(this, R.color.meatchopbg_red))
                                .setPriority(Notification.PRIORITY_MAX)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setAutoCancel(true)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                        R.mipmap.notification_largeicon))
                                .setContentIntent(pendingIntent);
                intent.putExtra("notificationtitle", title);
                intent.putExtra("notificationtext", contenttext);
            } else {
                String imageUrl = imageuri.toString();
                Bitmap bitmap = getBitmapfromUrl(imageUrl);
                //Setting the intent to open any activty from our appliction when user clicked the notification
                notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.mipmap.ic_tmc_notification)
                                .setContentTitle(remoteMessage.getNotification().getTitle())
                                .setContentText(remoteMessage.getNotification().getBody())
                                .setSound(defaultSoundUri)
                                .setColor(ContextCompat.getColor(this, R.color.meatchopbg_red))
                                .setPriority(Notification.PRIORITY_MAX)
                                .setAutoCancel(true)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setStyle(new NotificationCompat.BigPictureStyle()
                                        .bigPicture(bitmap)
                                        .bigLargeIcon(BitmapFactory.decodeResource(getResources(),
                                                R.mipmap.icon))
                                )
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                        R.mipmap.notification_largeicon))
                                .setContentIntent(pendingIntent);
            }

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
            //special notification connfiguration for the oreo device
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Notification for device above Nought", NotificationManager.IMPORTANCE_HIGH);
                channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

    }

}
