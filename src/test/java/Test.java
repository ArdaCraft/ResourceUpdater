import me.ardacraft.updater.Updater;
import me.ardacraft.updater.Utils;

import java.io.File;

/**
 * @author dags <dags@dags.me>
 */
public class Test {

    public static void main(String[] args) {
        try {
            File home = new File(new File("").getAbsolutePath());
            Utils.log("{0}", home);
            Updater downloader = new Updater();
            downloader.init(home);
            downloader.launch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
