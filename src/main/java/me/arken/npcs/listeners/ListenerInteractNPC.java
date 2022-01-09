package me.arken.npcs.listeners;

import me.arken.npcs.npc.NPCInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ListenerInteractNPC implements Listener {

    @EventHandler
    public void onInteract(NPCInteractEvent event) {
        event.getPlayer().sendMessage("yeet");
    }
}
