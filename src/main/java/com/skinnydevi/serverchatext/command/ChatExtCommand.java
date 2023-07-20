package com.skinnydevi.serverchatext.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import com.skinnydevi.serverchatext.config.ChatExtConfig;
import com.skinnydevi.serverchatext.event.ChatMessageEvent;
import com.skinnydevi.serverchatext.handler.PlayerExtensionManager;
import com.skinnydevi.serverchatext.handler.PlayerExtensions;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class ChatExtCommand {
    private static final String CMD_PREFIX = "chatext";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        boolean allowNoOp = ChatExtConfig.ALLOW_NOOP_PREFIXES.get();

        dispatcher.register(broadcast());
        dispatcher.register(nickname());
        dispatcher.register(realname());

        dispatcher.register(
                Commands.literal(CMD_PREFIX).requires(src -> src.hasPermission(allowNoOp ? 0 : 4))
                .then(change())
                .then(reset())
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> broadcast() {
        return Commands.literal("broadcast").requires(src -> src.hasPermission(4))
                .then(Commands.argument("message", MessageArgument.message())
                        .executes(ctx -> {
                            MinecraftServer server = ctx.getSource().getServer();
                            String message = "&c[&lBROAD&f&lCAST&r&c]:&r " + MessageArgument.getMessage(ctx, "message").getString();

                            server.execute(() -> {
                                String coloured = ChatMessageEvent.interpretColours(message);
                                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                                    player.sendSystemMessage(Component.literal(coloured));
                                }

                                ChatMessageEvent.logChatToConsole(coloured);
                            });

                            return 0;
                        })
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> nickname() {
        boolean allowNoOpNick = ChatExtConfig.ALLOW_NOOP_NICKNAME.get();

        return Commands.literal("nickname").requires(src -> src.hasPermission(allowNoOpNick ? 0 : 4))
                .then(nicknameChange())
                .then(nicknameReset());

    }

    private static LiteralArgumentBuilder<CommandSourceStack> nicknameChange() {
        return Commands.literal("change").then(Commands.argument("personal_nickname", MessageArgument.message())
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    String nickname = MessageArgument.getMessage(ctx, "personal_nickname").getString();

                    if (nickname.length() > 25) {
                        sendMessage(ctx.getSource(), Component.literal("Your nickname is too long! Max: 25 characters."));
                        return 1;
                    }

                    PlayerExtensionManager.changePlayerNickname(player, nickname);
                    sendMessage(ctx.getSource(), Component.literal("Nickname change succesfully"));
                    return 0;
                })
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> nicknameReset() {
        return Commands.literal("reset")
                .executes(ctx -> {
                    PlayerExtensionManager.changePlayerNickname(ctx.getSource().getPlayerOrException(), PlayerExtensionManager.NULL_EXTENSION);
                    sendMessage(ctx.getSource(), Component.literal("Nickname reset succesfully"));
                    return 0;
                });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> realname() {
        return Commands.literal("realname").then(Commands.argument("nickname", MessageArgument.message()).executes(
                ctx -> {
                    MinecraftServer server = ctx.getSource().getServer();
                    String nicknameInSearch = MessageArgument.getMessage(ctx, "nickname").getString();

                    for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                        PlayerExtensions exts = PlayerExtensionManager.getPlayer(p);
                        String rawNickname = ChatFormatting.stripFormatting(ChatMessageEvent.interpretColours(exts.getNickname()));

                        if (Objects.equals(rawNickname, PlayerExtensionManager.NULL_EXTENSION)) continue;
                        if (Objects.equals(rawNickname, nicknameInSearch)) {
                            sendMessage(ctx.getSource(),
                                    Component.literal("Player's real name is: " + ChatFormatting.GREEN + exts.getRealName())
                            );

                            return 0;
                        }
                    }

                    sendMessage(ctx.getSource(),
                            Component.literal("No online player has the specified nickname")
                    );

                    return 1;
                }
        ));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> reset() {
        LiteralArgumentBuilder<CommandSourceStack> reset = Commands.literal("reset");

        boolean allowNoOp = ChatExtConfig.ALLOW_NOOP_PREFIXES.get();
        if (allowNoOp) {
            reset = reset.then(resetPersonalPrefix());
            reset = reset.then(resetPersonalSuffix());
            reset = reset.then(resetPersonalAll());
        }

        reset = reset.then(resetPrefix());
        reset = reset.then(resetSuffix());
        reset = reset.then(resetAll());
        reset = reset.then(resetNickname());

        return reset;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetAll() {
        return Commands.literal("all").requires(src -> src.hasPermission(4))
                .then(Commands.argument("targetplayer", EntityArgument.players()).executes(ctx -> {
                    ServerPlayer player = EntityArgument.getPlayer(ctx, "targetplayer");

                    PlayerExtensionManager.changePlayerPrefix(player, PlayerExtensionManager.NULL_EXTENSION);
                    PlayerExtensionManager.changePlayerSuffix(player, PlayerExtensionManager.NULL_EXTENSION);
                    PlayerExtensionManager.changePlayerNickname(player, PlayerExtensionManager.NULL_EXTENSION);

                    sendMessage(ctx.getSource(),
                            Component.literal("Reset all for player " + ChatFormatting.GREEN + player.getName().getString())
                    );

                    return 0;
                }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetPrefix() {
        return Commands.literal("prefix").requires(src -> src.hasPermission(4))
                .then(Commands.argument("targetplayer", EntityArgument.players()).executes(ctx -> {
                    ServerPlayer player = EntityArgument.getPlayer(ctx, "targetplayer");

                    PlayerExtensionManager.changePlayerPrefix(player, PlayerExtensionManager.NULL_EXTENSION);

                    sendMessage(ctx.getSource(),
                            Component.literal("Reset prefix for player " + ChatFormatting.GREEN + player.getName().getString())
                    );

                    return 0;
                }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetSuffix() {
        return Commands.literal("suffix").requires(src -> src.hasPermission(4))
                .then(Commands.argument("targetplayer", EntityArgument.players()).executes(ctx -> {
                    ServerPlayer player = EntityArgument.getPlayer(ctx, "targetplayer");

                    PlayerExtensionManager.changePlayerSuffix(player, PlayerExtensionManager.NULL_EXTENSION);

                    sendMessage(ctx.getSource(),
                            Component.literal("Reset suffix for player " + ChatFormatting.GREEN + player.getName().getString())
                    );

                    return 0;
                }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetPersonalAll() {
        return Commands.literal("customall").executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();

            PlayerExtensionManager.changePlayerPrefix(player, PlayerExtensionManager.NULL_EXTENSION);
            PlayerExtensionManager.changePlayerSuffix(player, PlayerExtensionManager.NULL_EXTENSION);

            sendMessage(ctx.getSource(), Component.literal("Reset all successfully"));

            return 0;
        });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetPersonalPrefix() {
        return Commands.literal("customprefix").executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();

            PlayerExtensionManager.changePlayerPrefix(player, PlayerExtensionManager.NULL_EXTENSION);

            sendMessage(ctx.getSource(), Component.literal( "Reset prefix successfully"));

            return 0;
        });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetPersonalSuffix() {
        return Commands.literal("customsuffix").executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();

            PlayerExtensionManager.changePlayerSuffix(player, PlayerExtensionManager.NULL_EXTENSION);

            sendMessage(ctx.getSource(), Component.literal("Reset suffix successfully"));

            return 0;
        });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetNickname() {
        return Commands.literal("nickname").requires(src -> src.hasPermission(4))
                .then(Commands.argument("targetplayer", EntityArgument.players()).executes(ctx -> {
                    ServerPlayer player = EntityArgument.getPlayer(ctx, "targetplayer");

                    PlayerExtensionManager.changePlayerNickname(player, PlayerExtensionManager.NULL_EXTENSION);
                    sendMessage(ctx.getSource(),
                            Component.literal("Nickname reset succesfully for player " + ChatFormatting.GREEN + player.getName().getString())
                    );
                    return 0;
                }
                ));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> change() {
        LiteralArgumentBuilder<CommandSourceStack> change = Commands.literal("change");

        boolean allowNoOp = ChatExtConfig.ALLOW_NOOP_PREFIXES.get();
        if (allowNoOp) {
            change = change.then(changePersonalPrefix());
            change = change.then(changePersonalSuffix());
        }

        change = change.then(changePrefix());
        change = change.then(changeSuffix());
        change.then(changeNickname());

        return change;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> changePrefix() {
        return Commands.literal("prefix").requires(src -> src.hasPermission(4)).then(
                Commands.argument("targetplayer", EntityArgument.players())
                .then(Commands.argument("prefix", StringArgumentType.string()).executes(ctx -> {
                    String prefix = StringArgumentType.getString(ctx, "prefix");
                    ServerPlayer player = EntityArgument.getPlayer(ctx, "targetplayer");

                    PlayerExtensionManager.changePlayerPrefix(player, prefix);

                    sendMessage(ctx.getSource(),
                            Component.literal("Changed prefix for player " + ChatFormatting.GREEN + player.getName().getString())
                    );

                    return 0;
                }))
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> changeSuffix() {
        return Commands.literal("suffix").requires(src -> src.hasPermission(4)).then(
                Commands.argument("targetplayer", EntityArgument.players())
                        .then(Commands.argument("suffix", StringArgumentType.string()).executes(ctx -> {
                            String suffix = StringArgumentType.getString(ctx, "suffix");
                            ServerPlayer player = EntityArgument.getPlayer(ctx, "targetplayer");

                            PlayerExtensionManager.changePlayerSuffix(player, suffix);

                            sendMessage(ctx.getSource(),
                                    Component.literal("Changed suffix for player " + ChatFormatting.GREEN + player.getName().getString())
                            );

                            return 0;
                        }))
        );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> changeNickname() {
        return Commands.literal("nickname").requires(src -> src.hasPermission(4)).then(
                Commands.argument("targetplayer", EntityArgument.players())
                        .then(Commands.argument("nickname", MessageArgument.message()).executes(ctx -> {
                            String nickname = MessageArgument.getMessage(ctx, "nickname").getString();
                            ServerPlayer player = EntityArgument.getPlayer(ctx, "targetplayer");

                            PlayerExtensionManager.changePlayerNickname(player, nickname);
                            sendMessage(ctx.getSource(),
                                    Component.literal("Changed nickname for player " + ChatFormatting.GREEN + player.getName().getString())
                            );

                            return 0;
                        })
        ));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> changePersonalPrefix() {
        return Commands.literal("customprefix").then(
                Commands.argument("prefix", StringArgumentType.string()).executes(ctx -> {
                    String prefix = StringArgumentType.getString(ctx, "prefix");
                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                    PlayerExtensionManager.changePlayerPrefix(player, prefix);

                    sendMessage(ctx.getSource(), Component.literal("Changed prefix successfully"));

                    return 0;
                })
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> changePersonalSuffix() {
        return Commands.literal("customsuffix").then(
                Commands.argument("suffix", StringArgumentType.string()).executes(ctx -> {
                    String suffix = StringArgumentType.getString(ctx, "suffix");
                    ServerPlayer player = ctx.getSource().getPlayerOrException();

                    PlayerExtensionManager.changePlayerSuffix(player, suffix);

                    sendMessage(ctx.getSource(), Component.literal("Reset suffix successfully"));

                    return 0;
                })
        );
    }

    private static void sendMessage(CommandSourceStack src, MutableComponent message) throws CommandSyntaxException {
        if (src.isPlayer()) {
            src.getPlayerOrException().sendSystemMessage(Component.literal(ChatFormatting.GOLD + "[" + ChatFormatting.DARK_GREEN + "ChatExt" + ChatFormatting.GOLD + "] "
                    ).append(ChatFormatting.DARK_GREEN + message.getString()));
        } else {
            src.getServer().sendSystemMessage(message);
        }
    }
}
