package com.skinnydevi.serverchatext.event;

import com.skinnydevi.serverchatext.ServerChatExt;
import com.skinnydevi.serverchatext.config.ChatExtConfig;
import com.skinnydevi.serverchatext.handler.PlayerExtensionManager;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber
public class ChatMessageEvent {
    private static Component previousMessage = Component.empty();

    @SubscribeEvent
    public static void onServerChatMessage(ServerChatEvent event) {
        if (!ChatExtConfig.ENABLE_CHATEXT.get()) return;

        ServerPlayer serverPlayer = event.getPlayer();
        Component message = event.getMessage();

        boolean timestampAllowed = ChatExtConfig.CHAT_TIMESTAMP.get();
        boolean prefixAllowed = ChatExtConfig.ALLOW_PREFIX.get();
        boolean suffixAllowed = ChatExtConfig.ALLOW_SUFFIX.get();

        MutableComponent finalMessage = Component.empty();

        if (timestampAllowed) {
            finalMessage.append(messageTimestamp());
        }

        if (prefixAllowed) {
            PlayerExtensionManager.applyPrefix(serverPlayer, finalMessage);
        }

        if (suffixAllowed) {
            PlayerExtensionManager.applySuffix(serverPlayer, finalMessage, event.getMessage());
        }

        if (serverPlayer.server.isDedicatedServer()) {
            sendGlobalChatMessage(event, serverPlayer, finalMessage);
        } else {
            boolean isFinalMessage = false;
            if (previousMessage.equals(message) && !previousMessage.equals(Component.empty())) {
                sendGlobalChatMessage(event, serverPlayer, finalMessage);
                isFinalMessage = true;
            }

            previousMessage = isFinalMessage ? Component.empty() : message;
        }
        event.setCanceled(true);
    }

    private static void sendGlobalChatMessage(ServerChatEvent event, ServerPlayer serverPlayer, MutableComponent finalMessage) {
        event.setCanceled(true);
        serverPlayer.server.execute(() -> {
            String coloured = interpretColours(finalMessage.getString());
            for (ServerPlayer player : serverPlayer.server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(Component.literal(coloured));
            }

            logChatToConsole(coloured);
        });
    }

    public static String interpretColours(String message) {
        final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)&[0-9a-fk-or]");
        Matcher matcher = STRIP_FORMATTING_PATTERN.matcher(message);

        ArrayList<String> coloursFound = new ArrayList<>();
        while (matcher.find()) {
            String match = matcher.group();
            if (!coloursFound.contains(match)) coloursFound.add(match);
        }

        for (String c : coloursFound) {
            message = message.replaceAll(c, ChatFormatting.getByCode(c.charAt(1)).toString());
        }

        return message;
    }

    public static void logChatToConsole(String message) {
        ServerChatExt.LOGGER.info(ChatFormatting.stripFormatting(message));
    }

    private static MutableComponent messageTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat(ChatExtConfig.TIMESTAMP_FORMAT.get());
        char configColour = ChatExtConfig.TIMESTAMP_COLOUR.get().charAt(1);

        return Component.literal(
                "&" + configColour + formatter.format(new Date()) + " | " + "&" + ChatFormatting.RESET.getChar()
        );
    }
}
