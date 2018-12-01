package ajou.hci.atm.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ajou.hci.atm.model.TotalVO;

public class TOTALINFODBHelper extends SQLiteOpenHelper implements DBHelperInterface {

    private static final String SQL_CREATE_TOTALINFO_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.TotalEntry.TABLE_NAME + " (" +
                    FeedReaderContract.TotalEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedReaderContract.TotalEntry.COLUMN_NAME_UID + " TEXT," +
                    FeedReaderContract.TotalEntry.COLUMN_NAME_DATE + " TEXT," +
                    FeedReaderContract.TotalEntry.COLUMN_NAME_SLEEP + " INTEGER," +
                    FeedReaderContract.TotalEntry.COLUMN_NAME_PHONE + " INTEGER," +
                    FeedReaderContract.TotalEntry.COLUMN_NAME_USABLE + " INTEGER)";

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public TOTALINFODBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL(SQL_CREATE_TOTALINFO_ENTRIES);


    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String uid, String date, TotalVO tvo) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        //DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO " + FeedReaderContract.TotalEntry.TABLE_NAME +
                " VALUES(null, '" + uid + "', '" + date + "', " + tvo.getSleep_m() + ", " + tvo.getPhone_m() + ", " + tvo.getUsable_m() +");");

    }

    public TotalVO getTotalVO(String uid, String date) {
        SQLiteDatabase db = getReadableDatabase();
        TotalVO totalVO = null;
        try (Cursor cursor = db.rawQuery("SELECT * FROM TOTAL_INFO where uid = " + "'" + uid + "' and date = '" + date + "'", null)) {

            while (cursor.moveToNext()) {
                totalVO = new TotalVO();
                totalVO.setDate(cursor.getString(2));
                totalVO.setSleep_m(cursor.getInt(3));
                totalVO.setPhone_m(cursor.getInt(4));
                totalVO.setUsable_m(cursor.getInt(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return totalVO;
    }

    public void update(String uid, String dateStr, TotalVO totalVO) {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("UPDATE TOTAL_INFO SET sleep = "+totalVO.getSleep_m()+", phone="+totalVO.getPhone_m() + ", usable="+totalVO.getUsable_m()+" WHERE uid='" + uid + "' and date ='" + dateStr+"'");

    }
    @Override
    public Cursor raw() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM TOTAL_INFO", new String[]{});
    }


}

