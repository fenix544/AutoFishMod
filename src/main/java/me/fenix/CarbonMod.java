package me.fenix;

import me.fenix.gui.ModuleInfoGui;
import me.fenix.module.ModuleManager;
import me.fenix.module.settings.SettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;

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

    public void setUsername(String username) {
        try {
            Field sessionField = Minecraft.class.getDeclaredField("session");
            sessionField.setAccessible(true);
            Session o = (Session) sessionField.get(Minecraft.getMinecraft());

            Field usernameField = o.getClass().getDeclaredField("username");
            usernameField.setAccessible(true);
            usernameField.set(o, username);

            Field type = o.getClass().getDeclaredField("sessionType");
            type.setAccessible(true);
            type.set(o, Session.Type.LEGACY);

            sessionField.set(Minecraft.getMinecraft(), o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
