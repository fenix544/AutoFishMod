package me.fenix.module.settings;

import com.google.gson.JsonObject;
import me.fenix.module.Module;
import me.fenix.module.ModuleManager;
import me.fenix.util.Util;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.Optional;

public class SettingsManager {

    private final Logger logger = LogManager.getLogger();
    private final File SETTINGS_FILE = new File(Minecraft.getMinecraft().mcDataDir, "Carbon/settings.json");

    public void saveModulesSettings(ModuleManager moduleManager) {
        JsonObject jsonObject = new JsonObject();

        for (Module module : moduleManager.getModules()) {
            if (module.getSettings().isEmpty()) continue;
            JsonObject moduleObject = new JsonObject();

            module.getSettings().forEach(setting -> {
                if (!setting.isSave()) return;
                if (setting.getType() == Long.class) {
                    moduleObject.addProperty(setting.getName(), (Long) setting.getValue());
                }
            });

            moduleObject.addProperty("key", Keyboard.getKeyName(module.getKeyCode()));
            jsonObject.add(module.getName(), moduleObject);
        }

        Util.writeJson(SETTINGS_FILE, jsonObject);
        logger.info("[CarbonClient/SettingManager] Settings saved in " + SETTINGS_FILE.getAbsolutePath());
    }

    @SuppressWarnings("unchecked")
    public void loadModuleSettings(ModuleManager moduleManager) {
        JsonObject jsonObject = Util.readJson(SETTINGS_FILE);
        if (jsonObject == null) {
            logger.info("[CarbonClient/SettingManager] No json found, skipping");
            return;
        }

        logger.info("[CarbonClient/SettingManager] Loading settings...");

        jsonObject.entrySet().forEach(entry -> {
            Module module = moduleManager.getByName(entry.getKey());
            if (module == null) return;

            entry.getValue().getAsJsonObject().entrySet().forEach(settingEntry -> {
                if (settingEntry.getKey().equals("key")) {
                    module.setKeyCode(Keyboard.getKeyIndex(settingEntry.getValue().getAsString()));
                    return;
                }

                Optional<Setting<?>> optionalSetting = module.getSettings()
                        .stream()
                        .filter(s -> s.getName().equalsIgnoreCase(settingEntry.getKey()))
                        .findFirst();
                if (!optionalSetting.isPresent()) return;

                Setting<?> setting = optionalSetting.get();
                if (setting.getType() == Long.class) {
                    ((Setting<Long>) setting).setValue(settingEntry.getValue().getAsLong());
                }
            });
        });
        logger.info("[CarbonClient/SettingManager] Settings loaded!");
    }
}
