package ajou.hci.atm.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import ajou.hci.atm.R;
import ajou.hci.atm.data.ACTIVITYDBHelper;
import ajou.hci.atm.data.TIMECOUNTERDBHelper;
import ajou.hci.atm.fragments.CalendarFragment;
import ajou.hci.atm.fragments.HomeFragment;
import ajou.hci.atm.fragments.PermissionFragment;
import ajou.hci.atm.model.SumVO;
import ajou.hci.atm.summary.SummaryFragment;
import ajou.hci.atm.utils.ActivityService;

public class MainActivity extends AppCompatActivity {
//        implements HomeFragment.OnFragmentInteractionListener,
//        CalendarFragment.OnFragmentInteractionListener,
//        SummaryFragment.OnFragmentInteractionListener {

    public static final String TAG = "MainActivity";

    public static DatabaseReference Ajou_DB;
    public static FirebaseUser user;
    public static GoogleApiClient mClient;

    //권한 허용 관련 변수
    public boolean battery_p, locsms_p, googlefit_p, app_p, noti_p = false;
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private static final int PERMISSION = 2;
    private static final int APP_PERMISSION = 3;
    private static final int NOTI_PERMISSION = 4;
    private static final int LOCSMSPERMISSION = 5;
    private static final int OVERLAY_PERMISSION = 7;
    private static final int MY_PERMISSION_REQUEST_STORAGE = 9;
    private static final int REQUEST_LOCATION = 10;


    private Intent mServiceIntent;
    private ActivityService activityService;
    private BottomBar bottomBar;

    private static String[] FRAGMENT_TAGS = {"HOME", "SUMMARY", "CALENDAR", "PERMISSION"};
    private ACTIVITYDBHelper activitydbHelper;
    private TIMECOUNTERDBHelper timecounterdbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.i(TAG, "0zoo MainActivity 실행");


        Ajou_DB = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        activitydbHelper = new ACTIVITYDBHelper(getApplicationContext(), "ACTIVITY.db", null, 1);
        timecounterdbHelper = new TIMECOUNTERDBHelper(getApplicationContext(), "TIMECOUNTER.db", null, 1);

        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .build();


        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.SHARED_PREF), MODE_PRIVATE);
        if ("xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Boolean isAutoStart = preferences.getBoolean("xiaomi_autostart", false);

            if (!isAutoStart) {
                Intent i = new Intent();
                i.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                startActivity(i);
                preferences.edit().putBoolean("xiaomi_autostart", true).apply();
            }

        }

        getPermission();

        setUpBottomBar();

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, HomeFragment.newInstance(), FRAGMENT_TAGS[0]);
        transaction.commitAllowingStateLoss();

    }


    private boolean isMyServiceNotRunning(Class<?> serviceClass) {
        //Log.i(TAG, "isMyServiceRunning()");
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setUpBottomBar() {
        bottomBar = findViewById(R.id.bottomBar);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {

                String tag = null;
                Fragment selectedFragment = null;
                if (tabId == R.id.tab_home) {
                    tag = FRAGMENT_TAGS[0];
                    selectedFragment = HomeFragment.newInstance();
                } else if (tabId == R.id.tab_summary) {
                    tag = FRAGMENT_TAGS[1];
                    selectedFragment = SummaryFragment.newInstance();
                } else if (tabId == R.id.tab_cal) {
                    tag = FRAGMENT_TAGS[2];
                    selectedFragment = CalendarFragment.newInstance();
                } else if (tabId == R.id.tab_permission) {
                    tag = FRAGMENT_TAGS[3];
                    selectedFragment = PermissionFragment.newInstance();
                }


                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentContainer, selectedFragment, tag);
                transaction.commitAllowingStateLoss();

            }
        });
    }

    @Override
    public void onBackPressed() {
        // back 버튼 막기
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getBooleanExtra("survey", false)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentContainer, SummaryFragment.newInstance());
            transaction.commitAllowingStateLoss();


            //setUpBottomBar();
            bottomBar.selectTabAtPosition(1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.i(TAG, "onStart()");

        activityService = new ActivityService();

        if (isMyServiceNotRunning(activityService.getClass())) {
            Log.i(TAG, "onStart()-!isMyServiceRunning");

            mServiceIntent = new Intent(this, activityService.getClass());
            startService(mServiceIntent);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //(TAG, "onDestroy()");
        if (mServiceIntent != null) {
            stopService(mServiceIntent);
        }
    }

    //----------------------Permission --------------------------------

    public long getSleepTime() {
        long diff = 0;
        long tmp;
        try {
            long now = System.currentTimeMillis();
            Date date = new Date(now);

            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String n = sdfNow.format(date);
            long m = sdfNow.parse(n).getTime();

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);

            long startTime = cal.getTimeInMillis();
            Date stime = new Date(startTime);
            String s = sdfNow.format(stime);
            long start = sdfNow.parse(s).getTime();

            ArrayList<SumVO> sumVOArrayList = timecounterdbHelper.getSleepList(user.getUid(), getDateStr(), m / 60000);

            tmp = start / 60000;
            for (int i = 0; i < sumVOArrayList.size(); i++) {

                long curdiff = sumVOArrayList.get(i).getMin() - tmp;
                tmp = sumVOArrayList.get(i).getMin();
                if (curdiff > diff) {
                    diff = curdiff;

                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    private void checkPermission() {



        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);
        }

    }

    public void getPermission() {
        //절전모드 방지
        //Log.i(TAG, "getPermission()");

        //checkPermission();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isWhiteListing = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm != null) {
                isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
                //Log.i(TAG, "0zoo getPermission");
            }
        }

        if (!isWhiteListing) {
            //Log.i(TAG, "0zoo !isWhiteListing");
            @SuppressLint("BatteryLife") Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            battery_p = true;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            preferences.edit().putBoolean("BATTERY_PERMISSION",true).apply();
            startActivityForResult(i, APP_PERMISSION);

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Log.i(TAG, "onActivityResult()");

        switch (requestCode) {

            //1.
            case APP_PERMISSION:
                //Log.i(TAG, "APP_PERMISSION");
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), NOTI_PERMISSION);
                break;

            //2.
            case NOTI_PERMISSION:
                //Log.i(TAG, "NOTI_PERMISSION");
                boolean isPermissionAllowed = isNotificationPermissionAllowed();
                noti_p = true;
                if (!isPermissionAllowed) {
                    Intent noti_intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivityForResult(noti_intent, OVERLAY_PERMISSION);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && !Settings.canDrawOverlays(getApplicationContext())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, LOCSMSPERMISSION);
                    }
                }
                break;

            //3.
            case OVERLAY_PERMISSION:
                //Log.i(TAG, "OVERLAY_PERMISSION");
                app_p = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !Settings.canDrawOverlays(getApplicationContext())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, LOCSMSPERMISSION);
                } else {
                    getDataPermission();

                }
                break;

            //4.
            case LOCSMSPERMISSION:
                //Log.i(TAG, "LOCSMSPERMISSION");
                getDataPermission();
                break;

            case PERMISSION:
