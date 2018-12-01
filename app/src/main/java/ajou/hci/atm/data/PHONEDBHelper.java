package ajou.hci.atm.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ajou.hci.atm.model.PhoneVO;

public class PHONEDBHelper extends SQLiteOpenHelper implements DBHelperInterface {

    private static final String SQL_CREATE_PHONEUSAGE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.PhoneUsageEntry.TABLE_NAME + " (" +
                    FeedReaderContract.PhoneUsageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedReaderContract.PhoneUsageEntry.COLUMN_NAME_UID + " TEXT," +
                    FeedReaderContract.PhoneUsageEntry.COLUMN_NAME_DATE + " TEXT," +
                    FeedReaderContract.PhoneUsageEntry.COLUMN_NAME_DAYOFWEEK + " TEXT," +
                    FeedReaderContract.PhoneUsageEntry.COLUMN_NAME_TIMETABLE + " TEXT," +
                    FeedReaderContract.PhoneUsageEntry.COLUMN_NAME_TOTAL + " TEXT," +
                    FeedReaderContract.PhoneUsageEntry.COLUMN_NAME_TYPE + " TEXT," +
                    FeedReaderContract.PhoneUsageEntry.COLUMN_NAME_PERCENT + " TEXT," +
                    FeedReaderContract.PhoneUsageEntry.COLUMN_NAME_STIME + " TEXT," +
                    FeedReaderContract.PhoneUsageEntry.COLUMN_NAME_ETIME + " TEXT)";

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public PHONEDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL(SQL_CREATE_PHONEUSAGE_ENTRIES);


    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String uid, String date, PhoneVO pvo) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        //DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO " + FeedReaderContract.PhoneUsageEntry.TABLE_NAME +
                " VALUES(null, '" + uid + "', '" + date + "', '" + pvo.getDayOfWeek() + "','" + pvo.getTimeTable() + "','" + pvo.getTotal() + "','" + pvo.getType() + "','" + pvo.getPercent() + "','" + pvo.getsTime() + "','" + pvo.geteTime() + "' );");


    }

    public ArrayList<PhoneVO> getPhoneVOs(String uid, String date) {
        ArrayList<PhoneVO> phoneVOS = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT * FROM PHONE_USAGE where uid = " + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                PhoneVO pvo = new PhoneVO();
                pvo.setTimeTable(cursor.getString(4));
                pvo.setType(cursor.getString(6));
                pvo.setTotal(cursor.getString(5));
                pvo.setDayOfWeek(cursor.getString(3));
                pvo.setDate(cursor.getString(2));
                pvo.setPercent(cursor.getString(7));
                pvo.setsTime(cursor.getString(8));
                pvo.seteTime(cursor.getString(9));
                phoneVOS.add(pvo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return phoneVOS;
    }

//    public PhoneVO getPhoneVOsWithTimeTable(String uid, String date, String timeTable) {
//        SQLiteDatabase db = getReadableDatabase();
//        PhoneVO pvo = new PhoneVO();
//
//        try (Cursor cursor = db.rawQuery("SELECT * FROM PHONE_USAGE where uid = " + "'" + uid + "' and date = '" + date + "' and timeTable='" + timeTable + "'", null)) {
//            while (cursor.moveToNext()) {
//
//                pvo.setTimeTable(cursor.getString(4));
//                pvo.setType(cursor.getString(6));
//                pvo.setTotal(cursor.getString(5));
//                pvo.setDayOfWeek(cursor.getString(3));
//                pvo.setDate(cursor.getString(2));
//                pvo.setPercent(cursor.getString(7));
//                pvo.setsTime(cursor.getString(8));
//                pvo.seteTime(cursor.getString(9));
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return pvo;
//    }


//    public void updateVO(String uid, String dateStr, PhoneVO pvo) {
//        SQLiteDatabase db = getWritableDatabase();
//        db.execSQL("UPDATE PHONE_USAGE SET total = '" + pvo.getTotal()
//                + "', percent=" + pvo.getPercent() + ", type = '" + pvo.getType()
//                + "'  WHERE uid='" + uid + "' and date ='" + dateStr
//                + "' and timeTable='" + pvo.getTimeTable() + "'");
//    }

    @Override
    public Cursor raw() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM PHONE_USAGE", new String[]{});
    }
}

