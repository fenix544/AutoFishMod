package me.fenix.gui.settings;

import me.fenix.CarbonMod;
import me.fenix.module.Module;
import me.fenix.module.settings.Setting;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalSettingsGui extends GuiScreen {

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        List<Module> filteredModules = getModules()
                .stream()
                .filter(module -> module.getSettings().stream().anyMatch(Setting::isSave))
                .collect(Collectors.toList());

        int buttonId = 0;
        int buttonX = 20;
        int buttonY = 50;

        this.buttonList.add(new GuiButton(buttonId, buttonX, buttonY, 20 + this.fontRendererObj.getStringWidth("Keybinds"), 20, "Keybinds"));

        for (Module module : filteredModules) {
            String buttonText = module.getName();
            buttonY += 30;
            buttonId++;

            this.buttonList.add(new GuiButton(buttonId, buttonX, buttonY, 20 + this.fontRendererObj.getStringWidth(buttonText), 20, buttonText));
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new KeybindsGui());
            return;
        }

        getModules().stream()
                .filter(module -> module.getName().equals(button.displayString))
                .findFirst()
                .ifPresent(module -> this.mc.displayGuiScreen(new ModuleSettingsGui(module)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, "Settings", this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public List<Module> getModules() {
        return CarbonMod.getInstance().getModuleManager().getModules();
    }

}
