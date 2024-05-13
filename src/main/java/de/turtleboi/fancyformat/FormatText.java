package de.turtleboi.fancyformat;

import de.turtleboi.fancyformat.node.Node;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FormatText {
    private final Node root;

    /** Lazily populated cache of already parsed formats. */
    private final Map<Format<?>, Object> cachedFormats = new ConcurrentHashMap<>();

    public FormatText(@NotNull Node root) {
        this.root = root;
    }

    public <T> FormatText(T text, @NotNull Format<T> format) {
        this(format.parseRoot(text));
    }

    @SuppressWarnings("unchecked")
    public <T> T toFormat(@NotNull Format<T> format) {
        Object parsed = cachedFormats.computeIfAbsent(format, f -> f.parse(root));
        return ((T) parsed);
    }
}
