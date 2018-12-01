package ajou.hci.atm.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ajou.hci.atm.model.LocationVO;
import ajou.hci.atm.models.Location;

public class LOCATIONDBHelper extends SQLiteOpenHelper implements DBHelperInterface {

    private static final String SQL_CREATE_LOCATION_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.LocationNEntry.TABLE_NAME + " (" +
                    FeedReaderContract.LocationNEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedReaderContract.LocationNEntry.COLUMN_NAME_UID + " TEXT," +
                    FeedReaderContract.LocationNEntry.COLUMN_NAME_DATE + " TEXT," +
                    FeedReaderContract.LocationNEntry.COLUMN_NAME_TIME + " TEXT not null unique," +
                    FeedReaderContract.LocationNEntry.COLUMN_NAME_LATITUDE + " TEXT," +
                    FeedReaderContract.LocationNEntry.COLUMN_NAME_LONGITUDE + " TEXT," +
                    FeedReaderContract.LocationNEntry.COLUMN_NAME_POINAME + " TEXT," +
                    FeedReaderContract.LocationNEntry.COLUMN_NAME_ADDRNAME + " TEXT," +
                    FeedReaderContract.LocationNEntry.COLUMN_NAME_RADIUS + " TEXT)";

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public LOCATIONDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 MONEYBOOK이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL(SQL_CREATE_LOCATION_ENTRIES);
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(String uid, Location lvo) {
        String date = lvo.getTime().split(" ")[0];

        SQLiteDatabase db = getWritableDatabase();
        try {

            db.execSQL("INSERT INTO " + FeedReaderContract.LocationNEntry.TABLE_NAME +
                    " VALUES(null, '" + uid + "', '" + date + "', '" + lvo.getTime() + "','" + lvo.getLatitude() + "','" + lvo.getLongitude() + "','"
                    + lvo.getPoiName() + "','" + lvo.getAddrName() + "','" + lvo.getRadius() + "');");

            //Log.i("0zoo", "db insert: "+ lvo.toString());
        }catch (Exception e){
            //Log.i("0zoo", e.getLocalizedMessage());
        }
    }

    public Location getLocationByTime(String uid, String time){
        SQLiteDatabase db = getReadableDatabase();
        Location location = null;

        try (Cursor cursor = db.rawQuery("SELECT * FROM LOCATION where uid=" + "'" + uid + "' and time = '" + time + "'", null)) {
            while (cursor.moveToNext()) {
                //Log.i("0zoo", "location is already exist"+ cursor.getString(3));
                location = new Location(cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8));
            }
            return location;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Location> getLocationList(String uid, String date) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Location> locList = new ArrayList<>();

        try (Cursor cursor = db.rawQuery("SELECT * FROM LOCATION where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                Location location = new Location(cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8));

                locList.add(location);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locList;
    }

//    public void insert(String uid, String date, LocationVO lvo) {
//        // 읽고 쓰기가 가능하게 DB 열기
//        SQLiteDatabase db = getWritableDatabase();
//        //DB에 입력한 값으로 행 추가
//        db.execSQL("INSERT INTO " + FeedReaderContract.LocationNEntry.TABLE_NAME +
//                " VALUES(null, '" + uid + "', '" + date + "', '" + lvo.getTime() + "','" + lvo.getAccuracy() + "','" + lvo.getLatitude() + "','" + lvo.getLongitude() + "','" + lvo.getAltitude() + "','" + lvo.getFlag() + "');");
//    }

    public String getLast(String uid, String date) {
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        try (Cursor cursor = db.rawQuery("SELECT * FROM LOCATION where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                result = cursor.getString(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public LocationVO getLastLVO(String uid, String date) {
        SQLiteDatabase db = getReadableDatabase();
        LocationVO lvo = new LocationVO();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력

        try (Cursor cursor = db.rawQuery("SELECT * FROM LOCATION where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                lvo.setTime(cursor.getString(3));
                lvo.setAccuracy(cursor.getString(4));
                lvo.setLatitude(cursor.getString(5));
                lvo.setLongitude(cursor.getString(6));
                lvo.setAltitude(cursor.getString(7)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return lvo;
    }

    public ArrayList<LocationVO> getLocList(String uid, String date) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<LocationVO> locList = new ArrayList<>();
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력

        try (Cursor cursor = db.rawQuery("SELECT * FROM LOCATION where uid=" + "'" + uid + "' and date = '" + date + "'", null)) {
            while (cursor.moveToNext()) {
                LocationVO lvo = new LocationVO();
                lvo.setTime(cursor.getString(3));
                lvo.setAccuracy(cursor.getString(4));
                lvo.setLatitude(cursor.getString(5));
                lvo.setLongitude(cursor.getString(6));
                lvo.setAltitude(cursor.getString(7)
                );
                locList.add(lvo);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locList;
    }

//    public ArrayList<LocationVO> getLocListWithFlag(String uid, String date) {
//        SQLiteDatabase db = getReadableDatabase();
//        ArrayList<LocationVO> locList = new ArrayList<>();
//        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
//
//        try (Cursor cursor = db.rawQuery("SELECT * FROM LOCATION where uid=" + "'" + uid + "' and date = '" + date + "' and isInUsable ='true'", null)) {
//            while (cursor.moveToNext()) {
//                LocationVO lvo = new LocationVO();
//                lvo.setTime(cursor.getString(3));
//                lvo.setAccuracy(cursor.getString(4));
//                lvo.setLatitude(cursor.getString(5));
//                lvo.setLongitude(cursor.getString(6));
//                lvo.setAltitude(cursor.getString(7)
//                );
//                locList.add(lvo);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return locList;
//    }


    public ArrayList<LocationVO> getLocVOWithSETime(String uid, String dateStr, String sdate, String edate) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<LocationVO> locationVOArrayList = new ArrayList<>();

        try (Cursor cursor = db.rawQuery("SELECT * FROM LOCATION where uid = " + "'" + uid + "' and date = " + "'" + dateStr + "' and strftime('%Y-%m-d  %H:%M:%S',time) >= " + "strftime('%Y-%m-d  %H:%M:%S','" + sdate + "') and strftime('%Y-%m-d  %H:%M:%S', time) <= " + "strftime('%Y-%m-d  %H:%M:%S','" + edate + "')", null)) {
            while (cursor.moveToNext()) {

                LocationVO lvo = new LocationVO();
                lvo.setTime(cursor.getString(3));
                lvo.setAccuracy(cursor.getString(4));
                lvo.setLatitude(cursor.getString(5));
                lvo.setLongitude(cursor.getString(6));
                lvo.setAltitude(cursor.getString(7));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return locationVOArrayList;
    }

    @Override
    public Cursor raw() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM LOCATION", new String[]{});
    }
}

