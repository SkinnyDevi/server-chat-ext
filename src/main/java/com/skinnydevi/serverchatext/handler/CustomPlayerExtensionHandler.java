package com.skinnydevi.serverchatext.handler;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomPlayerExtensionHandler {
    public static final String NULL_EXTENSION = "[chatext_null]";

    public static boolean checkChatExtNbtExists(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();

        return data.contains("chatext_suffix") && data.contains("chatext_prefix");
    }

    public static void createChatExtNbtData(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();

        if (checkChatExtNbtExists(player)) {
            throw new IllegalStateException("Player already has mod NBT attached.");
        }

        data.putString("chatext_suffix", NULL_EXTENSION);
        data.putString("chatext_prefix", NULL_EXTENSION);
    }

    public static boolean extensionIsNull(String string) {
        return string.equals(NULL_EXTENSION);
    }

    public static void changePlayerPrefix(ServerPlayer player, String prefix) {
        if (!checkChatExtNbtExists(player)) {
            createChatExtNbtData(player);
        }

        CompoundTag data = player.getPersistentData();
        data.putString("chatext_prefix", prefix);
    }

    public static void changePlayerSuffix(ServerPlayer player, String suffix) {
        if (!checkChatExtNbtExists(player)) {
            createChatExtNbtData(player);
        }

        CompoundTag data = player.getPersistentData();
        data.putString("chatext_suffix", suffix);
    }

    public static MutableComponent applyPrefix(ServerPlayer player, MutableComponent component) {
        PlayerExtensions extensions = new PlayerExtensions(player);

        String prefix = extensionIsNull(extensions.getPrefix()) ? "" : extensions.getPrefix();

        return component.append(prefix + player.getName().getString());
    }

    public static MutableComponent applySuffix(ServerPlayer player, MutableComponent component, Component message) {
        PlayerExtensions extensions = new PlayerExtensions(player);

        String suffix = extensionIsNull(extensions.getSuffix()) ? "" : extensions.getSuffix();

        if (!suffix.equals("")) component.append(" ");

        return component.append(suffix + ": " + message.getString());
    }
}

