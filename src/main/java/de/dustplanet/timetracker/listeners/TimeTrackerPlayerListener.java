package de.dustplanet.timetracker.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.dustplanet.timetracker.TimeTracker;

public class TimeTrackerPlayerListener implements Listener {
    private TimeTracker plugin;

    public TimeTrackerPlayerListener(TimeTracker instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        if (!plugin.isPlayerTracked(name)) {
            plugin.addTrackedPlayer(name);
        }
        plugin.addJoinedTime(name, System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        if (plugin.isPlayerTracked(name)) {
            plugin.calculatePlayTime(name);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        String name = event.getPlayer().getName();
        if (plugin.isPlayerTracked(name)) {
            plugin.calculatePlayTime(name);
        }
    }
}
