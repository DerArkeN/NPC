package me.arken.npcs.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class SettingsGUI extends GUI {

    @Override
    public ArrayList<ItemStack> getFunctionalItems() {
        return new ArrayList<>();
    }

    @Override
    public String getParentGUI() {
        return null;
    }

    @Override
    protected void handle(Player player, ItemStack currentItem, GUIManager guiManager) {
        player.openInventory(guiManager.getGUI(currentItem.getItemMeta().getDisplayName()).getInventory());
    }

}
