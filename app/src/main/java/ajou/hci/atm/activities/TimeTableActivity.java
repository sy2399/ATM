package ajou.hci.atm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import ajou.hci.atm.R;
import ajou.hci.atm.data.USERDBHelper;


public class TimeTableActivity extends AppCompatActivity {

    private Map<String, Integer> timeResIdMap = setupTimeResIdMap();
    private Map<String, TextView> textViewMap = new HashMap<>();
    private Map<String, Boolean> selectTimeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        boolean isTimeTableExist = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("USER_TIMETABLE", false);

        if (isTimeTableExist) {
            startActivity(new Intent(TimeTableActivity.this, MainActivity.class));
        }

        for (final String key : timeResIdMap.keySet()) {
            selectTimeMap.put(key, false);
            TextView tv = findViewById(timeResIdMap.get(key));
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isSelected = selectTimeMap.get(key);
                    if (isSelected) {
                        view.setBackgroundResource(R.color.white);
                    } else {
                        view.setBackgroundResource(R.color.wellDone);
                    }
                    selectTimeMap.put(key, !isSelected);
                }
            });
            textViewMap.put(key, tv);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_time_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_next) {
            String timeTableString = getSelectedTimeTableString();
            if (timeTableString.isEmpty()) {
                Toast.makeText(this, "하나 이상의 시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return false;
            }

            putTimeTableFirebase(timeTableString);

        }
        return super.onOptionsItemSelected(item);
    }

    private String getSelectedTimeTableString() {
        StringBuilder sb = new StringBuilder();
        for (String key : selectTimeMap.keySet()) {
            if (selectTimeMap.get(key)) {
                sb.append(",").append(key);
            }
        }
        if(sb.length()>0){
            sb.deleteCharAt(0);
        }
        //("TimeTableActivity", sb.toString());

        return sb.toString();
    }

    private void putTimeTableFirebase(final String data) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid()).child("timeTable")
                    .setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                     PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("USER_TIMETABLE", true).apply();

                    new USERDBHelper(getApplicationContext(), "USER.db", null, 1)
                            .updateUserTimeTable(user.getUid(), data);

                    startActivity(new Intent(TimeTableActivity.this, MainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


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

}
