package fr.alkanife.alkabot.music.data;

import com.google.gson.annotations.SerializedName;
import fr.alkanife.alkabot.music.Shortcut;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusicData {

    private int volume;
    @SerializedName("shortcuts")
    private List<Shortcut> shortcutList;

}
