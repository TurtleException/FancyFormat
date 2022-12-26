package de.turtle_exception.fancyformat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Format<T> {
    private final String name;
    private final Class<T> type;
    private final BiFunction<Node, T, Buffer<T>> bufferProvider;
    private final Function<Node, MessageBuilder<T>> builderProvider;
    private final Function<String, T> stringParser;
    private final Function<T, String> typeParser;

    public Format(
            @NotNull String name,
            @NotNull Class<T> type,
            @NotNull BiFunction<Node, T, Buffer<T>> bufferProvider,
            @NotNull Function<Node, MessageBuilder<T>> builderProvider,
            @Nullable Function<String, T> stringParser,
            @Nullable Function<T, String> typeParser
    ) {
        this.name = name;
        this.type = type;
        this.bufferProvider = bufferProvider;
        this.builderProvider = builderProvider;
        this.stringParser = stringParser;
        this.typeParser = typeParser;
    }

    public Format(
            @NotNull String name,
            @NotNull Class<T> type,
            @NotNull BiFunction<Node, T, Buffer<T>> bufferProvider,
            @NotNull Function<Node, MessageBuilder<T>> builderProvider
    ) {
        this(name, type, bufferProvider, builderProvider, null, null);
    }

    /* - - - */

    public @NotNull Buffer<T> newBuffer(@NotNull Node parent, @NotNull T raw) {
        return bufferProvider.apply(parent, raw);
    }

    public @NotNull MessageBuilder<T> newBuilder(@NotNull Node node) {
        return builderProvider.apply(node);
    }

    /* - - - */

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Class<T> getType() {
        return type;
    }

    /* - - - */

    public @NotNull T makeObject(@NotNull String str) throws UnsupportedOperationException {
        if (stringParser == null)
            throw new UnsupportedOperationException("Cannot parse String to object.");
        return stringParser.apply(str);
    }

    public @NotNull String makeString(@NotNull T t) throws UnsupportedOperationException {
        if (typeParser == null)
            throw new UnsupportedOperationException("Cannot parse object to String.");
        return typeParser.apply(t);
    }
}
