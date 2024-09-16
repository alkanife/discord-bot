package dev.alkanife.alkabot.util;

@FunctionalInterface
public interface IndexProcessor {
    String process(int index) throws Exception;
}