package dev.alkanife.alkabot.configuration.json.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortcutCommandConfig {

    private boolean bind, unbind, list, info;
}
