package org.igorgames.unnamepapi;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class PlayerData {
    public File file;
    public FileConfiguration config;

    public PlayerData(File file, FileConfiguration config) {
        this.file = file;
        this.config = config;
    }
}
