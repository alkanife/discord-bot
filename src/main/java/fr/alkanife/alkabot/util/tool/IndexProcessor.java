package fr.alkanife.alkabot.util.tool;

@FunctionalInterface
public interface IndexProcessor {
    String process(int index) throws Exception;
}