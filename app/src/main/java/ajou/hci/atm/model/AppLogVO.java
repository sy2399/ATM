package ajou.hci.atm.model;

import android.graphics.Bitmap;

/**
 * Created by imsoyeong on 2018. 6. 5..
 */

//APP LOG 저장 클래스
public class AppLogVO {

    private int id;
    private String packageName;
    private String packageFullName;
    private String stime;
    private String etime;
    private int total;
    private String isInUsable;

    public AppLogVO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getIsInUsable() {
        return isInUsable;
    }

    public void setIsInUsable(String isInUsable) {
        this.isInUsable = isInUsable;
    }

    public String getPackageFullName() {
        return packageFullName;
    }

    public void setPackageFullName(String packageFullName) {
        this.packageFullName = packageFullName;
    }

    @Override
    public String toString() {
        return "AppLogVO{" +
                "id=" + id +
                ", packageName='" + packageName + '\'' +
                ", stime='" + stime + '\'' +
                ", etime='" + etime + '\'' +
                ", total='" + total + '\'' +
                ", isInUsable='" + isInUsable + '\'' +
                '}';
    }
}
