package de.turtle_exception.fancyformat;

import de.turtle_exception.fancyformat.buffers.*;
import de.turtle_exception.fancyformat.builders.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

/** Each value of this enum represents a specific text format that is supported by this library. */
public enum Format {
    /** Raw text that does not have any style or feature. */
    PLAINTEXT(0,
            PlaintextBuffer::new,
            node -> new PlaintextBuilder(node).build()),
    /** A universal format that preserves every feature or style of every other format. */
    TURTLE(1,
            TurtleBuffer::new,
            node -> new TurtleBuilder(node).build()),
    /**
     * Discord Markdown format as specified by
     * <a href="https://support.discord.com/hc/en-us/articles/210298617">Discord Support: Markdown Text 101</a>.
     */
    DISCORD(2,
            DiscordBuffer::new,
            node -> new DiscordBuilder(node).build()),
    /**
     * Minecraft JSON format as specified by
     * <a href="https://minecraft.fandom.com/wiki/Raw_JSON_text_format">Minecraft Wiki: Raw JSON text format</a>.
     */
    MINECRAFT_JSON(3,
            MinecraftJsonBuffer::new,
            node -> new MinecraftJsonBuilder(node).build()),
    /**
     * Minecraft legacy formatting codes as specified by
     * <a href="https://minecraft.fandom.com/wiki/Formatting_codes">Minecraft Wiki: Formatting codes</a>.
     */
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

    /**
     * Provides a unique code representing this Format. Used for serialization in native text.
     * @return Unique code of this Format.
     */
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

    /**
     * Creates a native text String of this format and the provided message.
     * @param raw Message of this format.
     * @return Native text representation of {@code raw}.
     */
    @SuppressWarnings("unused")
    public @NotNull String toNative(@NotNull String raw) {
        return this.getCode() + "#" + raw;
    }
}
