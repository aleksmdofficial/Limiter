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
 * Handles item pickup and inventory interactions specifically for shulker boxes.
 */
public class ShulkerBoxEventHandler implements Listener {
    private final LimiterPlugin plugin;

    public ShulkerBoxEventHandler(LimiterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();

        if (isShulkerBox(item.getType())) {
            int maxAllowed = getShulkerBoxLimit(player, "shulker");
            int currentCount = countShulkerBoxes(player);

            if (currentCount + item.getAmount() > maxAllowed) {
                event.setCancelled(true);
                sendLimitMessage(player, "shulker", currentCount, maxAllowed);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Inventory inventory = event.getInventory();
            handleInventoryCheck(player, inventory);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            PlayerInventory inventory = player.getInventory();
            handleInventoryCheck(player, inventory);
        }
    }

    private void handleInventoryCheck(Player player, Inventory inventory) {
        enforceShulkerBoxLimit(player, inventory);
    }

    private void enforceShulkerBoxLimit(Player player, Inventory inventory) {
        int maxLimit = getShulkerBoxLimit(player, "shulker");
        int currentCount = countShulkerBoxes(player);

        if (currentCount > maxLimit) {
            int excess = currentCount - maxLimit;
            removeExcessShulkerBoxes(player, inventory, excess);
            sendLimitMessage(player, "shulker", currentCount - excess, maxLimit);
        }
    }

    private int getShulkerBoxLimit(Player player, String configKey) {
        int defaultLimit = plugin.getConfig().getInt("limit.default." + configKey);
        return plugin.getConfig().getConfigurationSection("limit").getKeys(false).stream()
                .filter(key -> player.hasPermission("holylimiter.limit." + key))
                .mapToInt(key -> plugin.getConfig().getInt("limit." + key + "." + configKey))
                .max().orElse(defaultLimit);
    }

    private int countShulkerBoxes(Player player) {
        return (int) player.getInventory().all(Material.SHULKER_BOX).values().stream()
                .mapToInt(ItemStack::getAmount)
                .sum();
    }

    private void removeExcessShulkerBoxes(Player player, Inventory inventory, int excessAmount) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && isShulkerBox(item.getType())) {
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

    private void sendLimitMessage(Player player, String configKey, int count, int max) {
        String messageTemplate = plugin.getConfig().getString("messages." + configKey)
                .replace("%" + configKey + "%", String.valueOf(count))
                .replace("%" + configKey + "_max%", String.valueOf(max));
        String formattedMessage = ColorFormatter.formatColors(messageTemplate);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formattedMessage));
    }

    private boolean isShulkerBox(Material material) {
        switch (material) {
            case SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case BLACK_SHULKER_BOX:
                return true;
            default:
                return false;
        }
    }
}

