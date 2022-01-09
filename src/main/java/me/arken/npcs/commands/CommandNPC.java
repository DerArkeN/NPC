package me.arken.npcs.commands;

import me.arken.npcs.NPCs;
import me.arken.npcs.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandHandler(command = "npc", permission = "op")
public class CommandNPC extends PluginCommand {

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                NPC npc = new NPC(onlinePlayer, player.getDisplayName());
                switch(args[0]) {
                    case "create":
                        npc.show();
                        npc.setLocation(player.getLocation());
                        npc.setSkin(player.getDisplayName(), "1111110");
                        npc.setMainHandItem(player.getInventory().getItemInMainHand());
                        ItemStack[] contents = {player.getInventory().getArmorContents()[0], null, player.getInventory().getArmorContents()[2], null};
                        npc.setArmorContents(contents);

                        player.sendMessage(NPCs.getPrefix() + "§aCreated NPC with Id: " + npc.getServerNPC().getId());
                }
            }
        }
    }

}
