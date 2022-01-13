package me.arken.npcs.listeners;

import me.arken.npcs.NPCs;
import me.arken.npcs.commands.CommandNPC;
import me.arken.npcs.gui.GUI;
import me.arken.npcs.gui.GUIManager;
import me.arken.npcs.npc.NPC;
import me.arken.npcs.npc.NPCInteractEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.ServerPlayer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ListenerInteractNPC implements Listener {

    private GUI mainGUI;
    private NPC npc;

    @EventHandler
    public void onInteract(NPCInteractEvent event) {
        Player player = event.getPlayer();
        ServerPlayer npc = event.getClicked().getServerNPC();

        this.mainGUI = GUIManager.getMainGUI(player, npc.getScoreboardName());
        this.npc = event.getClicked();

        if(player.getInventory().getItemInOffHand().equals(CommandNPC.getEditor()) || player.getInventory().getItemInMainHand().equals(CommandNPC.getEditor())) {
            player.openInventory(mainGUI.getInventory());
        }
    }

    @EventHandler
    public void onMainInventory(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        //Main GUI
        if(Objects.equals(event.getClickedInventory(), mainGUI.getInventory())) {
            switch(clicked.getItemMeta().getDisplayName()) {
                case "Set Name" -> player.openInventory(GUIManager.getNameGUI(player).getInventory());
                case "Set Skin" -> player.openInventory(GUIManager.getSkinGUI(player).getInventory());
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onNameInventory(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        //Name GUI
        if(Objects.equals(event.getClickedInventory(), nameGUI.getInventory())) {
            player.sendMessage("a");
            switch(clicked.getItemMeta().getDisplayName()) {
                case "Set Name" -> {
                    player.sendMessage("name set");
                    new AnvilGUI.Builder()
                            .onClose(consumer -> consumer.sendMessage(ChatColor.RED + "No name was set."))
                            .onComplete((consumer, text) -> {
                                npc.getServerNPC().setCustomName(Component.nullToEmpty("NPC"));
                                consumer.sendMessage(ChatColor.GREEN + "Skin was set to: " + text);
                                return AnvilGUI.Response.close();
                            })
                            .preventClose()
                            .text("Name")
                            .title("Set Name")
                            .plugin(NPCs.getPlugin())
                            .open(player);
                }
                case "Reset" -> {
                    npc.getServerNPC().setCustomName(Component.nullToEmpty("NPC"));
                }
            }
            event.setCancelled(true);
        }
    }


}
