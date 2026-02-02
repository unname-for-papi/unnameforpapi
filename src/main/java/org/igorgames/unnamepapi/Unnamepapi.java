package org.igorgames.unnamepapi;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class Unnamepapi extends JavaPlugin implements Listener {
    public static Map<UUID,PlayerData> playersData = new HashMap<>();
    public static FileConfiguration timeyml;
    public static File timefile;
    private void run() {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        timefile = new File(getDataFolder(), "time.yml");
        try {
            timefile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        timeyml = YamlConfiguration.loadConfiguration(timefile);
        if (!timeyml.contains("template.default")) {
            timeyml.set("template.default","%{D}%:%{H}%:%{M}%:%{S}%");
            try {
                timeyml.save(timefile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        File players = new File(getDataFolder(), "PlayersData");
        if (!players.exists()) {
            players.mkdirs();
        }
        File info = new File(getDataFolder(), "PlayersData/Do not delete or change");
        try {
            info.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getServer().getPluginManager().registerEvents(this, this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            File playerdata = new File(getDataFolder(), "PlayersData/"+player.getUniqueId().toString()+".yml");
            if (!playerdata.exists()) {
                try {
                    playerdata.createNewFile();
                    FileConfiguration playerdatayml = YamlConfiguration.loadConfiguration(playerdata);
                    for (World world : Bukkit.getWorlds()) {
                        playerdatayml.set("worlds." + world.getName() + ".died", 0);
                        playerdatayml.set("worlds." + world.getName() + ".kill", 0);
                    }
                    try {
                        playerdatayml.save(playerdata);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    playersData.put(player.getUniqueId(),new PlayerData(playerdata, playerdatayml));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (players.exists()) {
            File[] files = players.listFiles();
            for (File file : files) {
                if (file.getName().endsWith(".yml")) {
                    playersData.put(UUID.fromString(file.getName().replace(".yml","")),new PlayerData(file, YamlConfiguration.loadConfiguration(file)));
                }
            }
        }
    }
    public void everysec() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            FileConfiguration Playeryml = playersData.get(player.getUniqueId()).config;
            File Playerfile = playersData.get(player.getUniqueId()).file;
            if (!Playeryml.contains("worlds." + player.getWorld().getName() + ".time")) {
                Playeryml.set("worlds." + player.getWorld().getName() + ".time", 0);
            }
            Playeryml.set("worlds." + player.getWorld().getName() + ".time", Playeryml.getInt("worlds." + player.getWorld().getName() + ".time",0)+1);
            try {
                Playeryml.save(Playerfile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void onEnable() {
        run();
        new systempapi().register();
        getCommand("unnp").setExecutor(this::onCommand);
        getCommand("unnp").setTabCompleter(this::onTabComplete);
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            everysec();
        }, 0L, 20L);
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        File playerdata = new File(getDataFolder(), "PlayersData/"+player.getUniqueId().toString()+".yml");
        if (!playerdata.exists()) {
            try {
                playerdata.createNewFile();
                FileConfiguration playerdatayml = YamlConfiguration.loadConfiguration(playerdata);
                for (World world : Bukkit.getWorlds()) {
                    playerdatayml.set("worlds." + world.getName() + ".died", 0);
                    playerdatayml.set("worlds." + world.getName() + ".kill", 0);
                }
                try {
                    playerdatayml.save(playerdata);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                playersData.put(player.getUniqueId(),new PlayerData(playerdata, playerdatayml));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getEntity();
        Player killer = deadPlayer.getKiller();
        String worldName = deadPlayer.getWorld().getName();
        FileConfiguration deadPlayeryml = playersData.get(deadPlayer.getUniqueId()).config;
        File deadPlayerfile = playersData.get(deadPlayer.getUniqueId()).file;
        if (!deadPlayeryml.contains("worlds." + worldName)) {
            deadPlayeryml.set("worlds." + worldName + ".died", 0);
            deadPlayeryml.set("worlds." + worldName + ".kill", 0);
        }
        deadPlayeryml.set("worlds." + worldName + ".died", deadPlayeryml.getInt("worlds." + worldName + ".died",0)+1);
        try {
            deadPlayeryml.save(deadPlayerfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (killer != null) {
            FileConfiguration killerPlayeryml = playersData.get(killer.getUniqueId()).config;
            File killerPlayerfile = playersData.get(killer.getUniqueId()).file;
            if (!killerPlayeryml.contains("worlds." + worldName)) {
                killerPlayeryml.set("worlds." + worldName + ".died", 0);
                killerPlayeryml.set("worlds." + worldName + ".kill", 0);
            }
            killerPlayeryml.set("worlds." + worldName + ".kill", killerPlayeryml.getInt("worlds." + worldName + ".kill", 0)+1);
            try {
                killerPlayeryml.save(killerPlayerfile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c/unnp reload");
            return super.onCommand(sender, command, label, args);
        }
        if (args[0].equalsIgnoreCase("reload")) {
            try {
                run();
                sender.sendMessage("§aSuccess to reload");
            } catch (Exception e) {
                sender.sendMessage("§cFailed to reload");
                throw new RuntimeException(e);
            }
        }
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload");
        }
        return Collections.emptyList();
    }
}
