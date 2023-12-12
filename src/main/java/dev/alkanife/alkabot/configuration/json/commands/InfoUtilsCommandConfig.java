package dev.alkanife.alkabot.configuration.json.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoUtilsCommandConfig {

    private boolean server, member, emote;
}
