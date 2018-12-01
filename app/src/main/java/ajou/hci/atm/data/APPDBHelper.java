package ajou.hci.atm.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ajou.hci.atm.model.ActivityVO;
import ajou.hci.atm.model.AppLogVO;
import ajou.hci.atm.model.SumVO;

public class APPDBHelper extends SQLiteOpenHelper implements DBHelperInterface {
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private static final String SQL_CREATE_APP_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.AppEntry.TABLE_NAME + " (" +
                    FeedReaderContract.AppEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedReaderContract.AppEntry.COLUMN_NAME_UID + " TEXT," +
                    FeedReaderContract.AppEntry.COLUMN_NAME_DATE + " TEXT," +
                    FeedReaderContract.AppEntry.COLUMN_NAME_STIME + " TEXT," +
                    FeedReaderContract.AppEntry.COLUMN_NAME_ETIME + " TEXT," +
                    FeedReaderContract.AppEntry.COLUMN_NAME_PACKAGENAME + " TEXT," +
                    FeedReaderContract.AppEntry.COLUMN_NAME_TOTAL + " INTEGER," +
                    FeedReaderContract.AppEntry.COLUMN_NAME_ISINUSABLE + " TEXT," +
                    FeedReaderContract.AppEntry.COLUMN_NAME_PACKAGEFULLNAME + " TEXT)";

