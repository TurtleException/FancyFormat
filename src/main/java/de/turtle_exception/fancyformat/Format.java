package de.turtle_exception.fancyformat;

import de.turtle_exception.fancyformat.buffers.*;
import de.turtle_exception.fancyformat.builders.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum Format {
    PLAINTEXT(0,
            PlaintextBuffer::new,
            node -> new PlaintextBuilder(node).build()),
    TURTLE(1,
            TurtleBuffer::new,
            node -> new TurtleBuilder(node).build()),
    DISCORD(2,
            DiscordBuffer::new,
            node -> new DiscordBuilder(node).build()),
    MINECRAFT_JSON(3,
            MinecraftJsonBuffer::new,
            node -> new MinecraftJsonBuilder(node).build()),
    MINECRAFT_LEGACY(4,
            MinecraftLegacyBuffer::new,
            node -> new MinecraftLegacyBuilder(node).build());

    private final int code;
    private final BiFunction<Node, String, Buffer> mutatorStringToNode;
    private final   Function<Node, String>         mutatorNodeToString;

    Format(int code, BiFunction<Node, String, Buffer> mutatorStringToNode, Function<Node, String> mutatorNodeToString) {
        this.code = code;
        this.mutatorStringToNode = mutatorStringToNode;
        this.mutatorNodeToString = mutatorNodeToString;
    }

    public int getCode() {
        return code;
    }

    public Buffer newBuffer(@NotNull Node parent, @NotNull String raw) {
        return mutatorStringToNode.apply(parent, raw);
    }

    public Function<Node, String> getMutatorNodeToString() {
        return mutatorNodeToString;
    }

    /* - - - */

    public @NotNull String toNative(@NotNull String raw) {
        return this.getCode() + "#" + raw;
    }
}
