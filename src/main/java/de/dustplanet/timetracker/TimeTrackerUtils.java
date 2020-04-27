package de.dustplanet.timetracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;

public class TimeTrackerUtils {
    public static void saveHashMap(Object fileObj, File file) {
        try (FileOutputStream f = new FileOutputStream(file); ObjectOutputStream s = new ObjectOutputStream(f)) {
            s.writeObject(fileObj);
            s.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object loadHashMap(File file) {
        Object o = null;
        try (FileInputStream f = new FileInputStream(file); ObjectInputStream s = new ObjectInputStream(f)) {
            o = s.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static String formatTime(long time) {
        int days = (int) TimeUnit.MILLISECONDS.toDays(time);
        long hours = TimeUnit.MILLISECONDS.toHours(time) - TimeUnit.DAYS.toHours(days);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.DAYS.toMinutes(days) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.DAYS.toSeconds(days) - TimeUnit.HOURS.toSeconds(hours)
                - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
    }

}
