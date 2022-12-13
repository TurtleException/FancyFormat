package de.turtle_exception.fancyformat.styles;

import de.turtle_exception.fancyformat.Style;
import org.jetbrains.annotations.NotNull;

public interface VisualStyle extends Style {
    /** Provides the corresponding code to this style. The code is equal to the Minecraft legacy format code. */
    char getCode();

    /** Provides the name of this style. This is equal to the Minecraft name. */
    @NotNull String getName();

    /** Provides this style as an MOTD code. */
    @SuppressWarnings({"unused", "SpellCheckingInspection"})
    default @NotNull String getMotdCode() {
        return "\u00A7" + this.getCode();
    }
}
