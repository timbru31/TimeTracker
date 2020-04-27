package de.dustplanet.timetracker;

import java.io.File;
import java.io.IOException;
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
    private Calendar calendar = Calendar.getInstance();

    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            String uuid = player.getUniqueId().toString();
            if (isPlayerTracked(uuid)) {
                calculatePlayTime(uuid);
            }
        }
        TimeTrackerUtils.saveHashMap(trackedPlayers, time);
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
        trackedPlayers = (HashMap<String, Long[][]>) TimeTrackerUtils.loadHashMap(time);
        if (trackedPlayers == null) {
            trackedPlayers = new HashMap<>();
        }
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new TimeTrackerPlayerListener(this), this);

        getCommand("tracker").setExecutor(new TimeTrackerCommand(this));

        new Metrics(this, BSTATS_PLUGIN_ID);

        for (Player player : getServer().getOnlinePlayers()) {
            String uuid = player.getUniqueId().toString();
            if (isPlayerTracked(uuid)) {
                joinedTime.put(uuid, System.currentTimeMillis());
            }
        }
    }

    public boolean isPlayerTracked(String uuid) {
        return trackedPlayers.containsKey(uuid);
    }

    public boolean addTrackedPlayer(String uuid) {
        if (isPlayerTracked(uuid)) {
            return false;
        }
        Long[][] weeks = new Long[52][7];
        trackedPlayers.put(uuid, weeks);
        return true;
    }

    public void calculatePlayTime(String uuid) {
        long onlineTime = 0;
        if (joinedTime.containsKey(uuid)) {
            onlineTime = System.currentTimeMillis() - joinedTime.get(uuid);
        }
        calendar.clear();
        calendar.setTime(new Date());
        int week = calendar.get(Calendar.WEEK_OF_YEAR) - 1;
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        Long[][] priorOnlineTime = trackedPlayers.get(uuid);
        if (priorOnlineTime[week][day] == null) {
            priorOnlineTime[week][day] = onlineTime;
        } else {
            priorOnlineTime[week][day] += onlineTime;
        }
        if (joinedTime.containsKey(uuid)) {
            joinedTime.remove(uuid);
        }
        trackedPlayers.put(uuid, priorOnlineTime);
    }

    public void addJoinedTime(String name, long currentTimeMillis) {
        joinedTime.put(name, currentTimeMillis);
    }
}
