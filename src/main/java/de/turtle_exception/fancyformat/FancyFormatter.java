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

/**
 * Main class of the FancyFormat library. A FancyFormatter can create {@link FormatText FormatTexts} and manages basic
 * rules regarding formatting in cases where the syntax of a format does not satisfy the requirements for translating
 * to that format. It is not required to specify any rules before creating FormatTexts, but by not doing so any
 * edge-cases like Discord mentions will not be formatted and will instead show as raw text.
 * @see FancyFormatter#newText(String, Format)
 */
@SuppressWarnings("unused")
public class FancyFormatter {
    private char minecraftFormattingCode = '§';

    private BiFunction<MentionType, String, String> mentionAliasProvider;

    private @NotNull List<VisualStyle> codeBlockStyles = new ArrayList<>();
    private @NotNull List<VisualStyle>   mentionStyles = new ArrayList<>();
    private @NotNull List<VisualStyle>     quoteStyles = new ArrayList<>();

    private Gson gson = null;

    /** Creates a new FancyFormatter with default settings. */
    public FancyFormatter() {
        this.setMentionAliasProvider(null);
    }

    /* - CREATE TEXT - */

    /**
     * Creates a new {@link FormatText} object with the provided raw content ({@code content}) and {@link Format}
     * ({@code format}). The formatted String will be parsed into an AST and can then be translated to any format by
     * calling {@link FormatText#toString(Format)}. Any edge-cases when parsing to a formatted String are handled
     * according to the rules set by this {@link FancyFormatter}. These rules can still be changed by changing them on
     * this FancyFormatter instance.
     * @param content The raw content of a formatted text.
     * @param format The initial format of {@code content}.
     * @return A {@link FormatText} instance representing the provided message.
     */
    public @NotNull FormatText newText(@NotNull String content, @NotNull Format<?> format) {
        return new FormatText(this, content, format);
    }

