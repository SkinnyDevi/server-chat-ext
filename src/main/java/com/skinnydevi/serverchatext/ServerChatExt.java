package com.skinnydevi.serverchatext;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;

import com.skinnydevi.serverchatext.command.ChatExtCommand;
import com.skinnydevi.serverchatext.config.ChatExtConfig;

import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import org.slf4j.Logger;

@Mod(ServerChatExt.MODID)
public class ServerChatExt
{
    public static final String MODID = "serverchatext";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ServerChatExt() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ChatExtConfig.SPEC, "serverchatext-config.toml");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus= Mod.EventBusSubscriber.Bus.FORGE)
    public static class Commands {
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

            ChatExtCommand.register(dispatcher);
        }
    }
}
