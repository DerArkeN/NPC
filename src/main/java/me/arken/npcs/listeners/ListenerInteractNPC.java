package me.arken.npcs.listeners;

import me.arken.npcs.commands.CommandNPC;
import me.arken.npcs.npc.GUI;
import me.arken.npcs.npc.NPCInteractEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

public class ListenerInteractNPC implements Listener {

    private Inventory gui;

    @EventHandler
    public void onInteract(NPCInteractEvent event) {
        Player player = event.getPlayer();

        if(player.getInventory().getItemInOffHand().equals(CommandNPC.getEditor()) || player.getInventory().getItemInMainHand().equals(CommandNPC.getEditor())) {
            ItemStack rename = new ItemStack(Material.NAME_TAG);
            ItemStack remove = new ItemStack(Material.BARRIER);
            ArrayList<ItemStack> contents = new ArrayList<>();
            contents.add(rename);
            contents.add(remove);
            contents.add(rename);
            contents.add(remove);
            contents.add(rename);
            gui = new GUI(player, event.getClicked().getServerNPC().getScoreboardName(), contents, new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                    .getInventory();
            player.openInventory(gui);
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if(Objects.equals(event.getClickedInventory(), gui)) {
            event.setCancelled(true);
        }
    }
}
