package me.arken.npcs.gui;

import me.arken.npcs.NPCs;
import me.arken.npcs.npc.NPC;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NameGUI extends GUI {

    public NameGUI(NPC npc) {
        super(npc);
    }

    @Override
    public List<ItemStack> getFunctionalItems() {
        return List.of(createFunctionalItem("Set Name", Material.NAME_TAG));
    }

    @Override
    protected void handle(Player player, ItemStack currentItem, NPC npc) {
        if("Set Name".equals(currentItem.getItemMeta().getDisplayName())) {
            new AnvilGUI.Builder()
                    .onComplete((target, text) -> {
                        String name = ChatColor.translateAlternateColorCodes('&', text);
                        npc.setName(name);
                        target.sendMessage(NPCs.getPrefix() + "§aName set to " + name);
                        return AnvilGUI.Response.close();
                    })
                    .text("")
                    .itemLeft(new ItemStack(Material.PAPER))
                    .title("Enter name")
                    .plugin(NPCs.getPlugin())
                    .open(player);
        }
    }

}
