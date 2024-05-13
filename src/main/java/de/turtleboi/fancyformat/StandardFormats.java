package de.turtleboi.fancyformat;

import de.turtleboi.fancyformat.format.FancyFormat;
import de.turtleboi.fancyformat.format.PlainTextFormat;

public class StandardFormats {
    public static final PlainTextFormat PLAINTEXT = new PlainTextFormat();
    public static final     FancyFormat FANCY     = new FancyFormat();

    private StandardFormats() { }
}
