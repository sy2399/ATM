package ajou.hci.atm.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RestartService extends BroadcastReceiver {
    public static final String ACTION_RESTART_SERVICE = "Action.Restart";

    private NotificationManager mNotificationManager;
    public DatabaseReference Ajou_DB;
    public FirebaseAuth mAuth;
    public FirebaseUser user = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.i(ACTION_RESTART_SERVICE, "Service Stops, let's restart again.");

        Ajou_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            //Log.i("imsso", "Restart");
            Toast.makeText
            (context, "Collecting data...", Toast.LENGTH_SHORT).show();
            Intent activity = new Intent(context, ActivityService.class);
            context.startService(activity);

            Intent noti = new Intent(context, ShowNotificationListenerService.class);
            context.startService(noti);

        //}


//        if (name.equals("com.example.hci.dphci.ACTIONRESTART.ActivityRestartService")) {
//            Toast.makeText
//                    (context, "Collecting data...", Toast.LENGTH_SHORT).show();
//            context.startService(new Intent(context, ActivityService.class));
//        }
//        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
//        if(manager.isMusicActive())
//        {
//            // do something - or do it not
//        }
    }

    public String getDateStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return sdfNow.format(date);
    }

    public static boolean isScreenOn(Context context) {
        return ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isInteractive();
    }

}
