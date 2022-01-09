package me.arken.npcs.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final NPC clicked;
    private final Player player;
    private final EnumWrappers.EntityUseAction useAction;

    public NPCInteractEvent(NPC clicked, Player player, EnumWrappers.EntityUseAction useAction) {
        this.clicked = clicked;
        this.player = player;
        this.useAction = useAction;
    }

    public NPC getClicked() {
        return clicked;
    }

    public Player getPlayer() {
        return player;
    }

    public EnumWrappers.EntityUseAction getUseAction() {
        return useAction;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
