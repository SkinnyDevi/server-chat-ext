package com.skinnydevi.serverchatext.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class PlayerExtensions {
    String suffix;
    String prefix;
    ServerPlayer player;

    public PlayerExtensions(ServerPlayer player) {
        this.player = player;

        CompoundTag data = player.getPersistentData();

        this.prefix = data.getString("chatext_prefix");
        this.suffix = data.getString("chatext_suffix");
    }

    public String getSuffix() {
        return suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
