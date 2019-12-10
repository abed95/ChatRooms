package com.abedo.chatappphp.fcm;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.abedo.chatappphp.ChatActivity;
import com.abedo.chatappphp.R;
import com.abedo.chatappphp.models.Message;
import com.abedo.chatappphp.utils.Session;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * created by Abedo95 on 12/6/2019
 */
public class FirebaseService extends FirebaseMessagingService {

    private final static String TAG = "FirebaseService";
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e(TAG, "onMessageReceived: "+remoteMessage.getFrom());
        Log.e(TAG, "onMessageReceived: "+remoteMessage.getData().get("message"));

        /**
         * be sure that data size > 0
         */

        if (remoteMessage.getData().size() > 0) {
            // get values from data that sent from php by fcm
            Log.e("message content", Objects.requireNonNull(remoteMessage.getData().get("message")));
            String messageContent = remoteMessage.getData().get("message");
            String roomId = remoteMessage.getData().get("room_id");
            String userId = remoteMessage.getData().get("user_id");
            String username = remoteMessage.getData().get("username");
            String messageType = remoteMessage.getData().get("type");
            String timestamp = remoteMessage.getData().get("timestamp");
            // Create new message and assign value to it
            Message message = new Message();
            message.setContent(messageContent);
            message.setRoomId(roomId);
            message.setUserId(userId);
            message.setUsername(username);
            message.setType(messageType);
            message.setTimestamp(timestamp);
            Log.e(TAG, "onMessageReceived: "+message.toString() );
            //sendNotification(message);
            boolean isInBackground = isAppIsInBackground(this);
            Log.e(TAG, "On Create : "+isInBackground );

            // check if the sender of message is current user or not
            if (!(Integer.valueOf(userId) == Session.newInstance().getUser().id)) {                // check if app in background or not
                if (isAppIsInBackground(this)) {
                    Log.e(TAG, "onMessageReceived: "+isAppIsInBackground(this) );
                    // app is in background show notification to user
                    sendNotification(message);
                } else {
                    // app is forground and user see it now send broadcast to update chat
                    // you can send broadcast to do anything if you want !
                    Log.e(TAG, "onMessageReceived: "+"Enter Else" );
                    Intent intent = new Intent("UpdateChatActivity");
                    intent.putExtra("msgBroadcast", message);
                    sendBroadcast(intent);
                }
            }
        }
    }

    /**
     * Method check if app is in background or in foreground
     *
     * @param context this contentx
     * @return true if app is in background or false if app in foreground
     */

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                            Log.e(TAG, "isAppIsInBackground: IF "+isInBackground );
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = null;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
                Log.e(TAG, "isAppIsInBackground:ELSE "+isInBackground );

            }
        }

        return isInBackground;
    }


    /**
     * Method send notification
     *
     * @param message message object
     */
    private void sendNotification(Message message) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("msg", message);
        intent.putExtra("room_id", Integer.parseInt(message.getRoomId()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelId =message.getRoomId();

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.mipmap.ic_launcher_caht_php)
                .setContentTitle(message.getUsername())
                .setContentText(message.getContent())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        if (notificationManager != null) {
//            notificationManager.notify(0, notificationBuilder.build());
//        }

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}



