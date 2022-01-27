package me.arken.npcs.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import jline.internal.Nullable;
import me.arken.npcs.NPCs;
import net.minecraft.network.chat.Component;
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
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {

    private final ServerPlayer serverNPC;
    private final Player player;

    private boolean isSkinSet = false;
    private boolean showOnTab = false;
    private ItemStack mainHandItem = null;
    private ItemStack offHandItem = null;
    private ItemStack[] armorContents = null;
    private String name = "NPC";
    private String bitmask;

    public NPC(Player player) {
        this.player = player;

        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel serverLevel = ((CraftWorld) player.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);

        serverNPC = new ServerPlayer(minecraftServer, serverLevel, gameProfile);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(NPCs.getPlugin(), PacketType.fromClass(ServerboundInteractPacket.class)) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                EnumWrappers.EntityUseAction entityUseAction = event.getPacket().getEnumEntityUseActions().read(0).getAction();

                if(entityUseAction.equals(EnumWrappers.EntityUseAction.INTERACT_AT) || entityUseAction.equals(EnumWrappers.EntityUseAction.INTERACT)) {
                    EnumWrappers.Hand hand = event.getPacket().getEnumEntityUseActions().read(0).getHand();
                    if(hand.equals(EnumWrappers.Hand.MAIN_HAND)) {
                        NPCInteractEvent npcInteractionEvent = new NPCInteractEvent(getNPC(), player, entityUseAction);
                        Bukkit.getScheduler().runTask(NPCs.getPlugin(), () -> Bukkit.getPluginManager().callEvent(npcInteractionEvent));
                    }
                }
            }
        });

        NPCs.getNpcs().add(serverNPC);
    }

    public void setLocation(Location location) {
        serverNPC.setPos(location.getX(), location.getY(), location.getZ());

        update();
    }

    public void update() {
        hide();
        show();
    }

    public void show(){
        ServerPlayer serverPlayer = toServerPlayer(player);

        serverPlayer.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, serverNPC));
        serverPlayer.connection.send(new ClientboundAddPlayerPacket(serverNPC));
        serverPlayer.connection.send(new ClientboundRotateHeadPacket(serverNPC, (byte) ((serverNPC.getBukkitEntity().getLocation().getYaw()%360)*256/360)));
        if(!name.equals("NPC")) {
            serverPlayer.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_DISPLAY_NAME, serverNPC));
        }
        if(isSkinSet) serverPlayer.connection.send(new ClientboundSetEntityDataPacket(serverNPC.getId(), serverNPC.getEntityData(), false));
        if(!showOnTab) Bukkit.getScheduler().runTaskLaterAsynchronously(NPCs.getPlugin(), () -> serverPlayer.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.
                Action.REMOVE_PLAYER, serverNPC)), 20);
        if(mainHandItem != null) serverPlayer.connection.send(new ClientboundSetEquipmentPacket(serverNPC.getId(), List.of(Pair.of(EquipmentSlot.MAINHAND,
                CraftItemStack.asNMSCopy(mainHandItem)))));
        if(offHandItem != null) serverPlayer.connection.send(new ClientboundSetEquipmentPacket(serverNPC.getId(), List.of(Pair.of(EquipmentSlot.MAINHAND,
                CraftItemStack.asNMSCopy(offHandItem)))));
        if(armorContents != null) {
            serverPlayer.connection.send(new ClientboundSetEquipmentPacket(serverNPC.getId(), List.of(Pair.of(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(armorContents[3])))));
            serverPlayer.connection.send(new ClientboundSetEquipmentPacket(serverNPC.getId(), List.of(Pair.of(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(armorContents[2])))));
            serverPlayer.connection.send(new ClientboundSetEquipmentPacket(serverNPC.getId(), List.of(Pair.of(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(armorContents[1])))));
            serverPlayer.connection.send(new ClientboundSetEquipmentPacket(serverNPC.getId(), List.of(Pair.of(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(armorContents[0])))));
        }
    }

    public void hide() {
        ServerPlayer serverPlayer = toServerPlayer(player);

        serverPlayer.connection.send(new ClientboundRemoveEntitiesPacket(serverNPC.getId()));
    }

    public boolean isSkinSet() {
        return isSkinSet;
    }

    //https://wiki.vg/Protocol#Client_Settings for bitmask
    public void setSkin(String username) {
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

                serverNPC.getGameProfile().getProperties().get("textures").clear();
                serverNPC.getGameProfile().getProperties().put("textures", new Property("textures", skin, signature));
            }

            else {
                Bukkit.getConsoleSender().sendMessage("Connection could not be opened when fetching player skin (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        SynchedEntityData entityData = serverNPC.getEntityData();
        bitmask = bitmask != null ? bitmask : "1111111";

        entityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 0);
        update();
        entityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) Integer.parseInt(bitmask, 2));

        isSkinSet = true;
        update();
    }

    public String getBitmask() {
        return bitmask;
    }

    public void updateSkinMask(int index, char var0) {
        SynchedEntityData entityData = serverNPC.getEntityData();

        StringBuilder stringBuilder = new StringBuilder(bitmask);
        stringBuilder.setCharAt(index, var0);

        bitmask = stringBuilder.toString();

        entityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 0);
        update();
        entityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) Integer.parseInt(bitmask, 2));

        update();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        try {
            Field nameField = GameProfile.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(serverNPC.getGameProfile(), name);
        }catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
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

    public ServerPlayer getServerNPC() {
        return serverNPC;
    }

    public NPC getNPC() {
        return this;
    }

    public UUID getUUID() {
        return serverNPC.getUUID();
    }

    private ServerPlayer toServerPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public boolean isShowOnTab() {
        return showOnTab;
    }

    public void setShowOnTab(boolean showOnTab) {
        this.showOnTab = showOnTab;
        update();
    }
}
