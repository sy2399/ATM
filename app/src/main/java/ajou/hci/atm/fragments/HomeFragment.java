package ajou.hci.atm.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ajou.hci.atm.R;
import ajou.hci.atm.data.ACTIVITYDBHelper;
import ajou.hci.atm.data.APPDBHelper;
import ajou.hci.atm.data.DBHelperInterface;
import ajou.hci.atm.data.EMADBHelper;
import ajou.hci.atm.data.LOCATIONDBHelper;
import ajou.hci.atm.data.NETWORKDBHelper;
import ajou.hci.atm.data.NOTIFICATIONDBHelper;
import ajou.hci.atm.data.PHONEDBHelper;
import ajou.hci.atm.data.TIMECOUNTERDBHelper;
import ajou.hci.atm.data.TOTALINFODBHelper;
import ajou.hci.atm.data.USERDBHelper;
import ajou.hci.atm.model.PhoneVO;
import ajou.hci.atm.model.SumVO;
import ajou.hci.atm.model.User;
import ajou.hci.atm.utils.CsvWriter;


public class HomeFragment extends Fragment {
    //private OnFragmentInteractionListener mListener;

    public DatabaseReference Ajou_DB;
    public FirebaseUser user;
    public FirebaseAuth mAuth;

    //public final String TAG = "HOMEFRAGMENT";

    private List<String> timeList = new ArrayList<>();

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private USERDBHelper userdbHelper;
    private ACTIVITYDBHelper activitydbHelper;
    private PHONEDBHelper phonedbHelper;
    private TIMECOUNTERDBHelper timecounterdbHelper;
    private User dbuser;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Ajou_DB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userdbHelper = new USERDBHelper(getContext(), "USER.db", null, 1);
        activitydbHelper = new ACTIVITYDBHelper(getContext(), "ACTIVITY.db", null, 1);
        phonedbHelper = new PHONEDBHelper(getContext(), "PHONE_USAGE.db", null, 1);
        timecounterdbHelper = new TIMECOUNTERDBHelper(getContext(), "TIMECOUNTER.db", null, 1);

        dbuser = userdbHelper.getUser(user.getUid());


    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        dbuser = userdbHelper.getUser(user.getUid());

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        userdbHelper.getResult();

        TextView phoneUsage = view.findViewById(R.id.totalPhone);
        TextView usableTime = view.findViewById(R.id.totalUsable);

        int phoneTotal = timecounterdbHelper.getTotalCount(user.getUid(), getDateStr());

        preferences.edit().putInt(getDateStr() + "phone", phoneTotal).apply();


        int usableTotal = activitydbHelper.getTotal(user.getUid(), getDateStr());
        long diff = getSleepTime();

        if(usableTotal != 0 && diff != 0){
            //if(!isCalcuateSleep){
            preferences.edit().putInt(getDateStr() + "sleep",(int)(long)diff).apply();

            usableTotal = (int)(long)(usableTotal - diff);
            if(usableTotal < 0){
                usableTotal = 0;

            }
            //preferences.edit().putBoolean(getDateStr(), true).apply();
            preferences.edit().putInt(getDateStr() + "sleep", usableTotal).apply();
            //디비에 저장?--------------------------------------------------------------------------
            //Log.i("sy2399", "in" + usableTotal);

            //}
        }



        //Log.i("sy2399", "Fragment" + preferences.getInt(getDateStr() + "sleep", 0));


        int phoneTotal_h = phoneTotal / 60;
        int phoneTotal_m = phoneTotal % 60;

        int usableTotal_h = usableTotal / 60;
        int usableTotal_m = usableTotal % 60;

        phoneUsage.setText("스마트폰 사용 시간\n" + phoneTotal_h + "시간 " + phoneTotal_m + "분");
        usableTime.setText("활용 가능 시간\n" + usableTotal_h + "시간 " + usableTotal_m + "분");


        String timeTable = dbuser.getTimeTable();
        //Log.i("sy2399", "timeTable" + timeTable);
        String[] time = timeTable.split(",");

        for (String aTime : time) {
            timeList.add(aTime.replace(" ", ""));
        }

