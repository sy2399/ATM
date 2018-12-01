package ajou.hci.atm.summary;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ajou.hci.atm.R;
import ajou.hci.atm.model.TimelineRow;

public class TimelineViewAdapter extends RecyclerView.Adapter<TimelineViewHolder> {

    public DatabaseReference Ajou_DB = FirebaseDatabase.getInstance().getReference();
    public FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public FirebaseUser user = mAuth.getCurrentUser();

    private List<TimelineRow> rowDataList;

    TimelineViewAdapter(ArrayList<TimelineRow> objects, boolean orderTheList) {
        this.rowDataList = objects;
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.ctimeline_row, parent, false);
        return new TimelineViewHolder(view, rowDataList.size());
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        TimelineRow item = rowDataList.get(position);
        holder.bindTimeline(item);
    }

    @Override
    public int getItemCount() {
        return rowDataList.size();
    }

    private ArrayList<TimelineRow> rearrangeByDate(ArrayList<TimelineRow> objects) {
        if (objects.get(0) == null) return objects;
        int size = objects.size();
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (objects.get(i).getDate() != null && objects.get(j).getDate() != null)
                    if (objects.get(i).getDate().compareTo(objects.get(j).getDate()) <= 0)
                        Collections.swap(objects, i, j);
            }

        }
        return objects;
    }

//    public String getDateStr() {
//        long now = System.currentTimeMillis();
//        Date date = new Date(now);
//        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        return sdfNow.format(date);
//    }


//    public List<TimelineRow> getRowDataList() {
//        return rowDataList;
//    }
//
//    public void setRowDataList(List<TimelineRow> rowDataList) {
//        this.rowDataList = rowDataList;
//    }


//    private String getPastTime(Date date) {
//
//        if (date == null) return "";
//        StringBuilder dateText = new StringBuilder();
//        Date today = new Date();
//        long diff = (today.getTime() - date.getTime()) / 1000;
//
//        long years = diff / (60 * 60 * 24 * 30 * 12);
//        long months = (diff / (60 * 60 * 24 * 30)) % 12;
//        long days = (diff / (60 * 60 * 24)) % 30;
//        long hours = (diff / (60 * 60)) % 24;
//        long minutes = (diff / 60) % 60;
//        long seconds = diff % 60;
//
//        if (years > 0) {
//            appendPastTime(dateText, years, R.plurals.years, months, R.plurals.months);
//        } else if (months > 0) {
//            appendPastTime(dateText, months, R.plurals.months, days, R.plurals.days);
//        } else if (days > 0) {
//            appendPastTime(dateText, days, R.plurals.days, hours, R.plurals.hours);
//        } else if (hours > 0) {
//            appendPastTime(dateText, hours, R.plurals.hours, minutes, R.plurals.minutes);
//        } else if (minutes > 0) {
//            appendPastTime(dateText, minutes, R.plurals.minutes, seconds, R.plurals.seconds);
//        } else if (seconds >= 0) {
//            dateText.append(res.getQuantityString(R.plurals.seconds, (int) seconds, (int) seconds));
//        }
//
//        return dateText.toString();
//    }

//    private void appendPastTime(StringBuilder s,
//                                long timespan, int nameId,
//                                long timespanNext, int nameNextId) {
//
//        s.append(res.getQuantityString(nameId, (int) timespan, timespan));
//        if (timespanNext > 0) {
//            s.append(' ').append(AND).append(' ');
//            s.append(res.getQuantityString(nameNextId, (int) timespanNext, timespanNext));
//        }
//    }



//    public void updateList(ArrayList<ActivityVO> avos) {
//        SummaryFragment fragment = new SummaryFragment();
//        this.rowDataList = fragment.dataSetting(avos);
//        notifyDataSetChanged();
//    }

}
