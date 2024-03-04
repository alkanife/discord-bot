package dev.alkanife.alkabot.token;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.token.json.SpotifyTokens;
import dev.alkanife.alkabot.token.json.Tokens;
import dev.alkanife.alkabot.util.JsonDataFileManager;
import lombok.Getter;

import java.io.File;

public class TokenManager extends JsonDataFileManager {

    @Getter
    private Tokens tokens;

    public TokenManager(Alkabot alkabot, File file) {
        super(alkabot, file, Tokens.class);

        readMessage = "Reading tokens from '" + file.getName() + "'";
        createMessage = "The token file was not found at the specified path. Creating a new one at '" + file.getAbsolutePath() + "'";
        updateMessage = "Updating tokens of '" + file.getName() + "'. Note that a restart of the bot is necessary when changing the Discord token.";
        loadMessage = "Loading tokens";
    }

    @Override
    public void cleanData() {
        if (getData() == null)
            setData(new Tokens(null, new SpotifyTokens()));

        Tokens fileTokens = (Tokens) getData();

        if (fileTokens.getSpotifyTokens() == null) {
            fileTokens.setSpotifyTokens(new SpotifyTokens(null, null));
        }

        setData(fileTokens);
    }

    @Override
    public boolean onLoad(boolean reloading) {
        tokens = (Tokens) getData();

        if (!reloading) {
            if (tokens.getDiscordToken() == null) {
                getAlkabot().getLogger().error("No Discord token provided!");
                return false;
            }
        }

        // TODO put spotify support in the configuration
        if (tokens.getSpotifyTokens().getClientSecret() == null || tokens.getSpotifyTokens().getClientId() == null) {
            getAlkabot().setSpotifySupport(false);
        }

        return true;
    }
}
