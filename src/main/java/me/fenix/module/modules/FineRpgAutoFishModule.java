package me.fenix.module.modules;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.fenix.module.Module;
import me.fenix.module.ModuleInfo;
import me.fenix.module.PacketHandler;
import me.fenix.module.settings.Setting;
import me.fenix.util.Util;
import me.fenix.util.WaitTimer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import org.lwjgl.input.Keyboard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@ModuleInfo(name = "AutoFish - FineRpg", keyCode = Keyboard.KEY_F)
public class FineRpgAutoFishModule extends Module {

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

    private final Map<String, String> itemUrl = new HashMap<>();

    private final WaitTimer timer = new WaitTimer();
    private boolean waiting;

    private boolean verification;
    private int windowId;
    private String itemNameExpected;

    public FineRpgAutoFishModule() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://gist.githubusercontent.com/fenix544/fb9fe817d6d6a02cd55c379d2f65d4f1/raw/02c2fe081e1c7138c3d1cd8bca22cdc658fbf3ed/dataset.json").openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            Gson gson = new Gson();
            JsonArray array = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), JsonArray.class);
            for (int i = 0; i < array.size(); i++) {
                JsonObject object = array.get(i).getAsJsonObject();
                object.entrySet().forEach(entry -> {
                    String name = entry.getKey();
                    String url = entry.getValue().getAsString();
                    this.itemUrl.put(name, url);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onEnable() {
        this.reset();
    }

    @Override
    public void onUpdate() {
        if (this.timer.hasTimeElapsed(this.delayBetweenCasts.getValue(), true)) {
            EntityPlayerSP player = this.mc.thePlayer;

            if (!this.waiting && player.fishEntity == null && !this.verification) {
                this.waiting = true;
                Util.rightClick();

//                this.scheduler.runAsyncTask(() -> {
//                    Util.sleep(1200L, TimeUnit.MILLISECONDS);
//
//                    double dx = player.fishEntity.posX - player.posX;
//                    double dy = player.fishEntity.posY - player.posY;
//                    double dz = player.fishEntity.posZ - player.posZ;
//                    double r = Math.sqrt(dx * dx + dy * dy + dz * dz);
//                    double yaw = -Math.atan2(dx, dz) / Math.PI * 180;
//                    if (yaw < 0) {
//                        yaw = 360 + yaw;
//                    }
//                    double pitch = -Math.asin(dy / r) / Math.PI * 180;
//
//                    System.out.println(pitch);
//
//                    for (float i = player.rotationPitch; i > pitch; i -= 1) {
//                        player.setPositionAndRotation(
//                                player.posX,
//                                player.posY,
//                                player.posZ,
//                                (float) yaw,
//                                i
//                        );
//                        Util.sleep(this.delayDuringRotation.getValue(), TimeUnit.MILLISECONDS);
//                    }
//                });
            }
        }
    }

    @PacketHandler(handle = S29PacketSoundEffect.class)
    public void onSoundEffect(S29PacketSoundEffect packet) {
        EntityPlayerSP player = this.mc.thePlayer;

        if (player.fishEntity == null ||
                !packet.getSoundName().equals("random.splash") ||
                Math.abs(packet.getX() - player.fishEntity.posX) > 1 && Math.abs(packet.getZ() - player.fishEntity.posZ) > 1)
            return;

        this.caughtFish.setValue(this.caughtFish.getValue() + 1);
        Util.rightClick();

        this.scheduler.runAsyncTask(() -> {
            Util.sleep(this.delayAfterCaught.getValue(), TimeUnit.MILLISECONDS);
            double random = ThreadLocalRandom.current().nextDouble(0.75, 0.95);

            for (float i = 0; i < this.pitchToRotate.getValue() * 2; i += random) {
                player.setPositionAndRotation(
                        player.posX,
                        player.posY,
                        player.posZ,
                        player.rotationYaw,
                        (float) (player.rotationPitch + random)
                );
                Util.sleep(this.delayDuringRotation.getValue(), TimeUnit.MILLISECONDS);
            }

            Util.sleep(this.delayAfterRotation.getValue(), TimeUnit.MILLISECONDS);
            for (float i = 0; i < this.pitchToRotate.getValue() * 2; i += random) {
                player.setPositionAndRotation(
                        player.posX,
                        player.posY,
                        player.posZ,
                        player.rotationYaw,
                        (float) (player.rotationPitch - random)
                );
                Util.sleep(this.delayDuringRotation.getValue(), TimeUnit.MILLISECONDS);
            }

            Util.sleep(this.delayAfterRotation.getValue(), TimeUnit.MILLISECONDS);
        }).whenComplete((unused, throwable) -> this.reset());
    }

    @PacketHandler(handle = S2DPacketOpenWindow.class)
    public void onOpenWindowPacket(S2DPacketOpenWindow packet) {
        this.verification = packet.getWindowTitle().getUnformattedText().startsWith("Kliknij podobny");
        this.windowId = packet.getWindowId();
    }

    @PacketHandler(handle = S2FPacketSetSlot.class)
    public void onSetSlotPacket(S2FPacketSetSlot packet) {
        if (!this.verification || packet.func_149175_c() != this.windowId) return;

        int slot = packet.func_149173_d();

        ItemStack itemStack = packet.func_149174_e();
        if (itemStack == null) return;

        if (slot == 0) {
            this.itemNameExpected = String.valueOf(Item.itemRegistry.getNameForObject(itemStack.getItem()));
        }

        if (slot <= 1) return;

        NBTTagCompound tag = itemStack.serializeNBT().getCompoundTag("tag");
        String value = tag.getCompoundTag("SkullOwner")
                .getCompoundTag("Properties")
                .getTagList("textures", 10)
                .getCompoundTagAt(0)
                .getString("Value");

        JsonObject jsonObject = (JsonObject) new JsonParser().parse(new String(Base64.getDecoder().decode(value)));
        String url = jsonObject.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();

        if (!this.itemUrl.containsKey(this.itemNameExpected)) {
            System.out.println("Not found");
            System.out.println("Key " + this.itemNameExpected + " Value: " + url);
            return;
        }

        if (this.itemUrl.get(this.itemNameExpected).equals(url)) {
            System.out.println("Found");
            this.mc.playerController.windowClick(
                    this.windowId,
                    slot,
                    0,
                    0,
                    this.mc.thePlayer
            );
            this.reset();
        }
    }

    private void reset() {
        this.waiting = false;
        this.verification = false;
        this.itemNameExpected = null;
        this.timer.reset();
    }
}
