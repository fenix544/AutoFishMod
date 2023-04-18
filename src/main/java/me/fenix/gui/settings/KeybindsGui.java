package me.fenix.gui.settings;

import me.fenix.CarbonMod;
import me.fenix.module.Module;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class KeybindsGui extends GuiScreen {

    private int buttonId = -1;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        int buttonId = 0;
        int buttonX = 20;
        int buttonY = 50;

        for (Module module : getModules()) {
            String buttonText = module.getName();

            this.buttonList.add(new GuiButton(buttonId, buttonX, buttonY, 20 + this.fontRendererObj.getStringWidth(buttonText), 20, buttonText));
            this.buttonList.add(new GuiButton(buttonId + 10, buttonX + 25 + this.fontRendererObj.getStringWidth(buttonText), buttonY, 20, 20, Keyboard.getKeyName(module.getKeyCode())));

            buttonY += 30;
            buttonId++;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            if (buttonId != -1) {
                Optional<Module> first = getModules().stream()
                        .filter(module -> module.getKeyCode() == keyCode)
                        .findFirst();

                if (first.isPresent())
                    return;

                getButtonById(buttonId).displayString = Keyboard.getKeyName(getModules().get(buttonId - 10).getKeyCode());
                return;
            }
            this.mc.displayGuiScreen(null);
        }

        if (buttonId != -1) {
            int moduleIndex = buttonId - 10;
            Module module = getModules().get(moduleIndex);
            module.setKeyCode(keyCode);
            getButtonById(buttonId).displayString = Keyboard.getKeyName(keyCode);
            buttonId = -1;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 10 && buttonId == -1) {
            button.displayString = "...";
            buttonId = button.id;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, "Keybinds", this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private GuiButton getButtonById(int id) {
        return this.buttonList.stream().filter(button -> button.id == id).findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public List<Module> getModules() {
        return CarbonMod.getInstance().getModuleManager().getModules();
    }
}
