package de.turtle_exception.fancyformat.formats;

import de.turtle_exception.fancyformat.Format;
import de.turtle_exception.fancyformat.buffers.SpigotComponentsBuffer;
import de.turtle_exception.fancyformat.builders.SpigotComponentsBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

public class SpigotComponentsFormat extends Format<BaseComponent[]> {
    private static final SpigotComponentsFormat INSTANCE = new SpigotComponentsFormat();

    private SpigotComponentsFormat() {
        super("SPIGOT_COMPONENTS", BaseComponent[].class, SpigotComponentsBuffer::new, SpigotComponentsBuilder::new);
    }

    public static @NotNull SpigotComponentsFormat get() {
        return INSTANCE;
    }
}
