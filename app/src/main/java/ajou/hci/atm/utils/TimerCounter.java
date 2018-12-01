package ajou.hci.atm.utils;

import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ajou.hci.atm.R;
import ajou.hci.atm.activities.App;
import ajou.hci.atm.activities.MainActivity;
import ajou.hci.atm.data.ACTIVITYDBHelper;
import ajou.hci.atm.data.APPDBHelper;
import ajou.hci.atm.data.LOCATIONDBHelper;
import ajou.hci.atm.data.NOTIFICATIONDBHelper;
import ajou.hci.atm.location.LocationUtils;
import ajou.hci.atm.model.ActivityVO;
import ajou.hci.atm.model.AppItem;
import ajou.hci.atm.model.AppLogVO;
import ajou.hci.atm.model.LocationVO;
import ajou.hci.atm.model.PhoneVO;
import ajou.hci.atm.model.SumVO;

/**
 * Created by arvi on 12/12/17.
 */

public class TimerCounter {
    int counter;
    private Context context;
    public GoogleSignInAccount googleSignInAccount;

    public static DatabaseReference Ajou_DB;
    public FirebaseAuth mAuth;
    public static FirebaseUser user = null;

    ArrayList<ActivityVO> activityVOArrayList = new ArrayList<>();
    ArrayList<AppItem> appLogVOArrayList = new ArrayList<>();
    ArrayList<LocationVO> locationVOArrayList = new ArrayList<>();
    private ArrayList<SumVO> sumVOArrayList = new ArrayList<>();
    private ArrayList<PhoneVO> phoneVOS = new ArrayList<>();
    public static final String TAG = "TimerCount_Activity";

    private Timer timer, timer2;
    private TimerTask timerTask;
    private TimerTask timerTask2;
    UsageStatsManager mUsageStatsManager;

    ACTIVITYDBHelper activitydbHelper;
    LOCATIONDBHelper locationdbHelper;
    NOTIFICATIONDBHelper notificationdbHelper;
    APPDBHelper appdbHelper;

    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;

    public TimerCounter(Context context, GoogleSignInAccount googleSignInAccount) {
        this.context = context;
        this.googleSignInAccount = googleSignInAccount;

        Ajou_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        activitydbHelper = new ACTIVITYDBHelper(context, "ACTIVITY.db", null, 1);
        locationdbHelper = new LOCATIONDBHelper(context, "LOCATION.db", null, 1);
        notificationdbHelper = new NOTIFICATIONDBHelper(context, "NOTIFICATION.db", null, 1);

        mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE); //Context.USAGE_STATS_SERVICE
        appdbHelper = new APPDBHelper(context, "APP.db", null, 1);

