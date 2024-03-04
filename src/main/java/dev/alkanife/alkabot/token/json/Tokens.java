package dev.alkanife.alkabot.token.json;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tokens {

    @SerializedName("discord_token")
    private String discordToken;
    @SerializedName("spotify_tokens")
    private SpotifyTokens spotifyTokens;

}
