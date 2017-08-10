package de.dustplanet.timetracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

// import net.milkbowl.vault.Vault;
// import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
// import de.bananaco.bpermissions.api.ApiLayer;
// import de.bananaco.bpermissions.api.CalculableType;

public class TimeTracker extends JavaPlugin implements Listener {
    private HashMap<String, Long[][]> trackedPlayers = new HashMap<String, Long[][]>();
    private HashMap<String, Long> joinedTime = new HashMap<String, Long>();
    private String[] dayValues = {"Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
    private File time;
    // private Economy economy;

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

    @SuppressWarnings("unchecked")
    public void onEnable() {
	// Check for Vault
	// Plugin vault = getServer().getPluginManager().getPlugin("Vault");
	// if (vault != null && vault instanceof Vault) {
	//     // If Vault is enabled, load the economy
	//     getLogger().info("Loaded Vault successfully");
	//     setupEconomy();
	// }
	joinedTime.clear();
	trackedPlayers.clear();
	time = new File(getDataFolder(), "time.dat");
	if (!time.exists()) {
	    time.getParentFile().mkdir();
	    try {
		time.createNewFile();
	    } catch (IOException e) {
		getServer().getLogger().info("[TimeTracker] couldn't create the 'time.dat' file! (I/O Exception)");
	    }
	}
	trackedPlayers = (HashMap<String, Long[][]>) loadHashMap(time);
	if (trackedPlayers == null) trackedPlayers = new HashMap<String, Long[][]>();
	PluginManager pm = getServer().getPluginManager();
	pm.registerEvents(this, this);
	for (Player p : getServer().getOnlinePlayers()) {
	    String name = p.getName();
	    if (isPlayerTracked(name)) {
		joinedTime.put(name, System.currentTimeMillis());
	    }
	}
    }

    // // Initialized to work with Vault
    // private Boolean setupEconomy() {
	// RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	// if (economyProvider != null) {
	//     economy = economyProvider.getProvider();
	// }
	// return (economy != null);
    // }

    private void saveHashMap(Object fileObj, File file) {
	FileOutputStream f;
	try {
	    f = new FileOutputStream(file);
	    ObjectOutputStream s = new ObjectOutputStream(f);
	    s.writeObject(fileObj);
	    s.flush();
	    s.close();
	}
	catch (FileNotFoundException e) {}
	catch (IOException e) {}
    }

    private Object loadHashMap(File file) {
	Object o = null;
	FileInputStream f;
	try {
	    f = new FileInputStream(file);
	    ObjectInputStream s = new ObjectInputStream(f);
	    o = s.readObject();
	    s.close();
	}
	catch (FileNotFoundException e) {}
	catch (IOException e) {}
	catch (ClassNotFoundException e) {}
	return o;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
	String name = event.getPlayer().getName();
	if (!isPlayerTracked(name)) {
	    addTrackedPlayer(name);
	}
	joinedTime.put(name, System.currentTimeMillis());
	Player player = event.getPlayer();
	World world = player.getWorld();
	// String group = ApiLayer.getGroups(world.getName(), CalculableType.USER, name)[0];
	// if (group.equalsIgnoreCase("normal")) {
	//     calculate(name);
	//     joinedTime.put(name, System.currentTimeMillis());
	//     Long[][] playerTime = trackedPlayers.get(name);
	//     long allTimePlayTime = 0;
	//     for (Long[] week : playerTime) {
	// 	long playTime = 0;
	// 	for (Long day : week) {
	// 	    if (day == null) continue;
	// 	    playTime += day;
	// 	}
	// 	allTimePlayTime += playTime;
	//     }
	//     int milliseconds = (int) allTimePlayTime;
	//     int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
	//     if (hours >= 7) {
	// 	player.sendMessage(ChatColor.GREEN + "Glückwunsch, du hast mehr als 7 Stunden gespielt und bist nun automatisch MITGLIED");
	// 	//player.sendMessage(ChatColor.YELLOW + "Du kannst nun 2 Homes haben und deine Base protecten. Hierzu einfach /dustplanet machen!");
	// 	ApiLayer.setGroup(world.getName(), CalculableType.USER, name, "mitglied");
	// 	economy.depositPlayer(name, 100);
	// 	player.sendMessage(ChatColor.GREEN + "Dir wurden ausserdem 100 Dusties gutgeschrieben!");
	//     }
	// }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
	String name = event.getPlayer().getName();
	if (isPlayerTracked(name)) {
	    calculate(name);
	}
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
	String name = event.getPlayer().getName();
	if (isPlayerTracked(name)) {
	    calculate(name);
	}
    }

    public boolean onCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
	if (args.length == 0) {
	    sender.sendMessage(ChatColor.GREEN + "TimeTracker version 1.0.2 by xGhOsTkiLLeRx");
	    sender.sendMessage(ChatColor.RED + "Für die Benutzung tippe bitte /tracker help|hilfe|h");
	}
	else {
	    if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("hilfe") || args[0].equalsIgnoreCase("h")) {
		if (sender.hasPermission("TimeTracker.help")) {
		    sender.sendMessage(ChatColor.GREEN + "TimeTracker version 1.0.2");
		    sender.sendMessage(ChatColor.YELLOW + "/tracker help|hilfe|h" + ChatColor.WHITE+ " - Zeigt diese Hilfe an");
		    sender.sendMessage(ChatColor.YELLOW + "/tracker get|g <SpielerName>" + ChatColor.WHITE+ " - Gibt einen Überblick über die Spielstunden pro Kalenderwoche");
		    sender.sendMessage(ChatColor.YELLOW + "/tracker get|g <SpielerName> <KW>" + ChatColor.WHITE+ " - Gibt einen Überblick über die Spielstunden in der angegebenen Kalenderwoche");
		}
		else sender.sendMessage(ChatColor.RED + "Du hast nicht die Berechtigung hierzu!");
	    }
	    else if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("g")) {
		if (sender.hasPermission("TimeTracker.get")) {
		    if (args.length == 1) {
			sender.sendMessage(ChatColor.RED + "Bitte gib einen Spielernamen an!");
		    }
		    else {
			String name = args[1];
			if (!isPlayerTracked(args[1])) {
			    sender.sendMessage(ChatColor.RED + "Dieser Spieler ist nicht auf der Liste!");
			    return true;
			}
			calculate(name);
			joinedTime.put(name, System.currentTimeMillis());
			if (args.length == 2) {
			    sender.sendMessage(ChatColor.GREEN + "Statistiken von " + name);
			    Long[][] playerTime = trackedPlayers.get(name);
			    long allTimePlayTime = 0;
			    for (Long[] week : playerTime) {
				long playTime = 0;
				for (Long day : week) {
				    if (day == null) continue;
				    playTime += day;
				}
				if (playTime != 0) {
				    sender.sendMessage(ChatColor.YELLOW + "Spielzeit in der Woche " + (Arrays.asList(playerTime).indexOf(week) + 1));
				    sender.sendMessage(calculateTime(playTime));
				}
				allTimePlayTime += playTime;
			    }
			    sender.sendMessage(ChatColor.YELLOW + "Spielzeit insgesamt");
			    sender.sendMessage(calculateTime(allTimePlayTime));
			}
			else {
			    int kw = 0;
			    try {
				kw = Integer.valueOf(args[2]);
				if (kw < 1 || kw > 52) {
				    sender.sendMessage(ChatColor.RED + "Bitte eine Kalenderwoche zwischen 1 und 52 angeben!");
				    return true;
				}
				int kwReal = kw;
				kw -= 1;
				Long[] week = trackedPlayers.get(name)[kw];
				if (week == null) {
				    sender.sendMessage(ChatColor.RED + "Keine Spielzeit in der Woche " + ChatColor.YELLOW + kwReal);
				    return true;
				}
				long onlineZeit = 0;
				for (Long day: week) {
				    if (day == null) day = (long) 0;
				    onlineZeit += day;
				}
				if (onlineZeit == 0) {
				    sender.sendMessage(ChatColor.RED + "Keine Spielzeit in der Woche " + ChatColor.YELLOW + kwReal);
				    return true;
				}
				sender.sendMessage(ChatColor.GREEN + "Statistiken von " + name + " in der Woche " + ChatColor.YELLOW + kwReal);
				int i = 0;
				for (Long day: week) {
				    if (day == null || day == 0) {
					sender.sendMessage(ChatColor.YELLOW + dayValues[i] + ChatColor.WHITE + ": Keine Spielzeit");
				    }
				    else sender.sendMessage(ChatColor.YELLOW + dayValues[i] + ChatColor.WHITE + ": " + calculateTime(day));
				    i++;
				}
			    }
			    catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Bitte gib eine Zahl als Woche an!");
				return true;
			    }

			}
		    }
		}
		else sender.sendMessage(ChatColor.RED + "Du hast nicht die Berechtigung hierzu!");
	    }
	    else sender.sendMessage(ChatColor.RED + "Dieses Argument ist unbekannt. Tippe für die Hilfe /tracker help|hilfe");
	}
	return true;
    }

    private boolean isPlayerTracked(String name) {
	return trackedPlayers.containsKey(name);
    }

    private boolean addTrackedPlayer(String name) {
	if (isPlayerTracked(name)) return false;
	Long[][] weeks = new Long[52][7];
	trackedPlayers.put(name, weeks);
	return true;
    }

    private void calculate(String name) {
	long onlineTime = 0;
	if (joinedTime.containsKey(name)) onlineTime = System.currentTimeMillis() - joinedTime.get(name);
	Calendar c = Calendar.getInstance();
	c.clear();
	c.setTime(new Date());
	int week = c.get(Calendar.WEEK_OF_YEAR) -1;
	// Minus 1, da die Woche bei 1 und nicht 0 startet
	int day = c.get(Calendar.DAY_OF_WEEK) - 1;
	Long[][] priorOnlineTime = trackedPlayers.get(name);
	if (priorOnlineTime[week][day] == null) priorOnlineTime[week][day] = onlineTime;
	else priorOnlineTime[week][day] += onlineTime;
	if (joinedTime.containsKey(name)) joinedTime.remove(name);
	trackedPlayers.put(name, priorOnlineTime);
    }

    private String calculateTime(long time) {
	int milliseconds = (int) time;
	int seconds = (int) (milliseconds / 1000) % 60 ;
	int minutes = (int) ((milliseconds / (1000*60)) % 60);
	int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
	int days = (int) ((milliseconds / (1000*60*60*24)) % 30);
	return days + " Tage, " + hours + " Stunden, " + minutes + " Minuten, " + seconds + " Sekunden";
    }
}
