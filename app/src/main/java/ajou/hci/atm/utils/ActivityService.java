package ajou.hci.atm.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ajou.hci.atm.data.ACTIVITYDBHelper;


//background 작업 실행시키는 class

public class ActivityService extends Service {

    private boolean authInProgress = false;
    private static final String AUTH_PENDING = "auth_state_pending";
    public DatabaseReference Ajou_DB;
    private FirebaseAuth mAuth;
    public static FirebaseUser user = null;

    TimerCounter tc;
    DBTimerCount dbTimerCount;
    Thread background;
    ACTIVITYDBHelper activitydbHelper;

    private static final String TAG = "ActivityService";

    public ActivityService(){
        Log.i(TAG, "create ActivityService class");
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind()");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");

        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Ajou_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        tc = new TimerCounter(getApplicationContext(), GoogleSignIn.getLastSignedInAccount(getApplicationContext()));
        dbTimerCount = new DBTimerCount(getApplicationContext(), GoogleSignIn.getLastSignedInAccount(getApplicationContext()));
        activitydbHelper = new ACTIVITYDBHelper(getApplicationContext(), "ACTIVITY.db", null, 1);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand()");


        //unregisterReceiver(new RestartService());

        tc.startTimer();
        dbTimerCount.startTimer();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        super.onDestroy();
        Intent intent = new Intent(getApplicationContext(), ActivityService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);

        this.stopSelf();

        tc.stopTimerTask();
        dbTimerCount.stopTimerTask();

    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "onTaskRemoved()");
        Intent intent = new Intent(getApplicationContext(), ActivityService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        super.onTaskRemoved(rootIntent);
        this.stopSelf();

    }

    public static String getDateStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return sdfNow.format(date);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "onLowMemory()");
    }

}