        client = LocationServices.getFusedLocationProviderClient(context);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
    }


    public void Timer() {

    }


    void startTimer() {

        //this.counter = counter;

        // set a new timer
        timer = new Timer();

        // initialize the timer task's job
        //initializeTimerTask();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    insertAndReadData();
                    getAppTimeline(context);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        if(isWifiAvailable(context)){
            //Toast.makeText(context,"WIFI", Toast.LENGTH_LONG).show();
            timer.schedule(timerTask, 1000, 10000 * 6 * 10);

        }else if(isNetworkAvailable(context)){
            //Toast.makeText(context,"NETWORK", Toast.LENGTH_LONG).show();
            timer.schedule(timerTask, 1000, 10000 * 6 * 30);

        }


    }

    public  boolean isWifiAvailable (Context context)
    {
        boolean br = false;
        ConnectivityManager cm = null;
        NetworkInfo ni = null;

        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        br = ((null != ni) && (ni.isConnected()) && (ni.getType() == ConnectivityManager.TYPE_WIFI));

        return br;
    }
    public  boolean isNetworkAvailable (Context context)
    {
        boolean br = false;
        ConnectivityManager cm = null;
        NetworkInfo ni = null;

        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        br = ((null != ni) && (ni.isConnected()) && (ni.getType() == ConnectivityManager.TYPE_MOBILE));

        return br;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getAppTimeline(Context context) {
        //Log.i("AppTimerCount", "getAppTimeLine()");
        List<AppItem> items = new ArrayList<>();
        DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Ajou_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        AppLogVO logVO = appdbHelper.getLastVO(user.getUid(), getDateStr());
        int count = appdbHelper.getTotalCount(user.getUid(), getDateStr());

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();

        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        long startTime = cal.getTimeInMillis();

        if (mUsageStatsManager != null) {


            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = new Date(startTime);
            Date entDate = new Date(endTime);
            UsageEvents events = mUsageStatsManager.queryEvents(startTime, endTime);
            UsageEvents.Event event = new UsageEvents.Event();

            long duration = 0;

            ArrayList<AppItem> foregroundList = new ArrayList<>();
            ArrayList<AppLogVO> finalList = new ArrayList<>();

            while (events.hasNextEvent()) {
                events.getNextEvent(event);
                String packageName = event.getPackageName();
                AppItem item = new AppItem();
                AppLogVO appLogVO = new AppLogVO();

                String pName = getAppNameFromPackage(event.getPackageName(), context);
                String timeStamp;

                if (pName.equals("systemui") || pName.equals("ATM") || pName.equals("gms") || pName.equals("launcher") || pName.equals("android") || pName.equals("telecom")
                        || pName.equals("Settings") || pName.equals("설정") || pName.equals("packageinstaller") || pName.equals("spage") || pName.equals("cellbroadcastreceiver")
                        || pName.equals("wssyncmldm") || pName.equals("captiveportallogin") || pName.equals("cleanmaster")
                        || pName.equals("lool") || pName.equals("systemui") || pName.equals("incallui") || pName.equals("daemoapp") || pName.equals("daemonapp") || pName.equals("버즈 런처")
                        || pName.equals("home") || pName.equals("보안")) {
                    //launcher는 카운트용으로만
                } else {
                    if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        timeStamp = String.valueOf(mDateFormat.format(new Date(event.getTimeStamp())));
                        item.setmUsageTime(0);
                        appLogVO.setPackageName(pName);
                        appLogVO.setStime(timeStamp);
                        appLogVO.setTotal(0);
                        appLogVO.setIsInUsable("false");
                        appLogVO.setPackageFullName(event.getPackageName());

                        AppLogVO equal = appdbHelper.getAppVOEqual(user.getUid(), getDateStr(), pName, timeStamp);
                        try {

                            if (count == 0)
                                appdbHelper.insert(user.getUid(), getDateStr(), appLogVO);
                            else {
                                Date ltime = sdfNow.parse(logVO.getStime());
                                Date atime = sdfNow.parse(timeStamp);

                                //Log.i("sy2399","forground" +pName + timeStamp);

                                if (atime.getTime() - ltime.getTime() > 0 && equal == null) {
                                    //Log.i("sy2399","insert" );

                                    appdbHelper.insert(user.getUid(), getDateStr(), appLogVO);
                                }

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {

                        timeStamp = String.valueOf(mDateFormat.format(new Date(event.getTimeStamp())));

                        AppLogVO tmpVO = appdbHelper.getAppVOWithTimeAndName(user.getUid(), getDateStr(), pName, timeStamp);
                        if (tmpVO != null) {
                            try {
                                Date ltime = sdfNow.parse(tmpVO.getStime());//디비에 저장된 앱이 켜진 시간
                                Date atime = sdfNow.parse(timeStamp);//꺼지는 시간
                                if (atime.getTime() - ltime.getTime() > 0) {

                                    appdbHelper.updateVO(user.getUid(), getDateStr(), tmpVO, timeStamp);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }

            }

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
        for (int i = 0; i < pNames.length; i++) {
            result = pNames[i];
        }
        return result;
    }


    private void insertAndReadData() {

        //Log.i("ActivityTimerCount()", "insertAndReadData()");
        readHistoryData();
    }


    private Task<DataReadResponse> readHistoryData() {
        Fitness.getRecordingClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .subscribe(DataType.TYPE_ACTIVITY_SEGMENT)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.i("onSuccess", "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.i("onFailure", "There was a problem subscribing.");
                    }
                });

        DataReadRequest readRequest = queryFitnessData();

        return Fitness.getHistoryClient(context, googleSignInAccount)
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {

                                printData(dataReadResponse);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }

    public DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.setTime(now);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        long startTime = cal.getTimeInMillis();


        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        //Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .read(DataType.TYPE_ACTIVITY_SEGMENT)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();
        // [END build_read_data_request]

        return readRequest;
    }


    public void printData(DataReadResponse dataReadResult) {

        if (dataReadResult.getBuckets().size() > 0) {

            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }

    private void dumpDataSet(DataSet dataSet) {

        String dataTypeName = dataSet.getDataType().getName();


        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        ActivityVO lastVO = new ActivityVO();

        for (DataPoint dp : dataSet.getDataPoints()) {

            if (dataTypeName.equals("com.google.activity.segment")) {

                for (Field field : dp.getDataType().getFields()) {
                    //Log.i("imsso", "Data point:");
                    //Log.i("imsso", "\tType: " + dp.getDataType().getName());
                    //Log.i("imsso", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                    //Log.i("imsso", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                    ActivityVO avo = new ActivityVO();
                    avo.setType(dp.getDataType().getName());
                    avo.setStartTime(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                    avo.setEndTime(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                    avo.setField(field.getName());
                    avo.setValue(dp.getValue(field).toString());

                    //Log.i("AVO", avo.toString());

                    Ajou_DB.child("GOOGLEFIT").child("Activity").child(user.getUid()).child(getDateStr()).child(avo.getStartTime()).setValue(avo);
                    ActivityVO lvo = activitydbHelper.getLastVO(user.getUid(), getDateStr());

                    //db에 있는 마지막 시간과 googlefit의 데이터 시간 비교해서 db시간 이후의 데이터만 inserrt!
                    try {
                        String last = activitydbHelper.getLastTime(user.getUid(), getDateStr());

                        if (!last.equals("")) {

                            String startTime = lvo.getStartTime();
                            String endTime = lvo.getEndTime();

                            Date startDate = dateFormat.parse(startTime);
                            Date endDate = dateFormat.parse(endTime);

                            Date fbstart = dateFormat.parse(avo.getStartTime());
                            Date fbend = dateFormat.parse(avo.getEndTime());

                            ActivityVO equal = activitydbHelper.getEqualVO(user.getUid(), getDateStr(), avo);

                            if (equal == null) {
                                if (fbstart.getTime() - endDate.getTime() >= 0 && !avo.getValue().equals(lvo.getValue())) {
                                    activitydbHelper.insert(user.getUid(), getDateStr(), avo);
                                    //Log.i("0zoo", "activity db insert!" + avo.toString());
                                    // activity에 처음 넣을 때 위치 정보 저장
                                    // 현재 시간이랑 비교해서 넣기
                                    if (avo.getValue().equals("3")) {
                                        // 이미 activity를 저장했지만, 로케이션 정보가 없는 경우에는 넣어주자.
                                        //Log.i("0zoo", "NOT MOVING! - lvo" + lvo.toString());

                                        String sTime = lvo.getStartTime();
                                        Date st = dateFormat.parse(sTime);
                                        long now = System.currentTimeMillis();

                                        if(locationdbHelper.getLocationByTime(user.getUid(), sTime)==null){
                                            //insertLocationDB(sTime);
                                            if((now-st.getTime())<=(10000 * 6 * 10))
                                                LocationUtils.insertDB(context, user.getUid(), sTime);
                                        }
                                    }

                                } else if ((fbstart.getTime() - startDate.getTime() >= 0 && endDate.getTime() - fbend.getTime() > 0 && avo.getValue().equals(lvo.getValue()))
                                        ||
                                        (fbstart.getTime() - startDate.getTime() >= 0 && endDate.getTime() - startDate.getTime() >= 0 && fbend.getTime() - endDate.getTime() > 0 && avo.getValue().equals(lvo.getValue()))) {
                                    if (endDate.getTime() < fbend.getTime())
                                        activitydbHelper.updateVO(user.getUid(), getDateStr(), lvo.getStartTime(), avo);
                                    if (avo.getValue().equals("3")) {
                                        // 이미 activity를 저장했지만, 로케이션 정보가 없는 경우에는 넣어주자.
                                        //Log.i("0zoo", "NOT MOVING! - lvo" + lvo.toString());

                                        String sTime = lvo.getStartTime();

                                        //Log.i("0zoo", "NOT MOVING! "+ isLocationExist);

                                        if(locationdbHelper.getLocationByTime(user.getUid(), sTime)==null){
                                            //insertLocationDB(sTime);
                                            LocationUtils.insertDB(context, user.getUid(), sTime);
                                        }
                                    }
                                }
                            }

                        } else {
                            activitydbHelper.insert(user.getUid(), getDateStr(), avo);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    lastVO = avo;
                }

                try {
                    if (lastVO.getValue() != null) {
                        long now = System.currentTimeMillis();

                        if (lastVO.getValue() != null) {
                            if (!lastVO.getValue().equals("3")) {
                                ArrayList<ActivityVO> emaList
                                        = activitydbHelper.getActivityWithFlag(user.getUid(), getDateStr());

                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                                int dbCount = emaList.size();
                                int phoneCount = preferences.getInt("SURVEY_COUNT", 0);

                                if (dbCount > 0) {
                                    if (dbCount > phoneCount) {
                                        // 알림 1번 보내주고
                                        // 프리퍼런스 업데이트
                                        //Log.i("0zoo1", dbCount + ", " + phoneCount);
                                        //if (Settings.canDrawOverlays(context)) {
                                        sendNotification();
                                        preferences.edit().putInt("SURVEY_COUNT", dbCount).apply();
                                        //}

                                    } else if (dbCount < phoneCount) {
                                        // 사용자가 서베이를 했음
                                        // 알림 x
                                        //Log.i("0zoo2", dbCount + ", " + phoneCount);

                                        preferences.edit().putInt("SURVEY_COUNT", dbCount).apply();

                                    } else {
                                        // 알림 x
                                        //Log.i("0zoo3", dbCount + ", " + phoneCount);
                                    }
                                }

                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }



    private void sendNotification() {

        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("survey", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ajoulogo)
                .setContentTitle("Survey Alert")
                .setContentText("" + activitydbHelper.getActivityWithFlag(user.getUid(), getDateStr()).size() + "개의 Survey 에 응답해주세요!")  // required
                .setSmallIcon(android.R.drawable.btn_star)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ajoulogo))
                .setBadgeIconType(R.drawable.ajoulogo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, mBuilder.build());

        //Log.i("0zoo", "노티보냄 ");
        //preferences.edit().putInt("SURVEY_COUNT", 0).apply();


    }


    public String getDateStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return sdfNow.format(date);
    }

    public String getTimeStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdfNow.format(date);
    }

    public void stopTimerTask() {
        // stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}