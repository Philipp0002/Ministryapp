package tk.phili.dienst.dienst;

import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by fipsi on 15.10.2016.
 */

public class FCM_Instance_ID_Service extends FirebaseInstanceIdService{

    final String tockenPreferenceKey = "fcm_tocken";
    final String topicName = "main";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        SharedPreferences sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(tockenPreferenceKey, FirebaseInstanceId.getInstance().getToken()).apply();

        FirebaseMessaging.getInstance().subscribeToTopic(topicName);
    }
}
