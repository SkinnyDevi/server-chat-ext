package com.skinnydevi.serverchatext.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

public class PlayerExtensionManager {
    public static final String NULL_EXTENSION = "[chatext_null]";
    private static final HashMap<ServerPlayer, PlayerExtensions> extensions = new HashMap<>();

    public static void loadPlayerExt(ServerPlayer player) {
        if (!checkChatExtNbtExists(player)) createChatExtNbtData(player);

        CompoundTag data = player.getPersistentData();
        String prefix = data.getString("chatext_prefix");
        String suffix = data.getString("chatext_suffix");
        extensions.put(player, new PlayerExtensions(player, prefix, suffix));
    }

    public static void savePlayerExt(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        PlayerExtensions exts = extensions.get(player);

        data.putString("chatext_prefix", exts.getPrefix());
        data.putString("chatext_suffix", exts.getSuffix());
    }

    public static void untrackPlayer(ServerPlayer player) {
        extensions.remove(player);
    }

    public static boolean checkChatExtNbtExists(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();

        return data.contains("chatext_suffix") && data.contains("chatext_prefix");
    }

    private static void createChatExtNbtData(ServerPlayer player) {
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
        PlayerExtensions exts = extensions.get(player);
        exts.setPrefix(prefix);
        extensions.put(player, exts);
    }

    public static void changePlayerSuffix(ServerPlayer player, String suffix) {
        PlayerExtensions exts = extensions.get(player);
        exts.setSuffix(suffix);
        extensions.put(player, exts);
    }

    public static MutableComponent applyPrefix(ServerPlayer player, MutableComponent component) {
        PlayerExtensions exts = extensions.get(player);

        String prefix = extensionIsNull(exts.getPrefix()) ? "" : exts.getPrefix();

        return component.append(prefix + exts.getPlayerName());
    }

    public static MutableComponent applySuffix(ServerPlayer player, MutableComponent component, Component message) {
        PlayerExtensions exts = extensions.get(player);

        String suffix = extensionIsNull(exts.getSuffix()) ? "" : exts.getSuffix();

        if (!suffix.equals("")) component.append(" ");

        return component.append(suffix + ": " + message.getString());
    }
}
