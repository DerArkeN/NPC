package me.arken.npcs.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import net.minecraft.server.level.*;
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
import java.util.concurrent.TimeUnit;

public class NPC {

    private final ServerPlayer serverNPC;
    private final Player player;

    private final Cache<Player, ServerPlayer> npcCache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.SECONDS).build();

    private boolean isSkinSet = false;
    private boolean showOnTab = false;
    private ItemStack mainHandItem = null;
    private ItemStack offHandItem = null;
    private ItemStack[] armorContents = null;

    public NPC(Player player, String name) {
        this.player = player;

        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel serverLevel = ((CraftWorld) player.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);

        serverNPC = new ServerPlayer(minecraftServer, serverLevel, gameProfile);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(NPCs.getPlugin(), PacketType.fromClass(ServerboundInteractPacket.class)) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                int entityId = event.getPacket().getIntegers().read(0);

                //EnumWrappers.EntityUseAction entityUseAction = event.getPacket().getEntityUseActions().read(0);
                handleInteractEvent(event.getPlayer(), entityId);
            }
        });

        NPCs.getNpcs().add(serverNPC);
    }

    private void handleInteractEvent(Player player, int entityId) {
        NPCs.getNpcs().stream().filter(npc -> npc.getId() == entityId)
                .forEach(npc -> Bukkit.getScheduler().runTaskLater(NPCs.getPlugin(), () -> {
                    ServerPlayer clicked = npcCache.getIfPresent(player);
                    if(clicked != null && clicked.equals(serverNPC)) return;
                    npcCache.put(player, serverNPC);

                    NPCInteractEvent npcInteractionEvent = new NPCInteractEvent(getNPC(), player, null);
                    Bukkit.getPluginManager().callEvent(npcInteractionEvent);
                }, 2));
    }

    public void setLocation(Location location) {
        serverNPC.setPos(location.getX(), location.getY(), location.getZ());

        update();
    }

    public void update() {
        hide();
        show();
    }

    public void show() {
        ServerPlayer serverPlayer = toServerPlayer(player);

        serverPlayer.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, serverNPC));
        serverPlayer.connection.send(new ClientboundAddPlayerPacket(serverNPC));
        serverPlayer.connection.send(new ClientboundRotateHeadPacket(serverNPC, (byte) ((serverNPC.getBukkitEntity().getLocation().getYaw()%360)*256/360)));
        if(isSkinSet) serverPlayer.connection.send(new ClientboundSetEntityDataPacket(serverNPC.getId(), serverNPC.getEntityData(), true));
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
