package ajou.hci.atm.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ajou.hci.atm.R;
import ajou.hci.atm.data.ACTIVITYDBHelper;
import ajou.hci.atm.data.APPDBHelper;
import ajou.hci.atm.data.EMADBHelper;
import ajou.hci.atm.data.LOCATIONDBHelper;
import ajou.hci.atm.data.TIMECOUNTERDBHelper;
import ajou.hci.atm.model.AppLogVO;
import ajou.hci.atm.model.EMAVO;
import ajou.hci.atm.model.LocationVO;


@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class CalendarFragment extends Fragment {
    //private final String TAG = "CALENDAR";

    private LOCATIONDBHelper locationdbHelper;
    private EMADBHelper emadbHelper;
    private APPDBHelper appdbHelper;
    private ACTIVITYDBHelper activitydbHelper;
    private TIMECOUNTERDBHelper timecounterdbHelper;

    //private OnFragmentInteractionListener mListener;

    PieChart pieChart;

    private static final int REQUEST_DATE = 0;

    public DatabaseReference Ajou_DB;
    public FirebaseUser user;
    public FirebaseAuth mAuth;

    private ArrayList<LocationVO> locationVOArrayList = new ArrayList<>();

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date selectedDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            //선택한 날짜 받아옴.
            //여기서 CalendarFragment 를 redirect시켜줘야함
            SimpleDateFormat sdfNowMD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            CalendarFragment newC = new CalendarFragment();
            Bundle bundle = new Bundle();
            bundle.putString("SELECTED_DATE", sdfNowMD.format(selectedDate));
            newC.setArguments(bundle);

            if (getActivity() != null) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.contentContainer, newC)
                        .commit();
            }
            //dateTv.setText(sdfNowMD.format(selectedDate));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Ajou_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        locationdbHelper = new LOCATIONDBHelper(getContext(), "LOCATION.db", null, 1);
        emadbHelper = new EMADBHelper(getContext(), "EMA.db", null, 1);
        appdbHelper = new APPDBHelper(getContext(), "APP.db", null, 1);
        activitydbHelper = new ACTIVITYDBHelper(getContext(), "ACTIVITY.db", null, 1);
        timecounterdbHelper = new TIMECOUNTERDBHelper(getContext(), "TIMECOUNTER.db", null, 1);
    }

    @Override
    public void onStart() {

        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_calendar, container, false);

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        locationdbHelper = new LOCATIONDBHelper(getContext(), "LOCATION.db", null, 1);
        emadbHelper = new EMADBHelper(getContext(), "EMA.db", null, 1);
        appdbHelper = new APPDBHelper(getContext(), "APP.db", null, 1);
        activitydbHelper = new ACTIVITYDBHelper(getContext(), "ACTIVITY.db", null, 1);
        String selectedDate = null;

        TextView phoneUsage = view.findViewById(R.id.totalPhone);
        TextView usableTime = view.findViewById(R.id.totalUsable);

        int phoneTotal = timecounterdbHelper.getTotalCount(user.getUid(), getDateStr());
        //appdbHelper.getTotal(user.getUid(), getDateStr());

        int phoneTotal_h = (phoneTotal / 60);
        int phoneTotal_m = phoneTotal % 60;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int usableTotal = preferences.getInt(getDateStr() + "sleep", activitydbHelper.getTotal(user.getUid(), getDateStr()));



        int usableTotal_h = usableTotal / 60;
        int usableTotal_m = usableTotal % 60;

        phoneUsage.setText("스마트폰 사용 시간\n" + phoneTotal_h + "시간 " + phoneTotal_m + "분");
        usableTime.setText("활용 가능 시간\n" + usableTotal_h + "시간 " + usableTotal_m + "분");
        //dateTv = (TextView) view.findViewById(R.id.todayCalendar);


        if (getArguments() != null) {
            selectedDate = (String) getArguments().getSerializable("SELECTED_DATE");
        }
        if (selectedDate == null) {
            //Date date = new Date(System.currentTimeMillis());
            //SimpleDateFormat sdfNowMD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            //dateTv.setText(sdfNowMD.format(date));

            String[] arr = {"공부/과제", "알바", "수면", "운동/산책", "문화생활", "식사/음주", "수업", "게임(폰)", "게임(PC)", "동아리활동", "수다",
                    "집안일", "기타"};
            //horizontalBarChart
            HorizontalBarChart hChart = view.findViewById(R.id.horizontalBarchart);

            //hashMap
            HashMap<String, Integer> hashMap = new HashMap<>();
//        //hashMap.put("key", 1);
            hashMap.put("공부/과제", 0);
            hashMap.put("알바", 0);
            hashMap.put("수면", 0);
            hashMap.put("운동/산책", 0);
            hashMap.put("문화생활", 0);
            hashMap.put("식사/음주", 0);
            hashMap.put("수업", 0);
            hashMap.put("게임(폰)", 0);
            hashMap.put("게임(PC)", 0);
            hashMap.put("동아리활동", 0);
            hashMap.put("수다", 0);
            hashMap.put("집안일", 0);
            hashMap.put("기타", 0);

            //Set 객체에 들어있는 값(key)를 iterator 인터페이스를 통해, 순차적으로 탐색할 준비
            //Set hashSet = hashMap.keySet();
            //Iterator iterator = hashSet.iterator();

            //hBar dataset List<>
            List<BarEntry> entries = new ArrayList<>();

            //hBar labels ArrayList<>
            ArrayList<String> labels = new ArrayList<>();

            //iterator를 통해 순차탐색
            //int hBarCounter = 0;
            //xAxis values
            //int i = 0;
            ArrayList<EMAVO> emavoArrayList = emadbHelper.getEMAVOs(user.getUid(), getDateStr());
            //final int result = 0;
            for (int j = 0; j < emavoArrayList.size(); j++) {
                //boolean isthere = false;
                String emas = emavoArrayList.get(j).getActivity();
                if (Arrays.asList(arr).contains(emas)) {

                    int v = hashMap.get(emas);
                    hashMap.put(emas, v + 1);

                } else {
                    //Log.i("sy2399", "기타" + emas);

                    int v = hashMap.get("기타");
                    hashMap.put("기타", v + 1);
                }
            }


            for (int k = 0; k < arr.length; k++) {
                int r = hashMap.get(arr[k]);
                entries.add(new BarEntry(k, r));
                labels.add(arr[k]);
            }
            BarDataSet set = new BarDataSet(entries, "");

            BarData data = new BarData(set);
            data.setValueFormatter(new DecimalRemover(new DecimalFormat("###")));
            data.setBarWidth(0.8f); // set custom bar width
            data.setValueTextSize(10f);
            hChart.setData(data);
            hChart.setFitBars(true); // make the x-axis fit exactly all bars
            hChart.invalidate(); // refresh
            hChart.getDescription().setEnabled(false);
            hChart.setDrawBarShadow(false);
            hChart.setDrawValueAboveBar(true);
            hChart.setDrawGridBackground(false);
            hChart.getLegend().setEnabled(false);
            hChart.setScaleEnabled(false);
            hChart.getRendererXAxis().getPaintAxisLabels().setTextAlign(Paint.Align.CENTER);

            set.setColors(ColorTemplate.MATERIAL_COLORS);


            XAxis xl = hChart.getXAxis();
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setDrawAxisLine(true);
            CategoryBarChartXaxisFormatter xaxisFormatter = new CategoryBarChartXaxisFormatter(labels);
            xl.setValueFormatter(xaxisFormatter);
            xl.setGranularity(1);
            xl.setCenterAxisLabels(false);
            xl.setGranularity(1);
            xl.setAxisMinimum(0);
            xl.setAxisMaximum(12);
            xl.setLabelCount(13);
            //밑부분 공백
            xl.setAxisMinimum(data.getXMin() - 1.5f);
            //윗부분 공백
            xl.setAxisMaximum(data.getXMax() + 1.5f);
            xl.setDrawGridLines(false);


            YAxis yl = hChart.getAxisLeft();
            yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            yl.setEnabled(false);
            yl.setDrawGridLines(false);
            yl.setAxisMinimum(0f);
            yl.setGranularity(1);
            yl.setCenterAxisLabels(false);
            yl.setInverted(false);

            YAxis yr = hChart.getAxisRight();
            yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            yr.setAxisMinimum(0f);
            yr.setCenterAxisLabels(false);
            yr.setEnabled(false);
            yr.setDrawLabels(false);
            yr.setInverted(false);
            yr.setDrawGridLines(false);


            pieChart = view.findViewById(R.id.piechart);

            pieChart.setUsePercentValues(true);
            pieChart.getDescription().setEnabled(false);
            pieChart.setExtraOffsets(20f, 5f, 20f, 0f);

            pieChart.setDragDecelerationFrictionCoef(0.95f);

            pieChart.setDrawHoleEnabled(false);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.setTransparentCircleRadius(61f);

            final HashMap<String, Long> apps = new HashMap<>();
            ArrayList<AppLogVO> appLogVOArrayList = appdbHelper.getAppVOsWithFlag(user.getUid(), getDateStr());
            AppComparator comp = new AppComparator();
            Collections.sort(appLogVOArrayList, comp);

            //Log.i("imsso", appLogVOArrayList.toString());
            for (int k = 0; k < appLogVOArrayList.size(); k++) {
                AppLogVO tmpVO = appLogVOArrayList.get(k);
                try {
                    if (tmpVO.getTotal() > 0) {
                        String pName = tmpVO.getPackageName();

                        if (apps.get(pName) == null)
                            apps.put(pName, (long) tmpVO.getTotal());
                        else {
                            long val = apps.get(pName);
                            apps.put(pName, val + (long) tmpVO.getTotal());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ArrayList<PieEntry> yValues = new ArrayList<>();
            Iterator it = sortHashMapByValues(apps, false).iterator();

            int counter = 0;

            while (it.hasNext() && counter < 6) {
                String temp = (String) it.next();
                yValues.add(new PieEntry(apps.get(temp), temp));
                counter++;
            }
//            printMap(apps);
//            Iterator<String> keys = apps.keySet().iterator();
//
//            while(keys.hasNext() && counter <6){
//                String key = keys.next();
//                yValues.add(new PieEntry(apps.get(key), key));
//
//                counter ++;
//            }

            pieChart.setDrawEntryLabels(false);
            pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션
            if (yValues.size() == 0) {
                yValues.add(new PieEntry(100, "None"));
            }

            PieDataSet dataSet = new PieDataSet(yValues, " ");
            dataSet.setSelectionShift(5f);
            dataSet.setValueTextSize(10);
            dataSet.setValueFormatter(new MaterialInValueFormatter());
            ArrayList<Integer> colors = new ArrayList<>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(colors);
            //dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

            PieData pdata = new PieData((dataSet));
            pdata.setValueTextSize(10f);
            pdata.setValueTextColor(Color.BLACK);

            pieChart.setData(pdata);

            //legend
            Legend l = pieChart.getLegend();
            l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);

        }

        return view;

    }

    public class CategoryBarChartXaxisFormatter implements IAxisValueFormatter {

        ArrayList<String> mValues;

        CategoryBarChartXaxisFormatter(ArrayList<String> values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            int val = (int) value;
            String label = "";
            if (val >= 0 && val < mValues.size()) {
                label = mValues.get(val);
            }
            return label;
        }
    }


    public String getDateStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return sdfNow.format(date);
    }

    class DecimalRemover extends PercentFormatter {

        protected DecimalFormat mFormat;

        DecimalRemover(DecimalFormat format) {
            this.mFormat = format;
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }

    class AppComparator implements Comparator<AppLogVO> {

        @Override
        public int compare(AppLogVO first, AppLogVO second) {
            double firstValue = (double) first.getTotal();
            double secondValue = (double) second.getTotal();

            // Order by descending
            return Double.compare(secondValue, firstValue);
        }
    }

    public List sortHashMapByValues(final HashMap<String, Long> unsortMap, final boolean order) {
        List<String> list = new ArrayList();

        list.addAll(unsortMap.keySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                Object v1 = unsortMap.get(o1);
                Object v2 = unsortMap.get(o2);
                return ((Comparable) v2).compareTo(v1);
            }
        });
        //Collections.reverse(list); // 주석시 내림 차순
        return list;
    }


    class MaterialInValueFormatter implements IValueFormatter {
        private DecimalFormat percentageFormat;
        private DecimalFormat valueFormat;
        private ArrayList<String> unit = new ArrayList<>();

        MaterialInValueFormatter() {
            percentageFormat = new DecimalFormat("###,###,##0.0");
            valueFormat = new DecimalFormat("###,###,###");
        }

        MaterialInValueFormatter(ArrayList<String> unit) {
            this.unit.addAll(unit);
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            if (unit.size() > 0) {
                return percentageFormat.format(value) + " %";
//            return percentageFormat.format(value) + " %" + "\n" + value + " " + unit.get(dataSetIndex);

//here I'm trying to add the unit to the value. it is either 'gm' or 'kg'. the array list 'unit' has the respective unit of the value
            } else
                return valueFormat.format(value) + " %";
        }
    }

}
