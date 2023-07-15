package com.skinnydevi.serverchatext.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ChatExtConfig {
    private ChatExtConfig() {}

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_CHATEXT;
    public static final ForgeConfigSpec.BooleanValue CHAT_TIMESTAMP;
    public static final ForgeConfigSpec.BooleanValue ALLOW_PREFIX;
    public static final ForgeConfigSpec.BooleanValue ALLOW_SUFFIX;
    public static final ForgeConfigSpec.BooleanValue ALLOW_NOOP_CHANGE;
    public static final ForgeConfigSpec.ConfigValue<String> TIMESTAMP_FORMAT;
    public static final ForgeConfigSpec.ConfigValue<String> TIMESTAMP_COLOUR;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("Server Chat Extended configuration");
        ENABLE_CHATEXT = builder.comment("Enable this chat mod. Defaults to true").define("enable_chatext", true);
        CHAT_TIMESTAMP = builder.comment("Enable a timestamp besides the message sent to indicate the time when the message was sent. Defaults to true").define("enable_chat_timestamp", true);
        ALLOW_PREFIX = builder.comment("Allow setting a prefix in chat. Defaults to true").define("allow_prefix", true);
        ALLOW_SUFFIX = builder.comment("Allow setting a suffix in chat. Defaults to true").define("allow_suffix", true);
        ALLOW_NOOP_CHANGE = builder.comment("Allows players which are not OP to change their own prefix and suffix. Defaults to false").define("allow_noop_change", false);
        TIMESTAMP_FORMAT = builder.comment("Set the format of the timestmap represented on chat. \nExamples can be found here: https://www.digitalocean.com/community/tutorials/java-simpledateformat-java-date-format#java-simpledateformat \nDefaults to \"HH:mm\".").define("timestamp_format", "HH:mm");
        TIMESTAMP_COLOUR = builder.comment("Colour of the timestamp. Use minecraft colour codes with '&'. Examples: &c, &l, &5.\nDefaults to gray (&7).").define("timestamp_colour", "&7");

        SPEC = builder.build();
    }
}