    /**
     * Attempts to parse the provided String to a FormatText as a native text. Native text is a special format that is
     * not defined by {@link Format}. It can be used to store messages in their initial format and minimal additional
     * information as to what format that is, so the message can be stored as a String and without any further data, but
     * can still be parsed into a {@link FormatText} later.
     * <br> The syntax of native text is not guaranteed to stay unchanged across major versions. A native string may be
     * incompatible in later versions.
     * @param nativeText Native representation of a message.
     * @return A {@link FormatText} instance representing the provided message.
     */
    public @NotNull FormatText ofNative(@NotNull String nativeText) {
        for (Format<?> value : Format.values())
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

    /**
     * Returns the character that is used as the format indicator when (de-)serializing Minecraft legacy text.
     * <br> This is the section sign ({@code §}) by default. This used to be {@code &} in an older version of Minecraft
     * and can still be changed by some server versions. Many Spigot plugins also still use {@code &} or other custom
     * characters to store messages in config or log files.
     * @return Minecraft legacy formatting code.
     */
    public char getMinecraftFormattingCode() {
        return minecraftFormattingCode;
    }

    /**
     * Sets the character that is used as the format indicator when (de-)serialising Minecraft legacy text to {@code c}.
     * <br> This is the section sign ({@code §}) by default. This used to be {@code &} in an older version of Minecraft
     * and can still be changed by some server versions. Many Spigot plugins also still use {@code &} or other custom
     * characters to store messages in config or log files.
     * @param c Minecraft legacy formatting code.
     * @return This FancyFormatter for chaining convenience.
     */
    public FancyFormatter setMinecraftFormattingCode(char c) {
        this.minecraftFormattingCode = c;
        return this;
    }

    /**
     * Returns the {@link BiFunction} that turns mention information into a human-readable String. The default function
     * simply returns the raw id.
     * @return BiFunction that provides an alias for some mention information.
     * @see FancyFormatter#setMentionAliasProvider(BiFunction)
     */
    public @NotNull BiFunction<MentionType, String, String> getMentionAliasProvider() {
        return mentionAliasProvider;
    }

    /**
     * This provides a way to parse mentions to more human-readable Strings. The provided MentionType and String as
     * arguments of {@link BiFunction#apply(Object, Object)} will never be {@code null}.
     * <br> An example for {@code JDA} would be:
     * <pre> {@code
     * (type, id) -> {
     *     // this is NOT NULL-SAFE!!
     *     return switch (type) {
     *         case USER, USER_ALT -> jda.getUserById(id).getAsTag();
     *         case CHANNEL -> jda.getChannelById(Channel.class, id).getName();
     *         case ROLE -> jda.getRoleById(id).getName();
     *         // you should probably handle this differently
     *         case CUSTOM_EMOJI, CUSTOM_EMOJI_ANIMATED -> jda.getEmojiById(id).getFormatted();
     *         // other types are not mentionable
     *         default -> id;
     *     }
     * }
     * } </pre>
     * @param mentionAliasProvider A function that takes in a MentionType and the content of a mention and returns the
     *                             text that should be displayed to represent that mention.
     * @return This FancyFormatter for chaining convenience.
     */
    @SuppressWarnings("UnusedReturnValue")
    public FancyFormatter setMentionAliasProvider(@Nullable BiFunction<MentionType, String, String> mentionAliasProvider) {
        this.mentionAliasProvider = mentionAliasProvider == null
                ? ((mentionType, s) -> s)
                : mentionAliasProvider;
        return this;
    }

    /**
     * Returns the List of {@link VisualStyle VisualStyles} that are applied to a text that would be a code block, but
     * cannot be displayed as such in some format. Styles are applied in the order of the List.
     * @return Styles that function as stand-in if a code block cannot be displayed.
     */
    public @NotNull List<VisualStyle> getCodeBlockStyles() {
        return List.copyOf(codeBlockStyles);
    }

    /**
     * Sets the List of {@link VisualStyle VisualStyles} that are applied to a text that would be a code block, but
     * cannot be displayed as such in some format. Styles are applied in the order of the List.
     * @param styles Styles that function as stand-in if a code block cannot be displayed (may be {@code null}).
     * @return This FancyFormatter for chaining convenience.
     */
    public FancyFormatter setCodeBlockStyles(List<VisualStyle> styles) {
        this.codeBlockStyles = new ArrayList<>(styles != null ? styles : List.of());
        return this;
    }

    /**
     * Sets the List of {@link VisualStyle VisualStyles} that are applied to a text that would be a code block, but
     * cannot be displayed as such in some format. Styles are applied in the order they are provided in.
     * @param styles Styles that function as stand-in if a code block cannot be displayed.
     * @return This FancyFormatter for chaining convenience.
     */
    public FancyFormatter setCodeStyles(@NotNull VisualStyle... styles) {
        return this.setCodeBlockStyles(Arrays.asList(styles));
    }

    /**
     * Returns the List of {@link VisualStyle VisualStyles} that are applied to a text that would be a stylized mention,
     * but cannot be displayed as such in some format. Styles are applied in the order of the List.
     * @return Styles that function as stand-in if a mention cannot be stylized.
     * @see FancyFormatter#getMentionAliasProvider()
     */
    public @NotNull List<VisualStyle> getMentionStyles() {
        return List.copyOf(mentionStyles);
    }

    /**
     * Sets the List of {@link VisualStyle VisualStyles} that are applied to a text that would be a stylized mention,
     * but cannot be displayed as such in some format. Styles are applied in the order of the List.
     * @param styles Styles that function as stand-in if a stylized mention cannot be displayed (may be {@code null}).
     * @return This FancyFormatter for chaining convenience.
     */
    public FancyFormatter setMentionStyles(List<VisualStyle> styles) {
        this.mentionStyles = new ArrayList<>(styles != null ? styles : List.of());
        return this;
    }

    /**
     * Sets the List of {@link VisualStyle VisualStyles} that are applied to a text that would be a stylized mention,
     * but cannot be displayed as such in some format. Styles are applied in the order they are provided in.
     * @param styles Styles that function as stand-in if a stylized mention cannot be displayed.
     * @return This FancyFormatter for chaining convenience.
     */
    public FancyFormatter setMentionStyles(@NotNull VisualStyle... styles) {
        return this.setMentionStyles(Arrays.asList(styles));
    }

    /**
     * Returns the List of {@link VisualStyle VisualStyles} that are applied to a text that would be a stylized quote,
     * but cannot be displayed as such in some format. Styles are applied in the order of the List.
     * @return Styles that function as stand-in if a stylized quote cannot be displayed.
     */
    public @NotNull List<VisualStyle> getQuoteStyles() {
        return List.copyOf(quoteStyles);
    }

    /**
     * Sets the List of {@link VisualStyle VisualStyles} that are applied to a text that would be a stylized quote, but
     * cannot be displayed as such in some format. Styles are applied in the order of the List.
     * @param styles Styles that function as stand-in if a stylized quote cannot be displayed (may be {@code null}).
     * @return This FancyFormatter for chaining convenience.
     */
    public FancyFormatter setQuoteStyles(List<VisualStyle> styles) {
        this.quoteStyles = new ArrayList<>(styles != null ? styles : List.of());
        return this;
    }

    /**
     * Sets the List of {@link VisualStyle VisualStyles} that are applied to a text that would be a stylized quote, but
     * cannot be displayed as such in some format. Styles are applied in the order they are provided in.
     * @param styles Styles that function as stand-in if a stylized quote cannot be displayed.
     * @return This FancyFormatter for chaining convenience.
     */
    public FancyFormatter setQuoteStyles(@NotNull VisualStyle... styles) {
        return this.setQuoteStyles(Arrays.asList(styles));
    }
}
