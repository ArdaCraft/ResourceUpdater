package me.ardacraft.updater.settings;

/**
 * @author dags <dags@dags.me>
 */
public class Properties {

    private static transient final Properties EMPTY = new Properties();

    public String api = "";
    public String user = "";
    public String repo = "";
    public int startup_delay = 0;
    public int interval_mins = 0;


    public String queryUrl() {
        return api + "/repos/" + user + "/" + repo + "/releases/latest";
    }

    public boolean present() {
        return this != EMPTY;
    }

    @Override
    public String toString() {
        return "api='" + api + "',user='" + user + "',repo='" + repo + "',interval=" + interval_mins;
    }
}
