package fr.alkanife.alkabot.music;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlkabotTrackPlaylist {

    private String title;
    private String url;
    private String thumbnailUrl;
    private AlkabotTrack firstTrack;
    private List<AlkabotTrack> tracks;

}
