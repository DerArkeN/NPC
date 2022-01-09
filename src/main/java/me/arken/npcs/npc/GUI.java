package me.arken.npcs.npc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GUI {

    private final Inventory inventory;

    private final ArrayList<ItemStack> functionalItems;
    private final ItemStack filler;

    public GUI(Player player, String name, ArrayList<ItemStack> functionalItems, ItemStack filler) {
        this.functionalItems = functionalItems;
        this.filler = filler;

        int size = functionalItems.size()*2;
        if(size <= 9) {
            size = 9;
        }else if(size <= 18){
            size = 18;
        }else if(size <= 27) {
            size = 27;
        }

        inventory = Bukkit.createInventory(player, size, name);

        for(int i=0; i < size; i++) {
            inventory.setItem(i, filler);
        }
        int x = 1;
        for(int i=0; i<functionalItems.size(); i++) {
            inventory.setItem(i+x, functionalItems.get(i));
            x++;
        }
    }

    public ArrayList<ItemStack> getFunctionalItems() {
        return functionalItems;
    }

    public ItemStack getFiller() {
        return filler;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
