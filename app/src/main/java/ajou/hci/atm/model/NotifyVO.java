package ajou.hci.atm.model;

/**
 * Created by imsoyeong on 2018. 4. 29..
 */

public class NotifyVO {
    private String time;
    private String packageName;
    private String pName;
    private String postTime;

    private String title;
    private String text;


    public NotifyVO() {
    }

    public NotifyVO(String time, String packageName, String postTime, String title, String text) {
        this.time = time;
        this.packageName = packageName;
        this.postTime = postTime;
        this.title = title;
        this.text = text;

    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "NotifyVO{" +
                "time='" + time + '\'' +
                ", packageName='" + packageName + '\'' +
                ", pName='" + pName + '\'' +
                ", postTime='" + postTime + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
