package me.fenix.module;

import me.fenix.module.settings.Setting;
import me.fenix.module.settings.SettingsBuilder;
import me.fenix.scheduler.Scheduler;
import me.fenix.util.StringUtil;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class Module {

    protected final SettingsBuilder settingsBuilder = new SettingsBuilder();
    protected final Minecraft mc = Minecraft.getMinecraft();
    private final Map<Class<?>, Method> packetHandlerMethods = new HashMap<>();
    protected Scheduler scheduler;

    private String name;
    private boolean enabled;
    private int keyCode;

    public void toggle() {
        this.enabled = !this.enabled;
        this.mc.ingameGUI.displayTitle(StringUtil.fixColors("&7[ &3Modules &7]"), null, 10, 10, 10);
        this.mc.ingameGUI.displayTitle(null, StringUtil.fixColors("&7Module &9" + this.name + " &7was " + (this.enabled ? "&aenabled" : "&cdisabled")), 10, 10, 10);

        if (this.enabled)
            onEnable();
        else
            onDisable();
    }

    protected void onEnable() {
    }

    protected void onDisable() {
        this.scheduler.shutdownTasks();
    }

    public void onUpdate() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public Map<Class<?>, Method> getPacketHandlerMethods() {
        return packetHandlerMethods;
    }

    public Optional<Method> getMethod(Class<?> clazz) {
        return Optional.ofNullable(packetHandlerMethods.get(clazz));
    }

    public List<Setting<?>> getSettings() {
        return settingsBuilder.getSettings();
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
