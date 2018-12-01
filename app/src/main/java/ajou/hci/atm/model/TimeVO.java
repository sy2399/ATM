package ajou.hci.atm.model;

public class TimeVO {
    private String sdate;
    private String edate;
    private String timeTable;

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    public String getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(String timeTable) {
        this.timeTable = timeTable;
    }

    @Override
    public String toString() {
        return "TimeVO{" +
                "sdate='" + sdate + '\'' +
                ", edate='" + edate + '\'' +
                ", timeTable='" + timeTable + '\'' +
                '}';
    }
}
