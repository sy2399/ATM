package ajou.hci.atm.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ajou.hci.atm.model.NotifyVO;

public class NOTIFICATIONDBHelper extends SQLiteOpenHelper implements DBHelperInterface {

    private static final String SQL_CREATE_NOTIFICATION_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.NotificationEntry.TABLE_NAME + " (" +
                    FeedReaderContract.NotificationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedReaderContract.NotificationEntry.COLUMN_NAME_UID + " TEXT," +
                    FeedReaderContract.NotificationEntry.COLUMN_NAME_DATE + " TEXT," +
                    FeedReaderContract.NotificationEntry.COLUMN_NAME_POSTTIME + " TEXT," +
                    FeedReaderContract.NotificationEntry.COLUMN_NAME_PACKAGENAME + " TEXT," +
                    FeedReaderContract.NotificationEntry.COLUMN_NAME_PNAME + " TEXT," +
                    FeedReaderContract.NotificationEntry.COLUMN_NAME_TEXT + " TEXT," +
                    FeedReaderContract.NotificationEntry.COLUMN_NAME_TITLE + " TEXT)";

//    private static final String SQL_DELETE_ACTIVITY_ENTRIES =
//            "DROP TABLE IF EXISTS " + FeedReaderContract.ActivityEntry.TABLE_NAME;

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public NOTIFICATIONDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL(SQL_CREATE_NOTIFICATION_ENTRIES);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String uid, String date, NotifyVO nvo) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        //DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO " + FeedReaderContract.NotificationEntry.TABLE_NAME +
                " VALUES(null, '" + uid + "', '" + date + "', '" + nvo.getPostTime() + "','" + nvo.getPackageName() + "','" + nvo.getpName() + "','" + nvo.getText() + "','" + nvo.getTitle() + "' );");
    }

//    public void update(String uid, String name, String email, String timetable) {
//        SQLiteDatabase db = getWritableDatabase();
//
//        // 입력한 항목과 일치하는 행의 가격 정보 수정
//    }
//
//    public void delete() {
//        SQLiteDatabase db = getWritableDatabase();
//        // 입력한 항목과 일치하는 행 삭제
//        db.execSQL(SQL_DELETE_ACTIVITY_ENTRIES);
//    }

    public NotifyVO getLastVO(String uid, String date) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        NotifyVO nvo = new NotifyVO();
        try (Cursor cursor = db.rawQuery("SELECT * FROM NOTIFICATION where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                nvo.setPostTime(cursor.getString(3));
                nvo.setPackageName(cursor.getString(4));
                nvo.setText(cursor.getString(5));
                nvo.setText(cursor.getString(6));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nvo;
    }


    public ArrayList<NotifyVO> getNotiVOs(String uid, String date) {
        SQLiteDatabase db = getReadableDatabase();

        ArrayList<NotifyVO> nvos = new ArrayList<>();
        try (Cursor cursor = db.rawQuery("SELECT * FROM NOTIFICATION where uid = " + "'" + uid + "' and date = " + "'" + date + "'", null)) {
            while (cursor.moveToNext()) {
                NotifyVO nvo = new NotifyVO();
                nvo.setPostTime(cursor.getString(3));
                nvo.setPackageName(cursor.getString(4));
                nvo.setpName(cursor.getString(5));
                nvo.setText(cursor.getString(6));
                nvo.setTitle(cursor.getString(7));
                nvos.add(nvo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nvos;
    }


    @Override
    public Cursor raw() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM NOTIFICATION", new String[]{});
    }
}

