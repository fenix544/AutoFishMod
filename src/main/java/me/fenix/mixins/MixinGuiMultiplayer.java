package me.fenix.mixins;

import me.fenix.gui.AccountsGui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends GuiScreen {

    @Inject(method = "createButtons", at = @At("HEAD"))
    public void createButtons(CallbackInfo callbackInfo) {
        this.buttonList.add(new GuiButton(2137, this.width / 2 + 80, 10, 100, 20, "Accounts"));
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    protected void actionPerformed(GuiButton button, CallbackInfo callbackInfo) throws IOException {
        if (button.enabled && button.id == 2137) {
            this.mc.displayGuiScreen(new AccountsGui(this));
        }
    }
}
