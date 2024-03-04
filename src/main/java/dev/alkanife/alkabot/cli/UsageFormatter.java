package dev.alkanife.alkabot.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * I know that jCommander has its own usage formatter with IUsageFormatter.
 * However, I prefer doing it this way to keep it simple.
 */
@AllArgsConstructor
public class UsageFormatter {

    private final JCommander jCommander;

    public void printUsage() {
        System.out.println("Usage: java -jar Alkabot.jar [options]");
        System.out.println();
        System.out.println("Options:");

        for (Param param : formatParameters()) {
            System.out.println("   " + param.name + "   " + param.description + param.getDefaultValue());
        }
    }

    private List<Param> formatParameters() {
        int nameWidth = calculateNameWidth();
        HashMap<Integer, Param> orderMap = new HashMap<>();

        for (ParameterDescription p : jCommander.getParameters()) {
            Param param = new Param();
            param.setName(formatName(nameWidth, p.getNames()));
            param.setDescription(p.getDescription());
            param.setDefaultValue(formatDefault(p.getDefault()));

            orderMap.put(p.getParameter().order(), param);
        }

        List<Param> params = new ArrayList<>();

        for (int i = 1; i <= orderMap.size(); i++) {
            params.add(orderMap.get(i));
        }

        return params;
    }

    private int calculateNameWidth() {
        int width = 0;

        for (ParameterDescription p : jCommander.getParameters()) {
            if (p.getNames().length() > width) {
                width = p.getNames().length();
            }
        }

        return width;
    }

    private String formatName(int width, String name) {
        StringBuilder nameBuilder = new StringBuilder(name);

        while (nameBuilder.length() < width)
            nameBuilder.append(" ");

        return nameBuilder.toString();
    }

    private String formatDefault(Object obj) {
        if (obj instanceof String) {
            return " (default: '" + obj + "')";
        } else if (obj instanceof Integer) {
            return " (default: " + obj + ")";
        } else {
            return "";
        }
    }

    @Data
    static class Param {
        private String name, description, defaultValue;
    }
}
