package me.fenix.module.modules;

import me.fenix.module.Module;
import me.fenix.module.ModuleInfo;
import me.fenix.module.PacketHandler;
import me.fenix.module.settings.Setting;
import me.fenix.util.Util;
import me.fenix.util.WaitTimer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "AutoFish - MyRpg", keyCode = Keyboard.KEY_H)
public class MyRpgAutoFishModule extends Module {

    private final Setting<Long> delayBetweenCasts = this.settingsBuilder
            .createSetting(new Setting<>(Long.class, 100L, "Delay between casts", true, "%name%: %value%ms"));
    private final Setting<Long> delayAfterCaught = this.settingsBuilder
            .createSetting(new Setting<>(Long.class, 100L, "Delay after caught", true, "%name%: %value%ms"));
    private final Setting<Long> delayDuringRotation = this.settingsBuilder
            .createSetting(new Setting<>(Long.class, 10L, "Delay during rotation", true, 1f, 50f, "%name%: %value%ms"));
    private final Setting<Integer> caughtFish = this.settingsBuilder
            .createSetting(new Setting<>(Integer.class, 0, "Caught fish"));

    private final WaitTimer timer = new WaitTimer();
    private boolean waiting;
    private RotationType rotationType = RotationType.UP;

    @Override
    protected void onEnable() {
        this.reset();
    }

    @Override
    public void onUpdate() {
        if (this.timer.hasTimeElapsed(this.delayBetweenCasts.getValue(), true)) {
            if (!this.waiting && this.mc.thePlayer.fishEntity == null) {
                this.waiting = true;
                Util.rightClick();
            }
        }
    }

    @PacketHandler(handle = S29PacketSoundEffect.class)
    public void onSoundEffect(S29PacketSoundEffect packet) {
        EntityPlayerSP thePlayer = this.mc.thePlayer;

        if (thePlayer.fishEntity == null) return;
        if (!packet.getSoundName().equals("random.splash")) return;
        if (Math.abs(packet.getX() - thePlayer.fishEntity.posX) > 1 && Math.abs(packet.getZ() - thePlayer.fishEntity.posZ) > 1)
            return;

        this.caughtFish.setValue(this.caughtFish.getValue() + 1);

        Util.rightClick();
        int lastSlot = thePlayer.inventory.currentItem;
        int slot = thePlayer.inventory.currentItem == 8 ? 0 : thePlayer.inventory.currentItem + 1;
        thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));

        Util.singleRotation(this.delayAfterCaught, this.delayDuringRotation, this.rotationType, () -> {
            thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(lastSlot));
            this.reset();
        });
    }

    private void reset() {
        this.waiting = false;
        this.rotationType = this.rotationType == RotationType.UP ? RotationType.DOWN : RotationType.UP;
        this.timer.reset();
    }

    public enum RotationType {
        UP, DOWN;

        public float calculate(float playerPitch, float value) {
            return this == UP ? playerPitch + value : playerPitch - value;
        }
    }
}
