package ajou.hci.atm.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ajou.hci.atm.data.ACTIVITYDBHelper;
import ajou.hci.atm.data.APPDBHelper;
import ajou.hci.atm.data.EMADBHelper;
import ajou.hci.atm.data.LOCATIONDBHelper;
import ajou.hci.atm.data.NOTIFICATIONDBHelper;
import ajou.hci.atm.data.PHONEDBHelper;
import ajou.hci.atm.data.TIMECOUNTERDBHelper;
import ajou.hci.atm.data.TOTALINFODBHelper;
import ajou.hci.atm.data.USERDBHelper;
import ajou.hci.atm.model.ActivityVO;
import ajou.hci.atm.model.AppLogVO;
import ajou.hci.atm.model.EMAVO;
import ajou.hci.atm.model.NotifyVO;
import ajou.hci.atm.model.PhoneVO;
import ajou.hci.atm.model.SumVO;
import ajou.hci.atm.model.TimeVO;
import ajou.hci.atm.model.TotalVO;
import ajou.hci.atm.model.User;
import ajou.hci.atm.models.Location;

public class DBTimerCount {
    private Context context;
    private GoogleSignInAccount googleSignInAccount;
    public DatabaseReference Ajou_DB;
    public FirebaseAuth mAuth;
    public FirebaseUser user = null;
    User userInfo;
    private Timer timer;
    private TimerTask timerTask;
    private ACTIVITYDBHelper activitydbHelper;
    private LOCATIONDBHelper locationdbHelper;
    private EMADBHelper emadbHelper;
    private APPDBHelper appdbHelper;
    private USERDBHelper userdbHelper;
    private PHONEDBHelper phonedbHelper;
    private NOTIFICATIONDBHelper notificationdbHelper;
    private TIMECOUNTERDBHelper timecounterdbHelper;
    private TOTALINFODBHelper totalinfodbHelper;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DBTimerCount(Context context, GoogleSignInAccount googleSignInAccount) {
        this.context = context;
        this.googleSignInAccount = googleSignInAccount;

        Ajou_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        activitydbHelper = new ACTIVITYDBHelper(context, "ACTIVITY.db", null, 1);
        locationdbHelper = new LOCATIONDBHelper(context, "LOCATION.db", null, 1);
        emadbHelper = new EMADBHelper(context, "EMA.db", null, 1);
        appdbHelper = new APPDBHelper(context, "APP.db", null, 1);
        userdbHelper = new USERDBHelper(context, "USER.db", null, 1);
        phonedbHelper = new PHONEDBHelper(context, "PHONE_USAGE.db", null, 1);
        notificationdbHelper = new NOTIFICATIONDBHelper(context, "NOTIFICATION.db", null, 1);
        timecounterdbHelper = new TIMECOUNTERDBHelper(context, "TIMECOUNTER.db", null, 1);
        totalinfodbHelper = new TOTALINFODBHelper(context, "TOTAL_INFO.db", null, 1);
    }

    public void Timer() {

    }


