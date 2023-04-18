package me.fenix.module.settings;

import java.util.ArrayList;
import java.util.List;

public class SettingsBuilder {

    private final List<Setting<?>> settings = new ArrayList<>();

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public <T> Setting<T> createSetting(Setting<T> setting) {
        this.settings.add(setting);
        return setting;
    }
}
