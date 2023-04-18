package me.fenix;

import me.fenix.gui.ModuleInfoGui;
import me.fenix.module.ModuleManager;
import me.fenix.module.settings.SettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "AutoFish", version = "1.0")
public class CarbonMod {

    private static CarbonMod instance;

    private ModuleManager moduleManager;
    private ModuleInfoGui moduleInfoGui;

    public static CarbonMod getInstance() {
        return instance;
    }

    public static void setInstance(CarbonMod instance) {
        CarbonMod.instance = instance;
    }

    public void setupCarbonMod() {
        this.moduleManager = new ModuleManager(new SettingsManager());
        this.moduleInfoGui = new ModuleInfoGui(Minecraft.getMinecraft());

        this.moduleManager.onEnable();
    }

    public ModuleInfoGui getModuleInfoGui() {
        return moduleInfoGui;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

}
