package de.turtle_exception.fancyformat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.turtle_exception.fancyformat.styles.VisualStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public class FancyFormatter {
    // TODO: is this guaranteed to be a char?
    private char minecraftFormattingCode = '§';

    private BiFunction<MentionType, String, String> mentionAliasProvider;

    private @NotNull List<VisualStyle>    codeStyles = new ArrayList<>();
    private @NotNull List<VisualStyle> mentionStyles = new ArrayList<>();
    private @NotNull List<VisualStyle>   quoteStyles = new ArrayList<>();

    private Gson gson = null;

    public FancyFormatter() {
        this.setMentionAliasProvider(null);
    }

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

    public @NotNull BiFunction<MentionType, String, String> getMentionAliasProvider() {
        return mentionAliasProvider;
    }

    @SuppressWarnings("UnusedReturnValue")
    public FancyFormatter setMentionAliasProvider(@Nullable BiFunction<MentionType, String, String> mentionAliasProvider) {
        this.mentionAliasProvider = mentionAliasProvider == null
                ? ((mentionType, s) -> s)
                : mentionAliasProvider;
        return this;
    }

    public @NotNull List<VisualStyle> getCodeStyles() {
        return List.copyOf(codeStyles);
    }

    public FancyFormatter setCodeStyles(List<VisualStyle> styles) {
        this.codeStyles = new ArrayList<>(styles != null ? styles : List.of());
        return this;
    }

    public FancyFormatter setCodeStyles(@NotNull VisualStyle... styles) {
        return this.setCodeStyles(Arrays.asList(styles));
    }

    public @NotNull List<VisualStyle> getMentionStyles() {
        return List.copyOf(mentionStyles);
    }

    public FancyFormatter setMentionStyles(List<VisualStyle> styles) {
        this.mentionStyles = new ArrayList<>(styles != null ? styles : List.of());
        return this;
    }

    public FancyFormatter setMentionStyles(@NotNull VisualStyle... styles) {
        return this.setMentionStyles(Arrays.asList(styles));
    }

    public @NotNull List<VisualStyle> getQuoteStyles() {
        return List.copyOf(quoteStyles);
    }

    public FancyFormatter setQuoteStyles(List<VisualStyle> styles) {
        this.quoteStyles = new ArrayList<>(styles != null ? styles : List.of());
        return this;
    }

    public FancyFormatter setQuoteStyles(@NotNull VisualStyle... styles) {
        return this.setQuoteStyles(Arrays.asList(styles));
    }
}
