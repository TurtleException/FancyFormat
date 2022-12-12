package de.turtle_exception.fancyformat.styles;

import org.jetbrains.annotations.NotNull;

public enum Color implements VisualStyle {
    BLACK(       '0', "black"       ,   0,   0,   0,   0,   0,   0),
    DARK_BLUE(   '1', "dark_blue"   ,   0,   0, 170,   0,   0,  42),
    DARK_GREEN(  '2', "dark_green"  ,   0, 170,   0,   0,  42,   0),
    DARK_AQUA(   '3', "dark_aqua"   ,   0, 170, 170,   0,  42,  42),
    DARK_RED(    '4', "dark_red"    , 170,   0,   0,  42,   0,   0),
    DARK_PURPLE( '5', "dark_purple" , 170,   0, 170,  42,   0,  42),
    GOLD(        '6', "gold"        , 255, 170,   0,  42,  42,   0),
    GRAY(        '7', "gray"        , 170, 170, 170,  42,  42,  42),
    DARK_GRAY(   '8', "dark_gray"   ,  85,  85,  85,  21,  21,  21),
    BLUE(        '9', "blue"        ,  85,  85, 255,  21,  21,  63),
    GREEN(       'a', "green"       ,  85, 255,  85,  21,  63,  21),
    AQUA(        'b', "aqua"        ,  85, 255, 255,  21,  63,  63),
    RED(         'c', "red"         , 255,  85,  85,  63,  21,  21),
    LIGHT_PURPLE('d', "light_purple", 255,  85, 255,  63,  21,  63),
    YELLOW(      'e', "yellow"      , 255, 255,  85,  63,  63,  21),
    WHITE(       'f', "white"       , 255, 255, 255,  63,  63,  63);

    private final char code;
    private final String name;

    // FOREGROUND COLOR
    private final int fr;
    private final int fg;
    private final int fb;

    // BACKGROUND COLOR
    private final int br;
    private final int bg;
    private final int bb;

    Color(char code, String name, int fr, int fg, int fb, int br, int bg, int bb) {
        this.code = code;
        this.name = name;
        this.fr = fr;
        this.fg = fg;
        this.fb = fb;
        this.br = br;
        this.bg = bg;
        this.bb = bb;
    }

    public char getCode() {
        return code;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public @NotNull String getMotdCode() {
        return "\u00A7" + this.code;
    }

    public String getName() {
        return name;
    }

    public int getFr() {
        return fr;
    }

    public int getFg() {
        return fg;
    }

    public int getFb() {
        return fb;
    }

    public int getBr() {
        return br;
    }

    public int getBg() {
        return bg;
    }

    public int getBb() {
        return bb;
    }
}
