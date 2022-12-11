package de.turtle_exception.fancyformat;

public enum MentionPolicy {
    /** Doesn't attempt to display a mention; Keeps the raw text. */
    RAW,
    /** Attempts to display text as close as possible to the way it is displayed in the Discord client. */
    DISPLAY,
    /** Like {@link MentionPolicy#DISPLAY}, but this will display user tags. */
    EXTENDED
}
