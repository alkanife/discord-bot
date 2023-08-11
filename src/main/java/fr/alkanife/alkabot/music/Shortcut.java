package fr.alkanife.alkabot.music;

import com.google.gson.annotations.SerializedName;
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
    @SerializedName("creation_date")
    private Date creationDate;
}
