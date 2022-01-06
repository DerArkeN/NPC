package me.arken.npcs.listeners;

import me.arken.npcs.NPCs;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ListenerMove implements Listener {

    //@EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        NPCs.getNpcs().stream().forEach(npc -> {

            Location location = npc.getBukkitEntity().getLocation();
            location.setDirection(player.getLocation().subtract(location).toVector());

            float yaw = location.getYaw();
            float pitch = location.getPitch();

            byte calcYaw = (byte) ((yaw%360)*256/360);
            byte calcPitch = (byte) ((pitch%360)*256/360);

            ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;

            connection.send(new ClientboundRotateHeadPacket(npc, calcYaw));
            connection.send(new ClientboundMoveEntityPacket.Rot(npc.getId(), calcYaw, calcPitch, false));
        });
    }

}
