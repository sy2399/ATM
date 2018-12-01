package ajou.hci.atm.model;

public class PhoneVO {
    private String dayOfWeek;
    private String timeTable;
    private String total;
    private String type;
    private String date;
    private String percent;
    private String sTime;
    private String eTime;

    public PhoneVO() {
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(String timeTable) {
        this.timeTable = timeTable;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }

    @Override
    public String toString() {
        return "PhoneVO{" +
                "dayOfWeek='" + dayOfWeek + '\'' +
                ", timeTable='" + timeTable + '\'' +
                ", total='" + total + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", percent='" + percent + '\'' +
                ", sTime='" + sTime + '\'' +
                ", eTime='" + eTime + '\'' +
                '}';
    }
}
