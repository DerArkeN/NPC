package me.arken.npcs.gui;

import me.arken.npcs.NPCs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class GUI implements Listener {

    private Inventory inventory;
    private final String name;

    public GUI() {
        name = this.getClass().getSimpleName().replaceAll("GUI", "");

        ItemStack fillerItem = new ItemStack(getFiller());
        ItemMeta fillerItemMeta = fillerItem.getItemMeta();
        fillerItemMeta.setDisplayName("");
        fillerItem.setItemMeta(fillerItemMeta);

        //Scheduler to load GUIManager first
        Bukkit.getScheduler().runTaskLater(NPCs.getPlugin(), () -> {
            int inventorySize = (getFunctionalItems().size()>=54) ? 54 : getFunctionalItems().size()+(9-getFunctionalItems().size()%9)*Math.min(1, getFunctionalItems().size()%9);

            inventory = Bukkit.createInventory(() -> null, inventorySize, getName());

            for(int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, fillerItem);
            }
            for(int i = 0; i < getFunctionalItems().size(); i++) {
                inventory.setItem(i, getFunctionalItems().get(i));
            }

            ItemStack back = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta backMeta = (SkullMeta) back.getItemMeta();
            backMeta.setOwner("MHF_ArrowLeft");
            backMeta.setDisplayName("Settings");
            back.setItemMeta(backMeta);

            inventory.setItem(inventory.getSize()-1, back);
        }, 20);

        Bukkit.getPluginManager().registerEvents(this, NPCs.getPlugin());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getClickedInventory() != null) {
            if(Arrays.equals(event.getClickedInventory().getContents(), this.getInventory().getContents())) {
                if(event.getCurrentItem() != null) {
                    if(event.getCurrentItem().getItemMeta().getDisplayName().equals("Settings")) {
                        event.getWhoClicked().openInventory(NPCs.getGuiManager().getGUI("Settings").getInventory());
                    }else {
                        handle((Player) event.getWhoClicked(), event.getCurrentItem(), NPCs.getGuiManager());
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    public abstract ArrayList<ItemStack> getFunctionalItems();

    public String getName() {
        return name;
    }

    protected abstract void handle(Player player, ItemStack currentItem, GUIManager guiManager);

    public Material getFiller() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    public Inventory getInventory() {
        return inventory;
    }

    protected final ItemStack createFunctionalItem(String name, Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}
