package fr.alkanife.alkabot.commands.admin;

import fr.alkanife.alkabot.commands.AbstractAdminCommand;
import fr.alkanife.alkabot.utils.MemoryUtils;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.management.ManagementFactory;
import java.time.Duration;

public class StatusCommand extends AbstractAdminCommand {

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getUsage() {
        return "status";
    }

    @Override
    public String getDescription() {
        return "Update & RAM usage";
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("```yaml\n[STATUS]\n\n");

        SelfUser selfUser = event.getJDA().getSelfUser();
        stringBuilder.append("Client: ").append(selfUser.getAsTag()).append(" [").append(selfUser.getId()).append("]\n");

        Duration duration = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
        String formattedElapsedTime = String.format("%d days, %02d hours, %02d minutes, %02d seconds",
                duration.toDaysPart(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
        stringBuilder.append("Uptime: ").append(formattedElapsedTime).append("\n\n");

        stringBuilder.append("Memory usage:\n")
                .append(" - Max: ").append(MemoryUtils.humanReadableByteCountBin(MemoryUtils.getMaxMemory())).append("\n")
                .append(" - Used: ").append(MemoryUtils.humanReadableByteCountBin(MemoryUtils.getUsedMemory())).append("\n")
                .append(" - Total: ").append(MemoryUtils.humanReadableByteCountBin(MemoryUtils.getTotalMemory())).append("\n")
                .append(" - Free: ").append(MemoryUtils.humanReadableByteCountBin(MemoryUtils.getFreeMemory()));

        stringBuilder.append("\n```");

        event.getMessage().reply(stringBuilder.toString()).queue();
    }
}