    private ACTIVITYDBHelper activitydbHelper;
    private TIMECOUNTERDBHelper timecounterdbHelper;
    DateFormat minFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public FirebaseUser user;

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public APPDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        activitydbHelper = new ACTIVITYDBHelper(context, "ACTIVITY.db", null, 1);
        timecounterdbHelper = new TIMECOUNTERDBHelper(context, "TIMECOUNTER.db", null, 1);

        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        db.execSQL(SQL_CREATE_APP_ENTRIES);


    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String uid, String date, AppLogVO avo) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        //DB에 입력한 값으로 행 추가
        //Log.i("APP INSERT@@@@@@@@@@", avo.toString());
        db.execSQL("INSERT INTO " + FeedReaderContract.AppEntry.TABLE_NAME +
                " VALUES(null, '" + uid + "', '" + date + "', '" + avo.getStime() + "','" + avo.getEtime() + "','" + avo.getPackageName() + "','" + avo.getTotal() + "','" + avo.getIsInUsable() + "','" + avo.getPackageFullName() + "' );");

        AppLogVO tmp = new AppLogVO();

        try (Cursor cursor = db.rawQuery("SELECT * FROM APP where uid=" + "'" + uid + "' and date = '" + date + "' and strftime('%Y-%m-d  %H:%M:%S',etime) >= " + "strftime('%Y-%m-d  %H:%M:%S','" + avo.getStime() + "')",null)) {
            while (cursor.moveToNext()) {
                tmp.setId(cursor.getInt(0));
                tmp.setPackageName(cursor.getString(5));
                tmp.setStime(cursor.getString(3));
                tmp.setEtime(cursor.getString(4));
                tmp.setTotal(cursor.getInt(6));
                tmp.setIsInUsable(cursor.getString(7));
                tmp.setPackageFullName(cursor.getString(8));

                //db.execSQL("delete from APP where _id='" + tmp.getId() + "'");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public AppLogVO getLastVO(String uid, String date) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        AppLogVO avo = new AppLogVO();

        try (Cursor cursor = db.rawQuery("SELECT * FROM APP where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                avo.setId(cursor.getInt(0));
                avo.setPackageName(cursor.getString(5));
                avo.setStime(cursor.getString(3));
                avo.setEtime(cursor.getString(4));
                avo.setTotal(cursor.getInt(6));
                avo.setIsInUsable(cursor.getString(7));
                avo.setPackageFullName(cursor.getString(8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return avo;
    }

    public int getTotalCount(String uid, String date) {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT count(*) FROM APP where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

//    public String getResultWithUIDAndDate(String uid, String date) {
//        // 읽기가 가능하게 DB 열기
//        SQLiteDatabase db = getReadableDatabase();
//        StringBuilder result = new StringBuilder();
//        try (Cursor cursor = db.rawQuery("SELECT * FROM APP where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
//            while (cursor.moveToNext()) {
//                result.append(cursor.getString(0))
//                        .append(cursor.getString(1))
//                        .append(cursor.getString(2))
//                        .append(cursor.getString(3))
//                        .append(cursor.getString(4))
//                        .append(cursor.getString(5))
//                        .append(cursor.getInt(6))
//                        .append("\n");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return result.toString();
//    }

    public ArrayList<AppLogVO> getAppVOs(String uid, String date) {
        ArrayList<AppLogVO> appLogVOArrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM APP where uid = " + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                AppLogVO avo = new AppLogVO();
                avo.setId(cursor.getInt(0));
                avo.setPackageName(cursor.getString(5));
                avo.setStime(cursor.getString(3));
                avo.setEtime(cursor.getString(4));
                avo.setTotal(cursor.getInt(6));
                avo.setIsInUsable(cursor.getString(7));
                avo.setPackageFullName(cursor.getString(8));

                appLogVOArrayList.add(avo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return appLogVOArrayList;
    }

    public int getTotal(String uid, String date) {
        SQLiteDatabase db = getReadableDatabase();
        int total = 0;
        try (Cursor cursor = db.rawQuery("SELECT total FROM APP where uid = " + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                total += cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return total / 60;
    }

    public AppLogVO getAppVOWithTimeAndName(String uid, String dateStr, String pName, String timeStamp) {
        SQLiteDatabase db = getReadableDatabase();
        AppLogVO avo = null;
        try (Cursor cursor = db.rawQuery("SELECT * FROM APP where uid = " + "'" + uid + "' and date = " + "'" + dateStr + "' and packageName='" + pName + "' and strftime('%Y-%m-d  %H:%M:%S',stime) <= " + "strftime('%Y-%m-d  %H:%M:%S','" + timeStamp + "') and etime='null'", null)) {
            while (cursor.moveToNext()) {
                avo = new AppLogVO();
                avo.setId(cursor.getInt(0));
                avo.setPackageName(cursor.getString(5));
                avo.setStime(cursor.getString(3));
                avo.setEtime(cursor.getString(4));
                avo.setTotal(cursor.getInt(6));
                avo.setIsInUsable(cursor.getString(7));
                avo.setPackageFullName(cursor.getString(8));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return avo;
    }

    public AppLogVO getAppVOEqual(String uid, String dateStr, String pName, String timeStamp) {
        SQLiteDatabase db = getReadableDatabase();
        AppLogVO avo = null;
        try (Cursor cursor = db.rawQuery("SELECT * FROM APP where uid = " + "'" + uid + "' and date = " + "'" + dateStr + "' and packageName='" + pName + "' and stime = '" + timeStamp + "'", null)) {
            ArrayList<AppLogVO> duplicates = new ArrayList<>();
            try {
                while (cursor.moveToNext()) {
                    avo = new AppLogVO();
                    avo.setId(cursor.getInt(0));
                    avo.setPackageName(cursor.getString(5));
                    avo.setStime(cursor.getString(3));
                    avo.setEtime(cursor.getString(4));
                    avo.setTotal(cursor.getInt(6));
                    avo.setIsInUsable(cursor.getString(7));
                    avo.setPackageFullName(cursor.getString(8));
                    duplicates.add(avo);
                    //Log.i("sy2399", "equal" + avo.toString());

                }

                if (duplicates.size() > 1) {
                    for (int i = 0; i < duplicates.size(); i++)
                        db.execSQL("delete from APP where _id='" + duplicates.get(i).getId() + "'");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return avo;
        }
    }

    public ArrayList<AppLogVO> getAppVOWithSETime(String uid, String dateStr, String stime, String etime) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<AppLogVO> appLogVOArrayList = new ArrayList<>();

        try (Cursor cursor = db.rawQuery("SELECT * FROM APP where uid = " + "'" + uid + "' and date = " + "'" + dateStr + "' and strftime('%Y-%m-d  %H:%M:%S',stime) >= " + "strftime('%Y-%m-d  %H:%M:%S','" + stime + "') and strftime('%Y-%m-d  %H:%M:%S', etime) <= strftime('%Y-%m-d  %H:%M:%S','" + etime + "')", null)) {
            while (cursor.moveToNext()) {
                AppLogVO avo = new AppLogVO();
                avo.setId(cursor.getInt(0));
                avo.setPackageName(cursor.getString(5));
                avo.setStime(cursor.getString(3));
                avo.setEtime(cursor.getString(4));
                avo.setTotal(cursor.getInt(6));
                avo.setIsInUsable(cursor.getString(7));
                avo.setPackageFullName(cursor.getString(8));

                db.execSQL("UPDATE APP SET " + FeedReaderContract.AppEntry.COLUMN_NAME_ISINUSABLE + "='true' WHERE _id=" + cursor.getInt(0));

                //Log.i("APPDB~~~~", stime + "  " + etime + " ////////  " + avo.getStime() + "   " + avo.getEtime());
                appLogVOArrayList.add(avo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return appLogVOArrayList;
    }

    public ArrayList<AppLogVO> getAppVOsWithFlag(String uid, String date) {
        ArrayList<AppLogVO> appLogVOArrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT * FROM APP where uid = " + "'" + uid + "' and date = '" + date + "' and isInUsable ='true'", null)) {
            while (cursor.moveToNext()) {
                AppLogVO avo = new AppLogVO();
                avo.setId(cursor.getInt(0));
                avo.setPackageName(cursor.getString(5));
                avo.setStime(cursor.getString(3));
                avo.setEtime(cursor.getString(4));
                avo.setTotal(cursor.getInt(6));
                avo.setIsInUsable(cursor.getString(7));
                avo.setPackageFullName(cursor.getString(8));

                appLogVOArrayList.add(avo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return appLogVOArrayList;
    }


    public void updateVO(String uid, String dateStr, AppLogVO tmpVO, String timeStamp) {
        SQLiteDatabase db = getWritableDatabase();
        String isInUsable = "false";

        try {
            long diff = dateFormat.parse(timeStamp).getTime() - dateFormat.parse(tmpVO.getStime()).getTime();


            diff = (diff / 1000);//DB에는 초 단위로 저장!!

            if (diff < 0) {
                diff = 0;
            }
            if (diff > (3600 * 10)) {
                diff = 0;
            }
            //###########################################
            //diff값을 넣을 때, Activity의 flag가 true인 데이터만 불러와서 해당 시간과 비교해서 그 안에 포함되면 flag를 true로 변경

            ArrayList<ActivityVO> usableTimes = activitydbHelper.getActivityWithTime(uid, dateStr);

            for (int i = 0; i < usableTimes.size(); i++) {
                String activitysTime = usableTimes.get(i).getStartTime();
                String activityeTime = usableTimes.get(i).getEndTime();
                long asTime = dateFormat.parse(activitysTime).getTime();
                long aeTime = dateFormat.parse(activityeTime).getTime();
                long appTime = dateFormat.parse(timeStamp).getTime();
                if (appTime - asTime >= 0 && aeTime - appTime >= 0) {
                    isInUsable = "true";
                }
            }
            //###########################################


            //
            //tmpVO.getId() -1;

            AppLogVO afterTMP = new AppLogVO();
            if(tmpVO.getId()!=0){

                try (Cursor cursor = db.rawQuery("SELECT * FROM APP where _id = "+ "'" + (tmpVO.getId()+1) + "'", null)) {
                    while (cursor.moveToNext()) {

                        afterTMP.setId(cursor.getInt(0));
                        afterTMP.setPackageName(cursor.getString(5));
                        afterTMP.setStime(cursor.getString(3));
                        afterTMP.setEtime(cursor.getString(4));
                        afterTMP.setTotal(cursor.getInt(6));
                        afterTMP.setIsInUsable(cursor.getString(7));
                        afterTMP.setPackageFullName(cursor.getString(8));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //Log.i("Exception", e.getMessage());

                }

            }

            if(afterTMP.getStime() !=null){
                long atime = dateFormat.parse(afterTMP.getStime()).getTime();
                long now = dateFormat.parse(timeStamp).getTime();
                if(now < atime){
                    db.execSQL("UPDATE APP SET etime = '" + timeStamp + "', total=" + diff + ",isInUsable = '" + isInUsable + "' WHERE uid='" + uid + "' and date ='" + dateStr + "' and _id=" + tmpVO.getId() + "");

                    long start = minFormat.parse(tmpVO.getStime().substring(0, 16)).getTime();
                    long end = minFormat.parse(timeStamp.substring(0, 16)).getTime();

                    long start_m = start / 60000;
                    long end_m = end / 60000;
                    //Log.i("bokim", "start " +tmpVO.getStime().substring(0,16) +"  "+ start_m + "  end " +timeStamp.substring(0,16)+"  "+ end_m + "  " + tmpVO.getPackageName());

                    if (end_m != start_m) {
                        for (long i = start_m; i <= end_m; i++) {

                            SumVO tmpvo = timecounterdbHelper.getEqual(user.getUid(), dateStr, i + "");

                            if (tmpvo == null) {
                                //Log.i("bokim", "timeCounter" + i);
                                SumVO svo = new SumVO();
                                svo.setMin(i);
                                svo.setFlag(1);
                                //svo.setClasses();
                                timecounterdbHelper.insert(user.getUid(), dateStr, svo);
                            }
                        }

                    }
                }
            }


            //이때 phoneUsageTime & UsableTime Summary 디비 등록?



        } catch (Exception e) {
            e.printStackTrace();
            //Log.i("Exception", e.getMessage());
        }
        //timestamp - tmpVo.getStime --> total
    }

    @Override
    public Cursor raw() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM APP", new String[]{});
    }


}

