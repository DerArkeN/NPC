package me.arken.npcs.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import me.arken.npcs.NPCs;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class NPC {

    private ServerPlayer npc;
    private Player player;
    private String name;

    public NPC(Player player, String name) {
        ServerPlayer serverPlayer = toServerPlayer(player);

        MinecraftServer minecraftServer = serverPlayer.getServer();
        ServerLevel serverLevel = serverPlayer.getLevel();
        GameProfile gameProfile =  new GameProfile(UUID.randomUUID(), player.getDisplayName());

        npc = new ServerPlayer(minecraftServer, serverLevel, gameProfile);
        npc.setPos(toServerPlayer(player).getPosition(0.0f));

        NPCs.getNpcs().add(npc);
    }

    public void show(Collection<? extends Player> players) {
        players.forEach(player -> {
            Bukkit.getServer().getOnlinePlayers();
            ServerPlayer serverPlayer = toServerPlayer(player);

            serverPlayer.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
            serverPlayer.connection.send(new ClientboundAddPlayerPacket(npc));
            serverPlayer.connection.send(new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true));
        });
    }

    public void show(Player player) {
        Bukkit.getServer().getOnlinePlayers();
        ServerPlayer serverPlayer = toServerPlayer(player);

        serverPlayer.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
        serverPlayer.connection.send(new ClientboundAddPlayerPacket(npc));
        serverPlayer.connection.send(new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true));
    }

    public void hide(Collection<? extends Player> players) {
        players.forEach(player -> {
            Bukkit.getServer().getOnlinePlayers();
            ServerPlayer serverPlayer = toServerPlayer(player);

            serverPlayer.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc));
            serverPlayer.connection.send(new ClientboundRemoveEntitiesPacket(npc.getId()));
        });
    }

    public void hide(Player player) {
        Bukkit.getServer().getOnlinePlayers();
        ServerPlayer serverPlayer = toServerPlayer(player);

        serverPlayer.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc));
        serverPlayer.connection.send(new ClientboundRemoveEntitiesPacket(npc.getId()));
    }

    public void setSkin(Player skin, String bitmask) {
        ServerPlayer serverPlayer = toServerPlayer(skin);
        Property property = (Property) serverPlayer.getGameProfile().getProperties().get("textures").toArray()[0];
        String texture = property.getValue();
        String signature = property.getSignature();

        npc.getGameProfile().getProperties().put("textures", new Property("textures", texture, signature));
        SynchedEntityData entityData = npc.getEntityData();
        entityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) Integer.parseInt(bitmask, 2));

        Bukkit.getOnlinePlayers().forEach(player -> {
            ServerPlayer serverPlayer1 = toServerPlayer(player);

            serverPlayer1.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
            serverPlayer1.connection.send(new ClientboundAddPlayerPacket(npc));
            serverPlayer1.connection.send(new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true));
        });
    }

    public void setSkin(Player skin) {
        ServerPlayer serverPlayer = toServerPlayer(skin);
        Property property = (Property) serverPlayer.getGameProfile().getProperties().get("textures").toArray()[0];
        String texture = property.getValue();
        String signature = property.getSignature();

        npc.getGameProfile().getProperties().put("textures", new Property("textures", texture, signature));
        SynchedEntityData entityData = npc.getEntityData();
        entityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) Integer.parseInt("1111111", 2));

        Bukkit.getOnlinePlayers().forEach(player -> {
            ServerPlayer serverPlayer1 = toServerPlayer(player);

            serverPlayer1.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
            serverPlayer1.connection.send(new ClientboundAddPlayerPacket(npc));
            serverPlayer1.connection.send(new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true));
        });
    }

    public void setMainHand(ItemStack itemStack) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            ServerPlayer serverPlayer = toServerPlayer(player);

            serverPlayer.connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(itemStack)))));
        });
    }

    public void setOffHand(ItemStack itemStack) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            ServerPlayer serverPlayer = toServerPlayer(player);

            serverPlayer.connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(itemStack)))));
        });
    }

    public ServerPlayer getNpc() {
        return npc;
    }

    private ServerPlayer toServerPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }
}
