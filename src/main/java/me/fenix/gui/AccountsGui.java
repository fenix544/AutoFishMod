package me.fenix.gui;

import me.fenix.CarbonMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class AccountsGui extends GuiScreen {

    private final GuiScreen previousScreen;
    private GuiTextField usernameField;

    private String text;
    private int colorText = 0x48ff00;

    public AccountsGui(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, "Cancel"));

        this.usernameField = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, 116, 200, 20);
        this.usernameField.setMaxStringLength(16);
        this.usernameField.setFocused(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.usernameField.updateCursorCounter();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 1) {
                this.mc.displayGuiScreen(this.previousScreen);
            } else if (button.id == 0) {
                String username = this.usernameField.getText();

                if (username.length() < 3) {
                    this.text = "Username must be at least 3 characters long";
                    this.colorText = 0xff0000;
                    return;
                }

                CarbonMod.getInstance().setUsername(username);
                this.text = "Logged in as " + username;
                this.usernameField.setText("");
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.usernameField.textboxKeyTyped(typedChar, keyCode)) {
            if (this.usernameField.getText().length() < 3) {
                this.text = "Username must be at least 3 characters long";
                this.colorText = 0xff0000;
            } else {
                this.text = null;
                this.colorText = 0x48ff00;
            }
        } else if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed(this.buttonList.get(0));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.usernameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Change account (Cracked)", this.width / 2, 20, 0x6229ff);
        this.drawString(this.fontRendererObj, "Enter username", this.width / 2 - 100, 100, 10526880);
        if (this.text != null) {
            this.drawCenteredString(this.fontRendererObj, this.text, this.width / 2, this.height / 4 + 85, this.colorText);
        }

        this.usernameField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
