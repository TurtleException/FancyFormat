package de.turtle_exception.fancyformat.util;

@FunctionalInterface
public interface ExceptionalFunction<T, R> {
    R apply(T t) throws Exception;
}
