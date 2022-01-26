package me.arken.npcs.gui;

import me.arken.npcs.NPCs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class SettingsGUI extends GUI {

    @Override
    public ArrayList<ItemStack> getFunctionalItems() {
        ArrayList<ItemStack> contents = new ArrayList<>();

        for(GUI gui : NPCs.getGuiManager().getGUIS()) {
            if(!gui.equals(this)) {
                ItemStack functionalItem = createFunctionalItem(gui.getName(), gui.getFunctionalItems().get(0).getType());contents.add(functionalItem);
            }
        }

        return contents;
    }

    @Override
    protected void handle(Player player, ItemStack currentItem, GUIManager guiManager) {
        player.openInventory(guiManager.getGUI(currentItem.getItemMeta().getDisplayName()).getInventory());
    }

}
