package dev.alkanife.alkabot.secrets;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.secrets.json.SpotifySecrets;
import dev.alkanife.alkabot.secrets.json.Secrets;
import dev.alkanife.alkabot.util.JsonDataFileManager;
import lombok.Getter;

import java.io.File;

public class SecretsManager extends JsonDataFileManager {

    @Getter
    private Secrets secrets;

    public SecretsManager(Alkabot alkabot, File file) {
        super(alkabot, file, Secrets.class);

        readMessage = "Reading secrets from '" + file.getName() + "'";
        createMessage = "The secret file was not found at the specified path. Creating a new one at '" + file.getAbsolutePath() + "'";
        updateMessage = "Updating secrets of '" + file.getName() + "'. Note that a restart of the bot is necessary when changing the Discord token.";
        loadMessage = "Loading secrets";
    }

    @Override
    public void cleanData() {
        if (getData() == null)
            setData(new Secrets(null, new SpotifySecrets()));

        Secrets fileSecrets = (Secrets) getData();

        if (fileSecrets.getSpotifySecrets() == null) {
            fileSecrets.setSpotifySecrets(new SpotifySecrets(null, null));
        }

        setData(fileSecrets);
    }

    @Override
    public boolean onLoad(boolean reloading) {
        secrets = (Secrets) getData();

        if (!reloading) {
            if (getAlkabot().getArgs().getOverrideDiscordToken() == null) {
                if (secrets.getDiscordToken() == null) {
                    getAlkabot().getLogger().error("No Discord token provided!");
                    return false;
                }
            } else {
                getAlkabot().getLogger().info("Overriding Discord token");
                secrets.setDiscordToken(getAlkabot().getArgs().getOverrideDiscordToken());
            }
        }

        // TODO put spotify support in the configuration

        if (getAlkabot().getArgs().getOverrideSpotifyClientSecret() != null) {
            getAlkabot().getLogger().info("Overriding Spotify client secret");
            secrets.getSpotifySecrets().setClientSecret(getAlkabot().getArgs().getOverrideSpotifyClientSecret());
        }

        if (getAlkabot().getArgs().getOverrideSpotifyClientId() != null) {
            getAlkabot().getLogger().info("Overriding Spotify client ID");
            secrets.getSpotifySecrets().setClientId(getAlkabot().getArgs().getOverrideSpotifyClientId());
        }

        if (secrets.getSpotifySecrets().getClientSecret() == null || secrets.getSpotifySecrets().getClientId() == null) {
            getAlkabot().setSpotifySupport(false);
        }

        return true;
    }
}
