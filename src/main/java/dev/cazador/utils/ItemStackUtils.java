package dev.cazador.utils;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;

import javax.annotation.Nullable;

public class ItemStackUtils {
    public ItemStackUtils() {
    }

    public static ItemStack setCustomData(ItemStack stack, String key, BsonValue value) {
        return stack != null && key != null ? stack.withMetadata(key, value) : stack;
    }

    public static ItemStack setCustomString(ItemStack stack, String key, String value) {
        return setCustomData(stack, key, value != null ? new BsonString(value) : null);
    }

    public static String getCustomString(ItemStack stack, String key) {
        return stack.getFromMetadataOrNull(key, Codec.STRING);
    }

    public static String[] getCustomStringArray(ItemStack stack, String key) {
        return  stack.getFromMetadataOrNull(key, Codec.STRING_ARRAY);
    }

    public static ItemStack setCustomInt(ItemStack stack, String key, Integer value) {
        return setCustomData(stack, key, value != null ? new BsonInt32(value) : null);
    }

    @Nullable
    public static Integer getCustomInt(ItemStack stack, String key) {
        return stack.getFromMetadataOrNull(key, Codec.INTEGER);
    }
}
