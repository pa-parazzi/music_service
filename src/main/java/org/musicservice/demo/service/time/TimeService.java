package org.musicservice.demo.service.time;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Component
public class TimeService {

    private final Clock clock = Clock.systemUTC();

    public LocalDate now(){
        return LocalDate.now(clock);
    }

    public LocalDate timeOfNewAlbumsRelease(){
        return now().minusMonths(12);
    }

}
