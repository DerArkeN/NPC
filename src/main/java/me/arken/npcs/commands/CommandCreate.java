package me.arken.npcs.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import me.arken.npcs.NPCs;
import me.arken.npcs.util.ExecuteCommand;
import me.arken.npcs.util.NPC;
import me.arken.npcs.util.PluginCommand;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

@ExecuteCommand(command = "createnpc", permission = "op")
public class CommandCreate extends PluginCommand {

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player player) {
            NPC npc = new NPC(player, player.getDisplayName());

            npc.show(player);
            npc.setMainHand(player.getInventory().getItemInMainHand());

            new BukkitRunnable() {
                @Override
                public void run() {
                    npc.setSkin(player);
                    player.sendMessage("Skin set");
                }
            }.runTaskLater(NPCs.getProvidingPlugin(NPCs.class), 5*20);
        }
    }

    private void backup(CommandSender sender) {
        if(sender instanceof Player player) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            ServerPlayer serverPlayer = craftPlayer.getHandle();

            MinecraftServer minecraftServer = serverPlayer.getServer();
            ServerLevel serverLevel = serverPlayer.getLevel();
            GameProfile gameProfile =  new GameProfile(UUID.randomUUID(), player.getDisplayName());

            ServerPlayer npc = new ServerPlayer(minecraftServer, serverLevel, gameProfile);
            npc.setPos(serverPlayer.getPosition(0.0f));

            ServerGamePacketListenerImpl connection = serverPlayer.connection;

            Property property = (Property) serverPlayer.getGameProfile().getProperties().get("textures").toArray()[0];
            String texture = property.getValue();
            String signature = property.getSignature();

            //Create NPC
            connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
            connection.send(new ClientboundAddPlayerPacket(npc));

            //Copy skin inner lines
            npc.getGameProfile().getProperties().put("textures", new Property("textures", texture, signature));

            //Copy skin outer lines
            SynchedEntityData entityData = npc.getEntityData();
            entityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) Integer.parseInt("1111110", 2)); //Bit mask -> No cape

            //Copy held items
            connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.MAINHAND, serverPlayer.getMainHandItem()))));
            connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.OFFHAND, serverPlayer.getOffhandItem()))));

            //Copy armor
            connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.HEAD, serverPlayer.getInventory().getArmor(3)))));
            connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.CHEST, serverPlayer.getInventory().getArmor(2)))));
            connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.LEGS, serverPlayer.getInventory().getArmor(1)))));
            connection.send(new ClientboundSetEquipmentPacket(npc.getId(), List.of(Pair.of(EquipmentSlot.FEET, serverPlayer.getInventory().getArmor(0)))));

            //Update NPC
            connection.send(new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true));

            NPCs.getNpcs().add(npc);
        }
    }
}
