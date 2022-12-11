package de.turtle_exception.fancyformat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

public class FancyFormatter {
    // TODO: is this guaranteed to be a char?
    private char minecraftFormattingCode = '§';

    private Gson gson = null;

    public FancyFormatter() { }

    /* - CREATE TEXT - */

    public @NotNull FormatText newText(@NotNull String content, @NotNull Format format) {
        return new FormatText(this, content, format);
    }

    public @NotNull FormatText ofNative(@NotNull String nativeText) {
        for (Format value : Format.values())
            if (nativeText.startsWith(value.getCode() + "#"))
                return this.newText(nativeText.substring((value.getCode() + "#").length()), value);
        return this.newText(nativeText, Format.PLAINTEXT);
    }

    /* - INTERNAL - */

    @NotNull Gson getGson() {
        if (gson == null)
            gson = new GsonBuilder().create();
        return gson;
    }

    /* - SETTINGS & RULES - */

    public char getMinecraftFormattingCode() {
        return minecraftFormattingCode;
    }

    public FancyFormatter setMinecraftFormattingCode(char minecraftFormattingCode) {
        this.minecraftFormattingCode = minecraftFormattingCode;
        return this;
    }
}
