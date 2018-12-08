package ajou.hci.atm.model;

public class NetworkVO {
    String type;
    String name;
    String time;

    public NetworkVO() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "NetworkVO{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
