package dev.alkanife.alkabot.util.timetracker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class TimeTracking {
    private String title;
    private UUID uuid;
    private long startTime;
}
