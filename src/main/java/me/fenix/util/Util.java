package me.fenix.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
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
