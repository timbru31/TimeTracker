package de.dustplanet.timetracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.dustplanet.timetracker.commands.TimeTrackerCommand;
import de.dustplanet.timetracker.listeners.TimeTrackerPlayerListener;
import lombok.Getter;

public class TimeTracker extends JavaPlugin {
    private static final int BSTATS_PLUGIN_ID = 7334;
    @Getter
    private HashMap<String, Long[][]> trackedPlayers = new HashMap<>();
    private HashMap<String, Long> joinedTime = new HashMap<>();
    private File time;

    @Override
    public void onDisable() {
        for (Player p : getServer().getOnlinePlayers()) {
            String name = p.getName();
            if (isPlayerTracked(name)) {
                calculate(name);
            }
        }
        saveHashMap(trackedPlayers, time);
        joinedTime.clear();
        trackedPlayers.clear();
    }

    @SuppressWarnings("unused")
    @Override
    public void onEnable() {
        joinedTime.clear();
        trackedPlayers.clear();
        time = new File(getDataFolder(), "time.dat");
        if (!time.exists()) {
            time.getParentFile().mkdir();
            try {
                time.createNewFile();
            } catch (IOException e) {
                getLogger().info("Couldn't create the 'time.dat' file! (I/O Exception)");
                e.printStackTrace();
            }
        }
        trackedPlayers = (HashMap<String, Long[][]>) loadHashMap(time);
        if (trackedPlayers == null) {
            trackedPlayers = new HashMap<>();
        }
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new TimeTrackerPlayerListener(this), this);

        getCommand("tracker").setExecutor(new TimeTrackerCommand(this));

        new Metrics(this, -BSTATS_PLUGIN_ID);

        for (Player p : getServer().getOnlinePlayers()) {
            String name = p.getName();
            if (isPlayerTracked(name)) {
                joinedTime.put(name, System.currentTimeMillis());
            }
        }
    }

    private void saveHashMap(Object fileObj, File file) {
        try (FileOutputStream f = new FileOutputStream(file); ObjectOutputStream s = new ObjectOutputStream(f)) {
            s.writeObject(fileObj);
            s.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object loadHashMap(File file) {
        Object o = null;
        try (FileInputStream f = new FileInputStream(file); ObjectInputStream s = new ObjectInputStream(f)) {
            o = s.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return o;
    }

    public boolean isPlayerTracked(String name) {
        return trackedPlayers.containsKey(name);
    }

    public boolean addTrackedPlayer(String name) {
        if (isPlayerTracked(name)) {
            return false;
        }
        Long[][] weeks = new Long[52][7];
        trackedPlayers.put(name, weeks);
        return true;
    }

    public void calculate(String name) {
        long onlineTime = 0;
        if (joinedTime.containsKey(name)) {
            onlineTime = System.currentTimeMillis() - joinedTime.get(name);
        }
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(new Date());
        int week = c.get(Calendar.WEEK_OF_YEAR) - 1;
        int day = c.get(Calendar.DAY_OF_WEEK) - 1;
        Long[][] priorOnlineTime = trackedPlayers.get(name);
        if (priorOnlineTime[week][day] == null) {
            priorOnlineTime[week][day] = onlineTime;
        } else {
            priorOnlineTime[week][day] += onlineTime;
        }
        if (joinedTime.containsKey(name)) {
            joinedTime.remove(name);
        }
        trackedPlayers.put(name, priorOnlineTime);
    }

    public String calculateTime(long time) {
        int milliseconds = (int) time;
        int seconds = milliseconds / 1000 % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        int hours = (milliseconds / (1000 * 60 * 60)) % 24;
        int days = (milliseconds / (1000 * 60 * 60 * 24)) % 30;
        return days + " Tage, " + hours + " Stunden, " + minutes + " Minuten, " + seconds + " Sekunden";
    }

    public void addJoinedTime(String name, long currentTimeMillis) {
        joinedTime.put(name, currentTimeMillis);
    }
}
