package me.fenix.event;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import me.fenix.event.events.KeyEvent;
import me.fenix.event.events.PacketEvent;
import me.fenix.event.events.UpdateEvent;
import me.fenix.gui.settings.GlobalSettingsGui;
import me.fenix.module.Module;
import me.fenix.util.StringUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventHandler {
    private final List<Module> modules;

    public EventHandler(List<Module> modules) {
        this.modules = modules;
        EventManager.register(this);
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        this.getEnabledModules().forEach(module -> {
            module.getMethod(event.getPacket().getClass()).ifPresent(method -> {
                try {
                    method.invoke(module, event.getPacket());
                } catch (Exception ignored) {

                }
            });
        });
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        this.getEnabledModules().forEach(Module::onUpdate);
    }

    @EventTarget
    public void onKey(KeyEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();

        Optional<Module> first = this.modules.stream()
                .filter(module -> module.getKeyCode() == event.getKeyCode())
                .findFirst();

        first.ifPresent(module -> {
            Optional<Module> moduleOptional = this.getEnabledModules().stream().findFirst();
            if (moduleOptional.isPresent()) {
                minecraft.ingameGUI.displayTitle(StringUtil.fixColors("&7[ &3Modules &7]"), null, 10, 10, 10);
                minecraft.ingameGUI.displayTitle(null, StringUtil.fixColors("&7Module &9" + module.getName() + " &7cannot be enabled &8(&9" + moduleOptional.get().getName() + " &7is enabled&8)"), 10, 10, 10);
                return;
            }

            module.toggle();
        });

        if (event.getKeyCode() == Keyboard.KEY_P) {
            minecraft.displayGuiScreen(new GlobalSettingsGui());
        }
    }

    public List<Module> getEnabledModules() {
        return this.modules.stream()
                .filter(Module::isEnabled)
                .collect(Collectors.toList());
    }

}
