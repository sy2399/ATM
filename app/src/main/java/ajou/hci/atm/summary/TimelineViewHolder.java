package ajou.hci.atm.summary;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import ajou.hci.atm.R;
import ajou.hci.atm.data.APPDBHelper;
import ajou.hci.atm.model.APPTEMP;
import ajou.hci.atm.model.AppLogVO;
import ajou.hci.atm.model.TimelineRow;

public class TimelineViewHolder extends RecyclerView.ViewHolder {
    private TimelineRow row;

    private ImageView rowImage;
    private TextView rowTitle;
    private TextView rowDescription;
    private View rowUpperLine;
    private View rowLowerLine;
    private LinearLayout linearLayout;
    private ImageView icon1;
    private ImageView icon2;
    private ImageView icon3;
    private Button emaBtn;

    private APPDBHelper appdbHelper;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    private Context context;
    private PackageManager pm;

    //private ArrayList<APPTEMP> tempList = new ArrayList<>();

    private int listSize;
    private View backgroundView;

    //private TextView poiTextView;
    private TextView addrTextView;

    TimelineViewHolder(View itemView, int size) {
        super(itemView);

        //TextView rowDate = (TextView) view.findViewById(R.id.crowDate);
        rowTitle = itemView.findViewById(R.id.crowTitle);
        rowDescription = itemView.findViewById(R.id.crowDesc);
        rowImage = itemView.findViewById(R.id.appImg);
        rowUpperLine = itemView.findViewById(R.id.crowUpperLine);
        rowLowerLine = itemView.findViewById(R.id.crowLowerLine);
        linearLayout = itemView.findViewById(R.id.imageView);
        icon1 = itemView.findViewById(R.id.icon1);
        icon2 = itemView.findViewById(R.id.icon2);
        icon3 = itemView.findViewById(R.id.icon3);

        emaBtn = itemView.findViewById(R.id.emaBtn);

        appdbHelper = new APPDBHelper(itemView.getContext(), "APP.db", null, 1);

        context = itemView.getContext();
        pm = context.getPackageManager();

        listSize = size;
        backgroundView = itemView.findViewById(R.id.crowBackground);

        //poiTextView = itemView.findViewById(R.id.poiTextView);
        addrTextView = itemView.findViewById(R.id.addrTextView);
    }

