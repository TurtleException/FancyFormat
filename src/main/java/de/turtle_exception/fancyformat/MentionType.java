package de.turtle_exception.fancyformat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MentionType {
    USER(1),
    USER_ALT(2),
    CHANNEL(1),
    ROLE(2),
    SLASH_COMMAND(1),
    /** Unsupported */
    STANDARD_EMOJI(0),
    CUSTOM_EMOJI(1),
    CUSTOM_EMOJI_ANIMATED(2),
    UNIX_TIMESTAMP(2),
    UNIX_TIMESTAMP_STYLED(2);

    private final int prefixLength;

    MentionType(int prefixLength) {
        this.prefixLength = prefixLength;
    }

    public int getPrefixLength() {
        return prefixLength;
    }

    /* - - - */

    public static @Nullable MentionType fromRawMention(@NotNull String str) {
        if (str.startsWith("t:"))
            return (str.lastIndexOf(":") != 1)
                    ? UNIX_TIMESTAMP_STYLED
                    : UNIX_TIMESTAMP;
        if (str.startsWith(":"))
            return CUSTOM_EMOJI;
        if (str.startsWith("a:"))
            return CUSTOM_EMOJI_ANIMATED;
        if (str.startsWith("/"))
            return SLASH_COMMAND;
        if (str.startsWith("@&"))
            return ROLE;
        if (str.startsWith("#"))
            return CHANNEL;
        if (str.startsWith("@!"))
            return USER_ALT;
        if (str.startsWith("@"))
            return USER;
        return null;
    }
}