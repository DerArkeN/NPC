package me.arken.npcs.gui;

import me.arken.npcs.NPCs;
import me.arken.npcs.listeners.ListenerInteractNPC;
import me.arken.npcs.npc.NPC;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class SkinGUI extends GUI {

    @Override
    public ArrayList<ItemStack> getFunctionalItems() {
        ArrayList<ItemStack> contents = new ArrayList<>();
        contents.add(createFunctionalItem("Set Skin", Material.PLAYER_HEAD));
        contents.add(createFunctionalItem("Change layers", Material.WHITE_BANNER));
        contents.add(createFunctionalItem("Reset", Material.BARRIER));

        return contents;
    }

    @Override
    protected void handle(Player player, ItemStack currentItem, GUIManager guiManager) {
        NPC npc = ListenerInteractNPC.getCurrentNPC();

        switch(currentItem.getItemMeta().getDisplayName()) {
            case "Set Skin" -> {
                new AnvilGUI.Builder()
                        .onComplete((target, text) -> {
                            npc.setSkin(text, null);
                            target.sendMessage(NPCs.getPrefix() + "§aSkin set to " + text);
                            return AnvilGUI.Response.close();
                        })
                        .text("")
                        .itemLeft(new ItemStack(Material.PAPER))
                        .title("Enter username")
                        .plugin(NPCs.getPlugin())
                        .open(player);
            }
            case "Change layers" -> {
                LayerGUI layerGUI = new LayerGUI();
                player.openInventory(layerGUI.getInventory());
            }
            case "Reset" -> {
                npc.setSkin("Alex", null);
            }
        }
    }

    static class LayerGUI extends GUI {

        @Override
        public ArrayList<ItemStack> getFunctionalItems() {
            ArrayList<ItemStack> contents = new ArrayList<>();
            contents.add(createFunctionalItem("Cape", Material.GREEN_STAINED_GLASS_PANE));
            contents.add(createFunctionalItem("Jacket", Material.GREEN_STAINED_GLASS_PANE));
            contents.add(createFunctionalItem("Left Sleeve", Material.GREEN_STAINED_GLASS_PANE));
            contents.add(createFunctionalItem("Right Sleeve", Material.GREEN_STAINED_GLASS_PANE));
            contents.add(createFunctionalItem("Left Pants", Material.GREEN_STAINED_GLASS_PANE));
            contents.add(createFunctionalItem("Left Pants", Material.GREEN_STAINED_GLASS_PANE));
            contents.add(createFunctionalItem("Left Head", Material.GREEN_STAINED_GLASS_PANE));

            return contents;
        }

        @Override
        public String getParentGUI() {
            return "Skin";
        }

        @Override
        protected void handle(Player player, ItemStack currentItem, GUIManager guiManager) {

        }
    }

}
