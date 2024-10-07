package com.aleksmd.limiter.commands;

import com.aleksmd.limiter.LimiterPlugin;
import com.aleksmd.limiter.utils.ColorFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;

import java.util.Collections;
import java.util.List;

/**
 * Handles the execution and tab completion for the reload command of the HolyLimiter plugin.
 */
public class ReloadCommandExecutor implements CommandExecutor, TabCompleter {
    private final LimiterPlugin plugin;

    public ReloadCommandExecutor(LimiterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("holylimiter.reload")) {
                PluginManager pluginManager = plugin.getServer().getPluginManager();

                pluginManager.disablePlugin(plugin);
                plugin.reloadConfig();
                pluginManager.enablePlugin(plugin);

                sender.sendMessage(ColorFormatter.formatColors("&aConfiguration reloaded successfully!"));
            } else {
                sender.sendMessage(ColorFormatter.formatColors(plugin.getConfig().getString("messages.no-permission")));
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }
}

