package me.arken.npcs.gui;

import me.arken.npcs.NPCs;
import me.arken.npcs.listeners.ListenerInteractNPC;
import me.arken.npcs.npc.NPC;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class NameGUI extends GUI {

    @Override
    public ArrayList<ItemStack> getFunctionalItems() {
        ArrayList<ItemStack> contents = new ArrayList<>();
        contents.add(createFunctionalItem("Set Name", Material.NAME_TAG));
        contents.add(createFunctionalItem("Reset", Material.BARRIER));

        return contents;
    }

    @Override
    protected void handle(Player player, ItemStack currentItem, GUIManager guiManager) {
        NPC npc = ListenerInteractNPC.getCurrentNPC();

        switch(currentItem.getItemMeta().getDisplayName()) {
            case "Set Name" -> {
                new AnvilGUI.Builder()
                        .onComplete((target, text) -> {
                            npc.setName(text);
                            target.sendMessage(NPCs.getPrefix() + "§aName set to " + text);
                            return AnvilGUI.Response.close();
                        })
                        .text("")
                        .itemLeft(new ItemStack(Material.PAPER))
                        .title("Enter name")
                        .plugin(NPCs.getPlugin())
                        .open(player);
            }
            case "Reset" -> {
                npc.setName("NPC");
            }
        }
    }

}
