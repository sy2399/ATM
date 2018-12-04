package ajou.hci.atm.summary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ajou.hci.atm.R;
import ajou.hci.atm.data.ACTIVITYDBHelper;
import ajou.hci.atm.data.LOCATIONDBHelper;
import ajou.hci.atm.data.TIMECOUNTERDBHelper;
import ajou.hci.atm.model.ActivityVO;
import ajou.hci.atm.model.SumVO;
import ajou.hci.atm.model.TimelineRow;
import ajou.hci.atm.models.Location;

// 현재 사용하지 않는 메서드들은 주석처리 하였음.
public class SummaryFragment extends Fragment {
//    private SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//    private DatabaseReference Ajou_DB = FirebaseDatabase.getInstance().getReference();

    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private TIMECOUNTERDBHelper timecounterdbHelper;
    private RecyclerView mRecyclerView;
    private TimelineViewAdapter mAdapter;
    ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();
    private static final String TAG = "SummaryFragment";

    public static SummaryFragment newInstance() {
        return new SummaryFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.i(TAG, "onResume()");
        updateUI();

    }

    //TextView blankTextView;
    TextView phoneUsage;
    TextView usableTime;
    ACTIVITYDBHelper activitydbHelper;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.i(TAG, "onCreateView()");


        View view = inflater.inflate(R.layout.fragment_summary, container, false);


        phoneUsage = view.findViewById(R.id.totalPhone);
        usableTime = view.findViewById(R.id.totalUsable);

        //blankTextView = view.findViewById(R.id.blank);

        activitydbHelper = new ACTIVITYDBHelper(getActivity(), "ACTIVITY.db", null, 1);
        timecounterdbHelper = new TIMECOUNTERDBHelper(getActivity(), "TIMECOUNTER.db", null, 1);

        ArrayList<ActivityVO> avoData = activitydbHelper.getActivityVOs(user.getUid(), getDateStr());


        int phoneTotal = timecounterdbHelper.getTotalCount(user.getUid(), getDateStr());
        //appdbHelper.getTotal(user.getUid(), getDateStr());

        int phoneTotal_h = phoneTotal / 60;
        int phoneTotal_m = phoneTotal % 60;


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        int usableTotal = preferences.getInt(getDateStr() + "sleep", activitydbHelper.getTotal(user.getUid(), getDateStr()));


        //Log.i("sy2399", "Fragment" + preferences.getInt(getDateStr() + "sleep", 0));

        int usableTotal_h = usableTotal / 60;
        int usableTotal_m = usableTotal % 60;

        phoneUsage.setText("스마트폰 사용 시간\n" + phoneTotal_h + "시간 " + phoneTotal_m + "분");
        usableTime.setText("활용 가능 시간\n" + usableTotal_h + "시간 " + usableTotal_m + "분");


//        if (avoData.size() == 0) {
//            blankTextView.setVisibility(View.VISIBLE);
//            blankTextView.setText("Data is not ready!");
//        } else {
//            blankTextView.setVisibility(View.GONE);
//        }


        //list 에 수업시간 넣기
        //int count = 0;
        //timelineRowsList = dataSetting(avoData);

        // RecyclerView
        mRecyclerView = view.findViewById(R.id.timeline_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setNestedScrollingEnabled(false);

        updateUI();

        return view;
    }

    void updateUI() {
        timelineRowsList.clear();

        //Log.i(TAG, "updateUI()");

        activitydbHelper = new ACTIVITYDBHelper(getContext(), "ACTIVITY.db", null, 1);
        timelineRowsList = dataSetting(activitydbHelper.getActivityVOs(user.getUid(), getDateStr()));

        if (mAdapter == null && getContext() != null) {
            mAdapter = new TimelineViewAdapter(timelineRowsList);
            mRecyclerView.setAdapter(mAdapter);
        }else if(mAdapter != null){
            //mAdapter.notifyDataSetChanged();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i(TAG, "onActivityResult()");

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 123) {
            String result = (String) data.getSerializableExtra("DATA_CHANGED");
            if (result.equals("changed")) {
                updateUI();
            }
        }
    }


