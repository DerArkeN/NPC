package me.arken.npcs;

import me.arken.npcs.commands.PluginCommand;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class NPCs extends JavaPlugin {

    private static final Set<ServerPlayer> npcs = new HashSet<>();
    private static final String prefix = "§7[§aNPCs§7]§r ";

    @Override
    public void onEnable() {
        registerEvents("me.arken.npcs.listeners");
        registerCommands("me.arken.npcs.commands");
    }

    public static Set<ServerPlayer> getNpcs() {
        return npcs;
    }

    public static String getPrefix() {
        return prefix;
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

                Objects.requireNonNull(getCommand(pluginCommand.getCommand().command())).setExecutor(pluginCommand);
            }catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    public static JavaPlugin getPlugin() {
        return getProvidingPlugin(NPCs.class);
    }

}
