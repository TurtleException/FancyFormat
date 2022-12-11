package de.turtle_exception.fancyformat.styles;

import de.turtle_exception.fancyformat.Style;

// TODO: rename
public enum WrapperStyle implements Style {
    BOLD(         "**", "bold"         , 'l'),
    ITALICS(      "*" , "italic"       , 'o'),
    // this has to be differentiated or overlapping formats would cancel out in Discord md ("aaa *bbb _ccc* ddd_ eee")
    ITALICS_ALT(  "_" , "italic"       , 'o'),
    UNDERLINE(    "__", "underline"    , 'n'),
    STRIKETHROUGH("~~", "strikethrough", 'm'),
    SPOILER(      "||", "obfuscated"   , 'k');

    private final String discordMdChars;
    private final String minecraftName;
    private final char minecraftCode;

    WrapperStyle(String discordMdChars, String minecraftName, char minecraftCode) {
        this.discordMdChars = discordMdChars;
        this.minecraftName = minecraftName;
        this.minecraftCode = minecraftCode;
    }

    public String getDiscordMdChars() {
        return discordMdChars;
    }

    public String getMinecraftName() {
        return minecraftName;
    }

    public char getMinecraftCode() {
        return minecraftCode;
    }
}
