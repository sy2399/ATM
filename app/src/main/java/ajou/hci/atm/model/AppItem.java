package ajou.hci.atm.model;


import java.util.Locale;

//App Usage 받아서 임시로 저장하는 class
public class AppItem {
    private String mName;
    private String mPackageName;
    private long mEventTime;
    private String mTimeStamp;
    private long mUsageTime;
    private int mEventType;
    private int mCount;
    private long mMobile;
    private boolean mIsSystem;
    private String isInUsable;

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "name:%s package_name:%s time:%d total:%d type:%d system:%b count:%d",
                mName, mPackageName, mEventTime, mUsageTime, mEventType, mIsSystem, mCount);
    }

    public AppItem copy() {
        AppItem newItem = new AppItem();
        newItem.mName = this.mName;
        newItem.mPackageName = this.mPackageName;
        newItem.mEventTime = this.mEventTime;
        newItem.mUsageTime = this.mUsageTime;
        newItem.mEventType = this.mEventType;
        newItem.mIsSystem = this.mIsSystem;
        newItem.mCount = this.mCount;
        newItem.mTimeStamp = this.mTimeStamp;
        return newItem;
    }

    public String getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPackageName() {
        return mPackageName;
    }

    public void setmPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public long getmEventTime() {
        return mEventTime;
    }

    public void setmEventTime(long mEventTime) {
        this.mEventTime = mEventTime;
    }

    public long getmUsageTime() {
        return mUsageTime;
    }

    public void setmUsageTime(long mUsageTime) {
        this.mUsageTime = mUsageTime;
    }

    public int getmEventType() {
        return mEventType;
    }

    public void setmEventType(int mEventType) {
        this.mEventType = mEventType;
    }

    public int getmCount() {
        return mCount;
    }

    public void setmCount(int mCount) {
        this.mCount = mCount;
    }

    public long getmMobile() {
        return mMobile;
    }

    public void setmMobile(long mMobile) {
        this.mMobile = mMobile;
    }

    public boolean ismIsSystem() {
        return mIsSystem;
    }

    public void setmIsSystem(boolean mIsSystem) {
        this.mIsSystem = mIsSystem;
    }

    public String getIsInUsable() {
        return isInUsable;
    }

    public void setIsInUsable(String isInUsable) {
        this.isInUsable = isInUsable;
    }
}