package me.ardacraft.updater;

import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author dags <dags@dags.me>
 */
public class Utils {

    private static final Logger LOGGER = Logger.getLogger("Downloader");

    public static void delete(File input) {
        if (input.isDirectory()) {
            File[] children = input.listFiles();
            if (children != null) {
                for (File child : children) {
                    delete(child);
                }
            }
        }
        if (input.delete()) {
            log("Deleting {0}", input);
        }
    }

    public static void mkdirs(File file) {
        if (file.mkdirs()) {
            log("Making dir {0}", file);
        }
    }

    public static Date iso8601ToDate(String input) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return format.parse(input);
    }

    public static void log(String format, Object... args) {
        LOGGER.info(new MessageFormat(format).format(args));
    }
}
