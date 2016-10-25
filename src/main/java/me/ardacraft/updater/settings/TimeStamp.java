package me.ardacraft.updater.settings;

import me.ardacraft.updater.Utils;

import java.text.ParseException;
import java.util.Date;

/**
 * @author dags <dags@dags.me>
 */
public class TimeStamp {

    public String time_stamp = "";

    public boolean present() {
        return !time_stamp.isEmpty();
    }

    public boolean olderThan(String other) throws ParseException {
        if (!present()) {
            return true;
        }
        Date thisDate = Utils.iso8601ToDate(time_stamp);
        Date otherDate = Utils.iso8601ToDate(other);
        return thisDate.before(otherDate);
    }
}
