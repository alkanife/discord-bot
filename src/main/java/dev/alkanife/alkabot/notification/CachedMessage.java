package dev.alkanife.alkabot.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CachedMessage {

    private long id;
    private String content;
    private long author;
}