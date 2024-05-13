package de.turtleboi.fancyformat.util;

public class Util {
    private Util() { }

    public static boolean equalsAny(char c, char... chars) {
        for (char c1 : chars)
            if (c == c1)
                return true;
        return false;
    }
}
