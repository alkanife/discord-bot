package fr.alkanife.alkabot.token;

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
    private Spotify spotify;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Spotify {
        @SerializedName("client_id")
        private String clientId;
        @SerializedName("client_secret")
        private String clientSecret;
    }

}
