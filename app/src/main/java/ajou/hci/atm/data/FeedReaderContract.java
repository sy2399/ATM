package ajou.hci.atm.data;

import android.provider.BaseColumns;

interface FeedReaderContract {

    class UserEntry implements BaseColumns {
        static final String TABLE_NAME = "USER";
        static final String COLUMN_NAME_UID = "uid";
        static final String COLUMN_NAME_EMAIL = "email";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_TIMETABLE = "timeTable";

    }

    class ActivityEntry implements BaseColumns {
        static final String TABLE_NAME = "ACTIVITIES";
        static final String COLUMN_NAME_UID = "uid";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_STIME = "stime";
        static final String COLUMN_NAME_ETIME = "etime";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_VALUE = "value";
        static final String COLUMN_NAME_FLAG = "flag";
        static final String COLUMN_NAME_TOTAL = "total";

    }

    class EMAEntry implements BaseColumns {
        static final String TABLE_NAME = "EMA";
        static final String COLUMN_NAME_UID = "uid";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_STIME = "stime";
        static final String COLUMN_NAME_ETIME = "etime";
        static final String COLUMN_NAME_ACTIVITY = "activity";
        static final String COLUMN_NAME_LIKERT = "likert";
        static final String COLUMN_NAME_PERCENT = "percent";
        static final String COLUMN_NAME_INDEX = "ema_index";
        static final String COLUMN_NAME_CHECKTIME = "checkTime";


    }


    class LocationNEntry implements BaseColumns {
        static final String TABLE_NAME = "LOCATION";
        static final String COLUMN_NAME_UID = "uid";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_TIME = "time";
        static final String COLUMN_NAME_LATITUDE = "latitude";
        static final String COLUMN_NAME_LONGITUDE = "longitude";
        static final String COLUMN_NAME_POINAME= "poiName";
        static final String COLUMN_NAME_ADDRNAME = "addrName";
        static final String COLUMN_NAME_RADIUS = "radius";
    }

    class AppEntry implements BaseColumns {
        static final String TABLE_NAME = "APP";
        static final String COLUMN_NAME_UID = "uid";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_STIME = "stime";
        static final String COLUMN_NAME_ETIME = "etime";
        static final String COLUMN_NAME_PACKAGENAME = "packageName";
        static final String COLUMN_NAME_TOTAL = "total";
        static final String COLUMN_NAME_ISINUSABLE = "isInUsable";
        static final String COLUMN_NAME_PACKAGEFULLNAME = "packageFullName";


    }

    class TotalEntry implements BaseColumns {
        static final String TABLE_NAME = "TOTAL_INFO";
        static final String COLUMN_NAME_UID = "uid";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_SLEEP = "sleep";
        static final String COLUMN_NAME_PHONE = "phone";
        static final String COLUMN_NAME_USABLE = "usable";

    }

    class PhoneUsageEntry implements BaseColumns {
        static final String TABLE_NAME = "PHONE_USAGE";
        static final String COLUMN_NAME_UID = "uid";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_DAYOFWEEK = "day";
        static final String COLUMN_NAME_TIMETABLE = "timeTable";
        static final String COLUMN_NAME_TOTAL = "total";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_PERCENT = "percent";
        static final String COLUMN_NAME_STIME = "sTime";
        static final String COLUMN_NAME_ETIME = "eTime";
    }

    class NotificationEntry implements BaseColumns {
        static final String TABLE_NAME = "NOTIFICATION";
        static final String COLUMN_NAME_UID = "uid";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_POSTTIME = "postTime";
        static final String COLUMN_NAME_PACKAGENAME = "packageName";
        static final String COLUMN_NAME_PNAME = "pName";
        static final String COLUMN_NAME_TEXT = "text";
        static final String COLUMN_NAME_TITLE = "title";


    }


    class TimeCounterEntry implements BaseColumns {
        static final String TABLE_NAME = "TIMECOUNTER";
        static final String COLUMN_NAME_UID = "uid";
        static final String COLUMN_NAME_DATE = "date";
        static final String COLUMN_NAME_MIN = "min";
        static final String COLUMN_NAME_FLAG = "flag";
        static final String COLUMN_NAME_CLASS = "class";
    }


}
