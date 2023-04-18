package me.fenix.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import me.fenix.CarbonMod;
import me.fenix.module.Module;
import me.fenix.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModuleInfoGui extends Gui {
    private final Minecraft mc;

    public ModuleInfoGui(Minecraft mc) {
        this.mc = mc;
    }

    public void renderModuleInfo(FontRenderer fontRenderer) {
        this.mc.mcProfiler.startSection("ModulesRender");
        GlStateManager.pushMatrix();
        this.renderDebugModuleInfo(fontRenderer);
        GlStateManager.popMatrix();
        this.mc.mcProfiler.endSection();
    }

    private void renderDebugModuleInfo(FontRenderer fontRenderer) {
        List<Module> enabledModules = CarbonMod.getInstance().getModuleManager().getEnabledModules();
        if (enabledModules == null || enabledModules.isEmpty()) return;

        List<String> list = Lists.newArrayList();
        list.add("&8[&3!&8] &7Enabled modules: ");

        enabledModules.forEach(module -> list.add("&8[&3!&8] &8&l» &3" + module.getName() + " &8[&3" + Keyboard.getKeyName(module.getKeyCode()) + "&8]"));
        Optional<Module> autoFish = enabledModules.stream()
                .filter(module -> module.getName().contains("AutoFish"))
                .findFirst();
        autoFish.ifPresent(module -> {
            list.add("&m---------------------");
            list.add("&8[&3!&8] &7Settings for &3" + module.getName());
            module.getSettings().forEach(setting -> {
                String formatMessage = setting.getFormatMessage();
                if (formatMessage == null) {
                    list.add("&8[&3!&8] &8&l» &7" + setting.getName() + ": &3" + setting.getValue());
                } else {
                    list.add("&8[&3!&8] &8&l» &7" + setting.getFormatMessage().replace("%name%", setting.getName()).replace("%value%", "&3" + setting.getValue().toString()));
                }
            });
        });

        List<String> collect = list.stream().map(StringUtil::fixColors).collect(Collectors.toList());

        for (int i = 0; i < collect.size(); ++i) {
            String s = collect.get(i);

            if (!Strings.isNullOrEmpty(s)) {
                int j = fontRenderer.FONT_HEIGHT;
                int i1 = 2 + j * i;

                fontRenderer.drawString(s, 2, i1, 0xE0E0E0);
            }
        }
    }
}
