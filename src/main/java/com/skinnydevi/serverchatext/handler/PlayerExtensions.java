package com.skinnydevi.serverchatext.handler;

import com.skinnydevi.serverchatext.config.ChatExtConfig;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class PlayerExtensions {
    private final String playerName;
    private String prefix;
    private String suffix;
    private String nickname;

    public PlayerExtensions(ServerPlayer player, String prefix, String suffix, String nickname) {
        this.playerName = player.getName().getString();

        this.prefix = prefix;
        this.suffix = suffix;
        this.nickname = nickname;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealName() {
        return playerName;
    }

    public String getPlayerName() {
        if (Objects.equals(this.nickname, PlayerExtensionManager.NULL_EXTENSION)) return this.getRealName();

        return this.nickname + ChatExtConfig.NICKNAME_INDICATOR.get();
    }
}
