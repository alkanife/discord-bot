package dev.alkanife.alkabot.secrets;

import dev.alkanife.alkabot.Alkabot;
import dev.alkanife.alkabot.file.JsonFileManipulation;
import dev.alkanife.alkabot.secrets.json.Secrets;
import dev.alkanife.alkabot.secrets.json.SpotifySecrets;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SecretsManager extends JsonFileManipulation {

    @Getter
    private Secrets secrets;

    public SecretsManager(Alkabot alkabot) {
        super(alkabot, new File(alkabot.getArgs().getSecretFilePath()), Secrets.class);

        if (alkabot.getArgs().getOverrideSecrets() != null) {
            alkabot.getLogger().info("Overriding secrets");
            setOverride(alkabot.getArgs().getOverrideSecrets());
        }
    }

    @Override
    public boolean validateLoad(@NotNull Object data, boolean reload) {
        Secrets loadedSecrets = (Secrets) data;

        if (!reload) {
            if (loadedSecrets.getDiscordToken() == null) {
                getAlkabot().getLogger().error("No Discord token provided!");
                return false;
            }
        }

        // TODO put spotify support in the configuration
        if (loadedSecrets.getSpotifySecrets().getClientSecret() == null || loadedSecrets.getSpotifySecrets().getClientId() == null) {
            getAlkabot().setSpotifySupport(false);
        }

        secrets = loadedSecrets;
        return true;
    }

    @Override
    public @NotNull Object cleanData(@Nullable Object object) {
        Secrets secretObject = (Secrets) object;

        if (secretObject == null)
            secretObject = new Secrets();

        if (secretObject.getSpotifySecrets() == null)
            secretObject.setSpotifySecrets(new SpotifySecrets());

        return secretObject;
    }

    @Override
    public @Nullable Object getDataObject() {
        return secrets;
    }
}
