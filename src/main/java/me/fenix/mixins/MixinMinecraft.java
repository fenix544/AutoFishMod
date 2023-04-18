package me.fenix.mixins;

import com.darkmagician6.eventapi.EventManager;
import me.fenix.CarbonMod;
import me.fenix.event.events.KeyEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "startGame", at = @At("HEAD"))
    public void startGame(CallbackInfo ci) {
        CarbonMod carbonMod = new CarbonMod();
        carbonMod.setupCarbonMod();

        CarbonMod.setInstance(carbonMod);
    }

    @Inject(method = "shutdownMinecraftApplet", at = @At("HEAD"))
    public void shutdownMinecraftApplet(CallbackInfo ci) {
        CarbonMod.getInstance().getModuleManager().onDisable();
    }

    @Inject(method = "dispatchKeypresses", at = @At(value = "HEAD"))
    private void onKey(CallbackInfo callbackInfo) {
        try {
            GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;

            if (Keyboard.getEventKeyState() && (currentScreen == null || (currentScreen instanceof GuiContainer))) {
                EventManager.call(new KeyEvent(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey()));
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

}
