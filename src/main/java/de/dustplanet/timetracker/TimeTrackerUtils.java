package de.dustplanet.timetracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
        int milliseconds = (int) time;
        int seconds = milliseconds / 1000 % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        int hours = (milliseconds / (1000 * 60 * 60)) % 24;
        int days = (milliseconds / (1000 * 60 * 60 * 24)) % 30;
        return days + " Tage, " + hours + " Stunden, " + minutes + " Minuten, " + seconds + " Sekunden";
    }
}
