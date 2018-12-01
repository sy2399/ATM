package ajou.hci.atm.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ajou.hci.atm.model.EMAVO;

public class EMADBHelper extends SQLiteOpenHelper implements DBHelperInterface {

    private static final String SQL_CREATE_EMA_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.EMAEntry.TABLE_NAME + " (" +
                    FeedReaderContract.EMAEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedReaderContract.EMAEntry.COLUMN_NAME_UID + " TEXT," +
                    FeedReaderContract.EMAEntry.COLUMN_NAME_DATE + " TEXT," +
                    FeedReaderContract.EMAEntry.COLUMN_NAME_STIME+ " TEXT," +
                    FeedReaderContract.EMAEntry.COLUMN_NAME_ETIME+ " TEXT," +
                    FeedReaderContract.EMAEntry.COLUMN_NAME_ACTIVITY+ " TEXT," +
                    FeedReaderContract.EMAEntry.COLUMN_NAME_LIKERT+ " TEXT," +
                    FeedReaderContract.EMAEntry.COLUMN_NAME_PERCENT+ " INTEGER," +
                    FeedReaderContract.EMAEntry.COLUMN_NAME_INDEX+ " INTEGER," +
                    FeedReaderContract.EMAEntry.COLUMN_NAME_CHECKTIME + " TEXT)";

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public EMADBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL(SQL_CREATE_EMA_ENTRIES);


    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String uid, String date, EMAVO evo) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        //DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO "+ FeedReaderContract.EMAEntry.TABLE_NAME +
                " VALUES(null, '" + uid + "', '" +date+ "', '" + evo.getStime() + "','" +evo.getEtime() + "','"
                +evo.getActivity()+"','"+evo.getLikert()+"',"+ evo.getPercent() + ", " + evo.getIndex() + ",'" + evo.getCheckTime()+"');");


    }

    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder result = new StringBuilder();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력

        try (Cursor cursor = db.rawQuery("SELECT * FROM EMA", null)) {
            while (cursor.moveToNext()) {
                result.append(cursor.getString(0))
                        .append(cursor.getString(1))
                        .append(cursor.getString(2))
                        .append(cursor.getString(3))
                        .append(cursor.getString(4))
                        .append(cursor.getString(5))
                        .append(cursor.getString(6))
                        .append(cursor.getString(7))
                        .append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        return result.toString();
    }

//    public String getResultWithUIDAndDate(String uid, String date) {
//        // 읽기가 가능하게 DB 열기
//        SQLiteDatabase db = getReadableDatabase();
//        String result = "";
//        try (Cursor cursor = db.rawQuery("SELECT * FROM EMA where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
//            while (cursor.moveToNext()) {
//                result += cursor.getString(0)
//                        + cursor.getString(1)
//                        + cursor.getString(2)
//                        + cursor.getString(3)
//                        + cursor.getString(4)
//                        + cursor.getString(5)
//                        + cursor.getString(6)
//                        + cursor.getString(7)
//                        + "\n";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }


    public ArrayList<EMAVO> getEMAVOs(String uid, String date){
        ArrayList<EMAVO> emavos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM EMA where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                EMAVO evo = new EMAVO();
                evo.setStime(cursor.getString(3));
                evo.setEtime(cursor.getString(4));
                evo.setActivity(cursor.getString(5));
                evo.setLikert(cursor.getString(6));
                evo.setPercent(cursor.getInt(7));
                evo.setIndex(cursor.getInt(8));
                evo.setCheckTime(cursor.getString(9));
                emavos.add(evo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        return emavos;
    }

    @Override
    public Cursor raw() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM EMA" , new String[]{});
    }
}

