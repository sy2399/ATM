package ajou.hci.atm.model;

/**
 * Created by imsoyeong on 2018. 4. 2..
 */

public class User {
    private String name;
    private String email;

    private String timeTable;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;

    }

    public User(String name, String email, String timeTable) {
        this.name = name;
        this.email = email;
        this.timeTable = timeTable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(String timeTable) {
        this.timeTable = timeTable;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +

                ", timeTable='" + timeTable + '\'' +
                '}';
    }
}
