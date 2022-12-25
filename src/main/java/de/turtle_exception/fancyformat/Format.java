package de.turtle_exception.fancyformat;

import com.google.gson.JsonElement;
import de.turtle_exception.fancyformat.buffers.*;
import de.turtle_exception.fancyformat.builders.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

/** Each value of this enum represents a specific text format that is supported by this library. */
public final class Format<T> {
    /** Raw text that does not have any style or feature. */
    public static final Format<String> PLAINTEXT = new Format<>("PLAINTEXT", 0,
            PlaintextBuffer::new,
            node -> new PlaintextBuilder(node).build(),
            s -> s);
    /** A universal format that preserves every feature or style of every other format. */
    public static final Format<JsonElement> TURTLE = new Format<>("TURTLE", 1,
            TurtleBuffer::new,
            node -> new TurtleBuilder(node).build(),
            JsonElement::toString);
    /**
     * Discord Markdown format as specified by
     * <a href="https://support.discord.com/hc/en-us/articles/210298617">Discord Support: Markdown Text 101</a>.
     */
    public static final Format<String> DISCORD = new Format<>("DISCORD", 2,
            DiscordBuffer::new,
            node -> new DiscordBuilder(node).build(),
            s -> s);
    /**
     * Minecraft JSON format as specified by
     * <a href="https://minecraft.fandom.com/wiki/Raw_JSON_text_format">Minecraft Wiki: Raw JSON text format</a>.
     */
    public static final Format<JsonElement> MINECRAFT_JSON = new Format<>("MINECRAFT_JSON", 3,
            MinecraftJsonBuffer::new,
            node -> new MinecraftJsonBuilder(node).build(),
            JsonElement::toString);
    /**
     * Minecraft legacy formatting codes as specified by
     * <a href="https://minecraft.fandom.com/wiki/Formatting_codes">Minecraft Wiki: Formatting codes</a>.
     */
    public static final Format<String> MINECRAFT_LEGACY = new Format<>("MINECRAFT_LEGACY", 4,
            MinecraftLegacyBuffer::new,
            node -> new MinecraftLegacyBuilder(node).build(),
            s -> s);
    public static final Format<BaseComponent[]> SPIGOT_COMPONENTS = new Format<>("SPIGOT_FORMAT", 5,
            SpigotComponentsBuffer::new,
            node -> new SpigotComponentsBuilder(node).build(),
            components -> new TextComponent(components).toLegacyText());

    private final String name;
    private final int code;
    private final BiFunction<Node, T, Buffer<T>> mutatorStringToNode;
    private final   Function<Node, T>            mutatorNodeToObject;
    private final   Function<T   , String>       mutatorObjectToString;

    private Format(@NotNull String name, int code, BiFunction<Node, T, Buffer<T>> mutatorStringToNode, Function<Node, T> mutatorNodeToObject, Function<T, String> mutatorObjectToString) {
        this.name = name;
        this.code = code;
        this.mutatorStringToNode = mutatorStringToNode;
        this.mutatorNodeToObject = mutatorNodeToObject;
        this.mutatorObjectToString = mutatorObjectToString;
    }

    public @NotNull String getName() {
        return name;
    }

    /**
     * Provides a unique code representing this Format. Used for serialization in native text.
     * @return Unique code of this Format.
     */
    public int getCode() {
        return code;
    }

    public Buffer<T> newBuffer(@NotNull Node parent, @NotNull T raw) {
        return mutatorStringToNode.apply(parent, raw);
    }

    public Function<Node, T> getMutatorNodeToObject() {
        return mutatorNodeToObject;
    }

    public Function<T, String> getMutatorObjectToString() {
        return mutatorObjectToString;
    }

    /* - - - */

    public static @NotNull Format<?>[] values() {
        return new Format<?>[]{ PLAINTEXT, TURTLE, DISCORD, MINECRAFT_JSON, MINECRAFT_LEGACY };
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
