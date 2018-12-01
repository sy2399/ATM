package ajou.hci.atm.model;

/**
 * Created by imsoyeong on 2018. 11. 30..
 */

public class TotalVO {
    String date;
    int sleep_m;
    int phone_m;
    int usable_m;

    public TotalVO() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSleep_m() {
        return sleep_m;
    }

    public void setSleep_m(int sleep_m) {
        this.sleep_m = sleep_m;
    }

    public int getPhone_m() {
        return phone_m;
    }

    public void setPhone_m(int phone_m) {
        this.phone_m = phone_m;
    }

    public int getUsable_m() {
        return usable_m;
    }

    public void setUsable_m(int usable_m) {
        this.usable_m = usable_m;
    }

    @Override
    public String toString() {
        return "TotalVO{" +
                "date='" + date + '\'' +
                ", sleep_m=" + sleep_m +
                ", phone_m=" + phone_m +
                ", usable_m=" + usable_m +
                '}';
    }


}
