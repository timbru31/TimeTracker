package de.dustplanet.timetracker.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.dustplanet.timetracker.TimeTracker;

public class TimeTrackerCommand implements CommandExecutor {
    private final String[] dayValues = { "Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag" };
    private TimeTracker plugin;

    public TimeTrackerCommand(TimeTracker instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "TimeTracker version 1.0.2 by xGhOsTkiLLeRx");
            sender.sendMessage(ChatColor.RED + "Für die Benutzung tippe bitte /tracker help|hilfe|h");
        } else {
            if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("hilfe") || args[0].equalsIgnoreCase("h")) {
                if (sender.hasPermission("TimeTracker.help")) {
                    sender.sendMessage(ChatColor.GREEN + "TimeTracker version 1.0.2");
                    sender.sendMessage(ChatColor.YELLOW + "/tracker help|hilfe|h" + ChatColor.WHITE + " - Zeigt diese Hilfe an");
                    sender.sendMessage(ChatColor.YELLOW + "/tracker get|g <SpielerName>" + ChatColor.WHITE
                            + " - Gibt einen Überblick über die Spielstunden pro Kalenderwoche");
                    sender.sendMessage(ChatColor.YELLOW + "/tracker get|g <SpielerName> <KW>" + ChatColor.WHITE
                            + " - Gibt einen Überblick über die Spielstunden in der angegebenen Kalenderwoche");
                } else
                    sender.sendMessage(ChatColor.RED + "Du hast nicht die Berechtigung hierzu!");
            } else if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("g")) {
                if (sender.hasPermission("TimeTracker.get")) {
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.RED + "Bitte gib einen Spielernamen an!");
                    } else {
                        String name = args[1];
                        if (!plugin.isPlayerTracked(args[1])) {
                            sender.sendMessage(ChatColor.RED + "Dieser Spieler ist nicht auf der Liste!");
                            return true;
                        }
                        plugin.calculate(name);
                        plugin.addJoinedTime(name, System.currentTimeMillis());
                        if (args.length == 2) {
                            sender.sendMessage(ChatColor.GREEN + "Statistiken von " + name);
                            Long[][] playerTime = plugin.getTrackedPlayers().get(name);
                            long allTimePlayTime = 0;
                            for (Long[] week : playerTime) {
                                long playTime = 0;
                                for (Long day : week) {
                                    if (day == null)
                                        continue;
                                    playTime += day;
                                }
                                if (playTime != 0) {
                                    sender.sendMessage(
                                            ChatColor.YELLOW + "Spielzeit in der Woche " + (Arrays.asList(playerTime).indexOf(week) + 1));
                                    sender.sendMessage(plugin.calculateTime(playTime));
                                }
                                allTimePlayTime += playTime;
                            }
                            sender.sendMessage(ChatColor.YELLOW + "Spielzeit insgesamt");
                            sender.sendMessage(plugin.calculateTime(allTimePlayTime));
                        } else {
                            int kw = 0;
                            try {
                                kw = Integer.valueOf(args[2]);
                                if (kw < 1 || kw > 52) {
                                    sender.sendMessage(ChatColor.RED + "Bitte eine Kalenderwoche zwischen 1 und 52 angeben!");
                                    return true;
                                }
                                int kwReal = kw;
                                kw -= 1;
                                Long[] week = plugin.getTrackedPlayers().get(name)[kw];
                                if (week == null) {
                                    sender.sendMessage(ChatColor.RED + "Keine Spielzeit in der Woche " + ChatColor.YELLOW + kwReal);
                                    return true;
                                }
                                long onlineZeit = 0;
                                for (Long day : week) {
                                    if (day == null)
                                        day = (long) 0;
                                    onlineZeit += day;
                                }
                                if (onlineZeit == 0) {
                                    sender.sendMessage(ChatColor.RED + "Keine Spielzeit in der Woche " + ChatColor.YELLOW + kwReal);
                                    return true;
                                }
                                sender.sendMessage(
                                        ChatColor.GREEN + "Statistiken von " + name + " in der Woche " + ChatColor.YELLOW + kwReal);
                                int i = 0;
                                for (Long day : week) {
                                    if (day == null || day == 0) {
                                        sender.sendMessage(ChatColor.YELLOW + dayValues[i] + ChatColor.WHITE + ": Keine Spielzeit");
                                    } else
                                        sender.sendMessage(
                                                ChatColor.YELLOW + dayValues[i] + ChatColor.WHITE + ": " + plugin.calculateTime(day));
                                    i++;
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(ChatColor.RED + "Bitte gib eine Zahl als Woche an!");
                                return true;
                            }

                        }
                    }
                } else
                    sender.sendMessage(ChatColor.RED + "Du hast nicht die Berechtigung hierzu!");
            } else
                sender.sendMessage(ChatColor.RED + "Dieses Argument ist unbekannt. Tippe für die Hilfe /tracker help|hilfe");
        }
        return true;
    }

}
