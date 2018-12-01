package ajou.hci.atm.summary;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ajou.hci.atm.R;
import ajou.hci.atm.model.EMAVO;


public class MyCustomDialog extends DialogFragment implements DialogInterface {

    private boolean checkedResult;
    //editText
    private String typedText;

    //checkBoxes
    //private CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8, cb9, cb10, cb11, cb12;


    public static MyCustomDialog newInstance(String sdate, String edate){
        Bundle args = new Bundle();
        args.putSerializable("SDATE", sdate);
        args.putSerializable("EDATE", edate);

        MyCustomDialog fragment = new MyCustomDialog();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.dialog_my_custom, container, false);
        TextView tv = view.findViewById(R.id.heading2);
        final String sdate = (String)getArguments().getSerializable("SDATE");
        final String edate = (String)getArguments().getSerializable("EDATE");

        tv.setText(sdate.substring(11,16) + " ~ " + edate.substring(11,16));
        final EditText editText = view.findViewById(R.id.editText);

        final CheckBox cb1 = view.findViewById(R.id.checkBox);
        final CheckBox cb2 = view.findViewById(R.id.checkBox2);
        final CheckBox cb3 = view.findViewById(R.id.checkBox3);
        final CheckBox cb4 = view.findViewById(R.id.checkBox4);
        final CheckBox cb5 = view.findViewById(R.id.checkBox5);
        final CheckBox cb6 = view.findViewById(R.id.checkBox6);
        final CheckBox cb7 = view.findViewById(R.id.checkBox7);
        final CheckBox cb8 = view.findViewById(R.id.checkBox8);
        final CheckBox cb9 = view.findViewById(R.id.checkBox9);
        final CheckBox cb10 = view.findViewById(R.id.checkBox10);
        final CheckBox cb11 = view.findViewById(R.id.checkBox11);
        final CheckBox cb12 = view.findViewById(R.id.checkBox12);

        //checkbox ArrayList
        final ArrayList<String> selection = new ArrayList<>();
        final HashMap<String, Integer> result = new HashMap<>();

        Button action_ok = view.findViewById(R.id.action_ok);
        Button action_negative = view.findViewById(R.id.action_negative);
        action_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDismiss(MyCustomDialog.this);

            }
        });
        action_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clickView) {

                if (cb1.isChecked()) {
                    selection.add("공부/과제");
                }
                if (cb2.isChecked()) {
                    selection.add("알바");
                }
                if (cb3.isChecked()) {
                    selection.add("수면");
                }
                if (cb4.isChecked()) {
                    selection.add("운동/산책");
                }
                if (cb5.isChecked()) {
                    selection.add("문화생활");
                }
                if (cb6.isChecked()) {
                    selection.add("식사/음주");
                }
                if (cb7.isChecked()) {
                    selection.add("수업");
                }
                if (cb8.isChecked()) {
                    selection.add("게임(폰)");
                }
                if (cb9.isChecked()) {
                    selection.add("게임(PC)");
                }
                if (cb10.isChecked()) {
                    selection.add("동아리 활동");
                }
                if (cb11.isChecked()) {
                    selection.add("수다");
                }
                if (cb12.isChecked()) {
                    selection.add("집안일(청소/빨래)");
                }

                typedText = editText.getText().toString();
                //selection.add(typedText);
                checkedResult = IsCheckButtonClicked(view);

                if (checkedResult || !typedText.equals("")) {

                    EMAVO emavo = new EMAVO();
                    emavo.setStime(sdate);
                    emavo.setEtime(edate);
                    String checked = selection.toString().replace("[", "");
                    checked = checked.replace("]", "");
                    checked = checked.replace(" ", "");
                    if(checked != ""){
                        emavo.setActivity(checked+ "," + typedText);

                    }else{
                        emavo.setActivity(typedText);
                    }


                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    //RadioBox dialog = RadioBox.newInstance(sdate, edate, emavo.getCheckboxes());

                    UsablePercentDialog dialog = UsablePercentDialog.newInstance(sdate, edate, emavo.getActivity());

                    dialog.show(manager, "CheckBox!! --> Radio Group");
                    dialog.setCancelable(true);
                    onDismiss(MyCustomDialog.this);
                } else if (!checkedResult) {
                    if (typedText.equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("체크박스가 비었어요!");
                        builder.setMessage("체크박스에 본인의 활동을 표시해 주세요 :)");
                        builder.setPositiveButton("확인",
                                new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builder.show();
                    }
                }

            }

        });

        return view;

    }
    private boolean IsCheckButtonClicked(View view){
        boolean selectionExists = false;
        selectionExists = (((CheckBox) view.findViewById(R.id.checkBox)).isChecked() ||
                ((CheckBox) view.findViewById(R.id.checkBox2)).isChecked() ||
                ((CheckBox) view.findViewById(R.id.checkBox3)).isChecked() ||
                ((CheckBox) view.findViewById(R.id.checkBox4)).isChecked() ||
                ((CheckBox) view.findViewById(R.id.checkBox5)).isChecked() ||
                ((CheckBox) view.findViewById(R.id.checkBox6)).isChecked() ||
                ((CheckBox) view.findViewById(R.id.checkBox7)).isChecked() ||
                ((CheckBox) view.findViewById(R.id.checkBox8)).isChecked() ||
                ((CheckBox) view.findViewById(R.id.checkBox9)).isChecked() ||
                ((CheckBox) view.findViewById(R.id.checkBox10)).isChecked() ||
                ((CheckBox) view.findViewById(R.id.checkBox11)).isChecked() ||
                ((CheckBox) view.findViewById(R.id.checkBox12)).isChecked());
        return selectionExists;

    }


    @Override
    public void cancel() {

    }

}