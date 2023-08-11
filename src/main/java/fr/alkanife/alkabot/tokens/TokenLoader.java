package fr.alkanife.alkabot.tokens;

import com.google.gson.GsonBuilder;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.utils.tools.JsonLoader;

import java.io.File;
import java.nio.file.Files;

public class TokenLoader extends JsonLoader {

    public TokenLoader(Alkabot alkabot) {
        super(alkabot);
    }

    @Override
    public String getReloadMessage() {
        return "Reloading tokens - Please note that the Discord token will not be reloaded";
    }

    @Override
    public void processLoad(boolean reload) throws Exception {
        String content = Files.readString(new File(alkabot.getParameters().getTokensPath()).toPath());
        Tokens tokens = new GsonBuilder().serializeNulls().create().fromJson(content, Tokens.class);

        if (tokens == null) {
            tokens = new Tokens(null, new Tokens.Spotify());
        } else {
            if (tokens.getSpotify() == null) {
                tokens.setSpotify(new Tokens.Spotify());
            }
        }

        if (tokens.getSpotify().getClientId() == null || tokens.getSpotify().getClientSecret() == null) {
            alkabot.getLogger().info("Disabling Spotify support");
            alkabot.setSpotifySupport(false);
        }

        success = true;
        alkabot.setTokens(tokens);
    }
}
