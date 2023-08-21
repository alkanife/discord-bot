package fr.alkanife.alkabot.token;

import com.google.gson.GsonBuilder;
import fr.alkanife.alkabot.Alkabot;
import fr.alkanife.alkabot.log.Logs;
import fr.alkanife.alkabot.util.tool.JsonLoader;

import java.io.File;
import java.nio.file.Files;

public class TokenLoader extends JsonLoader {

    public TokenLoader(Alkabot alkabot) {
        super(alkabot, new File(alkabot.getParameters().getTokensPath()));
    }

    @Override
    public void processLoad() throws Exception {
        alkabot.getLogger().debug("Using token file at path '" + file.getPath() + "'");

        String content = Files.readString(file.toPath());
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

        alkabot.setTokens(tokens);
    }
}
