package me.arken.npcs.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GUI {

    private final Inventory inventory;

    private final ArrayList<ItemStack> functionalItems;
    private final Material filler;

    public GUI(Player player, String name, ArrayList<ItemStack> functionalItems, Material filler) {
        this.functionalItems = functionalItems;
        this.filler = filler;

        ItemStack fillerItem = new ItemStack(filler);
        ItemMeta fillerItemMeta = fillerItem.getItemMeta();
        fillerItemMeta.setDisplayName("");
        fillerItem.setItemMeta(fillerItemMeta);

        int inventorySize = (functionalItems.size()>=54) ? 54 : functionalItems.size()+(9-functionalItems.size()%9)*Math.min(1, functionalItems.size()%9);

        inventory = Bukkit.createInventory(player, inventorySize, name);

        for(int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, fillerItem);
        }
        for(int i = 0; i < functionalItems.size(); i++) {
            inventory.setItem(i, functionalItems.get(i));
        }
    }

    public ArrayList<ItemStack> getFunctionalItems() {
        return functionalItems;
    }

    public Material getFiller() {
        return filler;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
