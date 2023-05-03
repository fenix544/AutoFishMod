package me.fenix.mixins;

import me.fenix.CarbonMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderTooltip", at = @At("HEAD"))
    protected void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        CarbonMod carbonMod = CarbonMod.getInstance();
        if (carbonMod != null && carbonMod.getModuleInfoGui() != null) {
            carbonMod.getModuleInfoGui().renderModuleInfo(Minecraft.getMinecraft().fontRendererObj);
        }
    }


}
