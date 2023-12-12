package dev.alkanife.alkabot.configuration.json.notifications;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModNotifConfig {

    @SerializedName("channel_id")
    private String channelId;

    private boolean ban, unban, kick, timeout;

    @SerializedName("deafen_member")
    private boolean deafenMember;

    @SerializedName("undeafen_member")
    private boolean undeafenMember;

    @SerializedName("mute_member")
    private boolean muteMember;

    @SerializedName("unmute_member")
    private boolean unmuteMember;

    @SerializedName("change_member_nickname")
    private boolean changeMemberNickname;
}
