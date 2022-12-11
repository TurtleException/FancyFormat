package de.turtle_exception.fancyformat.buffers;

import de.turtle_exception.fancyformat.Buffer;
import de.turtle_exception.fancyformat.Node;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaintextBuffer extends Buffer {
    public PlaintextBuffer(@NotNull Node parent, @NotNull String raw) {
        super(parent, raw);
    }

    @Override
    public @NotNull List<Node> parse() {
        return this.asText();
    }
}
