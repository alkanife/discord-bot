package dev.alkanife.alkabot.discord;

import dev.alkanife.alkabot.Alkabot;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class AutoroleManager {

    @Getter
    private final Alkabot alkabot;

    @Getter @Setter
    private Role autoRole;

    public AutoroleManager(Alkabot alkabot) {
        this.alkabot = alkabot;
    }

    public boolean loadRole() {
        if (alkabot.getConfig().getAutoRoleConfig().isEnable()) {
            autoRole = alkabot.getGuildManager().getGuild().getRoleById(alkabot.getConfig().getAutoRoleConfig().getRoleId());

            if (autoRole == null) {
                alkabot.getLogger().warn("The Discord role for the auto-role was not found!");
                return false;
            } else {
                alkabot.getLogger().debug("Auto-role: {}", autoRole.getName());
                return true;
            }
        }

        return true;
    }

    public boolean applyRole(Member member) {
        try {
            if (alkabot.getConfig().getAutoRoleConfig().isEnable()) {
                if (autoRole == null) {
                    alkabot.getLogger().warn("Could not apply auto-role for {}: the Discord role was not found!", member.getEffectiveName());
                    return false;
                } else {
                    member.getGuild().modifyMemberRoles(member, autoRole).queue();
                }
            }

            return true;
        } catch (Exception exception) {
            alkabot.getLogger().error("Failed to auto-role {}!", member.getEffectiveName());
            alkabot.getLogger().debug("Full trace:", exception);
            return false;
        }
    }
}
