package fr.alkanife.alkabot.configuration;

import fr.alkanife.alkabot.Alkabot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.event.Level;

public class ConfigurationInitializer extends AbstractWorker {

    public ConfigurationInitializer(boolean reload) {
        super(reload);

        Alkabot.debug("Initializing configuration");

        // Check for guild
        Guild guild = Alkabot.getJda().getGuildById(Alkabot.getConfig().getGuild().getGuild_id());
        if (guild == null) {
            log(Level.ERROR, "The Discord guild '" + Alkabot.getConfig().getGuild().getGuild_id() + "' was not found");
            setStatus(Status.FAIL);
            return;
        }
        Alkabot.debug("Guild: " + guild.getName());
        Alkabot.setGuild(guild);

        // Check for welcome message channel
        if (Alkabot.getConfig().getWelcome_message().isEnable()) {
            TextChannel textChannel = Alkabot.getJda().getTextChannelById(Alkabot.getConfig().getWelcome_message().getChannel_id());
            if (textChannel == null) {
                log(Level.WARN, "Disabling welcome messages because the channel '" + Alkabot.getConfig().getWelcome_message().getChannel_id() + "' was not found");
                Alkabot.getConfig().getWelcome_message().setEnable(false);
            } else {
                Alkabot.debug("Welcome message channel: " + textChannel.getName());
                Alkabot.setWelcomeMessageChannel(textChannel);
            }
        }

        // Check for welcome message channel
        if (Alkabot.getConfig().getAuto_role().isEnable()) {
            Role role = guild.getRoleById(Alkabot.getConfig().getAuto_role().getRole_id());
            if (role == null) {
                log(Level.WARN, "Disabling auto-role because the role '" + Alkabot.getConfig().getAuto_role().getRole_id() + "' was not found");
                Alkabot.getConfig().getAuto_role().setEnable(false);
            } else {
                Alkabot.debug("Auto-role: " + role.getName());
                Alkabot.setAutoRole(role);
            }
        }
    }
}
