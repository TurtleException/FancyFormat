package de.turtle_exception.fancyformat.formats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.turtle_exception.fancyformat.Format;
import de.turtle_exception.fancyformat.buffers.TurtleBuffer;
import de.turtle_exception.fancyformat.builders.TurtleBuilder;
import org.jetbrains.annotations.NotNull;

public class TurtleFormat extends Format<JsonElement> {
    private static final TurtleFormat INSTANCE = new TurtleFormat();

    private static final Gson gson = new GsonBuilder()
            .create();

    private TurtleFormat() {
        super("TURTLE", JsonElement.class, TurtleBuffer::new, TurtleBuilder::new, s -> gson.fromJson(s, JsonElement.class), JsonElement::toString);
    }

    public static @NotNull TurtleFormat get() {
        return INSTANCE;
    }
}