//                Log.i(TAG, "PERMISSION");
                getDataPermission();
                break;


        }

    }

    //5.
    public void getDataPermission() {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                ) {
            //Log.i(TAG, "getDataPermission()-NONE ACCESS_FINE_LOCATION,RECEIVE_SMS");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECEIVE_SMS}, LOCSMSPERMISSION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Log.i(TAG, "onRequestPermissionsResult()");

        switch (requestCode) {
            case LOCSMSPERMISSION:
                //배터리, 구글핏, 위치, SMS 권한 허용 후
                locsms_p = true;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    FitnessOptions fitnessOptions = FitnessOptions.builder()
                            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
                            .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
                            .build();

                    if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
                        GoogleSignIn.requestPermissions(
                                this,
                                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                                GoogleSignIn.getLastSignedInAccount(this),
                                fitnessOptions);

                    } else {
                        googlefit_p = true;
                        activityService = new ActivityService();
                        if (isMyServiceNotRunning(activityService.getClass())) {
                            mServiceIntent = new Intent(this, activityService.getClass());
                            startService(mServiceIntent);
                        }
                    }
                }
                break;

            case GOOGLE_FIT_PERMISSIONS_REQUEST_CODE:
                googlefit_p = true;
                activityService = new ActivityService();
                if (isMyServiceNotRunning(activityService.getClass())) {
                    mServiceIntent = new Intent(this, activityService.getClass());
                    startService(mServiceIntent);
                }
                Toast.makeText(getApplicationContext(), "권한허용" +
                                "\n 배터리 절전모드 권한 " + battery_p +
                                "\n 위치 접근 권한" + locsms_p +
                                "\n Google Fit 접근권한" + googlefit_p +
                                "\n 앱 사용 기록 접근 권한" + app_p +
                                "\n 알림 접근 권한" + noti_p,
                        Toast.LENGTH_LONG).show();
                break;

            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                }
                break;

            case REQUEST_LOCATION:
                checkPermission();
                break;

        }
    }

    private boolean isNotificationPermissionAllowed() {
        //Log.i(TAG, "isNotificationPermissionAllowed()");

        Set<String> notificationListenerSet = NotificationManagerCompat.getEnabledListenerPackages(this);

        for (String packageName : notificationListenerSet) {
            if (packageName == null) {
                continue;
            }
            if (packageName.equals(getPackageName())) {
                return true;
            }
        }

        return false;
    }

    public String getDateStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return sdfNow.format(date);
    }


}