package de.turtle_exception.fancyformat.styles;

import de.turtle_exception.fancyformat.Style;

/**
 * A code block is a highlighted part of a text that may not nest any formatting. This escapes all formatting indicators.
 */
public enum CodeBlock implements Style {
    /** Inline CodeBlocks are simply highlighted and prevent nested formatting. They are not separated by a newline. */
    INLINE,
    /** Block code is a CodeBlock that is separated by a newline. It may also support syntax-highlighting. */
    BLOCK
}
