package ajou.hci.atm.model;

/**
 * Created by imsoyeong on 2018. 5. 7..
 */

//ActivityVO - 운동 정보 저장 클래스
public class ActivityVO {
    private String type;
    private String startTime;
    private String endTime;
    private String field;
    private String value;

    private String flag;
    private int total;

    public ActivityVO() {
    }

    public ActivityVO(String type, String startTime, String endTime, String field, String value) {
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.field = field;
        this.value = value;
    }

    public ActivityVO(String type, String startTime, String endTime, String field, String value, String flag) {
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.field = field;
        this.value = value;
        this.flag = flag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "ActivityVO{" +
                "type='" + type + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", field='" + field + '\'' +
                ", value='" + value + '\'' +
                ", flag='" + flag + '\'' +
                ", total=" + total +
                '}';
    }
}


