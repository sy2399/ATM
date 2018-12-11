package ajou.hci.atm.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ajou.hci.atm.model.SumVO;

public class TIMECOUNTERDBHelper extends SQLiteOpenHelper implements DBHelperInterface {

    private static final String SQL_CREATE_TIMECOUNTER_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.TimeCounterEntry.TABLE_NAME + " (" +
                    FeedReaderContract.TimeCounterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedReaderContract.TimeCounterEntry.COLUMN_NAME_UID + " TEXT," +
                    FeedReaderContract.TimeCounterEntry.COLUMN_NAME_DATE + " TEXT," +
                    FeedReaderContract.TimeCounterEntry.COLUMN_NAME_MIN + " INTEGER," +
                    FeedReaderContract.TimeCounterEntry.COLUMN_NAME_FLAG + " INTEGER," +
                    FeedReaderContract.TimeCounterEntry.COLUMN_NAME_CLASS + " TEXT)";

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public TIMECOUNTERDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL(SQL_CREATE_TIMECOUNTER_ENTRIES);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String uid, String date, SumVO svo) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        //DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO " + FeedReaderContract.TimeCounterEntry.TABLE_NAME +
                " VALUES(null, '" + uid + "', '" + date + "', " + svo.getMin() + "," + svo.getFlag() + ",'" + svo.getClasses() + "' );");

    }


    SumVO getEqual(String uid, String dateStr, String timeStamp) {
        SQLiteDatabase db = getReadableDatabase();
        SumVO svo = null;
        try (Cursor cursor = db.rawQuery("SELECT * FROM TIMECOUNTER where uid = " + "'" + uid + "' and date = " + "'" + dateStr + "' and min = " + timeStamp + "", null)) {
            while (cursor.moveToNext()) {
                svo = new SumVO();
                svo.setMin(cursor.getInt(3));
                svo.setFlag(cursor.getInt(4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return svo;
    }

    public int getTotalCount(String uid, String date) {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT count(*) FROM TIMECOUNTER where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public ArrayList<SumVO> getSleepList(String uid, String date, long timeStamp) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<SumVO> sumVOArrayList = new ArrayList<>();

        try (Cursor cursor = db.rawQuery("SELECT * FROM TIMECOUNTER where uid=" + "'" + uid + "' and date = '" + date + "' and min <=" + timeStamp + " order by min asc", null)) {
            while (cursor.moveToNext()) {
                SumVO sumVO = new SumVO();
                sumVO.setMin(cursor.getInt(3));
                sumVOArrayList.add(sumVO);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sumVOArrayList;
    }

    public int getTotalInClass(String uid, String date, long start_m, long end_m) {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT count(*), min FROM TIMECOUNTER where uid=" + "'" + uid + "' and date = '" + date + "' and min>=" + start_m + " and min <=" + end_m, null)) {
            while (cursor.moveToNext()) {
                Log.i("sy2399", "getTotalInClass" + start_m +  "  " + end_m + "  " + cursor.getInt(1));

                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public Cursor raw() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM TIMECOUNTER", new String[]{});
    }


}

