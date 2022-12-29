package fr.alkanife.alkabot.configuration.tokens;

public class Tokens {

    private String discord_token;
    private Spotify spotify;

    public Tokens() {}

    public Tokens(String discord_token, Spotify spotify) {
        this.discord_token = discord_token;
        this.spotify = spotify;
    }

    public static class Spotify {
        private String client_id;
        private String client_secret;

        public Spotify() {}

        public Spotify(String client_id, String client_secret) {
            this.client_id = client_id;
            this.client_secret = client_secret;
        }

        public String getClient_id() {
            return client_id;
        }

        public String getClient_secret() {
            return client_secret;
        }
    }

    public String getDiscord_token() {
        return discord_token;
    }

    public Spotify getSpotify() {
        return spotify;
    }
}
