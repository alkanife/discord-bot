package dev.alkanife.alkabot.discord;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.lang.Lang;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class WelcomeMessageManager {

    @Getter
    private final Alkabot alkabot;

    @Getter @Setter
    private TextChannel textChannel;

    public WelcomeMessageManager(Alkabot alkabot) {
        this.alkabot = alkabot;
    }

    public boolean loadChannel() {
        if (alkabot.getConfig().getWelcomeMessageConfig().isEnable()) {
            textChannel = alkabot.getJda().getTextChannelById(alkabot.getConfig().getWelcomeMessageConfig().getChannelId());

            if (textChannel == null) {
                alkabot.getLogger().warn("The welcome message channel was not found!");
                return false;
            } else {
                alkabot.getLogger().debug("Welcome message channel: {}", textChannel.getName());
                return true;
            }
        }

        return true;
    }

    public boolean welcomeMember(Member member) {
        try {
            if (alkabot.getConfig().getWelcomeMessageConfig().isEnable()) {
                if (textChannel == null) {
                    alkabot.getLogger().warn("Could not to send welcome message for {}: the channel was not found!", member.getEffectiveName());
                    return false;
                } else {
                    textChannel.sendMessage(
                            Lang.t("welcome_messages")
                                    .parseMemberMention(member)
                                    .parseMemberNames(member)
                                    .getValue()
                    ).queue();
                }
            }

            return true;
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to send welcome message for {}", member.getEffectiveName());
            alkabot.getLogger().debug("Full trace:", exception);
            return false;
        }
    }
}