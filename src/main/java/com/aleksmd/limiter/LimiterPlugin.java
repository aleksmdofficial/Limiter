package com.aleksmd.limiter;

import com.aleksmd.limiter.commands.ReloadCommandExecutor;
import com.aleksmd.limiter.eventhandlers.InventoryEventHandler;
import com.aleksmd.limiter.eventhandlers.ShulkerBoxEventHandler;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the HolyLimiter plugin.
 * This class sets up command executors and event handlers on plugin enable, and performs cleanup on plugin disable.
 */
public final class LimiterPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Load the default configuration file
        saveDefaultConfig();

        // Register event handlers
        getServer().getPluginManager().registerEvents(new InventoryEventHandler(this), this);
        getServer().getPluginManager().registerEvents(new ShulkerBoxEventHandler(this), this);

        // Initialize the command executor for the "holylimiter" command
        ReloadCommandExecutor commandExecutor = new ReloadCommandExecutor(this);
        getCommand("holylimiter").setExecutor(commandExecutor);
        getCommand("holylimiter").setTabCompleter(commandExecutor);

        getLogger().info("HolyLimiter enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("HolyLimiter disabled!");
    }
}


