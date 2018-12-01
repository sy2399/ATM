package ajou.hci.atm.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ajou.hci.atm.data.NOTIFICATIONDBHelper;
import ajou.hci.atm.model.NotifyVO;

/**
 * Created by imsoyeong on 2018. 4. 29..
 */

public class ShowNotificationListenerService extends NotificationListenerService {
    private static final String TAG = "nListenerService";
    public static DatabaseReference Ajou_DB;
    static FirebaseUser user = null;

    NOTIFICATIONDBHelper notificationdbHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        //Log.i(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i(TAG, "onStartCommand()");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i(TAG, "onDestroy()");
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //Log.i(TAG, "onTaskRemoved()");

        // workaround for kitkat: set an alarm service to trigger service again
        Intent intent = new Intent(getApplicationContext(), ShowNotificationListenerService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        super.onTaskRemoved(rootIntent);
        //do something you want
        //stop service
        this.stopSelf();

    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //Log.i(TAG, "onNotificationPosted()");

//        Log.i("NotificationListener", " onNotificationPosted() - " + sbn.toString());
//        Log.i("NotificationListener", " PackageName:" + sbn.getPackageName());
//        Log.i("NotificationListener", " PostTime:" + sbn.getPostTime());
//        Log.i("NotificationListener", " PostTime_getTime:" + getTimeStr());
        notificationdbHelper = new NOTIFICATIONDBHelper(getApplicationContext(), "NOTIFICATION.db", null, 1);
        try {
            Notification notificatin = sbn.getNotification();
            Bundle extras = notificatin.extras;
            String title = extras.getString(Notification.EXTRA_TITLE);
            int smallIconRes = extras.getInt(Notification.EXTRA_SMALL_ICON);
            Bitmap largeIcon = ((Bitmap) extras.getParcelable(Notification.EXTRA_LARGE_ICON));
            CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
            CharSequence subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);


            NotifyVO nvo = new NotifyVO();
            java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            if(title == null || text ==null ){

            }else{

                if(sbn.getPackageName().contains("messaging") || sbn.getPackageName().contains("mms")){


                    String regex = "[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z]+"; // "[가-힣]+"
                    Pattern pattern = Pattern.compile(regex);

                    String contents = charSequenceToString(text);
                    Matcher matcher = pattern.matcher(contents);

                    String msg ="";
                    while (matcher.find()) { // 일치하는 모든 것을 찾아낸다.

                        msg += " " + matcher.group();
                    }
                    nvo.setTime(getTimeStr());
                    nvo.setPackageName(sbn.getPackageName());
                    nvo.setpName(getAppNameFromPackage(sbn.getPackageName(), getApplicationContext()));
                    nvo.setPostTime(dateFormat.format(sbn.getPostTime()) );
                    nvo.setTitle(title);
                    nvo.setText(msg);

                    //nvo = new NotifyVO(getTimeStr(), sbn.getPackageName(), dateFormat.format(sbn.getPostTime()) , title, msg);
                }else{
                    nvo.setTime(getTimeStr());
                    nvo.setPackageName(sbn.getPackageName());
                    nvo.setpName(getAppNameFromPackage(sbn.getPackageName(), getApplicationContext()));

                    nvo.setPostTime(dateFormat.format(sbn.getPostTime()) );
                    nvo.setTitle(title);
                    nvo.setText(charSequenceToString(text));
                   // nvo = new NotifyVO(getTimeStr(), sbn.getPackageName(), dateFormat.format(sbn.getPostTime()) , title, charSequenceToString(text));

                }
            }
            Ajou_DB = FirebaseDatabase.getInstance().getReference();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            user = mAuth.getCurrentUser();
           if(nvo.getPackageName() !=  null) {
               notificationdbHelper.insert(user.getUid(),getDateStr(), nvo);

           }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String getAppNameFromPackage(String packageName, Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> pkgAppsList = context.getPackageManager().queryIntentActivities(mainIntent, 0);

        for (ResolveInfo app : pkgAppsList) {
            if (app.activityInfo.packageName.equals(packageName)) {
                return app.activityInfo.loadLabel(context.getPackageManager()).toString();
            }
        }
        String[] pNames = packageName.split("\\.");
        String result = "";
        for(int i=0;i<pNames.length;i++){
            result = pNames[i];
        }
        return result;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Log.i(TAG, " onNotificationRemoved() - " + sbn.toString());
    }
    public static String getDateStr(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());

        return sdfNow.format(date);
    }
    public String getTimeStr(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
        return sdfNow.format(date);
    }
    public String charSequenceToString(CharSequence cs){
        final StringBuilder sb = new StringBuilder(cs.length());
        sb.append(cs);
        return sb.toString();
    }
}
