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
        // %unnamepapi____worldonlineadding____world____world_nether____world_the_end%
        // %unnamepapi____itemcount____grass_block%
        // %unnamepapi____diedsworld____world____world_nether____world_the_end%
        // %unnamepapi____killsworld____world____world_nether____world_the_end%
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
        }

        return null;
    }
}
