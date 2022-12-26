package de.turtle_exception.fancyformat.formats;

import de.turtle_exception.fancyformat.Format;
import de.turtle_exception.fancyformat.buffers.MinecraftLegacyBuffer;
import de.turtle_exception.fancyformat.builders.MinecraftLegacyBuilder;
import org.jetbrains.annotations.NotNull;

public class MinecraftLegacyFormat extends Format<String> {
    private static final MinecraftLegacyFormat INSTANCE = new MinecraftLegacyFormat();

    public MinecraftLegacyFormat() {
        super("MINECRAFT_LEGACY", String.class, MinecraftLegacyBuffer::new, MinecraftLegacyBuilder::new, s -> s, s -> s);
    }

    public static @NotNull MinecraftLegacyFormat get() {
        return INSTANCE;
    }
}
