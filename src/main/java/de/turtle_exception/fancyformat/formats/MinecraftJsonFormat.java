package de.turtle_exception.fancyformat.formats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.turtle_exception.fancyformat.Format;
import de.turtle_exception.fancyformat.buffers.MinecraftJsonBuffer;
import de.turtle_exception.fancyformat.builders.MinecraftJsonBuilder;
import org.jetbrains.annotations.NotNull;

public class MinecraftJsonFormat extends Format<JsonElement> {
    private static final MinecraftJsonFormat INSTANCE = new MinecraftJsonFormat();

    private static final Gson gson = new GsonBuilder()
            .create();

    private MinecraftJsonFormat() {
        super("MINECRAFT_JSON", JsonElement.class, MinecraftJsonBuffer::new, MinecraftJsonBuilder::new, s -> gson.fromJson(s, JsonElement.class), JsonElement::toString);
    }

    public static @NotNull MinecraftJsonFormat get() {
        return INSTANCE;
    }
}
