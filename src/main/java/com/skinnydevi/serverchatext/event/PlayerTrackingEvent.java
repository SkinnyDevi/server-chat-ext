package com.skinnydevi.serverchatext.event;

import com.skinnydevi.serverchatext.ServerChatExt;
import com.skinnydevi.serverchatext.handler.PlayerExtensionManager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerTrackingEvent {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer)) return;

        ServerPlayer player = (ServerPlayer) e.getEntity();
        ServerChatExt.LOGGER.info("[ChatExt] Loading extensions for: " + player.getName().getString());
        PlayerExtensionManager.loadPlayerExt(player);
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer)) return;

        ServerPlayer player = (ServerPlayer) e.getEntity();
        ServerChatExt.LOGGER.info("[ChatExt] Saving extensions for: " + player.getName().getString());
        PlayerExtensionManager.savePlayerExt(player);
        PlayerExtensionManager.untrackPlayer(player);
    }
}
