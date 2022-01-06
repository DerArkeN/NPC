package me.arken.npcs;

import me.arken.npcs.util.PluginCommand;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public final class NPCs extends JavaPlugin {

    private static Set<ServerPlayer> npcs = new HashSet<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerEvents("me.arken.npcs.listeners");
        registerCommands("me.arken.npcs.commands");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Set<ServerPlayer> getNpcs() {
        return npcs;
    }

    private void registerEvents(String packageDir) {
        Set<Class<? extends Listener>> classes = new Reflections(packageDir).getSubTypesOf(Listener.class);

        classes.forEach(clazz -> {
            try {
                Listener listener = clazz.getDeclaredConstructor().newInstance();
                getServer().getPluginManager().registerEvents(listener, this);
            }catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }

    private void registerCommands(String packageDir) {
        Set<Class<? extends PluginCommand>> classes = new Reflections(packageDir).getSubTypesOf(PluginCommand.class);

        classes.forEach(clazz -> {
            try {
                PluginCommand pluginCommand = clazz.getDeclaredConstructor().newInstance();

                getCommand(pluginCommand.getCommand().command()).setExecutor(pluginCommand);
            }catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }
}
