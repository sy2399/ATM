package ajou.hci.atm.model;

import android.app.usage.UsageStats;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Entity class represents usage stats and app icon.
 */
public class CustomUsageStats implements Parcelable {
    private UsageStats usageStats;
    private Drawable appIcon;

    protected CustomUsageStats(Parcel in) {
        usageStats = in.readParcelable(UsageStats.class.getClassLoader());
    }

    public CustomUsageStats() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(usageStats, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CustomUsageStats> CREATOR = new Creator<CustomUsageStats>() {
        @Override
        public CustomUsageStats createFromParcel(Parcel in) {
            return new CustomUsageStats(in);
        }

        @Override
        public CustomUsageStats[] newArray(int size) {
            return new CustomUsageStats[size];
        }
    };

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public void setUsageStats(UsageStats usageStats) {
        this.usageStats = usageStats;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}