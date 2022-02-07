package me.arken.npcs.gui;

import me.arken.npcs.npc.NPC;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class GUIManager {

    private final Set<GUI> GUIS = new HashSet<>();

    public GUIManager(NPC npc) {
        Set<Class<? extends GUI>> classes = new Reflections("me.arken.npcs.gui").getSubTypesOf(GUI.class);

        classes.forEach(clazz -> {
            try {
                GUI gui = clazz.getDeclaredConstructor(NPC.class).newInstance(npc);
                GUIS.add(gui);
            }catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    public void addGUI(GUI gui) {
        try {
            gui = gui.getClass().getDeclaredConstructor().newInstance();
            GUIS.add(gui);
        }catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public GUI getGUI(String name) {
        for(GUI gui : GUIS) {
            if(gui.getName().equals(name)) {
                return gui;
            }
        }
        return null;
    }

    public Set<GUI> getGUIS() {
        return GUIS;
    }
}
