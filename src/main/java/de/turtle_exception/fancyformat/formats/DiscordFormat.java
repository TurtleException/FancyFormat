package de.turtle_exception.fancyformat.formats;

import de.turtle_exception.fancyformat.Format;
import de.turtle_exception.fancyformat.buffers.DiscordBuffer;
import de.turtle_exception.fancyformat.builders.DiscordBuilder;
import org.jetbrains.annotations.NotNull;

public class DiscordFormat extends Format<String> {
    private static final DiscordFormat INSTANCE = new DiscordFormat();

    public DiscordFormat() {
        super("DISCORD", String.class, DiscordBuffer::new, DiscordBuilder::new, s -> s, s -> s);
    }

    public static @NotNull DiscordFormat get() {
        return INSTANCE;
    }
}
