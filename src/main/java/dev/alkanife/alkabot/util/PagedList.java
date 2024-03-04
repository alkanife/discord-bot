package dev.alkanife.alkabot.util;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Getter
public class PagedList {

    private int pages;
    private int page;

    public boolean parsePage(SlashCommandInteractionEvent event, int listSize, String outOfRangeMessage) {
        if (!endsWithZero(listSize)) {
            for (int i = 0; i < 11; i++) {
                if (endsWithZero(listSize))
                    break;

                listSize++;
            }
        }

        pages = listSize / 10;

        OptionMapping pageOption = event.getOption("page");

        page = 0;

        if (pageOption != null)
            page = ((int) pageOption.getAsLong()) - 1;

        if (page < 0)
            page = 0;

        if ((page+1) >= pages) {
            event.getHook().sendMessage(outOfRangeMessage).queue();
            return false;
        }

        return true;
    }

    public String toStringList(IndexProcessor processor) {
        StringBuilder result = new StringBuilder();

        for (int i = (page * 10); i < ((page * 10) + 10); i++) {
            try {
                result.append(processor.process(i)).append("\n");
            } catch (Exception ignored) {
                break;
            }
        }

        return result.toString();
    }

    private boolean endsWithZero(int i) {
        return i % 10 == 0;
    }
}
