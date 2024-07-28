package dev.alkanife.alkabot.util.command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;

@AllArgsConstructor
public class CommandArranger {

    private final JCommander jCommander;

    public LinkedHashMap<String, String> getResult() {
        HashMap<Integer, ParameterDescription> orderMap = new HashMap<>();

        for (ParameterDescription p : jCommander.getParameters())
            orderMap.put(p.getParameter().order(), p);

        LinkedHashMap<String, String> commands = new LinkedHashMap<>();

        for (int i = 1; i <= orderMap.size(); i++) {
            ParameterDescription param = orderMap.get(i);

            if (param != null)
                commands.put(param.getNames(), param.getDescription());
        }

        return commands;
    }

}
