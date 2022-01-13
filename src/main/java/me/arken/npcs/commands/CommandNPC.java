package me.arken.npcs.commands;

import me.arken.npcs.NPCs;
import me.arken.npcs.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

@CommandHandler(command = "npc", permission = "op")
public class CommandNPC extends PluginCommand {

    private static final ItemStack editor = new ItemStack(Material.NAME_TAG);

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                NPC npc = new NPC(onlinePlayer, "NPC");
                switch(args[0]) {
                    case "create":
                        npc.setLocation(player.getLocation());

                        player.sendMessage(NPCs.getPrefix() + "§aCreated NPC with Id: " + npc.getServerNPC().getId());
                    case "editor":
                        ItemMeta itemMeta = editor.getItemMeta();
                        itemMeta.setDisplayName(ChatColor.AQUA + "Editor");
                        editor.setItemMeta(itemMeta);

                        if(!(player.getInventory().contains(editor))) player.getInventory().setItemInOffHand(editor);
                }
            }
        }
    }

    public static ItemStack getEditor() {
        return editor;
    }
}
