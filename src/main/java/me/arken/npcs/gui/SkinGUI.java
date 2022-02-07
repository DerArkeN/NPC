package me.arken.npcs.gui;

import me.arken.npcs.NPCs;
import me.arken.npcs.npc.NPC;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SkinGUI extends GUI {

    public SkinGUI(NPC npc) {
        super(npc);
    }

    @Override
    public List<ItemStack> getFunctionalItems() {
        return List.of(createFunctionalItem("Set Skin", Material.PLAYER_HEAD));
    }

    @Override
    protected void handle(Player player, ItemStack currentItem, NPC npc) {
        switch(currentItem.getItemMeta().getDisplayName()) {
            case "Set Skin" -> {
                new AnvilGUI.Builder()
                        .onComplete((target, text) -> {
                            npc.setSkin(text);

                            target.sendMessage(NPCs.getPrefix() + "§aSkin set to " + text);
                            return AnvilGUI.Response.close();
                        })
                        .text("")
                        .itemLeft(new ItemStack(Material.PAPER))
                        .title("Enter username")
                        .plugin(NPCs.getPlugin())
                        .open(player);
            }
            case "Layer" -> {
                player.openInventory(npc.getGuiManager().getGUI("Layer").getInventory());
            }
        }
    }

    static class LayerGUI extends GUI {

        public LayerGUI(NPC npc) {
            super(npc);
        }

        @Override
        public List<ItemStack> getFunctionalItems() {
            return List.of(
                    createFunctionalItem("Cape", Material.GREEN_STAINED_GLASS_PANE),
                    createFunctionalItem("Jacket", Material.GREEN_STAINED_GLASS_PANE),
                    createFunctionalItem("Left Sleeve", Material.GREEN_STAINED_GLASS_PANE),
                    createFunctionalItem("Right Sleeve", Material.GREEN_STAINED_GLASS_PANE),
                    createFunctionalItem("Left Pants", Material.GREEN_STAINED_GLASS_PANE),
                    createFunctionalItem("Right Pants", Material.GREEN_STAINED_GLASS_PANE),
                    createFunctionalItem("Hat", Material.GREEN_STAINED_GLASS_PANE)
                    );
        }

        @Override
        public String getParentGUI() {
            return "Skin";
        }

        @Override
        protected void handle(Player player, ItemStack currentItem, NPC npc) {
            String itemName = currentItem.getItemMeta().getDisplayName();
            int index = getIndex(itemName);

            if(npc.isSkinSet()) {
                if(currentItem.getType().equals(Material.GREEN_STAINED_GLASS_PANE)) {
                    npc.updateSkinMask(index, '0');
                    currentItem.setType(Material.RED_STAINED_GLASS_PANE);
                    player.sendMessage(NPCs.getPrefix() + "§a" + itemName + " disabled!");
                }else {
                    npc.updateSkinMask(index, '1');
                    currentItem.setType(Material.GREEN_STAINED_GLASS_PANE);
                    player.sendMessage(NPCs.getPrefix() + "§a" + itemName + " enabled!");
                }
            }
        }

        private int getIndex(String itemName) {
            switch(itemName) {
                case "Cape" -> {
                    return 6;
                }
                case "Jacket" -> {
                    return 5;
                }
                case "Left Sleeve" -> {
                    return 4;
                }
                case "Right Sleeve" -> {
                    return 3;
                }
                case "Left Pants" -> {
                    return 2;
                }
                case "Right Pants" -> {
                    return 1;
                }
                case "Hat" -> {
                    return 0;
                }
            }
            return -1;
        }

    }
}
