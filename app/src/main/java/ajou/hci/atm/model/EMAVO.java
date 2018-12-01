package ajou.hci.atm.model;

public class EMAVO {
    private String stime;
    private String etime;
    private String checkTime;
    private String activity;
    private String likert;
    private int percent;
    private int index;

    public EMAVO() {
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

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getLikert() {
        return likert;
    }

    public void setLikert(String likert) {
        this.likert = likert;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "EMAVO{" +
                "stime='" + stime + '\'' +
                ", etime='" + etime + '\'' +
                ", checkTime='" + checkTime + '\'' +
                ", activity='" + activity + '\'' +
                ", likert='" + likert + '\'' +
                ", percent=" + percent +
                ", index=" + index +
                '}';
    }
}
