package de.turtle_exception.fancyformat.util;

@FunctionalInterface
public interface ExceptionalRunnable {
    void run() throws Exception;
}
