package me.fenix.module.modules;

import me.fenix.module.Module;
import me.fenix.module.ModuleInfo;
import me.fenix.module.PacketHandler;
import me.fenix.module.settings.Setting;
import me.fenix.util.Util;
import me.fenix.util.WaitTimer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S45PacketTitle;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "AutoFish - PvpIq", keyCode = Keyboard.KEY_I)
public class PvpIqAutoFishModule extends Module {

    private final Setting<Long> delayBetweenCasts = this.settingsBuilder
            .createSetting(new Setting<>(Long.class, 100L, "Delay between casts", true, "%name%: %value%ms"));
    private final Setting<Long> delayInCheckRod = this.settingsBuilder
            .createSetting(new Setting<>(Long.class, 1000L, "Delay in check rod", true, 300, 2000, "%name%: %value%ms"));
    private final Setting<Integer> caughtFish = this.settingsBuilder
            .createSetting(new Setting<>(Integer.class, 0, "Caught fish"));

    private final WaitTimer timer = new WaitTimer();
    private final WaitTimer checkRodTimer = new WaitTimer();

    private boolean waiting;
    private boolean verificationIn, verificationComplete;

    @Override
    protected void onEnable() {
        this.reset();
    }

    @Override
    public void onUpdate() {
        if (this.verificationComplete) {
            this.reset();
            startCast();
            return;
        }

        if (this.verificationIn) {
            Util.rightClick();
            return;
        }

        startCast();
    }

    private void startCast() {
        if (this.timer.hasTimeElapsed(this.delayBetweenCasts.getValue(), true)) {
            if (!this.waiting && this.mc.thePlayer.fishEntity == null) {
                this.waiting = true;
                Util.rightClick();

                if (this.checkRodTimer.hasTimeElapsed(this.delayInCheckRod.getValue(), true)) {
                    if (this.mc.thePlayer.fishEntity == null) {
                        this.reset();
                        this.startCast();
                    }
                }
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

        Util.rightClick();

        this.caughtFish.setValue(this.caughtFish.getValue() + 1);
    }

    @PacketHandler(handle = S02PacketChat.class)
    public void onChat(S02PacketChat packet) {
        String unformattedText = packet.getChatComponent().getUnformattedText();

        if (unformattedText.equals("Polow zakonczony powodzeniem."))
            this.verificationComplete = true;
    }

    @PacketHandler(handle = S45PacketTitle.class)
    public void onTitlePacket(S45PacketTitle packet) {
        String title = packet.getMessage().getUnformattedText().replace("§", "").replace("a", "").replace("7", "");
        if (title.contains(";;;;;;;;;;"))
            this.verificationIn = true;
    }

    private void reset() {
        this.verificationIn = false;
        this.verificationComplete = false;
        this.timer.reset();
        this.checkRodTimer.reset();
        this.waiting = false;
    }
}
