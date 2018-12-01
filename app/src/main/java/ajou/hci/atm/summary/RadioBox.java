package ajou.hci.atm.summary;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ajou.hci.atm.R;
import ajou.hci.atm.data.ACTIVITYDBHelper;
import ajou.hci.atm.data.EMADBHelper;
import ajou.hci.atm.model.EMAVO;

public class RadioBox extends DialogFragment {

    //RadioBox
    private RadioGroup rg;

    //dismissListener
    private DialogInterface.OnDismissListener onDismissListener;

    private EMADBHelper emadbHelper;
    private ACTIVITYDBHelper activitydbHelper;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static RadioBox newInstance(String sdate, String edate, ArrayList<EMAVO> emavos) {
        Bundle args = new Bundle();
        args.putSerializable("SDATE", sdate);
        args.putSerializable("EDATE", edate);
        args.putSerializable("checkboxes", emavos);
        RadioBox fragment = new RadioBox();
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
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.radio_box, null);

        emadbHelper = new EMADBHelper(getContext(), "EMA.db", null, 1);
        activitydbHelper = new ACTIVITYDBHelper(getContext(), "ACTIVITY.db", null, 1);

        final String stime = (String)getArguments().getSerializable("SDATE");
        final ArrayList<EMAVO> checkboxes = (ArrayList<EMAVO>)getArguments().getSerializable("checkboxes");

        //라디오박스 선택 값 보여주기
        rg = v.findViewById(R.id.percentradioGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });


        if(getActivity()!=null){

            return new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
                    .setView(v)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog1, int which) {

                                    //radiobutton은 각각 선언이 안됨 --> 그룹으로 호출 후 사용.
                                    int id = rg.getCheckedRadioButtonId();

                                    //getCheckedRadioButtonId() 의 리턴값은 선택된 RadioButton 의 id 값.
                                    RadioButton rb = (RadioButton) v.findViewById(id);




                                    ArrayList<EMAVO> emavos = emadbHelper.getEMAVOs(user.getUid(), getDateStr());


                                    boolean flag = false;
                                    if (emavos.size() == 0) {
                                        flag = true;
                                    } else {
                                        for (int i = 0; i < emavos.size(); i++) {
                                            if (!emavos.get(i).getStime().equals(stime)) {
                                                flag = true;
                                            }
                                        }
                                    }

                                    if (flag) {
                                        //Log.i("sy2399", "checkboxes " + checkboxes);
                                        for(int i=0;i<checkboxes.size();i++){
                                            checkboxes.get(i).setLikert(rb.getText().toString());
                                            checkboxes.get(i).setCheckTime(getNow() + "");
                                            emadbHelper.insert(user.getUid(), getDateStr(), checkboxes.get(i));
                                        }
                                    }else{

                                    }

                                    //1.Activity Flag 변경!!
                                    activitydbHelper.updateWithSTime(user.getUid(), getDateStr(),stime);
                                    //2.EMA 넣을 때 sqLite에 있는 Location, AppLog 불러와서 Firebase에 같이 넣기!
                                    //Firebase > Summary > Activity > Detail > EMA 시작 시간 >


                                    //*****************************************************

                                    //라디오박스 닫기
                                    dialog1.dismiss();
                                    Toast.makeText(getContext(), "저장 되었습니다!", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .setNegativeButton(android.R.string.no,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                    .create();
        }

        return null;

    }

    private void sendResult(int resultCode, String changed) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("DATA_CHANGED", "changed");

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
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

    public String getNow() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        return dateFormat.format(date);
    }
}