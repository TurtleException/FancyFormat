package de.turtle_exception.fancyformat.styles;

import de.turtle_exception.fancyformat.Style;

/** A quote is a highlighted (usually indented) part of a text. */
public enum Quote implements Style {
    /** A quote that will end with a newline ("{@code \n}") */
    SINGLE_LINE,
    /** A quote that will not end before the end of the text. */
    MULTI_LINE
}
