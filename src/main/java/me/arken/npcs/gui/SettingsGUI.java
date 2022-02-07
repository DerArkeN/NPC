package me.arken.npcs.gui;

import me.arken.npcs.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SettingsGUI extends GUI {

    public SettingsGUI(NPC npc) {
        super(npc);
    }

    @Override
    public List<ItemStack> getFunctionalItems() {
        return new ArrayList<>();
    }

    @Override
    public String getParentGUI() {
        return null;
    }

    @Override
    protected void handle(Player player, ItemStack currentItem, NPC npc) {
        player.openInventory(npc.getGuiManager().getGUI(currentItem.getItemMeta().getDisplayName()).getInventory());
    }

}
