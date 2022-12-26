package de.turtle_exception.fancyformat.formats;

import de.turtle_exception.fancyformat.Format;
import de.turtle_exception.fancyformat.buffers.PlaintextBuffer;
import de.turtle_exception.fancyformat.builders.PlaintextBuilder;
import org.jetbrains.annotations.NotNull;

public class PlaintextFormat extends Format<String> {
    private static final PlaintextFormat INSTANCE = new PlaintextFormat();

    public PlaintextFormat() {
        super("PLAINTEXT", String.class, PlaintextBuffer::new, PlaintextBuilder::new, s -> s, s -> s);
    }

    public static @NotNull PlaintextFormat get() {
        return INSTANCE;
    }
}
