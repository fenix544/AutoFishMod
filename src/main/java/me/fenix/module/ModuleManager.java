package me.fenix.module;

import me.fenix.event.EventHandler;
import me.fenix.module.modules.FineRpgAutoFishModule;
import me.fenix.module.modules.HeavenRpgAutoFishModule;
import me.fenix.module.modules.MyRpgAutoFishModule;
import me.fenix.module.modules.PvpIqAutoFishModule;
import me.fenix.module.settings.SettingsManager;
import me.fenix.scheduler.Scheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {

    private final Logger logger = LogManager.getLogger();
    private final List<Module> modules = new ArrayList<>();
    private final SettingsManager settingsManager;

    public ModuleManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    private void registerModules() {
        register(new MyRpgAutoFishModule());
        register(new HeavenRpgAutoFishModule());
        register(new PvpIqAutoFishModule());
        register(new FineRpgAutoFishModule());
    }

    public void onEnable() {
        logger.info("[CarbonClient/ModuleManager] Registering modules...");
        this.registerModules();
        logger.info("[CarbonClient/ModuleManager] Modules registered!");
        this.settingsManager.loadModuleSettings(this);
        new EventHandler(this.modules);
    }

    public void onDisable() {
        this.settingsManager.saveModulesSettings(this);
    }

    private void register(Module module) {
        if (module == null)
            return;

        ModuleInfo annotation = module.getClass().getAnnotation(ModuleInfo.class);
        module.setName(annotation.name());
        module.setEnabled(false);
        module.setKeyCode(annotation.keyCode());
        module.setScheduler(new Scheduler(module.getName()));

        for (Method method : module.getClass().getMethods()) {
            if (method.isAnnotationPresent(PacketHandler.class)) {
                PacketHandler packetHandler = method.getAnnotation(PacketHandler.class);
                module.getPacketHandlerMethods().put(packetHandler.handle(), method);
            }
        }

        this.modules.add(module);
    }

    public Module getByName(String name) {
        return this.modules.stream()
                .filter(module -> module.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Module> getEnabledModules() {
        return this.modules.stream()
                .filter(Module::isEnabled)
                .collect(Collectors.toList());
    }

    public List<Module> getModules() {
        return this.modules;
    }
}
