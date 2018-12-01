package ajou.hci.atm.model;

public class APPTEMP {
    private String packageName;
    private String pSname;
    private long total;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getpSname() {
        return pSname;
    }

    public void setpSname(String pSname) {
        this.pSname = pSname;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "APPTEMP{" +
                "packageName='" + packageName + '\'' +
                ", pSname='" + pSname + '\'' +
                ", total=" + total +
                '}';
    }
}
