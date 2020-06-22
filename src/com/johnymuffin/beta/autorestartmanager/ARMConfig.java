package com.johnymuffin.beta.autorestartmanager;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class ARMConfig extends Configuration {
    private ARMConfig armConfig;
    private AutoRestartManager plugin;

    public ARMConfig(AutoRestartManager plugin) {
        super(new File(plugin.getDataFolder(), "config.yml"));
        this.reload();
    }

    private void write() {
        //Main
        generateConfigOption("config-version", 1);
        //Setting
        generateConfigOption("restart-time", 60 * 24);
        generateConfigOption("maximum-players", 3);
        generateConfigOption("maximum-restart-time-extension", 60 * 12);
        generateConfigOption("restart-countdown-time", 10);


    }

    private void generateConfigOption(String key, Object defaultValue) {
        if (this.getProperty(key) == null) {
            this.setProperty(key, defaultValue);
        }
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    //Getters Start
    public Object getConfigOption(String key) {
        return this.getProperty(key);
    }

    public String getConfigString(String key) {
        return String.valueOf(getConfigOption(key));
    }

    public Integer getConfigInteger(String key) {
        return Integer.valueOf(getConfigString(key));
    }

    public Long getConfigLong(String key) {
        return Long.valueOf(getConfigString(key));
    }

    public Double getConfigDouble(String key) {
        return Double.valueOf(getConfigString(key));
    }

    public Boolean getConfigBoolean(String key) {
        return Boolean.valueOf(getConfigString(key));
    }


    //Getters End


    private void reload() {
        this.load();
        this.write();
        this.save();
    }
}
