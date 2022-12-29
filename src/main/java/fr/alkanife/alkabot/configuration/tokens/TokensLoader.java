package fr.alkanife.alkabot.configuration.tokens;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.alkanife.alkabot.Alkabot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TokensLoader {

    private Tokens tokens = null;

    public TokensLoader() {
        Alkabot.getLogger().info("Reading tokens...");

        File tokenFile;
        try {
            tokenFile = new File(Alkabot.getTokensFilePath());

            Alkabot.debug("Full token file path: " + tokenFile.getPath());

            if (!tokenFile.exists()) {
                Alkabot.getLogger().error("The token file was not found");
                return;
            }
        } catch (Exception exception) {
            Alkabot.getLogger().error("Invalid token file path");
            exception.printStackTrace();
            return;
        }

        String tokenFileContent;
        try {
            tokenFileContent = Files.readString(tokenFile.toPath());
        } catch (IOException exception) {
            Alkabot.getLogger().error("Failed to read the token file content!");
            exception.printStackTrace();
            return;
        }

        try {
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .create();
            tokens = gson.fromJson(tokenFileContent, Tokens.class);
        } catch (Exception exception) {
            Alkabot.getLogger().error("Failed to read the JSON of the token file");
            Alkabot.getLogger().error("File content: " + tokenFileContent);
            exception.printStackTrace();
        }
    }

    public Tokens getTokens() {
        return tokens;
    }
}
