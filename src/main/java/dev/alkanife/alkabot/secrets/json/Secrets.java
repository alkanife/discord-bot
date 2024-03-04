package dev.alkanife.alkabot.secrets.json;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Secrets {

    @SerializedName("discord_token")
    private String discordToken;
    @SerializedName("spotify")
    private SpotifySecrets spotifySecrets;

}
