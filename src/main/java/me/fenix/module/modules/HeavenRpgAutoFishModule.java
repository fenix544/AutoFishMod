package me.fenix.module.modules;

import me.fenix.module.Module;
import me.fenix.module.ModuleInfo;
import me.fenix.module.PacketHandler;
import me.fenix.module.settings.Setting;
import me.fenix.util.Util;
import me.fenix.util.WaitTimer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.TimeUnit;

@ModuleInfo(name = "AutoFish - HeavenRpg", keyCode = Keyboard.KEY_M)
public class HeavenRpgAutoFishModule extends Module {

    private final Setting<Long> delayBetweenCasts = this.settingsBuilder
            .createSetting(new Setting<>(Long.class, 150L, "Delay between casts", true, "%name%: %value%ms"));
    private final Setting<Long> delayAfterCaught = this.settingsBuilder
            .createSetting(new Setting<>(Long.class, 300L, "Delay after caught", true, "%name%: %value%ms"));
    private final Setting<Long> delayAfterRotation = this.settingsBuilder
            .createSetting(new Setting<>(Long.class, 300L, "Delay after rotation", true, "%name%: %value%ms"));
    private final Setting<Long> delayDuringRotation = this.settingsBuilder
            .createSetting(new Setting<>(Long.class, 15L, "Delay during rotation", true, 1f, 50f, "%name%: %value%ms"));
    private final Setting<Long> pitchToRotate = this.settingsBuilder
            .createSetting(new Setting<>(Long.class, 10L, "Pitch to rotate", true, 1f, 90f, "%name%: %value%f"));
    private final Setting<Integer> caughtFish = this.settingsBuilder
            .createSetting(new Setting<>(Integer.class, 0, "Caught fish"));

    private final WaitTimer timer = new WaitTimer();
    private boolean waiting;

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

        this.scheduler.runAsyncTask(() -> {
            Util.sleep(this.delayAfterCaught.getValue(), TimeUnit.MILLISECONDS);
            for (float i = 0; i < this.pitchToRotate.getValue(); i++) {
                player.setPositionAndRotation(
                        player.posX,
                        player.posY,
                        player.posZ,
                        player.rotationYaw,
                        player.rotationPitch + 1
                );
                Util.sleep(this.delayDuringRotation.getValue(), TimeUnit.MILLISECONDS);
            }

            Util.sleep(this.delayAfterRotation.getValue(), TimeUnit.MILLISECONDS);
            for (float i = 0; i < this.pitchToRotate.getValue(); i++) {
                player.setPositionAndRotation(
                        player.posX,
                        player.posY,
                        player.posZ,
                        player.rotationYaw,
                        player.rotationPitch - 1
                );
                Util.sleep(this.delayDuringRotation.getValue(), TimeUnit.MILLISECONDS);
            }

            Util.sleep(this.delayAfterRotation.getValue(), TimeUnit.MILLISECONDS);
        }).whenComplete((unused, throwable) -> this.reset());
    }

    private void reset() {
        this.waiting = false;
        this.timer.reset();
    }

}
