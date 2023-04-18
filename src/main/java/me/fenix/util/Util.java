package me.fenix.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.fenix.module.modules.MyRpgAutoFishModule;
import me.fenix.module.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Util {

    private static final Logger logger = LogManager.getLogger();

    public static void rightClick() {
        Minecraft minecraft = Minecraft.getMinecraft();
        ItemStack stack = minecraft.thePlayer.inventory.getCurrentItem();
        if (!isNullOrEmpty(stack) && stack.getItem() instanceof ItemFishingRod) {
            minecraft.playerController.sendUseItem(minecraft.thePlayer, minecraft.theWorld, stack);
        }
    }

    public static boolean isNullOrEmpty(Item item) {
        return item == null;
    }

    public static boolean isNullOrEmpty(ItemStack stack) {
        return stack == null || isNullOrEmpty(stack.getItem());
    }

    public static void singleRotation(Setting<Long> delayAfterCaught, Setting<Long> delayDuringRotation, MyRpgAutoFishModule.RotationType rotationType, Runnable runnable) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        CompletableFuture.runAsync(() -> {
            Util.sleep(delayAfterCaught.getValue(), TimeUnit.MILLISECONDS);
            for (int i = 1; i < 9; i++) {
                player.setPositionAndRotation(
                        player.posX,
                        player.posY,
                        player.posZ,
                        player.rotationYaw,
                        rotationType.calculate(player.rotationPitch, (float) (i * 0.25))
                );
                Util.sleep(delayDuringRotation.getValue(), TimeUnit.MILLISECONDS);
            }
        }).thenAccept(aVoid -> runnable.run());
    }

    public static void makeRotation(Setting<Long> delayAfterCaught,
                                    Setting<Long> delayAfterRotation,
                                    Setting<Long> settingPitch,
                                    Setting<Long> delayDuringRotation,
                                    Runnable runnable) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        CompletableFuture.runAsync(() -> {
            sleep(delayAfterCaught.getValue(), TimeUnit.MILLISECONDS);
            for (float i = 0; i < settingPitch.getValue(); i++) {
                player.setPositionAndRotation(
                        player.posX,
                        player.posY,
                        player.posZ,
                        player.rotationYaw,
                        player.rotationPitch + 1
                );
                sleep(delayDuringRotation.getValue(), TimeUnit.MILLISECONDS);
            }

            sleep(delayAfterRotation.getValue(), TimeUnit.MILLISECONDS);
            for (float i = 0; i < settingPitch.getValue(); i++) {
                player.setPositionAndRotation(
                        player.posX,
                        player.posY,
                        player.posZ,
                        player.rotationYaw,
                        player.rotationPitch - 1
                );
                sleep(delayDuringRotation.getValue(), TimeUnit.MILLISECONDS);
            }

            sleep(delayAfterRotation.getValue(), TimeUnit.MILLISECONDS);
            runnable.run();
        });
    }

    public static void sleep(long timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void writeJson(File file, JsonObject jsonObject) {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            BufferedWriter writer = Files.newBufferedWriter(file.toPath());
            writer.write(jsonObject.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JsonObject readJson(File file) {
        try {
            BufferedReader reader = Files.newBufferedReader(file.toPath());
            return new JsonParser().parse(reader).getAsJsonObject();
        } catch (Exception e) {
            logger.info("[CarbonClient/SettingManager] Failed to load config.json");
            return null;
        }
    }
}
