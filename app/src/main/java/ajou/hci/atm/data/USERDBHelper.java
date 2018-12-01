package ajou.hci.atm.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;

import ajou.hci.atm.model.User;


public class USERDBHelper extends SQLiteOpenHelper implements DBHelperInterface {

    private static final String SQL_CREATE_USER_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.UserEntry.TABLE_NAME + " (" +
                    FeedReaderContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedReaderContract.UserEntry.COLUMN_NAME_UID + " TEXT," +
                    FeedReaderContract.UserEntry.COLUMN_NAME_NAME + " TEXT," +
                    FeedReaderContract.UserEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    FeedReaderContract.UserEntry.COLUMN_NAME_TIMETABLE + " TEXT)";

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public USERDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL(SQL_CREATE_USER_ENTRIES);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String uid, User user) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        //DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO " + FeedReaderContract.UserEntry.TABLE_NAME +
                " VALUES(null, '" + uid + "', '" + user.getName() + "', '" + user.getEmail() + "','" + user.getTimeTable() + "');");


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder result = new StringBuilder();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        try (Cursor cursor = db.rawQuery("SELECT * FROM USER", null)) {
            while (cursor.moveToNext()) {
                result.append(cursor.getString(0))
                        .append(cursor.getString(1))
                        .append(cursor.getString(2))
                        .append(cursor.getString(3))
                        .append(cursor.getString(4))
                        .append("\n");
            }
        }
        return result.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public User getUser(String uid) {
        SQLiteDatabase db = getReadableDatabase();
        User user = new User();
        try (Cursor cursor = db.rawQuery("SELECT * FROM USER where uid = " + "'" + uid + "'", null)) {
            while (cursor.moveToNext()) {
                //cursor.getString(0);
                //cursor.getString(1);
                //cursor.getInt(2);
                user.setName(cursor.getString(2));
                user.setEmail(cursor.getString(3));
                user.setTimeTable(cursor.getString(4));
            }
        }
        return user;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getTimeList(String uid) {
        SQLiteDatabase db = getReadableDatabase();
        String timeList = "";

        try (Cursor cursor = db.rawQuery("SELECT timeTable FROM USER where uid = " + "'" + uid + "'", null)) {
            while (cursor.moveToNext()) {
                timeList = cursor.getString(0);
            }
        }


        return timeList;
    }

    public void updateUserTimeTable(String uid, String timeTable){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE USER SET timeTable='"+timeTable+"' WHERE uid='"+ uid+"'");
    }

    /*
    public void updateWithSTime(String uid, String date, String stime) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL("UPDATE ACTIVITIES SET flag = 'false' WHERE uid='" + uid + "' and date ='" + date + "' and stime='" + stime + "' and value ='3' and flag='true'");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     */

    @Override
    public Cursor raw() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM USER", new String[]{});
    }
}

