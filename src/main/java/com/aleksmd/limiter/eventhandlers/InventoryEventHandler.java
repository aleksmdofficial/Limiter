package com.aleksmd.limiter.eventhandlers;

import com.aleksmd.limiter.LimiterPlugin;
import com.aleksmd.limiter.utils.ColorFormatter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Handles various events to enforce item pickup and inventory management rules.
 */
public class InventoryEventHandler implements Listener {
    private final LimiterPlugin plugin;

    public InventoryEventHandler(LimiterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem().getItemStack();

        if (itemStack.getType() == Material.TOTEM_OF_UNDYING) {
            int maxAllowed = getMaxItemLimit(player, "totem");
            int currentCount = getItemCount(player, Material.TOTEM_OF_UNDYING);

            if (currentCount + itemStack.getAmount() > maxAllowed) {
                event.setCancelled(true);
                notifyPlayer(player, "totem", currentCount, maxAllowed);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Inventory inventory = event.getInventory();
            checkInventory(player, inventory);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            PlayerInventory inventory = player.getInventory();
            checkInventory(player, inventory);
        }
    }

    private void checkInventory(Player player, Inventory inventory) {
        enforceItemLimit(player, inventory, Material.TOTEM_OF_UNDYING, "totem");
    }

    private void enforceItemLimit(Player player, Inventory inventory, Material material, String configKey) {
        int maxLimit = getMaxItemLimit(player, configKey);
        int currentCount = getItemCount(player, material);

        if (currentCount > maxLimit) {
            int excess = currentCount - maxLimit;
            removeExcessItems(player, inventory, material, excess);
            notifyPlayer(player, configKey, currentCount - excess, maxLimit);
        }
    }

    private int getMaxItemLimit(Player player, String configKey) {
        int defaultLimit = plugin.getConfig().getInt("limit.default." + configKey);
        return plugin.getConfig().getConfigurationSection("limit").getKeys(false).stream()
                .filter(key -> player.hasPermission("holylimiter.limit." + key))
                .mapToInt(key -> plugin.getConfig().getInt("limit." + key + "." + configKey))
                .max().orElse(defaultLimit);
    }

    private int getItemCount(Player player, Material material) {
        return player.getInventory().all(material).values().stream()
                .mapToInt(ItemStack::getAmount)
                .sum();
    }

    private void removeExcessItems(Player player, Inventory inventory, Material material, int excessAmount) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material) {
                int toRemove = Math.min(excessAmount, item.getAmount());
                item.setAmount(item.getAmount() - toRemove);
                excessAmount -= toRemove;
                if (item.getAmount() <= 0) {
                    inventory.remove(item);
                }
                if (excessAmount <= 0) break;
            }
        }
    }

    private void notifyPlayer(Player player, String configKey, int count, int max) {
        String messageTemplate = plugin.getConfig().getString("messages." + configKey)
                .replace("%" + configKey + "%", String.valueOf(count))
                .replace("%" + configKey + "_max%", String.valueOf(max));
        String formattedMessage = ColorFormatter.formatColors(messageTemplate);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formattedMessage));
    }
}