    public String getDateStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return sdfNow.format(date);
    }

    private ArrayList<APPTEMP> getSortedAppLogs(String temps, String tempe) {

        ArrayList<AppLogVO> apps = appdbHelper.getAppVOWithSETime(user.getUid(), getDateStr(), temps, tempe);
        AppComparator comp = new AppComparator();
        Collections.sort(apps, comp);

        final HashMap<String, Long> appsMap = new HashMap<>();
        for (int k = 0; k < apps.size(); k++) {
            AppLogVO tmpVO = apps.get(k);
            if (tmpVO.getTotal() > 0) {
                String pName = tmpVO.getPackageFullName();
                if (appsMap.get(pName) == null)
                    appsMap.put(pName, (long) tmpVO.getTotal());
                else {
                    long val = appsMap.get(pName);
                    appsMap.put(pName, val + (long) tmpVO.getTotal());
                }
            }
        }
        Iterator it = sortHashMapByValues(appsMap).iterator();
        int counter = 0;

        ArrayList<APPTEMP> tempList = new ArrayList<>();


        while (it.hasNext() && counter < 4) {
            String temp = (String) it.next();
            APPTEMP at = new APPTEMP();
            at.setPackageName(temp);
            if (appsMap.get(temp) != null)
                at.setTotal(appsMap.get(temp));
            tempList.add(at);
            counter++;
        }

        //Log.i("sy2399", "getSortedApp" + tempList.size());
        AppTEMPComparator comparator = new AppTEMPComparator();
        Collections.sort(tempList, comparator);

        return tempList;

    }

    private DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    void bindTimeline(TimelineRow item) {

        //final boolean[] checkedFlag = {false};
        row = item;
        //Log.i("imsso","bindTimeLine " + item.toString());

        String sDate = row.getDescription().split(" ")[0];
        String eDate = row.getDescription().split(" ")[2];

        String temps = getDateStr() + " " + sDate + ":00";
        String tempe = getDateStr() + " " + eDate + ":00";

        try {
            Date s = dateFormat2.parse(temps);
            Date e = dateFormat2.parse(tempe);

            if (e.getTime() - s.getTime() >= (10000 * 6 * 30) && item.getAvo().getValue().equals("3")) {

                ArrayList<APPTEMP> tempList = getSortedAppLogs(temps, tempe);

                bindIcons(tempList);

                if (row.getAvo().getFlag().equals("true")) {
                    emaBtn.setVisibility(View.VISIBLE);
                    emaBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyCustomDialog dialog = MyCustomDialog.newInstance(row.getAvo().getStartTime(), row.getAvo().getEndTime());

                            FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                            dialog.show(fm, "MOVE");
                            dialog.setCancelable(true);
                            emaBtn.setTag("button");
                            //emaBtn.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            } else {
                emaBtn.setVisibility(View.INVISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //final float scale = context.getResources().getDisplayMetrics().density;

        int position = getAdapterPosition();

        //int pixels = (int) (row.getBellowLineSize() * scale + 0.5f);

        if (position == 0 && listSize == 1) {
            rowUpperLine.setVisibility(View.INVISIBLE);
            rowLowerLine.setVisibility(View.INVISIBLE);

        } else if (position == 0) {
            rowUpperLine.setVisibility(View.INVISIBLE);
            rowLowerLine.setVisibility(View.VISIBLE);
            rowLowerLine.setBackgroundColor(row.getBellowLineColor());

            //rowLowerLine.getLayoutParams().width = pixels;

        } else if (position > 0 && position == listSize - 1) {
            rowUpperLine.setVisibility(View.VISIBLE);
            rowUpperLine.setBackgroundColor(row.getBellowLineColor());
            rowLowerLine.setVisibility(View.INVISIBLE);

            //rowUpperLine.getLayoutParams().width = pixels;
        } else {
            rowUpperLine.setVisibility(View.VISIBLE);
            rowUpperLine.setBackgroundColor(row.getBellowLineColor());
            rowLowerLine.setVisibility(View.VISIBLE);
            rowLowerLine.setBackgroundColor(row.getBellowLineColor());

//            rowUpperLine.getLayoutParams().width = pixels;
//            rowLowerLine.getLayoutParams().width = pixels;

        }


        if (row.getTitle() == null)
            rowTitle.setVisibility(View.GONE);
        else {
            rowTitle.setText(row.getTitle());
            if (row.getTitleColor() != 0)
                rowTitle.setTextColor(row.getTitleColor());
        }

        if (row.getDescription() == null)
            rowDescription.setVisibility(View.GONE);
        else {
            rowDescription.setText(row.getDescription());
            if (row.getDescriptionColor() != 0)
                rowDescription.setTextColor(row.getDescriptionColor());
        }


        if (row.getImage() != null) {
            rowImage.setImageBitmap(row.getImage());
        }

//        pixels = (int) (row.getImageSize() * scale + 0.5f);
//        rowImage.getLayoutParams().width = pixels;
//        rowImage.getLayoutParams().height = pixels;


        if (row.getBackgroundColor() == 0)
            backgroundView.setBackground(null);
        else {
//            if (row.getBackgroundSize() == -1) {
//                backgroundView.getLayoutParams().width = pixels;
//                backgroundView.getLayoutParams().height = pixels;
//            } else {
//                int BackgroundPixels = (int) (row.getBackgroundSize() * scale + 0.5f);
//                backgroundView.getLayoutParams().width = BackgroundPixels;
//                backgroundView.getLayoutParams().height = BackgroundPixels;
//            }
            GradientDrawable background = (GradientDrawable) backgroundView.getBackground();
            if (background != null) {
                background.setColor(row.getBackgroundColor());
            }
        }

//            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) rowImage.getLayoutParams();
//            marginParams.setMargins(0, (int) (pixels / 2) * -1, 0, (pixels / 2) * -1);

        //String poiName = row.getPoiName();
        String addrName = row.getAddrName();


        if (addrName == null || addrName.equals("")) {
            addrTextView.setVisibility(View.GONE);
        } else {
            addrTextView.setVisibility(View.VISIBLE);
            addrTextView.setText(addrName);
        }

    }

    private void bindIcons(final ArrayList<APPTEMP> tempList) {

        int size = tempList.size();

        //Log.i("imsso","bindIcons ");

        if (size == 0) {
            linearLayout.setVisibility(View.INVISIBLE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);

            Drawable appIcon1, appIcon2, appIcon3;

            //if (size >= 1) {
            //if(tempList.size() >=1){
            icon1.setVisibility(View.VISIBLE);
            try {
                appIcon1 = pm.getApplicationIcon(tempList.get(0).getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                appIcon1 = context.getDrawable(R.drawable.android);
                e.printStackTrace();
            }
            icon1.setImageDrawable(appIcon1);

            icon1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "" + getAppNameFromPackage(tempList.get(0).getPackageName(), context), Toast.LENGTH_SHORT).show();
                }
            });
            //}
            //}

            if (size >= 2) {
                //if(tempList.size()>=2){
                icon2.setVisibility(View.VISIBLE);

                try {
                    appIcon2 = pm.getApplicationIcon(tempList.get(1).getPackageName());
                } catch (PackageManager.NameNotFoundException e) {
                    appIcon2 = context.getDrawable(R.drawable.android);
                    e.printStackTrace();
                }
                icon2.setImageDrawable(appIcon2);

                icon2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "" + getAppNameFromPackage(tempList.get(1).getPackageName(), context), Toast.LENGTH_SHORT).show();
                    }
                });
                // }
            }

            if (size >= 3) {
                //if(tempList.size() >= 3){
                icon3.setVisibility(View.VISIBLE);
                try {
                    appIcon3 = pm.getApplicationIcon(tempList.get(2).getPackageName());
                } catch (PackageManager.NameNotFoundException e) {
                    appIcon3 = context.getDrawable(R.drawable.android);
                    e.printStackTrace();
                }
                icon3.setImageDrawable(appIcon3);
                icon3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "" + getAppNameFromPackage(tempList.get(2).getPackageName(), context), Toast.LENGTH_SHORT).show();
                    }
                });
                // }
            }
        }
    }

    private List sortHashMapByValues(final HashMap<String, Long> unsortMap) {

        List<String> list = new ArrayList<>(unsortMap.keySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                Object v1 = unsortMap.get(o1);
                Object v2 = unsortMap.get(o2);
                return ((Comparable) v2).compareTo(v1);
            }
        });
        //Collections.reverse(list); // 주석시 내림차순
        return list;
    }

    private class AppComparator implements Comparator<AppLogVO> {

        @Override
        public int compare(AppLogVO first, AppLogVO second) {
            double firstValue = (double) first.getTotal();
            double secondValue = (double) second.getTotal();

            // Order by descending
            return Double.compare(secondValue, firstValue);

            /*
            if (firstValue > secondValue) {
                return -1;
            } else if (firstValue < secondValue) {
                return 1;
            } else {
                return 0;
            }
             */
        }
    }

    private class AppTEMPComparator implements Comparator<APPTEMP> {

        @Override
        public int compare(APPTEMP first, APPTEMP second) {
            long firstValue = first.getTotal();
            long secondValue = second.getTotal();
            // Order by descending
            return Long.compare(secondValue, firstValue);
        }

    }

//    public Bitmap getAppIcon(byte[] b) {
//        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
//        return bitmap;
//    }


    private String getAppNameFromPackage(String packageName, Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> pkgAppsList = context.getPackageManager()
                .queryIntentActivities(mainIntent, 0);

        for (ResolveInfo app : pkgAppsList) {
            if (app.activityInfo.packageName.equals(packageName)) {
                return app.activityInfo.loadLabel(context.getPackageManager()).toString();
            }
        }
        String[] pNames = packageName.split("\\.");
        String result = "";
        for (String pName : pNames) {
            result = pName;
        }
        return result;
    }


}