    public void startTimer() {

        // set a new timer
        timer = new Timer();

        // initialize the timer task's job
        initializeTimerTask();

        if (isWifiAvailable(context)) {
            timer.schedule(timerTask, 1000, 10000 * 6 * 10);

        } else if (isNetworkAvailable(context)) {
            timer.schedule(timerTask, 1000, 10000 * 6 * 30);

        }
    }


    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    insertFB();
                    calculateSummary();
                    calculateTotal();
                    checkAPP();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public void stopTimerTask() {
        // stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void checkAPP() {
        ArrayList<AppLogVO> appLogVOS = appdbHelper.getAppVOs(user.getUid(), getDateStr());
        for (int i = 0; i < appLogVOS.size(); i++) {
            if (i > 0) {
                try {
                    long stime = dateFormat.parse(appLogVOS.get(i).getStime()).getTime();
                    long etime = dateFormat.parse(appLogVOS.get(i - 1).getEtime()).getTime();

                    if (etime > stime) {
                        appdbHelper.delete(i - 1);

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private void calculateTotal() {
        int phoneTotal = timecounterdbHelper.getTotalCount(user.getUid(), getDateStr());
        int usableTotal = activitydbHelper.getTotal(user.getUid(), getDateStr());
        long diff = getSleepTime();

        if (usableTotal != 0 && diff != 0) {
            usableTotal = (int) (long) (usableTotal - diff);
            if (usableTotal < 0) {
                usableTotal = 0;
            }
        }
        TotalVO dbTvo = totalinfodbHelper.getTotalVO(user.getUid(), getDateStr());
        TotalVO totalVO = new TotalVO();
        totalVO.setDate(getDateStr());
        totalVO.setPhone_m(phoneTotal);
        totalVO.setUsable_m(usableTotal);
        totalVO.setSleep_m((int) (long) diff);

        if (dbTvo == null) {
            totalinfodbHelper.insert(user.getUid(), getDateStr(), totalVO);
        } else {
            totalinfodbHelper.update(user.getUid(), getDateStr(), totalVO);
        }


    }

    private void calculateSummary() {
        ArrayList<TimeVO> times = getTodayTimeVO();
        for (int i = 0; i < times.size(); i++) {

            TimeVO tvo = times.get(i);

            String sdate = tvo.getSdate();
            String edate = tvo.getEdate();

            ArrayList<AppLogVO> apps = appdbHelper.getAppVOWithSETime(user.getUid(), getDateStr(), sdate, edate);

            Ajou_DB.child("PhoneUsageInClass").child(user.getUid()).child(getDateStr()).child(times.get(i).getTimeTable()).child("appList").setValue(apps);

            DateFormat minuiteFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            try {
                long start = minuiteFormat.parse(sdate.substring(0, 16)).getTime();
                long end = minuiteFormat.parse(edate.substring(0, 16)).getTime();

                long start_m = start / 60000;
                long end_m = end / 60000;


                int totalInClass = timecounterdbHelper.getTotalInClass(user.getUid(), getDateStr(), start_m, end_m);

                PhoneVO pvo = new PhoneVO();
                pvo.setTimeTable(times.get(i).getTimeTable());
                pvo.setDayOfWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + "");
                pvo.setTotal(totalInClass + "");
                double percent = ((double) totalInClass / (double) 75) * 100.0;
                String type;
                if (percent >= 50)
                    type = "Red";
                else if (percent >= 10)
                    type = "Yellow";
                else type = "Blue";
                pvo.setType(type);
                pvo.setDate(getDateStr());
                pvo.setPercent(percent + "");
                pvo.setsTime(times.get(i).getSdate());
                pvo.seteTime(times.get(i).getEdate());

                PhoneVO equal = phonedbHelper.getEqual(user.getUid(), getDateStr(), pvo);
                if (equal == null) {
                    phonedbHelper.insert(user.getUid(), getDateStr(), pvo);

                } else {
                    phonedbHelper.update(user.getUid(), getDateStr(), pvo);
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

    }

    private ArrayList<TimeVO> getTodayTimeVO() {
        ArrayList<String> timeList = new ArrayList<>();
        ArrayList<TimeVO> timeVOS = new ArrayList<>();
        String timeTable = userdbHelper.getTimeList(user.getUid());
        String[] times = timeTable.split(",");

        for (String time1 : times) {
            timeList.add(time1.replace(" ", ""));
        }

        String day = null;
        Date now = new Date();

        Calendar stimeCal = Calendar.getInstance();
        stimeCal.setTime(now);

        Calendar etimeCal = Calendar.getInstance();

        etimeCal.setTime(now);

        Calendar cal = Calendar.getInstance();

        int d = cal.get(Calendar.DAY_OF_WEEK);
        switch (d) {
//            case (1):
//                day = "sun";
//                break;
            case (2):
                day = "mon";
                break;
            case (3):
                day = "tue";
                break;
            case (4):
                day = "wed";
                break;
            case (5):
                day = "thu";
                break;
            case (6):
                day = "fri";
                break;

            //test - 토요일일때 금요일로 인식하게

//            case(7):
//                day = "fri";
//                break;

        }

        if (timeList.size() != 0) {
            for (int i = 0; i < timeList.size(); i++) {

                final String time = timeList.get(i);
                if (day != null) {
                    if (time.contains(day)) {
                        //오늘의 시간표가 걸러짐
                        TimeVO tv = new TimeVO();
                        tv.setTimeTable(time);
                        if (time.equals(day + "0")) {
                            //A교시 9:00-10:15
                            stimeCal.set(Calendar.MILLISECOND, 0);
                            stimeCal.set(Calendar.SECOND, 0);
                            stimeCal.set(Calendar.MINUTE, 0);
                            stimeCal.set(Calendar.HOUR_OF_DAY, 9);

                            etimeCal.set(Calendar.MILLISECOND, 0);
                            etimeCal.set(Calendar.SECOND, 0);
                            etimeCal.set(Calendar.MINUTE, 15);
                            etimeCal.set(Calendar.HOUR_OF_DAY, 10);
                        } else if (time.equals(day + "1")) {
                            //B교시 10:30-11:45
                            stimeCal.set(Calendar.MILLISECOND, 0);
                            stimeCal.set(Calendar.SECOND, 0);
                            stimeCal.set(Calendar.MINUTE, 30);
                            stimeCal.set(Calendar.HOUR_OF_DAY, 10);

                            etimeCal.set(Calendar.MILLISECOND, 0);
                            etimeCal.set(Calendar.SECOND, 0);
                            etimeCal.set(Calendar.MINUTE, 45);
                            etimeCal.set(Calendar.HOUR_OF_DAY, 11);
                        } else if (time.equals(day + "2")) {
                            //C교시 12:00-13:15
                            stimeCal.set(Calendar.MILLISECOND, 0);
                            stimeCal.set(Calendar.SECOND, 0);
                            stimeCal.set(Calendar.MINUTE, 0);
                            stimeCal.set(Calendar.HOUR_OF_DAY, 12);

                            etimeCal.set(Calendar.MILLISECOND, 0);
                            etimeCal.set(Calendar.SECOND, 0);
                            etimeCal.set(Calendar.MINUTE, 15);
                            etimeCal.set(Calendar.HOUR_OF_DAY, 13);
                        } else if (time.equals(day + "3")) {
                            //D교시 13:30-14:45
                            stimeCal.set(Calendar.MILLISECOND, 0);
                            stimeCal.set(Calendar.SECOND, 0);
                            stimeCal.set(Calendar.MINUTE, 30);
                            stimeCal.set(Calendar.HOUR_OF_DAY, 13);

                            etimeCal.set(Calendar.MILLISECOND, 0);
                            etimeCal.set(Calendar.SECOND, 0);
                            etimeCal.set(Calendar.MINUTE, 45);
                            etimeCal.set(Calendar.HOUR_OF_DAY, 14);
                        } else if (time.equals(day + "4")) {
                            //E교시 15:00-16:15
                            stimeCal.set(Calendar.MILLISECOND, 0);
                            stimeCal.set(Calendar.SECOND, 0);
                            stimeCal.set(Calendar.MINUTE, 30);
                            stimeCal.set(Calendar.HOUR_OF_DAY, 15);

                            etimeCal.set(Calendar.MILLISECOND, 0);
                            etimeCal.set(Calendar.SECOND, 0);
                            etimeCal.set(Calendar.MINUTE, 15);
                            etimeCal.set(Calendar.HOUR_OF_DAY, 16);
                        } else if (time.equals(day + "5")) {
                            //F교시 16:30-17:45
                            stimeCal.set(Calendar.MILLISECOND, 0);
                            stimeCal.set(Calendar.SECOND, 0);
                            stimeCal.set(Calendar.MINUTE, 30);
                            stimeCal.set(Calendar.HOUR_OF_DAY, 16);

                            etimeCal.set(Calendar.MILLISECOND, 0);
                            etimeCal.set(Calendar.SECOND, 0);
                            etimeCal.set(Calendar.MINUTE, 45);
                            etimeCal.set(Calendar.HOUR_OF_DAY, 17);
                        } else if (time.equals(day + "6")) {
                            //G교시 18:00-19:15
                            stimeCal.set(Calendar.MILLISECOND, 0);
                            stimeCal.set(Calendar.SECOND, 0);
                            stimeCal.set(Calendar.MINUTE, 00);
                            stimeCal.set(Calendar.HOUR_OF_DAY, 18);

                            etimeCal.set(Calendar.MILLISECOND, 0);
                            etimeCal.set(Calendar.SECOND, 0);
                            etimeCal.set(Calendar.MINUTE, 15);
                            etimeCal.set(Calendar.HOUR_OF_DAY, 19);
                        } else if (time.equals(day + "7")) {
                            //H교시 19:30-20:45
                            stimeCal.set(Calendar.MILLISECOND, 0);
                            stimeCal.set(Calendar.SECOND, 0);
                            stimeCal.set(Calendar.MINUTE, 30);
                            stimeCal.set(Calendar.HOUR_OF_DAY, 19);

                            etimeCal.set(Calendar.MILLISECOND, 0);
                            etimeCal.set(Calendar.SECOND, 0);
                            etimeCal.set(Calendar.MINUTE, 45);
                            etimeCal.set(Calendar.HOUR_OF_DAY, 20);
                        }

                        tv.setSdate(dateFormat.format(stimeCal.getTime()));
                        tv.setEdate(dateFormat.format(etimeCal.getTime()));
                        timeVOS.add(tv);
                    }
                }


            }
        }
        return timeVOS;
    }

    private void insertFB() {
        //특정 시간 간격으로 FB에 데이터 넣기
        final ArrayList<ActivityVO> activityVOArrayList = activitydbHelper.getActivityVOs(user.getUid(), getDateStr());
        final ArrayList<Location> locationVOArrayList = locationdbHelper.getLocationList(user.getUid(), getDateStr());
        final ArrayList<EMAVO> emavoArrayList = emadbHelper.getEMAVOs(user.getUid(), getDateStr());
        final ArrayList<AppLogVO> appLogVOArrayList = appdbHelper.getAppVOs(user.getUid(), getDateStr());
        final ArrayList<NotifyVO> notifyVOArrayList = notificationdbHelper.getNotiVOs(user.getUid(), getDateStr());
        Ajou_DB.child("Activity").child(user.getUid()).child(getDateStr()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                int dbCount = activityVOArrayList.size();
                if (dbCount > count)
                    Ajou_DB.child("Activity").child(user.getUid()).child(getDateStr()).setValue(activityVOArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Ajou_DB.child("APPLog").child(user.getUid()).child(getDateStr()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                int dbCount = appLogVOArrayList.size();
                if (dbCount > count)
                    Ajou_DB.child("APPLog").child(user.getUid()).child(getDateStr()).setValue(appLogVOArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Ajou_DB.child("Location").child(user.getUid()).child(getDateStr()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                int dbCount = locationVOArrayList.size();
                if (dbCount > count)
                    Ajou_DB.child("Location").child(user.getUid()).child(getDateStr()).setValue(locationVOArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Ajou_DB.child("EMA").child(user.getUid()).child(getDateStr()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                int dbCount = emavoArrayList.size();
                if (dbCount > count)
                    Ajou_DB.child("EMA").child(user.getUid()).child(getDateStr()).setValue(emavoArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Ajou_DB.child("NOTIFICATION").child(user.getUid()).child(getDateStr()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                int dbCount = notifyVOArrayList.size();
                if (dbCount > count)
                    Ajou_DB.child("NOTIFICATION").child(user.getUid()).child(getDateStr()).setValue(notifyVOArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private long getSleepTime() {
        long diff = 0;
        long tmp = 0;
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
                //Log.i("sy2399", tmp + " " + sumVOArrayList.get(i).getMin() + "  " + curdiff);
                tmp = sumVOArrayList.get(i).getMin();
                if (curdiff > diff) {
                    diff = curdiff;

                }
            }
            //Log.i("sy2399", "diff : " + diff);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }

    private boolean isWifiAvailable(Context context) {
        boolean br = false;
        ConnectivityManager cm = null;
        NetworkInfo ni = null;

        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        br = ((null != ni) && (ni.isConnected()) && (ni.getType() == ConnectivityManager.TYPE_WIFI));

        return br;
    }

    private boolean isNetworkAvailable(Context context) {
        boolean br = false;
        ConnectivityManager cm = null;
        NetworkInfo ni = null;

        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        br = ((null != ni) && (ni.isConnected()) && (ni.getType() == ConnectivityManager.TYPE_MOBILE));

        return br;
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

}
