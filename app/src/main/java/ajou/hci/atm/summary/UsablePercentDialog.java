package ajou.hci.atm.summary;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ajou.hci.atm.R;
import ajou.hci.atm.model.EMAVO;

public class UsablePercentDialog extends DialogFragment {

    //RadioBox

    //dismissListener
    private DialogInterface.OnDismissListener onDismissListener;

    private String sTime;
    private String eTime;

    private Map<String, EMAVO> emavoMap = new HashMap<>();

    public static UsablePercentDialog newInstance(String sdate, String edate, String checkboxes) {
        Bundle args = new Bundle();
        args.putSerializable("SDATE", sdate);
        args.putSerializable("EDATE", edate);
        args.putSerializable("checkboxes", checkboxes);
        UsablePercentDialog fragment = new UsablePercentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.usable_percent, null);

        sTime = (String) getArguments().getSerializable("SDATE");
        eTime = (String) getArguments().getSerializable("EDATE");
        String checkboxes = (String) getArguments().getSerializable("checkboxes");

        Log.i("dialog", checkboxes);

        String[] activities = checkboxes.split(",");
        for (String key : activities) {
            EMAVO value = new EMAVO();
            value.setStime(sTime);
            value.setEtime(eTime);
            value.setActivity(key);
            emavoMap.put(key, value);
        }

        RecyclerView mRecyclerView = view.findViewById(R.id.percentRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        UsablePercentAdapter mAdapter = new UsablePercentAdapter(activities);
        mRecyclerView.setAdapter(mAdapter);

        final Dialog dialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
                .setView(view)
                .create();

        Button okButton = view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateIndex()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("순서를 체크해주세요!")
                            .setMessage("순서가 중복되었는지 확인해보세요 :)")
                            .setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                            .setCancelable(true)
                            .show();
                } else {

                    RadioBox radioBoxDialog = RadioBox.newInstance(sTime, eTime, new ArrayList<>(emavoMap.values()));
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    radioBoxDialog.show(manager, "CheckBox!! --> Radio Group");

                    radioBoxDialog.setCancelable(true);
                    dialog.dismiss();
                    //Toast.makeText(getContext(), "저장 되었습니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return dialog;

    }

    private boolean validateIndex() {
        List<Integer> indexList = new ArrayList<>();
        for (EMAVO emavo : emavoMap.values()) {
            int index = emavo.getIndex();
            if (index == 0) return false;
            indexList.add(index);
        }
        HashSet<Integer> distinctData = new HashSet<>(indexList);
        return distinctData.size() == indexList.size();
    }


    private class UsablePercentAdapter extends RecyclerView.Adapter<UsagePercentViewHolder> {
        private String[] rowDataList;

        UsablePercentAdapter(String[] checks) {
            this.rowDataList = checks;
        }

        @NonNull
        @Override
        public UsagePercentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.usage_row, parent, false);
            return new UsagePercentViewHolder(view, rowDataList.length);
        }

        @Override
        public void onBindViewHolder(@NonNull UsagePercentViewHolder holder, int position) {
            String item = rowDataList[position];
            holder.bindString(item);
        }

        @Override
        public int getItemCount() {
            return rowDataList.length;
        }
    }

    private class UsagePercentViewHolder extends RecyclerView.ViewHolder {

        private TextView selected, selectedIndex;
        private Spinner indexSpinner;
        private RadioGroup rg;
        private ArrayList<String> indexes;
        private ArrayAdapter spinnerAdapter;

        UsagePercentViewHolder(View itemView, int size) {
            super(itemView);
            selected = itemView.findViewById(R.id.selectedText);
            selectedIndex = itemView.findViewById(R.id.selectedIndex);
            indexSpinner = itemView.findViewById(R.id.indexspinner);
            rg = itemView.findViewById(R.id.percentradioGroup);

            indexes = new ArrayList<>();
            indexes.add("선택");
            for (int i = 1; i <= size; i++) {
                indexes.add(i + "");
            }
            spinnerAdapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, indexes);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            indexSpinner.setAdapter(spinnerAdapter);
        }

        void bindString(final String item) {
            selected.setText(item);

            final EMAVO emavo = emavoMap.get(item);

            emavo.setPercent(20);

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton rb = itemView.findViewById(checkedId);
                    emavo.setPercent(Integer.parseInt(rb.getText().toString().replace("%", "")));
                    Log.i("dialog1", emavoMap.get(item).toString());
                }
            });


            indexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String text = indexSpinner.getItemAtPosition(position).toString();
                    selectedIndex.setText(text);
                    Log.i("dialog", text);
                    int index;
                    if (text.equals("선택")) {
                        index = 0;
                    } else {
                        index = Integer.parseInt(text);
                    }
                    emavo.setIndex(index);
                    Log.i("dialog1", emavoMap.get(item).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });
            Log.i("dialog", "bindString()" + emavo.toString());

        }
    }


    public String getDateStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return sdfNow.format(date);
    }

    public String getNow() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        return dateFormat.format(date);
    }

}