package de.turtle_exception.fancyformat.styles;

import de.turtle_exception.fancyformat.Style;
import org.jetbrains.annotations.NotNull;

public enum FormatStyle implements Style {
    BOLD(         "**", "bold"         , 'l'),
    ITALICS(      "*" , "italic"       , 'o'),
    // this has to be differentiated or overlapping formats would cancel out in Discord md ("aaa *bbb _ccc* ddd_ eee")
    ITALICS_ALT(  "_" , "italic"       , 'o'),
    UNDERLINE(    "__", "underline"    , 'n'),
    STRIKETHROUGH("~~", "strikethrough", 'm'),
    SPOILER(      "||", "obfuscated"   , 'k');

    private final String wrapper;
    private final String name;
    private final char code;

    FormatStyle(String wrapper, String name, char code) {
        this.wrapper = wrapper;
        this.name = name;
        this.code = code;
    }

    public @NotNull String getWrapper() {
        return wrapper;
    }

    public @NotNull String getName() {
        return name;
    }

    public char getCode() {
        return code;
    }
}
