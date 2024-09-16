package dev.alkanife.alkabot.discord;

import dev.alkanife.alkabot.Alkabot;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;

public class GuildManager {

    @Getter
    private final Alkabot alkabot;

    @Getter @Setter
    private Guild guild;

    public GuildManager(Alkabot alkabot) {
        this.alkabot = alkabot;
    }

    public boolean loadGuild() {
        try {
            Guild guild = alkabot.getJda().getGuildById(alkabot.getConfig().getGuildConfig().getGuildId());

            if (guild == null) {
                alkabot.getLogger().error("The Discord guild '{}' was not found", alkabot.getConfig().getGuildConfig().getGuildId());
                return false;
            }

            this.guild = guild;

            return true;
        } catch (Exception exception) {
            alkabot.getLogger().error("Fatal: The given Discord guild ID is not valid.");
            alkabot.getLogger().debug("Full trace:", exception);
            return false;
        }
    }
}