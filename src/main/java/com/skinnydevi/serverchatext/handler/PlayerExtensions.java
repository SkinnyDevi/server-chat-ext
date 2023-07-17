package com.skinnydevi.serverchatext.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class PlayerExtensions {
    private final String playerName;
    private String prefix;
    private String suffix;

    public PlayerExtensions(ServerPlayer player, String prefix, String suffix) {
        CompoundTag data = player.getPersistentData();
        this.playerName = player.getName().getString();

        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPlayerName() {
        return playerName;
    }
}
