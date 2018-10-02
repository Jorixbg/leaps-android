package club.leaps.presentation.test;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import club.leaps.presentation.MainActivity;
import club.leaps.presentation.R;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by IvanGachmov on 12/6/2017.
 */

public class FirebaseMessageService extends com.google.firebase.messaging.FirebaseMessagingService {

    private String user_id;
    private String user_name;
    private String user_image;
    private String text;
    private String click_action;
    private String seen;
    final static String GROUP_KEY_NOTIFY = "group_key_notify";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        String notification_title = remoteMessage.getNotification().getTitle();
//        String notification_message = remoteMessage.getNotification().getBody();
//        String click_action = remoteMessage.getNotification().getClickAction();
//        String from_user_id = remoteMessage.getData().get("from_user_id");


      /*  NotificationCompat.Builder builder1 =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("New Message")
                        .setContentText("You have a new message from Kassidy")
                        .setGroup(GROUP_KEY_NOTIFY);

        NotificationCompat.Builder builder2 =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("New Message")
                        .setContentText("You have a new message from Caitlyn")
                        .setGroup(GROUP_KEY_NOTIFY);

        NotificationCompat.Builder builder3 =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("New Message")
                        .setContentText("You have a new message from Jason")
                        .setGroup(GROUP_KEY_NOTIFY);

        int notificationId1 = 101;
        int notificationId2 = 102;
        int notificationId3 = 103;



        notifyMgr.notify(notificationId1, builder1.build());
        notifyMgr.notify(notificationId2, builder2.build());
        notifyMgr.notify(notificationId3, builder3.build());



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification_title)
                .setContentText(notification_message);*/


        Intent resultIntent = new Intent(click_action);
        if(user_id!=null) {
            resultIntent.putExtra("userId", user_id);
            resultIntent.putExtra("userFullName", user_name);
            resultIntent.putExtra("userImage", user_image);
        }

        Log.e("Comeon",user_id+"");


        handleIntent(resultIntent);



        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Sets an ID for the notification
        int mNotificationId = (int) System.currentTimeMillis();

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        // mNotifyMgr.notify(mNotificationId, newMessageNotification);


    }

    public void handleIntent(Intent intent) {

        Bundle bundle = intent.getExtras();
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        user_id = bundle.getString("userid");
        user_name = bundle.getString("user_name");
        user_image = bundle.getString("userimage");
        text = bundle.getString("body");
        click_action = bundle.getString("click_action");
        seen = bundle.getString("seenuser");

        Log.e("Comeon",user_id+"");
        Log.e("Comeon",user_name+"");
        Log.e("Comeon",user_image+"");
        Log.e("Comeon",text+"");
        Log.e("Comeon",seen+"");

        if(!MainActivity.isInApp()) {
            Intent resultIntent = new Intent(click_action);
            if (user_id != null) {
                resultIntent.putExtra("userid", user_id);
                resultIntent.putExtra("user_name", user_name);
                resultIntent.putExtra("userimage", user_image);
                resultIntent.putExtra("seenuser", seen);
            }
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            int notificationId0 = 100;
            NotificationCompat.Builder builderSummary =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(user_name)
                            .setContentText(text)
                            .setGroup(GROUP_KEY_NOTIFY)
                            .setSound(uri)
                            .setGroupSummary(true)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

            NotificationManager notifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notifyMgr.notify(notificationId0, builderSummary.build());
        }

    }
}
