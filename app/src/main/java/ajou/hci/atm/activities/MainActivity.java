package ajou.hci.atm.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
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
import ajou.hci.atm.model.SumVO;
import ajou.hci.atm.summary.SummaryFragment;
import ajou.hci.atm.utils.ActivityService;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static DatabaseReference Ajou_DB;
    public static FirebaseUser user;
    public static GoogleApiClient mClient;

    //권한 허용 관련 변수
    public boolean battery_p, appUse_p, notification_p, overlay_p, sms_p, googlefit_p, storage_p, location_p = false;
    private static final int BATTERY_PERMISSION = 1;
    private static final int APP_USE_PERMISSION = 2;
    private static final int NOTIFICATION_PERMISSION = 3;
    private static final int OVERLAY_PERMISSION = 4;
    private static final int SMS_PERMISSION = 5;
    private static final int GOOGLE_FIT_PERMISSIONS = 6;
    private static final int STORAGE_PERMISSION = 7;
    private static final int LOCATION_PERMISSION = 8;
    private static final int RUNTIME_PERMISSION = 9;


    private Intent mServiceIntent;
    private ActivityService activityService;
    private BottomBar bottomBar;

    private static String[] FRAGMENT_TAGS = {"HOME", "SUMMARY", "CALENDAR"};
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

        setUpBottomBar();

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, HomeFragment.newInstance(), FRAGMENT_TAGS[0]);
        transaction.commit();

        if (isAllPermissionAllowed()) {
            Log.i(TAG, "All of permission is allowed!");
            startService();
        } else {
            getPermission();
            getRuntimePermission();
        }

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
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentContainer, selectedFragment, tag);
                transaction.commit();
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
            transaction.commit();

            //setUpBottomBar();
            bottomBar.selectTabAtPosition(1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.i(TAG, "onStart()");

        activityService = new ActivityService();
        //notiService = new ShowNotificationListenerService();

        if (isMyServiceNotRunning(activityService.getClass())) {
            Log.i(TAG, "onStart()-!isMyServiceRunning");

            mServiceIntent = new Intent(this, activityService.getClass());
            startService(mServiceIntent);
        }
        //Log.i("sy2399", "MainActivity" + preferences.getInt(getDateStr() + "sleep", 0));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //(TAG, "onDestroy()");
        if (mServiceIntent != null) {
            stopService(mServiceIntent);
        }
    }


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

    public String getDateStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return sdfNow.format(date);
    }

    //----------------------Permission --------------------------------

    private void getPermission() {

        //if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        checkBatteryPermission();

        checkAppUsePermission();

        checkNotificationPermission();

        checkOverlayPermission();

        checkGoogleFitPermission();

        if (isAllPermissionAllowed()) {
            Log.i(TAG, "All of permission is allowed!");
            startService();
        }
    }

    private void checkGoogleFitPermission() {
        // 6. GOOGLE_FIT_PERMISSIONS
        if (!googlefit_p) {
            FitnessOptions fitnessOptions = FitnessOptions.builder()
                    .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
                    .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
                    .build();

            if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                        this,
                        GOOGLE_FIT_PERMISSIONS,
                        GoogleSignIn.getLastSignedInAccount(this),
                        fitnessOptions);
            } else {
                googlefit_p = true;
            }
        }
    }

    private void checkOverlayPermission() {
        // 4. OVERLAY_PERMISSION
        if (!overlay_p) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION);
            } else {
                overlay_p = true;
            }
        }
    }

    private void checkNotificationPermission() {
        // 3. notification
        if (!notification_p) {
            boolean isPermissionAllowed = isNotificationPermissionAllowed();
            if (!isPermissionAllowed) {
                startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"),
                        NOTIFICATION_PERMISSION);
            } else {
                notification_p = true;
            }
        }
    }

    private void checkBatteryPermission(){
        // 1. battery
        if (!battery_p) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isWhiteListing = false;

            if (pm != null) {
                isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
            }

            if (!isWhiteListing) {
                @SuppressLint("BatteryLife") Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivityForResult(i, BATTERY_PERMISSION);
            } else {
                battery_p = true;
            }
        }
    }

    private void checkAppUsePermission(){
        // 2. app use
        if (!appUse_p) {
            boolean granted;
            AppOpsManager appOps = (AppOpsManager) getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getApplicationContext().getPackageName());

            if (mode == AppOpsManager.MODE_DEFAULT) {
                granted = (getApplicationContext().checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
            } else {
                granted = (mode == AppOpsManager.MODE_ALLOWED);
            }

            if (!granted) {
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), APP_USE_PERMISSION);
            } else {
                appUse_p = true;
            }
        }
    }

    private void getRuntimePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION);
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, RUNTIME_PERMISSION);
            }
        } else {
            sms_p = true;
            storage_p = true;
            location_p = true;
        }
    }


    private boolean isAllPermissionAllowed() {
        return battery_p && appUse_p && notification_p && overlay_p && sms_p && googlefit_p && storage_p && location_p;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult()");

        if (resultCode != RESULT_OK) {
            Log.i(TAG, "onActivityResult() - RESULT_CANCEL!! ");
            //getPermission();
        }

        switch (requestCode) {
            case BATTERY_PERMISSION:
                checkBatteryPermission();
                Log.i(TAG, "onActivityResult()-BATTERY_PERMISSION");
                break;

            case APP_USE_PERMISSION:
                checkAppUsePermission();
                Log.i(TAG, "onActivityResult()-APP_USE_PERMISSION");
                break;

            case NOTIFICATION_PERMISSION:
                checkNotificationPermission();
                Log.i(TAG, "onActivityResult()-NOTIFICATION_PERMISSION");
                break;

            case OVERLAY_PERMISSION:
                checkOverlayPermission();
                break;
        }

        if (isAllPermissionAllowed()) {
            startService();
        }
    }

    private void startService() {
        Toast.makeText(getApplicationContext(), "권한허용" +
                        "\n 배터리 절전모드 권한 " + battery_p +
                        "\n 위치 접근 권한" + location_p +
                        "\n 저장소 접근 권한" + storage_p +
                        "\n sms 접근 권한" + sms_p +
                        "\n 앱 위에 표시 권한" + overlay_p +
                        "\n Google Fit 접근권한" + googlefit_p +
                        "\n 앱 사용 기록 접근 권한" + appUse_p +
                        "\n 알림 접근 권한" + notification_p,
                Toast.LENGTH_LONG).show();
        activityService = new ActivityService();
        if (isMyServiceNotRunning(activityService.getClass())) {
            mServiceIntent = new Intent(this, activityService.getClass());
            startService(mServiceIntent);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case SMS_PERMISSION:
                if (verifyPermissions(grantResults)) {
                    sms_p = true;
                    getRuntimePermission();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION);
                }
                break;
            case GOOGLE_FIT_PERMISSIONS:
                checkGoogleFitPermission();
                break;
            case STORAGE_PERMISSION:
                if (verifyPermissions(grantResults)) {
                    storage_p = true;
                    getRuntimePermission();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
                }
                break;
            case LOCATION_PERMISSION:
                if (verifyPermissions(grantResults)) {
                    location_p = true;
                    getRuntimePermission();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
                }
                break;
            case RUNTIME_PERMISSION:
                if (verifyPermissions(grantResults)) {
                    sms_p = true;
                    storage_p = true;
                    location_p = true;
                    if (isAllPermissionAllowed()) {
                        startService();
                    } else {
                        getPermission();
                    }
                } else {
                    getRuntimePermission();
                }

                break;
        }

        if (isAllPermissionAllowed()) {
            startService();
        }
    }


    public static boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) return false;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    private boolean isNotificationPermissionAllowed() {
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


}
