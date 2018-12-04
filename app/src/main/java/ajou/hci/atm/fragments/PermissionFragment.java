package ajou.hci.atm.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.util.List;
import java.util.Set;

import ajou.hci.atm.R;
import ajou.hci.atm.utils.ActivityService;
import ajou.hci.atm.utils.ShowNotificationListenerService;


public class PermissionFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private  final int BATTERY_PERMISSION = 1;
    private  final int APP_PERMISSION = 2;
    private  final int NOTI_PERMISSION = 3;
    private  final int TOP_PERMISSION = 4;
    private  final int LOC_PERMISSION = 5;
    private  final int SMS_PERMISSION = 6;
    private final int GOOGLE_FIT_PERMISSION = 7;
    private final int FILE_PERMISSION = 8;

    boolean p1, p2, p3, p4, p5, p6, p7, p8 = false;

    TextView battery_p;
    TextView app_p ;
    TextView noti_p ;
    TextView topView_p ;
    TextView loc_p ;
    TextView file_p ;
    TextView google_p;
    Button startBtn;

    public PermissionFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment PermissionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PermissionFragment newInstance() {
        PermissionFragment fragment = new PermissionFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if(requestCode == BATTERY_PERMISSION){
            battery_p.setText("허용");
            p1 = true;
        }
        if(requestCode == APP_PERMISSION){
            app_p.setText("허용");
            p2 = true;
        }
        if(requestCode == NOTI_PERMISSION){
            noti_p.setText("허용");
            p3 = true;
        }
        if(requestCode == TOP_PERMISSION){
            topView_p.setText("허용");
            p4 = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOC_PERMISSION){
            loc_p.setText("허용");
            p5 = true;
        }

        if(requestCode == FILE_PERMISSION){
            file_p.setText("허용");
            p7 = true;
        }
        if(requestCode == GOOGLE_FIT_PERMISSION){
            google_p.setText("허용");
            p8 = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_permission, container, false);

        battery_p = (TextView)view.findViewById(R.id.battery_p);
        app_p = (TextView)view.findViewById(R.id.app_p);
        noti_p = (TextView)view.findViewById(R.id.noti_p);
        topView_p = (TextView)view.findViewById(R.id.topview_p);
        loc_p = (TextView) view.findViewById(R.id.location_p);
        file_p = (TextView)view.findViewById(R.id.file_p);
        google_p = (TextView)view.findViewById(R.id.google_p);
        startBtn = (Button) view.findViewById(R.id.startBtn);


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityService activityService = new ActivityService();
                if (isMyServiceNotRunning(activityService.getClass())) {
                    Log.i("buttpn", "click");
                    Toast.makeText(requireContext(), "Collecting Data", Toast.LENGTH_SHORT).show();
                    Intent mServiceIntent = new Intent(getContext(), activityService.getClass());
                    getContext().startService(mServiceIntent);

                }else{
                    Toast.makeText(requireContext(), "Collecting Data", Toast.LENGTH_SHORT).show();

                }

                if(isMyServiceNotRunning(new ShowNotificationListenerService().getClass())){
                    Toast.makeText(requireContext(), "Collecting Data", Toast.LENGTH_SHORT).show();
                    Intent mServiceIntent = new Intent(getContext(), new ShowNotificationListenerService().getClass());
                    getContext().startService(mServiceIntent);
                }else{
                    Toast.makeText(requireContext(), "Collecting Data", Toast.LENGTH_SHORT).show();

                }



            }
        });


        if(!checkBattery()){
            battery_p.setText("허용 안됨");
            battery_p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    @SuppressLint("BatteryLife") Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    i.setData(Uri.parse("package:" + getContext().getPackageName()));
                    startActivityForResult(i, BATTERY_PERMISSION);
                }
            });

        }

        if(!checkApp(getContext())){
            app_p.setText("허용 안됨");
            app_p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), APP_PERMISSION);

                }
            });

        }

        if(!checkNoti(getContext())){
            noti_p.setText("허용 안됨");
            noti_p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent noti_intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivityForResult(noti_intent, NOTI_PERMISSION);
                }
            });

        }

        if(!checkTopView(getContext())){
            topView_p.setText("허용 안됨");
            topView_p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                    startActivityForResult(intent, TOP_PERMISSION);
                }
            });

        }

        if(!checkLoc(getContext())){
            loc_p.setText("허용 안됨");
            loc_p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.i(TAG, "getDataPermission()-NONE ACCESS_FINE_LOCATION,RECEIVE_SMS");
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECEIVE_SMS}, LOC_PERMISSION);
                    loc_p.setText("허용");
                }
            });

        }


        if(!checkFile(getContext())){
            file_p.setText("허용 안됨");
            file_p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_PERMISSION);

                }
            });
        }

        if(!checkGoogle(getContext())){
            google_p.setText("허용 안됨");
            google_p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FitnessOptions fitnessOptions = FitnessOptions.builder()
                            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
                            .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
                            .build();

                    GoogleSignIn.requestPermissions(
                            getActivity(),
                            GOOGLE_FIT_PERMISSION,
                            GoogleSignIn.getLastSignedInAccount(getContext()),
                            fitnessOptions);
                }
            });


        }




        return view;

    }


    public boolean checkBattery(){
        PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        boolean isWhiteListing = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm != null) {
                isWhiteListing = pm.isIgnoringBatteryOptimizations(getContext().getPackageName());
            }
        }
        return isWhiteListing;
    }

    private boolean isMyServiceNotRunning(Class<?> serviceClass) {
        //Log.i(TAG, "isMyServiceRunning()");
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean checkApp(@NonNull final Context context) {
        // Usage Stats is theoretically available on API v19+, but official/reliable support starts with API v21.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }

        final AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

        if (appOpsManager == null) {
            return false;
        }

        final int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        if (mode != AppOpsManager.MODE_ALLOWED) {
            return false;
        }

        // Verify that access is possible. Some devices "lie" and return MODE_ALLOWED even when it's not.
        final long now = System.currentTimeMillis();
        final UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        final List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 1000 * 10, now);
        return (stats != null && !stats.isEmpty());
    }

    public boolean checkNoti(Context context) {
        //Log.i(TAG, "isNotificationPermissionAllowed()");

        Set<String> notificationListenerSet = NotificationManagerCompat.getEnabledListenerPackages(context);

        for (String packageName : notificationListenerSet) {
            if (packageName == null) {
                continue;
            }
            if (packageName.equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }

    public boolean checkTopView(Context context){
        boolean flag = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(getContext())) {
            flag = false;
        }
        return flag;
    }

    public boolean checkLoc(Context context){
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                ) {
           return false;
        }
        return true;

    }


    public boolean checkFile(Context context){
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            return false;
        }
        return true;
    }

    public boolean checkGoogle(Context context){
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), fitnessOptions)) {
            return false;

        }
        return true;
    }

}