    public String getDateStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdfNow.format(date);
    }

    public ArrayList<TimelineRow> dataSetting(ArrayList<ActivityVO> avoData) {
        //Log.i("imsso", "SummaryFragment " + avoData.size());
        Map<String, String> activityMap = new HashMap<>();
        activityMap.put("0", "In Vehicle");
        activityMap.put("72", "Sleeping");
        activityMap.put("109", "Light Sleep");
        activityMap.put("110", "Deep Sleep");
        activityMap.put("111", "REM Sleep");
        activityMap.put("112", "Awake(During Sleep Cycle)");
        activityMap.put("77", "Stair Climbing");
        activityMap.put("78", "Stair-climbing machine");
        activityMap.put("3", "Not Moving");

        activityMap.put("7", "Walking");
        activityMap.put("2", "On Foot");
        activityMap.put("8", "Running");
        activityMap.put("58", "Running");
        activityMap.put("1", "Biking");

        if(avoData.size()!=0){
            for (int i = 0; i < avoData.size(); i++) {
                ActivityVO tempVO = avoData.get(i);
                TimelineRow myRow = new TimelineRow(i);

                // To set the row Date (optional)
                if ((tempVO.getFlag()).equals("f_true")) {
                    myRow.setFlag(true);
                } else {
                    myRow.setFlag(false);
                }
//            Date sdate;
//            Date edate;
//            long interval = 0;
//            long interval_min = 0;
//            try {
//                sdate = transFormat.parse(tempVO.getStartTime());
//                edate = transFormat.parse(tempVO.getEndTime());
//                interval = edate.getTime() - sdate.getTime();
//                interval_min = interval / 60000;
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }

                //myRow.setDate(sdate);
                // To set the row Title (optional)
                // 방법2
                String key;
                String value;
                for (Map.Entry<String, String> elem : activityMap.entrySet()) {
                    key = elem.getKey();
                    value = elem.getValue();
                    if (tempVO.getValue().equals(key)) {
                        myRow.setTitle(value);

                        if (getActivity() != null) {
                            if(key.equals("0") ){
                                myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.automobile));
                                myRow.setBellowLineColor(Color.argb(255, 71, 184, 224));
                                myRow.setImageSize(30);
                                myRow.setBackgroundColor(Color.argb(255, 71, 184, 224));
                            }else if(key.equals("72")||key.equals("109")||key.equals("110")||key.equals("111")){
                                myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.moon));
                                myRow.setBellowLineColor(Color.argb(255, 255, 201, 82));
                                myRow.setImageSize(30);
                                myRow.setBackgroundColor(Color.argb(255, 255, 201, 82));
                            }else if(key.equals("77")||key.equals("78")||key.equals("7")||key.equals("2")||key.equals("8")||key.equals("58") || key.equals("112")){
                                myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.runner));
                                myRow.setBellowLineColor(Color.argb(255, 71, 184, 224));
                                myRow.setImageSize(30);
                                myRow.setBackgroundColor(Color.argb(255, 71, 184, 224));

                            }else if(key.equals("1")){
                                myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.bike));
                                myRow.setBellowLineColor(Color.argb(255, 71, 184, 224));
                                myRow.setImageSize(30);
                                myRow.setBackgroundColor(Color.argb(255, 71, 184, 224));
                            }else if(key.equals("3")){

                                Location location = getLocationData(tempVO.getStartTime());
                                if(location != null){
                                    myRow.setPoiName(location.getPoiName());
                                    myRow.setAddrName(location.getAddrName());
                                }


                                myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.notmoving));
                                myRow.setBellowLineColor(Color.argb(255, 255, 201, 82));
                                myRow.setImageSize(30);
                                myRow.setBackgroundColor(Color.argb(255, 255, 201, 82));
                            }


                        }
                    }
                }

                myRow.setBellowLineSize(5);
                myRow.setDescription(tempVO.getStartTime().substring(11, 16) + " ~ " + tempVO.getEndTime().substring(11, 16));

                myRow.setBackgroundSize(30);
                // To set row Date text color (optional)
                myRow.setDateColor(Color.argb(255, 0, 0, 0));
                // To set row Title text color (optional)
                myRow.setTitleColor(Color.argb(255, 0, 0, 0));
                // To set row Description text color (optional)
                myRow.setAvo(tempVO);

                timelineRowsList.add(myRow);


            }
        }
        return timelineRowsList;
    }

    private Location getLocationData(String startTime) {
        LOCATIONDBHelper locationdbHelper = new LOCATIONDBHelper(getContext(), "LOCATION.db", null, 1);
        return locationdbHelper.getLocationByTime(user.getUid(), startTime);
    }

}

