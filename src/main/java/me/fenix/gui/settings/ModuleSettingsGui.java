package me.fenix.gui.settings;

import me.fenix.module.Module;
import me.fenix.module.settings.Setting;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;

public class ModuleSettingsGui extends GuiScreen {

    private static final DecimalFormat decimalFormat = new DecimalFormat("###");
    private final Module module;

    public ModuleSettingsGui(Module module) {
        this.module = module;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        int buttonId = 0;
        int buttonX = 20;
        int buttonY = 50;

        for (Setting<?> setting : module.getSettings()) {
            if (!setting.isSave()) continue;

            float defaultValue;
            if (setting.getType() == Long.class) {
                defaultValue = ((Long) setting.getValue()).floatValue();
            } else if (setting.getType() == Integer.class) {
                defaultValue = ((Integer) setting.getValue()).floatValue();
            } else if (setting.getType() == Float.class) {
                defaultValue = (Float) setting.getValue();
            } else {
                continue;
            }

            GuiSlider slider = new GuiSlider(
                    getResponder(setting, setting.getType()),
                    buttonId++,
                    buttonX,
                    buttonY,
                    setting.getName(),
                    setting.getMin(),
                    setting.getMax(),
                    defaultValue,
                    (id, name, value) -> {
                        if (setting.getFormatMessage() == null) {
                            return String.format("%s: %sms", name, decimalFormat.format(value));
                        }
                        return setting.getFormatMessage()
                                .replace("%id%", String.valueOf(id))
                                .replace("%name%", name)
                                .replace("%value%", decimalFormat.format(value));
                    }
            );
            this.buttonList.add(slider);
            buttonY += 30;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, "Settings " + module.getName(), this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private <T> GuiPageButtonList.GuiResponder getResponder(Setting<?> setting, Class<T> clazz) {
        return new GuiPageButtonList.GuiResponder() {
            @Override
            public void func_175321_a(int p_175321_1_, boolean p_175321_2_) {

            }

            @SuppressWarnings("unchecked")
            @Override
            public void onTick(int id, float value) {
                if (clazz == Long.class) {
                    ((Setting<Long>) setting).setValue((long) value);
                } else if (clazz == Integer.class) {
                    ((Setting<Integer>) setting).setValue((int) value);
                }
            }

            @Override
            public void func_175319_a(int p_175319_1_, String p_175319_2_) {

            }
        };
    }
}
