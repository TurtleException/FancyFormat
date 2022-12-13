package de.turtle_exception.fancyformat.styles;

import org.jetbrains.annotations.NotNull;

/** A visual format that is not a {@link Color}. <i>(Like a typeface but without the font part...)</i> */
public enum FormatStyle implements VisualStyle {
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

    /** Provides the corresponding wrapper indicators. This is equal to the Discord markdown characters. */
    public @NotNull String getWrapper() {
        return wrapper;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public char getCode() {
        return code;
    }
}
