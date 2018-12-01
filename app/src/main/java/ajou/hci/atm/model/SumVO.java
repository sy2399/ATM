package ajou.hci.atm.model;

public class SumVO {
    private long min;
    private int flag;
    private String classes;


    public SumVO() {
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    @Override
    public String toString() {
        return "SumVO{" +
                "min=" + min +
                ", flag=" + flag +
                ", classes='" + classes + '\'' +
                '}';
    }
}
