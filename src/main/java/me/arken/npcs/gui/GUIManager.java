package me.arken.npcs.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GUIManager {

    public static GUI getMainGUI(Player player, String name) {
        ArrayList<ItemStack> contents = new ArrayList<>();
        contents.add(createFunctionalItem( "Set Name", Material.NAME_TAG));
        contents.add(createFunctionalItem("Set Skin", Material.PLAYER_HEAD));

        return new GUI(player, name, contents, Material.GRAY_STAINED_GLASS_PANE);
    }

    public static GUI getNameGUI(Player player) {
        ArrayList<ItemStack> contents = new ArrayList<>();
        contents.add(createFunctionalItem("Set Name", Material.ANVIL));
        contents.add(createFunctionalItem("Reset", Material.BARRIER));

        return new GUI(player, "Set Name", contents, Material.GRAY_STAINED_GLASS_PANE);
    }

    public static GUI getSkinGUI(Player player) {
        ArrayList<ItemStack> contents = new ArrayList<>();
        contents.add(createFunctionalItem("Set Skin", Material.ANVIL));
        contents.add(createFunctionalItem("Reset", Material.BARRIER));

        return new GUI(player, "Set Skin", contents, Material.GRAY_STAINED_GLASS_PANE);
    }

    private static ItemStack createFunctionalItem(String name, Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
