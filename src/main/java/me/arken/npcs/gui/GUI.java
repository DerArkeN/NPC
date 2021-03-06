package me.arken.npcs.gui;

import jline.internal.Nullable;
import me.arken.npcs.NPCs;
import me.arken.npcs.npc.NPC;
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
import java.util.List;

public abstract class GUI implements Listener {

    private Inventory inventory;
    private GUIManager guiManager = null;
    private final String name;
    private final NPC npc;

    public GUI(NPC npc) {
        this.name = this.getClass().getSimpleName().replaceAll("GUI", "");
        this.npc = npc;

        //Scheduler to load GUIManager first
        Bukkit.getScheduler().runTaskLater(NPCs.getPlugin(), () -> {
            this.guiManager = npc.getGuiManager();

            ItemStack fillerItem = new ItemStack(getFiller());
            ItemMeta fillerItemMeta = fillerItem.getItemMeta();
            fillerItemMeta.setDisplayName("");
            fillerItem.setItemMeta(fillerItemMeta);

            if(!guiManager.getGUIS().contains(this)) guiManager.addGUI(this);
            addContents(fillerItem);

        }, 20);

        Bukkit.getPluginManager().registerEvents(this, NPCs.getPlugin());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getClickedInventory() != null && event.getClickedInventory().getContents() != null) {
            if(Arrays.equals(event.getClickedInventory().getContents(), this.getInventory().getContents())) {
                if(event.getCurrentItem() != null) {
                    if(event.getCurrentItem().getItemMeta().getDisplayName().equals("Settings")) {
                        event.getWhoClicked().openInventory(guiManager.getGUI("Settings").getInventory());
                    }else {
                        handle((Player) event.getWhoClicked(), event.getCurrentItem(), npc);
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    public abstract List<ItemStack> getFunctionalItems();

    protected abstract void handle(Player player, ItemStack currentItem, NPC npc);

    @Nullable
    public String getParentGUI() {
        return "Settings";
    }

    public String getName() {
        return this.name;
    }

    public Material getFiller() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    protected final ItemStack createFunctionalItem(String name, Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private void addContents(ItemStack fillerItem) {
        ArrayList<ItemStack> functionalItems = new ArrayList<>(getFunctionalItems());
        for(GUI gui : guiManager.getGUIS()) {
            if(gui.getParentGUI() != null) {
                if(gui.getParentGUI().equals(getName())) {
                    ItemStack functionalItem = createFunctionalItem(gui.getName(), gui.getFunctionalItems().get(0).getType());
                    functionalItems.add(functionalItem);
                }
            }
        }

        int inventorySize = (functionalItems.size()>=54) ? 54 : functionalItems.size()+(9-functionalItems.size()%9)*Math.min(1, functionalItems.size()%9);

        inventory = Bukkit.createInventory(() -> null, inventorySize, getName());

        for(int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, fillerItem);
        }

        for(int i = 0; i < functionalItems.size(); i++) {
            inventory.setItem(i, functionalItems.get(i));
        }

        ItemStack back = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta backMeta = (SkullMeta) back.getItemMeta();
        backMeta.setOwner("MHF_ArrowLeft");
        backMeta.setDisplayName("Settings");
        back.setItemMeta(backMeta);

        inventory.setItem(inventory.getSize()-1, back);
    }

}
