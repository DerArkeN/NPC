package me.arken.npcs.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import jline.internal.Nullable;
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
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {

    private ServerPlayer npc;
    private Player player;

    private boolean isSkinSet = false;
    private ItemStack mainHandItem = null;
    private ItemStack offHandItem = null;
    private ItemStack[] armorContents = null;

    public NPC(Player player, String name) {
        this.player = player;

        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel serverLevel = ((CraftWorld) player.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);

        npc = new ServerPlayer(minecraftServer, serverLevel, gameProfile);

        NPCs.getNpcs().add(npc);
    }

    public void setLocation(Location location) {
        npc.setPos(location.getX(), location.getY(), location.getZ());
    }

    public void update() {
        hide();
        show();
    }

    public void show() {
        ServerPlayer serverPlayer = toServerPlayer(player);

        serverPlayer.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
        serverPlayer.connection.send(new ClientboundAddPlayerPacket(npc));
        serverPlayer.connection.send(new ClientboundRotateHeadPacket(npc, (byte) ((npc.getBukkitEntity().getLocation().getYaw()%360)*256/360)));
        if(isSkinSet) serverPlayer.connection.send(new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true));
        if(mainHandItem != null) serverPlayer.connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.MAINHAND,
                CraftItemStack.asNMSCopy(mainHandItem)))));
        if(offHandItem != null) serverPlayer.connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.MAINHAND,
                CraftItemStack.asNMSCopy(offHandItem)))));
        if(armorContents != null) {
            serverPlayer.connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(armorContents[3])))));
            serverPlayer.connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(armorContents[2])))));
            serverPlayer.connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(armorContents[1])))));
            serverPlayer.connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(armorContents[0])))));
        }
    }

    public void hide() {
        ServerPlayer serverPlayer = toServerPlayer(player);

        serverPlayer.connection.send(new ClientboundRemoveEntitiesPacket(npc.getId()));
    }

    public boolean isSkinSet() {
        return isSkinSet;
    }

    //https://wiki.vg/Protocol#Client_Settings for bitmask
    public void setSkin(String username, @Nullable String bitmask) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://api.ashcon.app/mojang/v2/user/%s", username)).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                ArrayList<String> lines = new ArrayList<>();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                reader.lines().forEach(lines::add);

                String reply = String.join(" ",lines);
                int indexOfValue = reply.indexOf("\"value\": \"");
                int indexOfSignature = reply.indexOf("\"signature\": \"");
                String skin = reply.substring(indexOfValue + 10, reply.indexOf("\"", indexOfValue + 10));
                String signature = reply.substring(indexOfSignature + 14, reply.indexOf("\"", indexOfSignature + 14));

                npc.getGameProfile().getProperties().put("textures", new Property("textures", skin, signature));
            }

            else {
                Bukkit.getConsoleSender().sendMessage("Connection could not be opened when fetching player skin (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        SynchedEntityData entityData = npc.getEntityData();
        bitmask = bitmask != null ? bitmask : "1111111";
        entityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) Integer.parseInt(bitmask, 2));

        isSkinSet = true;
        update();
    }

    public ItemStack getMainHandItem() {
        return mainHandItem;
    }

    public void setMainHandItem(ItemStack mainHandItem) {
        this.mainHandItem = mainHandItem;
        update();
    }

    public ItemStack getOffHandItem() {
        return offHandItem;
    }

    public void setOffHandItem(ItemStack offHandItem) {
        this.offHandItem = offHandItem;
        update();
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

    public void setArmorContents(ItemStack[] armorContents) {
        this.armorContents = armorContents;
        update();
    }

    public ServerPlayer getNpc() {
        return npc;
    }

    private ServerPlayer toServerPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }
}
