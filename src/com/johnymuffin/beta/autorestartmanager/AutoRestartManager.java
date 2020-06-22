package com.johnymuffin.beta.autorestartmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
    private int minuteCount;
    private boolean sequenceStarted;


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
        minuteCount = 0;
        sequenceStarted = false;

        //Run timer in Async thread so TPS doesn't effect the actual time
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, () -> {
            //Schedule code to run on the main thread
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                minuteUpdate();
            }, 0L);
        }, 20l, 20l * 60);


    }

    @Override
    public void onDisable() {
        log.info("[" + pluginName + "] Is Disabling");
    }

    private void minuteUpdate() {
        log.info("[" + pluginName + "] " + minuteCount + "/" + restartTime);
        minuteCount = minuteCount + 1;
        //Handle normal restart countdown
        if (!sequenceStarted) {
            if (minuteCount < restartTime) {
                return;
            }
            if (minuteCount > (restartTime + restartTimeExtension)) {
                startRestartTask();
            }
            if (Bukkit.getServer().getOnlinePlayers().length < maximumPlayers) {
                startRestartTask();
            }
            return;
        }
        //Handle restart task countdown
        if (restartCountdownTime <= 0) {
            //Shutdown server
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
            log.info("[" + pluginName + "] Shutting Down Server, Server Is Rhystarting");

            ARMCommandSender armCommandSender = new ARMCommandSender();
            armCommandSender.runCommand("/stop");
        }
        Bukkit.getServer().broadcastMessage(ChatColor.RED + "The server will restart in " + restartCountdownTime + " minutes.");
        restartCountdownTime = restartCountdownTime - 1;


    }

    private void startRestartTask() {
        log.info("[" + pluginName + "] A restart has been scheduled for " + restartCountdownTime + "minutes.");
        sequenceStarted = true;
    }


}
