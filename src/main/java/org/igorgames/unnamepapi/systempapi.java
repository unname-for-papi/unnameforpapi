package org.igorgames.unnamepapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class systempapi extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "unnamepapi";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Igorgames";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        params = params.replaceAll("^_+", "");
        String[] parts = params.split("____");

        if (parts[0].equalsIgnoreCase("worldonlineadding")) {
            List<World> worlds = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                World world = Bukkit.getWorld(parts[i]);
                if (world == null) {
                    return "the world " + parts[i] + " does not exist";
                }
                worlds.add(world);
            }

            int count = 0;
            for (Player playerother : Bukkit.getOnlinePlayers()) {
                for (World world : worlds) {
                    if (world.getName().equalsIgnoreCase(playerother.getWorld().getName())) {
                        count += 1;
                        break;
                    }
                }
            }
            return String.valueOf(count);
        } else if (parts[0].equalsIgnoreCase("itemcount")) {
            if (player == null) return "0";
            List<Material> items = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                Material item = Material.getMaterial(parts[i].toUpperCase());
                if (item == null) {
                    return "the item " + parts[i] + " does not exist";
                }
                items.add(item);
            }
            int count = 0;
            for (ItemStack stack : player.getInventory().getContents()) {
                if (stack != null && items.contains(stack.getType())) {
                    count += stack.getAmount();
                }
            }
            return String.valueOf(count);
        } else if (parts[0].equalsIgnoreCase("diedsworld")) {
            if (player == null) return "0";
            int count = 0;
            for (int i = 1; i < parts.length; i++) {
                count += Unnamepapi.playersData.get(player.getUniqueId()).config.getInt("worlds." + parts[i] + ".died",0);
            }
            return String.valueOf(count);
        } else if (parts[0].equalsIgnoreCase("killsworld")) {
            if (player == null) return "0";
            int count = 0;
            for (int i = 1; i < parts.length; i++) {
                count += Unnamepapi.playersData.get(player.getUniqueId()).config.getInt("worlds." + parts[i] + ".kill",0);
            }
            return String.valueOf(count);
        } else if (parts[0].equalsIgnoreCase("spenttimeworld_S")) {
            if (player == null) return "0";
            int count = 0;
            for (int i = 1; i < parts.length; i++) {
                count += Unnamepapi.playersData.get(player.getUniqueId()).config.getInt("worlds." + parts[i] + ".time",0);
            }
            return String.valueOf(count);
        } else if (parts[0].equalsIgnoreCase("spenttimeworld_M")) {
            if (player == null) return "0";
            int count = 0;
            for (int i = 1; i < parts.length; i++) {
                count += Math.floor(Unnamepapi.playersData.get(player.getUniqueId()).config.getInt("worlds." + parts[i] + ".time",0)/60);
            }
            return String.valueOf(count);
        } else if (parts[0].equalsIgnoreCase("spenttimeworld_H")) {
            if (player == null) return "0";
            int count = 0;
            for (int i = 1; i < parts.length; i++) {
                count += Math.floor(Unnamepapi.playersData.get(player.getUniqueId()).config.getInt("worlds." + parts[i] + ".time",0)/3600);
            }
            return String.valueOf(count);
        } else if (parts[0].equalsIgnoreCase("spenttimeworld_D")) {
            if (player == null) return "0";
            int count = 0;
            for (int i = 1; i < parts.length; i++) {
                count += Math.floor(Unnamepapi.playersData.get(player.getUniqueId()).config.getInt("worlds." + parts[i] + ".time",0)/86400);
            }
            return String.valueOf(count);
        } else if (parts[0].equalsIgnoreCase("spenttimeworldtemplate")) {
            if (player == null) return "0";
            int totalSeconds = 0;


            for (int i = 2; i < parts.length; i++) {
                totalSeconds += Unnamepapi.playersData
                        .get(player.getUniqueId())
                        .config.getInt("worlds." + parts[i] + ".time", 0);
            }


            int days = totalSeconds / 86400;
            int hours = (totalSeconds % 86400) / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;


            String formattedTimeS = String.format("%02d", seconds);
            String formattedTimeM = String.format("%02d", minutes);
            String formattedTimeH = String.format("%02d", hours);
            String formattedTimeD = String.format("%02d", days);


            String output = Unnamepapi.timeyml.getString("template." + parts[1], "the template " + parts[1] + " does not exist");

            output = output.replace("%{S}%", formattedTimeS);
            output = output.replace("%{M}%", formattedTimeM);
            output = output.replace("%{H}%", formattedTimeH);
            output = output.replace("%{D}%", formattedTimeD);

            return output;
        } else if (parts[0].equalsIgnoreCase("serveronline")) {
            int count = 0;
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                count += 1;
            }
            return String.valueOf(count);
        }

        return null;
    }
}