        ArrayList<PhoneVO> phoneVOS = phonedbHelper.getPhoneVOs(user.getUid(), getDateStr());
        drawTimeTable(view, timeList, phoneVOS);

        FloatingActionButton uploadBtn = view.findViewById(R.id.uploadBtn);


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ExportDatabaseCsvTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        return view;
    }

    private Map<String, Integer> timeResIdMap = setupTimeResIdMap();

    private Map<String, Integer> setupTimeResIdMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("mon0", R.id.mon0);
        map.put("mon1", R.id.mon1);
        map.put("mon2", R.id.mon2);
        map.put("mon3", R.id.mon3);
        map.put("mon4", R.id.mon4);
        map.put("mon5", R.id.mon5);
        map.put("mon6", R.id.mon6);
        map.put("mon7", R.id.mon7);

        map.put("tue0", R.id.tue0);
        map.put("tue1", R.id.tue1);
        map.put("tue2", R.id.tue2);
        map.put("tue3", R.id.tue3);
        map.put("tue4", R.id.tue4);
        map.put("tue5", R.id.tue5);
        map.put("tue6", R.id.tue6);
        map.put("tue7", R.id.tue7);

        map.put("wed0", R.id.wed0);
        map.put("wed1", R.id.wed1);
        map.put("wed2", R.id.wed2);
        map.put("wed3", R.id.wed3);
        map.put("wed4", R.id.wed4);
        map.put("wed5", R.id.wed5);
        map.put("wed6", R.id.wed6);
        map.put("wed7", R.id.wed7);

        map.put("thu0", R.id.thu0);
        map.put("thu1", R.id.thu1);
        map.put("thu2", R.id.thu2);
        map.put("thu3", R.id.thu3);
        map.put("thu4", R.id.thu4);
        map.put("thu5", R.id.thu5);
        map.put("thu6", R.id.thu6);
        map.put("thu7", R.id.thu7);

        map.put("fri0", R.id.fri0);
        map.put("fri1", R.id.fri1);
        map.put("fri2", R.id.fri2);
        map.put("fri3", R.id.fri3);
        map.put("fri4", R.id.fri4);
        map.put("fri5", R.id.fri5);
        map.put("fri6", R.id.fri6);
        map.put("fri7", R.id.fri7);

        return map;
    }

    public void drawTimeTable(View view, List<String> timeList, ArrayList<PhoneVO> phoneVOS) {
        for (int i = 0; i < timeList.size(); i++) {
            TextView tv = view.findViewById(timeResIdMap.get(timeList.get(i)));
            tv.setBackgroundResource(R.color.defaultTime);
        }
        try {
            if (Calendar.DAY_OF_WEEK != 2) {
                for (int i = 0; i < phoneVOS.size(); i++) {
                    PhoneVO tmp = phoneVOS.get(i);
                    int dayOfWeek = Integer.parseInt(tmp.getDayOfWeek());
                    if (Calendar.DAY_OF_WEEK >= dayOfWeek && System.currentTimeMillis() - dateFormat.parse(tmp.getsTime()).getTime() >= 0) {
                        String type = tmp.getType();
                        String time = tmp.getTimeTable();
                        String text = "폰 사용량 \n " + tmp.getPercent().split("\\.")[0] + "%";
                        TextView tv = view.findViewById(timeResIdMap.get(time));
                        tv.setText(text);
                        switch (type) {
                            case "Red":
                                tv.setBackgroundResource(R.color.lostUrMind);
                                break;
                            case "Yellow":
                                tv.setBackgroundResource(R.color.warning);
                                break;
                            case "Blue":
                                tv.setBackgroundResource(R.color.wellDone);
                                break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public long getSleepTime(){
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

            ArrayList <SumVO> sumVOArrayList   = timecounterdbHelper.getSleepList(user.getUid(), getDateStr(), m/60000);

            tmp = start/60000;
            for(int i=0;i<sumVOArrayList.size();i++){

                long curdiff = sumVOArrayList.get(i).getMin() - tmp;
                tmp = sumVOArrayList.get(i).getMin();
                if(curdiff > diff){
                    diff = curdiff;

                }
            }
            //Log.i("sy2399", "diff : " + diff);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }
    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }


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

    @SuppressLint("StaticFieldLeak")
    private class ExportDatabaseCsvTask extends AsyncTask<String, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(requireContext());

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();
        }

        protected Boolean doInBackground(final String... args) {

            try {
                createFiles();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (success) {
                Toast.makeText(requireContext(), "Export successful!", Toast.LENGTH_SHORT).show();
                shareCsvFile();
            } else {
                Toast.makeText(requireContext(), "Export failed", Toast.LENGTH_SHORT).show();
            }
        }

        private void shareCsvFile() {

            Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.setType("application/csv");

            ArrayList<Uri> uriList = getUriList();

            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
            startActivity(Intent.createChooser(shareIntent, "Share CSV"));

        }

        private ArrayList<Uri> getUriList() {
            ArrayList<Uri> uriList = new ArrayList<>();
            File exportDir = new File(Environment.getExternalStorageDirectory(), "/csv/");

            for (String fileName : csvFileNames) {
                File csvFile = new File(exportDir, fileName);
                Uri uri = FileProvider.getUriForFile(requireContext()
                        , requireContext().getApplicationContext().getPackageName() + ".fileprovider", csvFile);
                uriList.add(uri);
            }
            return uriList;
        }


        private String[] csvFileNames = {"ACTIVITY.csv", "APP.csv", "EMA.csv", "LOCATION.csv", "NOTIFICATION.csv", "PHONE_USAGE.csv", "TIMECOUNTER.csv", "TOTAL_INFO.csv", "NETWORK.csv", "USER.csv"};

        private void createFiles() throws IOException {
            ACTIVITYDBHelper activitydbHelper = new ACTIVITYDBHelper(requireContext(), "ACTIVITY.db", null, 1);
            createFile(csvFileNames[0], activitydbHelper);

            APPDBHelper appdbHelper = new APPDBHelper(requireContext(), "APP.db", null, 1);
            createFile(csvFileNames[1], appdbHelper);

            EMADBHelper emadbHelper = new EMADBHelper(requireContext(), "EMA.db", null, 1);
            createFile(csvFileNames[2], emadbHelper);

            LOCATIONDBHelper locationdbHelper = new LOCATIONDBHelper(requireContext(), "LOCATION.db", null, 1);
            createFile(csvFileNames[3], locationdbHelper);

            NOTIFICATIONDBHelper notificationdbHelper = new NOTIFICATIONDBHelper(requireContext(), "NOTIFICATION.db", null, 1);
            createFile(csvFileNames[4], notificationdbHelper);

            PHONEDBHelper phonedbHelper = new PHONEDBHelper(requireContext(), "PHONE_USAGE.db", null, 1);
            createFile(csvFileNames[5], phonedbHelper);

            TIMECOUNTERDBHelper timecounterdbHelper
                    = new TIMECOUNTERDBHelper(requireContext(), "TIMECOUNTER.db", null, 1);
            createFile(csvFileNames[6], timecounterdbHelper);

            TOTALINFODBHelper totalinfodbHelper = new TOTALINFODBHelper(requireContext(), "TOTAL_INFO.db", null, 1);
            createFile(csvFileNames[7], totalinfodbHelper);

            NETWORKDBHelper networkdbHelper = new NETWORKDBHelper(requireContext(), "NETWORK.db", null, 1);
            createFile(csvFileNames[8], networkdbHelper);

            USERDBHelper userdbHelper = new USERDBHelper(requireContext(), "USER.db", null, 1);
            createFile(csvFileNames[9], userdbHelper);
        }

        private void createFile(String fileName, DBHelperInterface helperInterface) throws IOException {
            File exportDir = new File(Environment.getExternalStorageDirectory(), "/csv/");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file = new File(exportDir, fileName);

            file.createNewFile();

            CsvWriter csvWrite = new CsvWriter(new FileWriter(file));

            Cursor curCSV = helperInterface.raw();
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                for (int i = 0; i < curCSV.getColumnNames().length; i++) {
                    mySecondStringArray[i] = curCSV.getString(i);
                }
                csvWrite.writeNext(mySecondStringArray);
            }
            csvWrite.close();
            curCSV.close();
        }
    }
}