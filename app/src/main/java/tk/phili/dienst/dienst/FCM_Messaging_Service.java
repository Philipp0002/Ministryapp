package tk.phili.dienst.dienst;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by fipsi on 15.10.2016.
 */

public class FCM_Messaging_Service extends FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getFrom().equals("/topics/main")){
            displayNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("content-text"));
        }
    }

    public void displayNotification(String s1, String s2){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.dienstapp_icon_nobckgrnd)
                        .setContentTitle(s1)
                        .setContentText(s2);


        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
