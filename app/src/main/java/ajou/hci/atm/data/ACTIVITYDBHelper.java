package ajou.hci.atm.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ajou.hci.atm.model.ActivityVO;

public class ACTIVITYDBHelper extends SQLiteOpenHelper implements DBHelperInterface {
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private static final String SQL_CREATE_ACTIVITY_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.ActivityEntry.TABLE_NAME + " (" +
                    FeedReaderContract.ActivityEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedReaderContract.ActivityEntry.COLUMN_NAME_UID + " TEXT," +
                    FeedReaderContract.ActivityEntry.COLUMN_NAME_DATE + " TEXT," +
                    FeedReaderContract.ActivityEntry.COLUMN_NAME_STIME + " TEXT," +
                    FeedReaderContract.ActivityEntry.COLUMN_NAME_ETIME + " TEXT," +
                    FeedReaderContract.ActivityEntry.COLUMN_NAME_TYPE + " TEXT," +
                    FeedReaderContract.ActivityEntry.COLUMN_NAME_VALUE + " TEXT," +
                    FeedReaderContract.ActivityEntry.COLUMN_NAME_FLAG + " TEXT," +
                    FeedReaderContract.ActivityEntry.COLUMN_NAME_TOTAL + " INTEGER)";

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public ACTIVITYDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL(SQL_CREATE_ACTIVITY_ENTRIES);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String uid, String date, ActivityVO avo) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        //DB에 입력한 값으로 행 추가

        String flag = "false";
        try {
            long diff = dateFormat.parse(avo.getEndTime()).getTime() - dateFormat.parse(avo.getStartTime()).getTime();
            diff = diff / 60000;
            if (avo.getValue().equals("3")) {
                if (diff >= 30) {
                    flag = "true";
                }
            }

            db.execSQL("INSERT INTO " + FeedReaderContract.ActivityEntry.TABLE_NAME +
                    " VALUES(null, '" + uid + "', '" + date + "', '" + avo.getStartTime() + "','" + avo.getEndTime() + "','" + avo.getType() + "','" + avo.getValue() + "','" + flag + "'," + diff + ");");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public int getTotalCount(String uid, String date){
//        int count = 0;
//        SQLiteDatabase db = getReadableDatabase();
//        try (Cursor cursor = db.rawQuery("SELECT count(*) FROM ACTIVITY where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
//            while (cursor.moveToNext()) {
//
//                count = cursor.getInt(0);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return count;
//    }

    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder result = new StringBuilder();

        try (Cursor cursor = db.rawQuery("SELECT * FROM ACTIVITIES", null)) {
            while (cursor.moveToNext()) {
                result.append(cursor.getString(0))
                        .append(cursor.getString(1))
                        .append(cursor.getString(2))
                        .append(cursor.getString(3))
                        .append(cursor.getString(4))
                        .append(cursor.getString(5))
                        .append(cursor.getString(6))
                        .append("  flag : ")
                        .append(cursor.getString(7))
                        .append("  total : ")
                        .append(cursor.getInt(8))
                        .append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력

        return result.toString();
    }

//    public String getResultWithUIDAndDate(String uid, String date) {
//        // 읽기가 가능하게 DB 열기
//        SQLiteDatabase db = getReadableDatabase();
//        StringBuilder result = new StringBuilder();
//        try (Cursor cursor = db.rawQuery("SELECT * FROM ACTIVITIES where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
//            while (cursor.moveToNext()) {
//                result.append(cursor.getString(0))
//                        .append(cursor.getString(1))
//                        .append(cursor.getString(2))
//                        .append(cursor.getString(3))
//                        .append(cursor.getString(4))
//                        .append(cursor.getString(5))
//                        .append(cursor.getString(6))
//                        .append("  flag : ")
//                        .append(cursor.getString(7))
//                        .append("  total : ")
//                        .append(cursor.getInt(8))
//                        .append("\n");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return result.toString();
//    }

    public String getLastTime(String uid, String date) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        try (Cursor cursor = db.rawQuery("SELECT * FROM ACTIVITIES where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                result = cursor.getString(4);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public ActivityVO getLastVO(String uid, String date) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        ActivityVO avo = new ActivityVO();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        try (Cursor cursor = db.rawQuery("SELECT * FROM ACTIVITIES where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                avo.setStartTime(cursor.getString(3));
                avo.setEndTime(cursor.getString(4));
                avo.setType(cursor.getString(5));
                avo.setValue(cursor.getString(6));
                avo.setFlag(cursor.getString(7));
                avo.setTotal(cursor.getInt(8));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return avo;
    }

    public ActivityVO getEqualVO(String uid, String date, ActivityVO activityVO) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        ActivityVO avo = null;

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        try (Cursor cursor = db.rawQuery("SELECT * FROM ACTIVITIES where uid='" + uid + "' and date = '" + date + "' and stime= '" + activityVO.getStartTime() + "' and etime = '" + activityVO.getEndTime() + "'", null)) {
            while (cursor.moveToNext()) {
                avo = new ActivityVO();
                avo.setStartTime(cursor.getString(3));
                avo.setEndTime(cursor.getString(4));
                avo.setType(cursor.getString(5));
                avo.setValue(cursor.getString(6));
                avo.setFlag(cursor.getString(7));
                avo.setTotal(cursor.getInt(8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return avo;
    }

    public ArrayList<ActivityVO> getActivityVOs(String uid, String date) {
        ArrayList<ActivityVO> activityVOArrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM ACTIVITIES where uid = " + "'" + uid + "' and date = " + "'" + date + "'", null)) {
            while (cursor.moveToNext()) {
                ActivityVO avo = new ActivityVO();
                avo.setStartTime(cursor.getString(3));
                avo.setEndTime(cursor.getString(4));
                avo.setType(cursor.getString(5));
                avo.setValue(cursor.getString(6));
                avo.setFlag(cursor.getString(7));
                avo.setTotal(cursor.getInt(8));
                activityVOArrayList.add(avo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return activityVOArrayList;
    }

    public ArrayList<ActivityVO> getActivityWithFlag(String uid, String date) {
        ArrayList<ActivityVO> activityVOArrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM ACTIVITIES where uid = " + "'" + uid + "' and date = " + "'" + date + "' and flag = 'true'", null)) {
            while (cursor.moveToNext()) {
                ActivityVO avo = new ActivityVO();
                avo.setStartTime(cursor.getString(3));
                avo.setEndTime(cursor.getString(4));
                avo.setType(cursor.getString(5));
                avo.setValue(cursor.getString(6));
                avo.setFlag(cursor.getString(7));
                avo.setTotal(cursor.getInt(8));
                activityVOArrayList.add(avo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return activityVOArrayList;
    }

    public int getTotal(String uid, String dateStr) {
        SQLiteDatabase db = getReadableDatabase();
        int total = 0;
        try (Cursor cursor = db.rawQuery("SELECT total FROM ACTIVITIES where uid = " + "'" + uid + "' and date = " + "'" + dateStr + "' and (cast(total as integer)) >= 30", null)) {
            while (cursor.moveToNext()) {
                total += Integer.parseInt(cursor.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return total;
    }

    public ArrayList<ActivityVO> getActivityWithTime(String uid, String date) {
        ArrayList<ActivityVO> activityVOArrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM ACTIVITIES where uid = " + "'" + uid + "' and date = " + "'" + date + "' and total >= 30 and value = '3'", null)) {
            while (cursor.moveToNext()) {
                ActivityVO avo = new ActivityVO();
                avo.setStartTime(cursor.getString(3));
                avo.setEndTime(cursor.getString(4));
                avo.setType(cursor.getString(5));
                avo.setValue(cursor.getString(6));
                avo.setFlag(cursor.getString(7));
                avo.setTotal(cursor.getInt(8));
                activityVOArrayList.add(avo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activityVOArrayList;
    }

    public void updateWithSTime(String uid, String date, String stime) {
        SQLiteDatabase db = getWritableDatabase();
        //Log.i("0zoo!!", "updateWithSTime()");
        try {
            db.execSQL("UPDATE ACTIVITIES SET flag = 'false' WHERE uid='" + uid + "' and date ='" + date + "' and stime='" + stime + "' and value ='3' and flag='true'");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void updateFlagWithStime(String uid, String date, String stime) {
//        SQLiteDatabase db = getWritableDatabase();
//        try{
//            db.execSQL("UPDATE ACTIVITIES SET flag = 'false' WHERE uid='" + uid + "' and date ='" + date + "' and stime='" + stime + "' and value ='3' and flag='true'");
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }

    public void updateVO(String uid, String date, String stime, ActivityVO newVO) {
        //Log.i("0zoo!!", "updateVO() newVO"+ newVO.toString());


        SQLiteDatabase db = getWritableDatabase();
        SQLiteDatabase db2 = getReadableDatabase();
        ActivityVO avo = new ActivityVO();
        try (Cursor cursor = db2.rawQuery("SELECT * FROM ACTIVITIES  WHERE uid='" + uid + "' and date ='" + date + "' and stime='" + stime + "'", null)) {
            while (cursor.moveToNext()) {
                avo.setStartTime(cursor.getString(3));
                avo.setEndTime(cursor.getString(4));
                avo.setType(cursor.getString(5));
                avo.setValue(cursor.getString(6));
                avo.setFlag(cursor.getString(7));
                avo.setTotal(cursor.getInt(8));
            }

            //Log.i("0zoo!!", "updateVO() avo"+ avo.toString());

            try {
                long diff = dateFormat.parse(newVO.getEndTime()).getTime() - dateFormat.parse(stime).getTime();
                diff = diff / 60000;
                String flag = "false";
                if (newVO.getValue().equals("3")) {
                    //Log.i("0zoo!!", "updateVO() equals 3 " + diff);
                    if (diff >= 30) {
                        flag = "true";
                    }
                }
                //Log.i("0zoo!!", "updateVO() flag"+ flag);

                db.execSQL("UPDATE ACTIVITIES SET etime = '" + newVO.getEndTime() + "', total=" + diff + ", flag = '" + flag + "'  WHERE uid='" + uid + "' and date ='" + date + "' and stime='" + stime + "'");

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public Cursor raw() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM ACTIVITIES", new String[]{});
    }
}

