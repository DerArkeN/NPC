package me.arken.npcs.listeners;

import com.comphenix.protocol.wrappers.EnumWrappers;
import me.arken.npcs.NPCs;
import me.arken.npcs.commands.CommandNPC;
import me.arken.npcs.gui.GUIManager;
import me.arken.npcs.npc.NPC;
import me.arken.npcs.npc.NPCInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ListenerInteractNPC implements Listener {

    private static NPC currentNPC;

    @EventHandler
    public void onInteract(NPCInteractEvent event) {
        Player player = event.getPlayer();
        currentNPC = event.getClicked();

        GUIManager guiManager = NPCs.getGuiManager();

        if(event.getUseAction() == EnumWrappers.EntityUseAction.INTERACT) {
            if(player.getInventory().getItemInOffHand().equals(CommandNPC.getEditor()) || player.getInventory().getItemInMainHand().equals(CommandNPC.getEditor())) {
                player.openInventory(guiManager.getGUI("Settings").getInventory());
            }
        }
    }

    public static NPC getCurrentNPC() {
        return currentNPC;
    }
}
