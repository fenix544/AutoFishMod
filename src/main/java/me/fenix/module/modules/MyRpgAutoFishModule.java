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

import java.util.concurrent.TimeUnit;

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
        EntityPlayerSP player = this.mc.thePlayer;

        if (player.fishEntity == null) return;
        if (!packet.getSoundName().equals("random.splash")) return;
        if (Math.abs(packet.getX() - player.fishEntity.posX) > 1 && Math.abs(packet.getZ() - player.fishEntity.posZ) > 1)
            return;

        this.caughtFish.setValue(this.caughtFish.getValue() + 1);
        Util.rightClick();

        int lastSlot = player.inventory.currentItem;
        int slot = lastSlot == 8 ? 0 : lastSlot + 1;
        player.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
        player.sendQueue.addToSendQueue(new C09PacketHeldItemChange(lastSlot));

        this.scheduler.runAsyncTask(() -> {
            Util.sleep(this.delayAfterCaught.getValue(), TimeUnit.MILLISECONDS);
            for (int i = 1; i < 9; i++) {
                player.setPositionAndRotation(
                        player.posX,
                        player.posY,
                        player.posZ,
                        player.rotationYaw,
                        this.rotationType.calculate(player.rotationPitch, (float) (i * 0.25))
                );
                Util.sleep(this.delayDuringRotation.getValue(), TimeUnit.MILLISECONDS);
            }
        }).whenComplete((aVoid, throwable) -> this.reset());
    }

    private void reset() {
        this.waiting = false;
        this.rotationType = this.rotationType == RotationType.UP ? RotationType.DOWN : RotationType.UP;
        this.timer.reset();
    }

    private enum RotationType {
        UP, DOWN;

        public float calculate(float playerPitch, float value) {
            return this == UP ? playerPitch + value : playerPitch - value;
        }
    }
}
