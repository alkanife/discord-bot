package fr.alkanife.alkabot.notification;

import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.configuration.json.notifications.MemberNotifConfig;
import fr.alkanife.alkabot.utils.Colors;
import fr.alkanife.alkabot.utils.NotifUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public class MemberNotification extends AbstractNotification {

    private final MemberNotifConfig jsonNotificationsMember;

    public MemberNotification(NotificationManager notificationManager) {
        super(notificationManager, NotificationChannel.MEMBER);
        jsonNotificationsMember = Alkabot.getConfig().getNotifications().getMember();
    }

    public void notifyJoin(GuildMemberJoinEvent event, boolean failWelcome, boolean failAutorole) {
        if (!jsonNotificationsMember.isJoin())
            return;

        User user = event.getMember().getUser();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder = NotifUtils.addUserAvatar(embedBuilder, user);
        embedBuilder.setTitle(Alkabot.t("notification.member.join.title"));
        embedBuilder.addField(Alkabot.t("notification.generic.member"), NotifUtils.notifUser(user), true);

        if (failAutorole || failWelcome) {
            embedBuilder.setColor(Colors.ORANGE);

            StringBuilder stringBuilder = new StringBuilder();
            if (failWelcome)
                stringBuilder.append(Alkabot.t("notification.member.join.fail.welcome"));
            if (failAutorole)
                stringBuilder.append("\n").append(Alkabot.t("notification.member.join.fail.autorole"));
            embedBuilder.setDescription(stringBuilder.toString());
        } else {
            embedBuilder.setColor(Colors.GREEN);
        }

        getNotificationManager().sendNotification(getNotificationChannel(), embedBuilder.build());
    }

    public void notifyLeave(MessageEmbed messageEmbed) {
        if (!jsonNotificationsMember.isLeave())
            return;

        // In MemberListener

        getNotificationManager().sendNotification(getNotificationChannel(), messageEmbed);
    }

}

