package dev.alkanife.alkabot.configuration.json.commands;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusicCommandConfig {

    private boolean play, playnext, forceplay, remove, skip, stop, destroy, shuffle, clear, queue, nowplaying;
    @SerializedName("shortcut")
    private ShortcutCommandConfig shortcutCommandConfig;
}
