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
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!plugin.isPlayerTracked(uuid)) {
            plugin.addTrackedPlayer(uuid);
        }
        plugin.addJoinedTime(uuid, System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        if (plugin.isPlayerTracked(uuid)) {
            plugin.calculatePlayTime(uuid);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        if (plugin.isPlayerTracked(uuid)) {
            plugin.calculatePlayTime(uuid);
        }
    }
}
