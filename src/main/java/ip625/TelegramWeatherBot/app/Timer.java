package ip625.TelegramWeatherBot.app;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Timer {

    //таймер для ежедневной рассылки (скопировано из Инета)
    static void setTimer(ClassProjectWeatherBot bot) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Europe/Moscow");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext;
        zonedNext = zonedNow.withHour(15).withMinute(0).withSecond(0);
        if (zonedNow.compareTo(zonedNext) > 0)
            zonedNext = zonedNext.plusDays(1);

        Duration duration = Duration.between(zonedNow, zonedNext);
        long initalDelay = duration.getSeconds();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(bot.updateSubscribers, initalDelay,
                24 * 60 * 60, TimeUnit.SECONDS);

    }

}
