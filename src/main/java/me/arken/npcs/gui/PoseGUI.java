package me.arken.npcs.gui;

import me.arken.npcs.npc.NPC;
import net.minecraft.world.entity.Pose;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PoseGUI extends GUI {

    public PoseGUI(NPC npc) {
        super(npc);
    }

    @Override
    public List<ItemStack> getFunctionalItems() {
        List<ItemStack> contents = new ArrayList<>();
        for(Pose pose : Pose.values()) {
            ItemStack poseItem = createFunctionalItem(pose.name(), Material.ARMOR_STAND);
            contents.add(poseItem);
        }
        return contents;
    }

    @Override
    protected void handle(Player player, ItemStack currentItem, NPC npc) {
        String itemName = currentItem.getItemMeta().getDisplayName();

        npc.setPose(Pose.valueOf(itemName));
    }
}
