package com.skinnydevi.serverchatext.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import com.skinnydevi.serverchatext.config.ChatExtConfig;
import com.skinnydevi.serverchatext.event.ChatMessageEvent;
import com.skinnydevi.serverchatext.handler.CustomPlayerExtensionHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ChatExtCommand {
    private static final String CMD_PREFIX = "chatext";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        boolean allowNoOp = ChatExtConfig.ALLOW_NOOP_CHANGE.get();

        dispatcher.register(
                Commands.literal(CMD_PREFIX).requires(src -> src.hasPermission(allowNoOp ? 0 : 4))
                .then(change())
                .then(reset())
                .then(broadcast())
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reset() {
        LiteralArgumentBuilder<CommandSourceStack> reset = Commands.literal("reset");

        boolean allowNoOp = ChatExtConfig.ALLOW_NOOP_CHANGE.get();
        if (allowNoOp) {
            reset = reset.then(resetPersonalPrefix());
            reset = reset.then(resetPersonalSuffix());
            reset = reset.then(resetPersonalAll());
        }

        reset = reset.then(resetPrefix());
        reset = reset.then(resetSuffix());
        reset = reset.then(resetAll());

        return reset;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> broadcast() {
        return Commands.literal("broadcast").requires(src -> src.hasPermission(4))
                .then(Commands.argument("message", StringArgumentType.string())
                    .executes(ctx -> {
                        ServerPlayer serverPlayer = ctx.getSource().getPlayerOrException();
                        String message = "&c[&lBROAD&f&lCAST&r&c]:&r " + StringArgumentType.getString(ctx, "message");

                        serverPlayer.server.execute(() -> {
                            String coloured = ChatMessageEvent.interpretColours(message);
                            for (ServerPlayer player : serverPlayer.server.getPlayerList().getPlayers()) {
                                player.sendSystemMessage(Component.literal(coloured));
                            }

                            ChatMessageEvent.logChatToConsole(coloured);
                        });

                        return 1;
                    })
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetAll() {
        return Commands.literal("all").requires(src -> src.hasPermission(4))
                .then(Commands.argument("targetplayer", EntityArgument.players()).executes(ctx -> {
                    ServerPlayer player = EntityArgument.getPlayer(ctx, "targetplayer");

                    CustomPlayerExtensionHandler.changePlayerPrefix(player, CustomPlayerExtensionHandler.NULL_EXTENSION);
                    CustomPlayerExtensionHandler.changePlayerSuffix(player, CustomPlayerExtensionHandler.NULL_EXTENSION);

                    ctx.getSource().getPlayerOrException().sendSystemMessage(
                            Component.literal(ChatFormatting.GOLD + "[" + ChatFormatting.DARK_GREEN + "ChatExt" + ChatFormatting.GOLD + "] "
                                    + ChatFormatting.DARK_GREEN + "Reset all for player " + ChatFormatting.GREEN + player.getName().getString()
                            )
                    );

                    return 1;
                }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetPrefix() {
        return Commands.literal("prefix").requires(src -> src.hasPermission(4))
                .then(Commands.argument("targetplayer", EntityArgument.players()).executes(ctx -> {
                    ServerPlayer player = EntityArgument.getPlayer(ctx, "targetplayer");

                    CustomPlayerExtensionHandler.changePlayerPrefix(player, CustomPlayerExtensionHandler.NULL_EXTENSION);

                    ctx.getSource().getPlayerOrException().sendSystemMessage(
                            Component.literal(ChatFormatting.GOLD + "[" + ChatFormatting.DARK_GREEN + "ChatExt" + ChatFormatting.GOLD + "] "
                                    + ChatFormatting.DARK_GREEN + "Reset prefix for player " + ChatFormatting.GREEN + player.getName().getString()
                            )
                    );

                    return 1;
                }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetSuffix() {
        return Commands.literal("suffix").requires(src -> src.hasPermission(4))
                .then(Commands.argument("targetplayer", EntityArgument.players()).executes(ctx -> {
                    ServerPlayer player = EntityArgument.getPlayer(ctx, "targetplayer");

                    CustomPlayerExtensionHandler.changePlayerSuffix(player, CustomPlayerExtensionHandler.NULL_EXTENSION);

                    ctx.getSource().getPlayerOrException().sendSystemMessage(
                            Component.literal(ChatFormatting.GOLD + "[" + ChatFormatting.DARK_GREEN + "ChatExt" + ChatFormatting.GOLD + "] "
                                    + ChatFormatting.DARK_GREEN + "Reset suffix for player " + ChatFormatting.GREEN + player.getName().getString()
                            )
                    );

                    return 1;
                }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetPersonalAll() {
        return Commands.literal("customall").executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();

            CustomPlayerExtensionHandler.changePlayerPrefix(player, CustomPlayerExtensionHandler.NULL_EXTENSION);
            CustomPlayerExtensionHandler.changePlayerSuffix(player, CustomPlayerExtensionHandler.NULL_EXTENSION);

            ctx.getSource().getPlayerOrException().sendSystemMessage(
                    Component.literal(ChatFormatting.GOLD + "[" + ChatFormatting.DARK_GREEN + "ChatExt" + ChatFormatting.GOLD + "] "
                            + ChatFormatting.DARK_GREEN + "Reset all successfully"
                    )
            );

            return 1;
        });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetPersonalPrefix() {
        return Commands.literal("customprefix").executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();

            CustomPlayerExtensionHandler.changePlayerPrefix(player, CustomPlayerExtensionHandler.NULL_EXTENSION);

            ctx.getSource().getPlayerOrException().sendSystemMessage(
                    Component.literal(ChatFormatting.GOLD + "[" + ChatFormatting.DARK_GREEN + "ChatExt" + ChatFormatting.GOLD + "] "
                            + ChatFormatting.DARK_GREEN + "Reset prefix successfully"
                    )
            );

            return 1;
        });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetPersonalSuffix() {
        return Commands.literal("customsuffix").executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();

            CustomPlayerExtensionHandler.changePlayerSuffix(player, CustomPlayerExtensionHandler.NULL_EXTENSION);

            ctx.getSource().getPlayerOrException().sendSystemMessage(
                    Component.literal(ChatFormatting.GOLD + "[" + ChatFormatting.DARK_GREEN + "ChatExt" + ChatFormatting.GOLD + "] "
                            + ChatFormatting.DARK_GREEN + "Reset suffix successfully"
                    )
            );

            return 1;
        });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> change() {
        LiteralArgumentBuilder<CommandSourceStack> change = Commands.literal("change");

        boolean allowNoOp = ChatExtConfig.ALLOW_NOOP_CHANGE.get();
        if (allowNoOp) {
            change = change.then(changePersonalPrefix());
            change = change.then(changePersonalSuffix());
        }

        change = change.then(changePrefix());
        change = change.then(changeSuffix());

        return change;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> changePrefix() {
        return Commands.literal("prefix").requires(src -> src.hasPermission(4)).then(
                Commands.argument("targetplayer", EntityArgument.players())
                .then(Commands.argument("prefix", StringArgumentType.string()).executes(ctx -> {
                    String prefix = StringArgumentType.getString(ctx, "prefix");
                    ServerPlayer player = EntityArgument.getPlayer(ctx, "targetplayer");

                    CustomPlayerExtensionHandler.changePlayerPrefix(player, prefix);

                    ctx.getSource().getPlayerOrException().sendSystemMessage(
                            Component.literal(ChatFormatting.GOLD + "[" + ChatFormatting.DARK_GREEN + "ChatExt" + ChatFormatting.GOLD + "] "
                                    + ChatFormatting.DARK_GREEN + "Changed prefix for player " + ChatFormatting.GREEN + player.getName().getString()
                            )
                    );

                    return 1;
                }))
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> changeSuffix() {
        return Commands.literal("suffix").requires(src -> src.hasPermission(4)).then(
                Commands.argument("targetplayer", EntityArgument.players())
                        .then(Commands.argument("suffix", StringArgumentType.string()).executes(ctx -> {
                            String suffix = StringArgumentType.getString(ctx, "suffix");
                            ServerPlayer player = EntityArgument.getPlayer(ctx, "targetplayer");

                            CustomPlayerExtensionHandler.changePlayerSuffix(player, suffix);

                            ctx.getSource().getPlayerOrException().sendSystemMessage(
                                    Component.literal(ChatFormatting.GOLD + "[" + ChatFormatting.DARK_GREEN + "ChatExt" + ChatFormatting.GOLD + "] "
                                            + ChatFormatting.DARK_GREEN + "Changed suffix for player " + ChatFormatting.GREEN + player.getName().getString()
                                    )
                            );

                            return 1;
                        }))
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> changePersonalPrefix() {
        return Commands.literal("customprefix").then(
                Commands.argument("prefix", StringArgumentType.string()).executes(ctx -> {
                    String prefix = StringArgumentType.getString(ctx, "prefix");
                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                    CustomPlayerExtensionHandler.changePlayerPrefix(player, prefix);

                    ctx.getSource().getPlayerOrException().sendSystemMessage(
                            Component.literal(ChatFormatting.GOLD + "[" + ChatFormatting.DARK_GREEN + "ChatExt" + ChatFormatting.GOLD + "] "
                                    + ChatFormatting.DARK_GREEN + "Changed prefix successfully"
                            )
                    );

                    return 1;
                })
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> changePersonalSuffix() {
        return Commands.literal("customsuffix").then(
                Commands.argument("suffix", StringArgumentType.string()).executes(ctx -> {
                    String suffix = StringArgumentType.getString(ctx, "suffix");
                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                    CustomPlayerExtensionHandler.changePlayerSuffix(player, suffix);

                    ctx.getSource().getPlayerOrException().sendSystemMessage(
                            Component.literal(ChatFormatting.GOLD + "[" + ChatFormatting.DARK_GREEN + "ChatExt" + ChatFormatting.GOLD + "] "
                                    + ChatFormatting.DARK_GREEN + "Reset suffix successfully"
                            )
                    );

                    return 1;
                })
        );
    }
}
