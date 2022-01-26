package me.arken.npcs.gui;

import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;

public class GUIManager {

    private final ArrayList<GUI> GUIS = new ArrayList<>();

    public GUIManager() {
        Set<Class<? extends GUI>> classes = new Reflections("me.arken.npcs.gui").getSubTypesOf(GUI.class);

        classes.forEach(clazz -> {
            try {
                GUI gui = clazz.getDeclaredConstructor().newInstance();

                GUIS.add(gui);
            }catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    public GUI getGUI(String name) {
        for(GUI gui : GUIS) {
            if(gui.getName().equals(name)) {
                return gui;
            }
        }
        return null;
    }

    public ArrayList<GUI> getGUIS() {
        return GUIS;
    }
}
