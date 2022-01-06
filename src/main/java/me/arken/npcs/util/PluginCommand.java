package me.arken.npcs.util;

import me.arken.npcs.util.ExecuteCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class PluginCommand implements CommandExecutor {
    private final ExecuteCommand executeCommand;

    public PluginCommand() {
        executeCommand = getClass().getDeclaredAnnotation(ExecuteCommand.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!executeCommand.permission().isEmpty() && !sender.hasPermission(executeCommand.permission())) {
            sender.sendMessage(ChatColor.RED + "You don't have the required permissions to execute this command.");
            return true;
        }
        execute(sender, command, label, args);
        return true;
    }

    protected abstract void execute(CommandSender sender, Command command, String label, String[] args);

    public ExecuteCommand getCommand() {
        return executeCommand;
    }
}
