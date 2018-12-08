package ajou.hci.atm.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ajou.hci.atm.model.NetworkVO;

public class NETWORKDBHelper extends SQLiteOpenHelper implements DBHelperInterface {

    private static final String SQL_CREATE_NETWORK_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.NetworkEntry.TABLE_NAME + " (" +
                    FeedReaderContract.NetworkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedReaderContract.NetworkEntry.COLUMN_NAME_UID + " TEXT," +
                    FeedReaderContract.NetworkEntry.COLUMN_NAME_DATE + " TEXT," +
                    FeedReaderContract.NetworkEntry.COLUMN_NAME_TIME + " TEXT," +
                    FeedReaderContract.NetworkEntry.COLUMN_NAME_TYPE + " TEXT," +
                    FeedReaderContract.NetworkEntry.COLUMN_NAME_NAME + " TEXT)";

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public NETWORKDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL(SQL_CREATE_NETWORK_ENTRIES);


    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String uid, String date, NetworkVO nvo) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        //DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO " + FeedReaderContract.NetworkEntry.TABLE_NAME +
                " VALUES(null, '" + uid + "', '" + date + "', '"+nvo.getTime() + "','" + nvo.getType() + "','" + nvo.getName() + "' );");


    }

    @Override
    public Cursor raw() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+FeedReaderContract.NetworkEntry.TABLE_NAME, new String[]{});
    }

}

