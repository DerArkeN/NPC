package me.arken.npcs.commands;

import me.arken.npcs.util.ExecuteCommand;
import me.arken.npcs.util.NPC;
import me.arken.npcs.util.PluginCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@ExecuteCommand(command = "npc", permission = "op")
public class CommandCreate extends PluginCommand {

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            NPC npc = npc = new NPC(player, player.getDisplayName());;
            switch(args[0]) {
                case "create":
                    npc.show();
                    npc.setLocation(player.getLocation());
                    npc.setSkin(player.getDisplayName(), "1111110");
                    npc.setMainHandItem(player.getInventory().getItemInMainHand());
                    ItemStack[] contents = {player.getInventory().getArmorContents()[0], null, player.getInventory().getArmorContents()[2], null};
                    npc.setArmorContents(contents);
            }
        }
    }

}
