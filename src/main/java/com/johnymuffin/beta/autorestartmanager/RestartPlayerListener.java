package com.johnymuffin.beta.autorestartmanager;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPreLoginEvent;

public class RestartPlayerListener extends PlayerListener {
    private final AutoRestartManager plugin;

    public RestartPlayerListener(AutoRestartManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerPreLogin(PlayerPreLoginEvent event) {
        if (!plugin.isShutdownStarted()) {
            return;
        }

        event.disallow(
                PlayerPreLoginEvent.Result.KICK_OTHER,
                "The server is restarting, please reconnect in a few minutes"
        );
    }
}
