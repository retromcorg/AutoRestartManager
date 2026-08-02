package com.johnymuffin.beta.autorestartmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class AutoRestartManager extends JavaPlugin {
    //Basic Plugin Info
    private static AutoRestartManager plugin;
    private Logger log;
    private String pluginName;
    private PluginDescriptionFile pdf;
    private ARMConfig config;

    //Restart Info
    private int restartTime;
    private int restartTimeExtension;
    private int maximumPlayers;
    private int restartCountdownTime;
    private int remainingRestartCountdownTime;
    private int minuteCount;
    private volatile boolean sequenceStarted;
    private volatile boolean shutdownStarted;


    @Override
    public void onEnable() {
        plugin = this;
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        pluginName = pdf.getName();
        log.info("[" + pluginName + "] Is Loading, Version: " + pdf.getVersion());
        config = new ARMConfig(plugin);
        //Config Values
        restartTime = config.getConfigInteger("restart-time");
        restartTimeExtension = config.getConfigInteger("maximum-restart-time-extension");
        maximumPlayers = config.getConfigInteger("maximum-players");
        restartCountdownTime = config.getConfigInteger("restart-countdown-time");
        //Default Values
        remainingRestartCountdownTime = restartCountdownTime;
        minuteCount = 0;
        sequenceStarted = false;
        shutdownStarted = false;

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            minuteUpdate();
        }, 20L, 20L * 60);

        getServer().getPluginManager().registerEvent(
                Event.Type.PLAYER_PRELOGIN,
                new RestartPlayerListener(this),
                Event.Priority.Highest,
                this
        );


    }

    @Override
    public void onDisable() {
        log.info("[" + pluginName + "] Is Disabling");
    }

    private void minuteUpdate() {
        minuteCount = minuteCount + 1;
        //Handle normal restart countdown
        if (!sequenceStarted) {
            if (minuteCount < restartTime) {
                return;
            }
            if (minuteCount >= (restartTime + restartTimeExtension)) {
                startRestartTask();
                return;
            }
            if (Bukkit.getServer().getOnlinePlayers().length < maximumPlayers) {
                startRestartTask();
            }
            return;
        }
        //Handle restart task countdown
        if (remainingRestartCountdownTime <= 0) {
            shutdownStarted = true;
            log.info("[" + pluginName + "] Shutting Down Server, Server Is Restarting.");
            Bukkit.getServer().broadcastMessage(ChatColor.RED + "The server is restarting.");
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                log.info("[" + pluginName + "] Saving Players");
                Bukkit.getServer().savePlayers();
                log.info("[" + pluginName + "] Saving Worlds");
                for (World world : Bukkit.getServer().getWorlds()) {
                    world.save();
                }
                log.info("[" + pluginName + "] Kicking Players");
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    p.kickPlayer("The server is restarting, please reconnect in a few minutes");
                }
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    Bukkit.shutdown();
                }, 20 * 5);
            }, 20 * 5);
            return;
        }
        Bukkit.getServer().broadcastMessage(ChatColor.RED + "The server will restart in " + remainingRestartCountdownTime + " minutes.");
        remainingRestartCountdownTime = remainingRestartCountdownTime - 1;


    }

    private void startRestartTask() {
        remainingRestartCountdownTime = restartCountdownTime;
        log.info("[" + pluginName + "] A restart has been scheduled for " + restartCountdownTime + " minutes.");
        sequenceStarted = true;
    }

    public boolean isRestartSequenceStarted() {
        return sequenceStarted;
    }

    public boolean isShutdownStarted() {
        return shutdownStarted;
    }
}
