package dev.alkanife.alkabot.data.music;

import com.google.gson.annotations.SerializedName;
import dev.alkanife.alkabot.lang.Lang;
import dev.alkanife.alkabot.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shortcut {

    private String name, query;
    @SerializedName("creator_id")
    private String creatorId;
    @SerializedName("creation_time")
    private long creationTime;

    public String getClickableName() {
        if (isUrlQuery())
            return "[" + name + "](" + query + ")";
        else
            return name;
    }

    public String getClickableQuery() {
        if (isUrlQuery())
            return "[" + Lang.get("music.link_query") + "](" + query + ")";
        else
            return query;
    }

    public boolean isUrlQuery() {
        return StringUtils.isURL(query);
    }
}
